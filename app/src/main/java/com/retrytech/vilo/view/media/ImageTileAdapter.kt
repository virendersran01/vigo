package com.retrytech.vilo.view.media

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.retrytech.vilo.R
import com.retrytech.vilo.databinding.TileImageBinding

class ImageTileAdapter : RecyclerView.Adapter<ImageTileAdapter.VHImageTileBase>() {

    var mList: List<String> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var onItemClick: OnItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHImageTileBase =
            LayoutInflater.from(parent.context).inflate(
                    R.layout.tile_image,
                    parent, false
            ).let { view ->
                VHImageTileBase(view)
            }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: VHImageTileBase, position: Int) {
        holder.update(mList[position])
    }

    interface OnItemClick {
        fun onClick(uri: String)
    }

    inner class VHImageTileBase(
            view: View
    ) : RecyclerView.ViewHolder(view) {
        var binding: TileImageBinding? = null

        init {
            binding = DataBindingUtil.bind(view)
        }

        fun update(
                uri: String
        ) {
            binding?.root?.setOnClickListener { onItemClick?.onClick(uri) }
            binding?.uri = uri
        }


    }
}



