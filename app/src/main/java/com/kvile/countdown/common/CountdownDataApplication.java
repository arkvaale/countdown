package com.kvile.countdown.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CountdownDataApplication {

    public static final String STORE_FILE_NAME = "countdowns";
    private final Context context;

    public CountdownDataApplication(Context context) {
        this.context = context;
    }

    public <T> void setList(String key, List<T> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        set(key, json);
    }

    public void set(String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public List<Countdown> getList() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE_FILE_NAME, Context.MODE_PRIVATE);
        List<Countdown> arrayItems = new ArrayList<>();
        String serializedObject = sharedPreferences.getString(STORE_FILE_NAME, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Countdown>>() {
            }.getType();
            arrayItems = gson.fromJson(serializedObject, type);
        }
        arrayItems.sort(Comparator.comparingLong(Countdown::getDaysRemaining));
        return arrayItems;
    }

}
