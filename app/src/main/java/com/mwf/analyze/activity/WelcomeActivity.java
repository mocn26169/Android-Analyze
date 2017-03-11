package com.mwf.analyze.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.mwf.analyze.R;

public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        toWritePoetry(null);
    }

    private void toActivity(Context _context, Class<? extends Activity> _class) {
        Intent intent = new Intent(_context, _class);
        startActivity(intent);
    }

    public void toMainActivity(View view) {
        Log.i(getClass().getSimpleName(), "toMainActivity");
        toActivity(this, MainActivity.class);
    }

    public void toAnalyzePoem(View view) {
        Log.i(getClass().getSimpleName(), "toAnalyzePoem");
        toActivity(this, AnalyzePoemActivity.class);
    }

    public void toWritePoetry(View view) {
        Log.i(getClass().getSimpleName(), "toWritePoetry");
        toActivity(this, WritePoetryActivity.class);
    }

}
