package com.retrytech.vilo.view.media

import android.app.Dialog
import android.content.DialogInterface
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.retrytech.vilo.R
import kotlinx.android.synthetic.main.imagepicker.*
import java.util.*

class BottomSheetImagePicker internal constructor() :
        BottomSheetDialogFragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private var url: String? = null
    private var currentPhotoUri: Uri? = null

    @DimenRes
    private var peekHeight = R.dimen.imagePickerPeekHeight

    @DimenRes
    private var columnSizeRes = R.dimen.imagePickerColumnSize

    var onDismiss: OnDismiss? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    private var type: Int? = -1
    private val adapter by lazy {
        ImageTileAdapter()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getInt("type", -1)

        if (requireContext().hasReadStoragePermission) {
            LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
        } else {
            requestReadStoragePermission(REQUEST_PERMISSION_READ_STORAGE)
        }
        if (savedInstanceState != null) {
            currentPhotoUri = savedInstanceState.getParcelable(STATE_CURRENT_URI)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View =
            inflater.inflate(R.layout.imagepicker, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title.text = if (type == 1) {
            "Select Video"
        } else {
            "Select Profile"
        }
        img_close.setOnClickListener { dismiss() }
        recycler.layoutManager = AutoFitLayoutManager(requireContext(), columnSizeRes)
        (recycler.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        recycler.adapter = adapter
        adapter.onItemClick = object : ImageTileAdapter.OnItemClick {
            override fun onClick(uri: String) {
                url = uri
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (url != null) {
            onDismiss?.onDismiss(url!!)
        } else {
            onDismiss?.onDismiss()
        }
        super.onDismiss(dialog)
    }

    interface OnDismiss {
        fun onDismiss(uri: String = "")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            super.onCreateDialog(savedInstanceState).apply {
                setOnShowListener {
                    val bottomSheet = findViewById<View>(R.id.design_bottom_sheet)
                    bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
                    bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(peekHeight)
                    bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
                }
            }

    private val bottomSheetCallback by lazy {
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                view?.alpha = if (slideOffset < 0f) 1f + slideOffset else 1f
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismissAllowingStateLoss()
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION_READ_STORAGE ->
                if (grantResults.isPermissionGranted)
                    LoaderManager.getInstance(this).initLoader(LOADER_ID, null, this)
                else dismissAllowingStateLoss()

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(STATE_CURRENT_URI, currentPhotoUri)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        if (id != LOADER_ID) throw IllegalStateException("illegal loader id: $id")
        val sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC"
        val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA
        )
        val uri: Uri = if (type == 1) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        return CursorLoader(requireContext(), uri, projection, null, null, sortOrder)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {

        data ?: return

        val columnIndex = data.getColumnIndex(MediaStore.Images.Media.DATA)
        val items = ArrayList<String>()
        while (data.moveToNext()) {
            val uri = data.getString(columnIndex)
            items.add(uri)
        }
        data.moveToFirst()
        adapter.mList = items
    }


    override fun onLoaderReset(loader: Loader<Cursor>) {
        adapter.mList = emptyList()
    }

    companion object {
        private const val LOADER_ID = 0x1337
        private const val REQUEST_PERMISSION_READ_STORAGE = 0x2000
        private const val STATE_CURRENT_URI = "stateUri"
        fun getNewInstance(type: Int) = BottomSheetImagePicker().apply {
            val bundle = Bundle()
            bundle.putInt("type", type)
            arguments = bundle
        }
    }

}


