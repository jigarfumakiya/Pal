package com.aspiration.pal;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by admin on 4/24/2015.
 */
public class ApplicatioFile extends Application
{

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "jfwX5fwWTS49q6RcbMKl8VG31UJIADuYkicVLKSe", "ngkDKf0Hg3luE4t5W3TtMceKzzAOsha28KKzKyJt");
    }
}
