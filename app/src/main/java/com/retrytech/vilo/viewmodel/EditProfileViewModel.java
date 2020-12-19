package com.retrytech.vilo.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.retrytech.vilo.model.user.User;
import com.retrytech.vilo.utils.Global;

import java.io.File;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditProfileViewModel extends ViewModel {

    public User user = null;
    public String imageUri = "";
    public ObservableBoolean isUsernameLoading = new ObservableBoolean(false);
    public ObservableBoolean isUsernameAvailable = new ObservableBoolean(true);
    public ObservableInt length = new ObservableInt();
    public MutableLiveData<Boolean> updateProfile = new MutableLiveData<>();
    public String curUserName = "";
    private String newUserName = "";
    private CompositeDisposable disposable = new CompositeDisposable();
    private String fullName = "", bio = "", fbUrl = "", instaUrl = "", youtubeUrl = "";


    public void afterUserNameTextChanged(CharSequence s) {
        newUserName = s.toString();

        if (!curUserName.equals(newUserName)) {
            checkForUserName();
        } else {
            if (!newUserName.isEmpty()) {
                isUsernameAvailable.set(false);
                isUsernameLoading.set(false);
            }
            isUsernameAvailable.set(true);
            isUsernameLoading.set(false);
        }
    }

    public void afterTextChanged(CharSequence charSequence, int type) {
        switch (type) {
            case 1:
                fullName = charSequence.toString();
                break;
            case 2:
                bio = charSequence.toString();
                length.set(bio.length());
                break;
            case 3:
                fbUrl = charSequence.toString();
                break;
            case 4:
                instaUrl = charSequence.toString();
                break;
            case 5:
                youtubeUrl = charSequence.toString();
                break;
            default:
                break;
        }
    }

    private void checkForUserName() {
        disposable.add(Global.initRetrofit().checkUsername(newUserName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isUsernameLoading.set(true))
                .doOnTerminate(() -> isUsernameLoading.set(false))
                .subscribe((checkUsername, throwable) -> {
                    if (checkUsername != null && checkUsername.getStatus() != null) {
                        isUsernameAvailable.set(checkUsername.getStatus());
                    }
                }));
    }

    public void updateUser() {
        HashMap<String, RequestBody> hashMap = new HashMap<>();
        hashMap.put("full_name", toRequestBody(fullName));
        hashMap.put("user_name", toRequestBody(newUserName));
        hashMap.put("bio", toRequestBody(bio));
        hashMap.put("fb_url", toRequestBody(fbUrl));
        hashMap.put("insta_url", toRequestBody(instaUrl));
        hashMap.put("youtube_url", toRequestBody(youtubeUrl));

        MultipartBody.Part body = null;
        if (imageUri != null && !imageUri.isEmpty()) {
            File file = new File(imageUri);
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body = MultipartBody.Part.createFormData("user_profile", file.getName(), requestFile);
        }
        disposable.add(Global.initRetrofit().updateUser(Global.ACCESS_TOKEN, hashMap, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe((updateUser, throwable) -> {
                    if (updateUser != null && updateUser.getStatus()) {
                        user = updateUser;
                        updateProfile.setValue(true);
                    }
                }));
    }

    public RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

    public void updateData() {
        fullName = user.getData().getFullName();
        newUserName = user.getData().getUserName();
        bio = user.getData().getBio();
        fbUrl = user.getData().getFbUrl();
        instaUrl = user.getData().getInstaUrl();
        youtubeUrl = user.getData().getYoutubeUrl();
    }
}
