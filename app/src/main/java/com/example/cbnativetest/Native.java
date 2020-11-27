package com.example.cbnativetest;

import kr.go.mobile.common.v3.CBApplication;

public class Native extends CBApplication {
    @Override
    public void onCreate() {
        enabledDevMode();
        super.onCreate();
    }
}
