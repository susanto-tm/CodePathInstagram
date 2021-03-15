package com.example.instagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("OJHyV7Q0i4P2MHEXM9cQOq1ME91DxUgqbpJAyPDf")
                .clientKey("H3WVayMtoyC6xczbFoCC9QM1piNLmXGhsFKjjzQT")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
