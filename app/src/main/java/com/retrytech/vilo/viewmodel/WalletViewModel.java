package com.retrytech.vilo.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.retrytech.vilo.model.wallet.CoinRate;
import com.retrytech.vilo.model.wallet.MyWallet;
import com.retrytech.vilo.model.wallet.RewardingActions;
import com.retrytech.vilo.utils.Global;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WalletViewModel extends ViewModel {

    public MutableLiveData<MyWallet> myWallet = new MutableLiveData<>();
    public MutableLiveData<CoinRate> coinRate = new MutableLiveData<>();
    public MutableLiveData<RewardingActions> rewardingActions = new MutableLiveData<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    public List<RewardingActions.Data> rewardingActionsList = new ArrayList<>();

    public void fetchMyWallet() {
        disposable.add(Global.initRetrofit().getMyWalletDetails(Global.ACCESS_TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe((wallet, throwable) -> {
                    if (wallet != null && wallet.getStatus() != null) {
                        myWallet.setValue(wallet);
                    }
                }));
    }

    public void fetchRewardingActions() {
        disposable.add(Global.initRetrofit().getRewardingAction(Global.ACCESS_TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe((actions, throwable) -> {
                    if (actions != null && actions.getStatus() != null) {
                        rewardingActionsList = actions.getData();
                        this.rewardingActions.setValue(actions);
                    }
                }));
    }

    public void fetchCoinRate() {
        disposable.add(Global.initRetrofit().getCoinRate(Global.ACCESS_TOKEN)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe((coinrate, throwable) -> {
                    if (coinrate != null && coinrate.getStatus() != null) {
                        this.coinRate.setValue(coinrate);
                    }
                }));
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

}
