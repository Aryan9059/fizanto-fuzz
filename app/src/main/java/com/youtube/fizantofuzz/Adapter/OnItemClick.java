package com.youtube.fizantofuzz.Adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface OnItemClick {
    void onCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public void onItemCLick(String uid, View view);
}
