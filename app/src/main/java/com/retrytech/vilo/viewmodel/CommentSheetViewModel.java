package com.retrytech.vilo.viewmodel;

import android.text.TextUtils;
import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.retrytech.vilo.adapter.CommentAdapter;
import com.retrytech.vilo.utils.Global;
import com.retrytech.vilo.utils.SessionManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CommentSheetViewModel extends ViewModel {
    public String postId;
    public String comment;
    public ObservableInt commentCount = new ObservableInt(0);
    public ObservableBoolean isLoading = new ObservableBoolean(false);
    public ObservableBoolean isEmpty = new ObservableBoolean(true);
    public CommentAdapter adapter = new CommentAdapter();
    public int start = 0;
    private int count = 15;
    public MutableLiveData<Boolean> onLoadMoreComplete = new MutableLiveData<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    public SessionManager sessionManager;

    public void afterCommentTextChanged(CharSequence s) {
        comment = s.toString();
    }


    public void fetchComments(boolean isLoadMore) {

        disposable.add(Global.initRetrofit().getPostComments(postId, count, start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isLoading.set(true))
                .doOnTerminate(() -> {
                    onLoadMoreComplete.setValue(true);
                    isLoading.set(false);
                })
                .subscribe((comment1, throwable) -> {

                    if (comment1 != null && comment1.getData() != null && !comment1.getData().isEmpty()) {

                        if (isLoadMore) {
                            adapter.loadMore(comment1.getData());
                        } else {
                            adapter.updateData(comment1.getData());
                        }
                        start = start + count;
                    }
                    isEmpty.set(adapter.getData().isEmpty());

                }));
    }

    public void onLoadMore() {
        fetchComments(true);
    }

    public void addComment() {
        if (!TextUtils.isEmpty(comment)) {
            callApiToSendComment();
        }
    }

    private void callApiToSendComment() {
        disposable.add(Global.initRetrofit().addComment(Global.ACCESS_TOKEN, postId, comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isLoading.set(true))
                .doOnTerminate(() -> isLoading.set(false))
                .subscribe((comment1, throwable) -> {
                    if (comment1 != null && comment1.getStatus() != null) {
                        Log.d("ADDED", "Success");
                        start = 0;
                        fetchComments(false);
                        onLoadMoreComplete.setValue(false);
                        commentCount.set(commentCount.get() + 1);
                    }
                }));
    }

    public void callApitoDeleteComment(String commentId, int position) {
        disposable.add(Global.initRetrofit().deleteComment(Global.ACCESS_TOKEN, commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isLoading.set(true))
                .doOnTerminate(() -> isLoading.set(false))
                .subscribe((deleteComment, throwable) -> {
                    if (deleteComment != null && deleteComment.getStatus() != null) {
                        Log.d("DELETED", "Success");
                        adapter.getData().remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeRemoved(position, adapter.getData().size());
                        commentCount.set(commentCount.get() - 1);

                    }
                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
