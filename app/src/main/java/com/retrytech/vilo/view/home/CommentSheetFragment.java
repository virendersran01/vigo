package com.retrytech.vilo.view.home;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.FragmentCommentSheetBinding;
import com.retrytech.vilo.utils.Global;
import com.retrytech.vilo.utils.SessionManager;
import com.retrytech.vilo.view.search.FetchUserActivity;
import com.retrytech.vilo.viewmodel.CommentSheetViewModel;
import com.retrytech.vilo.viewmodelfactory.ViewModelFactory;

import org.jetbrains.annotations.NotNull;


public class CommentSheetFragment extends BottomSheetDialogFragment {

    FragmentCommentSheetBinding binding;
    CommentSheetViewModel viewModel;
    public OnDismissListener onDismissListener;

    private void initView() {

        if (getArguments() != null && getArguments().getString("postid") != null) {
            viewModel.postId = getArguments().getString("postid");
            viewModel.commentCount.set(getArguments().getInt("commentCount"));
        }
        binding.refreshlout.setEnableRefresh(false);
        viewModel.fetchComments(false);
        if (getActivity() != null) {
            viewModel.sessionManager = new SessionManager(getActivity());
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        bottomSheetDialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog dialog = (BottomSheetDialog) dialog1;
            dialog.setCanceledOnTouchOutside(false);

        });

        return bottomSheetDialog;

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment_sheet, container, false);
        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelFactory(new CommentSheetViewModel()).createFor()).get(CommentSheetViewModel.class);

        initView();
        initListeners();
        initObserve();
        binding.setViewmodel(viewModel);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        closeKeyboard();
        onDismissListener.onDismissed(viewModel.commentCount.get());
    }

    private void initListeners() {
        viewModel.adapter.setListener((comment, position, type) -> {
            switch (type) {
                // On delete click
                case 1:
                    viewModel.callApitoDeleteComment(comment.getCommentsId(), position);
                    break;
                // On user Profile Click
                case 2:
                    Intent intent = new Intent(getActivity(), FetchUserActivity.class);
                    intent.putExtra("userid", comment.getUserId());
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        });
        binding.imgClose.setOnClickListener(v -> dismiss());
        binding.imgSend.setOnClickListener(v -> {
            if (Global.ACCESS_TOKEN.isEmpty()) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).initLogin(getActivity(), () -> viewModel.addComment());
                }
                closeKeyboard();
            } else {
                viewModel.addComment();
            }
        });
        binding.refreshlout.setOnLoadMoreListener(refreshLayout -> viewModel.onLoadMore());

    }

    private void initObserve() {
        viewModel.onLoadMoreComplete.observe(this, onLoadMore -> {
            binding.refreshlout.finishLoadMore();
            if (onLoadMore != null && !onLoadMore) {
                binding.etComment.setText("");
                closeKeyboard();
            }
        });

    }

    public void closeKeyboard() {
        if (getDialog() != null) {
            InputMethodManager im = (InputMethodManager) getDialog().getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null && getDialog().getCurrentFocus() != null) {
                im.hideSoftInputFromWindow(getDialog().getCurrentFocus().getWindowToken(), 0);
            }
            if (getDialog().getWindow() != null) {
                getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        }
    }

    public interface OnDismissListener {
        void onDismissed(int count);
    }
}