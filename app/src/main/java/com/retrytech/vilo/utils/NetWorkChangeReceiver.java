package com.retrytech.vilo.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import kotlin.jvm.JvmOverloads;

public class NetWorkChangeReceiver extends BroadcastReceiver {
    private OnNetworkStatusChange onNetworkStatusChange;

    @JvmOverloads
    public NetWorkChangeReceiver(OnNetworkStatusChange onNetworkStatusChange) {
        this.onNetworkStatusChange = onNetworkStatusChange;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            if (onNetworkStatusChange != null) {
                onNetworkStatusChange.isOnline(isOnline(context));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private Boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (cm != null) {
                networkInfo = cm.getActiveNetworkInfo();
            }
            if (networkInfo != null) {
                return networkInfo.isConnected();
            } else {
                return true;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }

    }

    public interface OnNetworkStatusChange {
        void isOnline(Boolean isOnline);
    }
}
