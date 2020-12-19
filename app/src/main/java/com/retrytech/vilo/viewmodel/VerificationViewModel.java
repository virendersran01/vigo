package com.retrytech.vilo.viewmodel;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.retrytech.vilo.utils.Global;

import java.io.File;
import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class VerificationViewModel extends ViewModel {


    public String proofUri = "", useUri = "";
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public ObservableBoolean isEnabled = new ObservableBoolean(false);
    private CompositeDisposable disposable = new CompositeDisposable();
    private String idNumber = "", name = "", address = "";

    public void requestVerify() {

        MultipartBody.Part body1 = null;
        if (proofUri != null && !proofUri.isEmpty()) {
            File file = new File(proofUri);
            RequestBody requestFile2 =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body1 = MultipartBody.Part.createFormData("post_sound", file.getName(), requestFile2);
        }

        MultipartBody.Part body2 = null;
        if (useUri != null && !useUri.isEmpty()) {
            File file = new File(useUri);
            RequestBody requestFile2 =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            body2 = MultipartBody.Part.createFormData("post_sound", file.getName(), requestFile2);
        }

        HashMap<String, RequestBody> hashMap = new HashMap<>();
        hashMap.put("id_number", toRequestBody(idNumber));
        hashMap.put("name", toRequestBody(name));
        hashMap.put("address", toRequestBody(address));

        disposable.add(Global.initRetrofit().verifyRequest(Global.ACCESS_TOKEN, hashMap, body1, body2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(disposable1 -> isLoading.setValue(true))
                .doOnTerminate(() -> isLoading.setValue(false))
                .subscribe((restResponse, throwable) -> {

                }));
    }

    public void afterTextChanged(CharSequence text, int type) {
        switch (type) {
            case 0:
                idNumber = text.toString();
                break;
            case 1:
                name = text.toString();
                break;
            case 2:
                address = text.toString();
                break;
            default:
                break;
        }
        checkValidData();
    }

    private void checkValidData() {
        isEnabled.set(idNumber != null &&
                !idNumber.isEmpty() &&
                name != null &&
                !name.isEmpty() &&
                address != null &&
                !address.isEmpty());
    }

    public RequestBody toRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
