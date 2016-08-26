package com.nazir.myapplication123;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity{

    @Bind(R.id.et)
    EditText mEditText;

    SharedPreferences sharedPreferences;

    @OnClick(R.id.btn)
    public void sendIP(){
        Intent intent = new Intent(this, GamePadActivity.class);
        intent.putExtra("address", mEditText.getText().toString());
        startActivity(intent);
        sharedPreferences.edit().putString("address",mEditText.getText().toString()).apply();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditText.setText(sharedPreferences.getString("address",""));



    }

}
