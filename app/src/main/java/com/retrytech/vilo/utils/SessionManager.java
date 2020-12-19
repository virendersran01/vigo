package com.retrytech.vilo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.retrytech.vilo.model.user.User;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this.pref = context.getSharedPreferences(Const.PREF_NAME, MODE_PRIVATE);
        this.editor = this.pref.edit();
    }

    public void saveStringValue(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringValue(String key) {
        return pref.getString(key, "");
    }

    public void saveBooleanValue(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBooleanValue(String key) {
        return pref.getBoolean(key, false);
    }

    public void saveUser(User user) {
        editor.putString(Const.USER, new Gson().toJson(user));
        editor.apply();
    }

    public User getUser() {
        String userString = pref.getString(Const.USER, "");
        if (!userString.isEmpty()) {
            return new Gson().fromJson(userString, User.class);
        }
        return null;
    }

    public void saveFavouriteMusic(String id) {
        ArrayList<String> fav = (ArrayList<String>) getFavouriteMusic();
        if (fav != null) {
            if (fav.contains(id)) {
                fav.remove(id);

            } else {
                fav.add(id);

            }
        } else {
            fav = new ArrayList<>();
            fav.add(id);

        }
        editor.putString(Const.FAV, new Gson().toJson(fav));
        editor.apply();
    }

    public List<String> getFavouriteMusic() {
        String userString = pref.getString(Const.FAV, "");
        if (!userString.isEmpty()) {
            return new Gson().fromJson(userString, new TypeToken<ArrayList<String>>() {
            }.getType());
        }
        return new ArrayList<>();
    }

    public void clear() {
        editor.clear().apply();
    }

}
