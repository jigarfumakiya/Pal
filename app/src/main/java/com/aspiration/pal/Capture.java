package com.aspiration.pal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

/**
 * Created by admin on 4/24/2015.
 */
public class Capture extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture);

    }

    public void addanother(View v) {

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

    }
}

