package com.nazir.myapplication123;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;


public class GamePadActivity extends AppCompatActivity  implements OrientationSensorInterface{

    @Bind(R.id.text)
    TextView mTextView;
    @Bind(R.id.btn)
    Button btn;
    private Orientation orientationSensor;

    public DatagramSocket mSocket;
    public DatagramPacket mPacket;

    Boolean fwrdBtn = false;
    Boolean backBtn = false;
    Boolean handBrakeBtn = false;
    Boolean calibrated = false;
    @OnClick(R.id.btn)
    public void onClick() {
        for (int i = 0; i < mCalibrated.length; i++) {
            mCalibrated[i] = mLast[i];
        }
        calibrated = true;
        btn.setVisibility(View.GONE);
    }
    @OnClick(R.id.fwrd)
    public void onForwardClick(){
        fwrdBtn = false;
    }
    @OnTouch(R.id.fwrd)
    public boolean onForwardTouch(){
        fwrdBtn = true;
        return false;
    }

    @OnClick(R.id.back)
    public void onBackClick(){
        backBtn = false;
    }
    @OnTouch(R.id.back)
    public boolean onBackTouch(){
        backBtn = true;
        return false;
    }

    @OnClick(R.id.handbrake)
    public void onhandBrakeClick(){
        handBrakeBtn = false;
    }
    @OnTouch(R.id.handbrake)
    public boolean onhandBrakeTouch(){
        handBrakeBtn= true;
        return false;
    }

    private double[] mCalibrated;
    private double[] mLast;


    public String getIP(Intent intent){
        String address = intent.getStringExtra("address");
        return address;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main2);
        ButterKnife.bind(GamePadActivity.this);
        mLast = new double[3];
        mCalibrated = new double[3];
        for (int i = 0; i < mCalibrated.length; i++) {
            mCalibrated[i] = 0;
            mLast[i] = 0;
        }

        String address = getIP(getIntent());
        try {
            mSocket = new DatagramSocket();
            mPacket = new DatagramPacket(new byte[0], 0);
            new ConnectTask().execute(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        orientationSensor = new Orientation(this,this);
        orientationSensor.init(0.1,0.1,0.1);
        orientationSensor.on(1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationSensor.off();
    }

    @Override
    public void orientation(Double AZIMUTH, Double PITCH, Double ROLL) {

        Log.d("Azimuth",String.valueOf(AZIMUTH));
        Log.d("PITCH",String.valueOf(PITCH));
        Log.d("ROLL",String.valueOf(ROLL));

        mLast[0] = AZIMUTH;
        mLast[1] = PITCH;
        mLast[2] = ROLL;

        AZIMUTH = AZIMUTH - mCalibrated[0];
        PITCH = PITCH - mCalibrated[1];
        ROLL = ROLL - mCalibrated[2];

        showValues(PITCH);
        String data = PITCH+" "+fwrdBtn+" "+backBtn+" "+handBrakeBtn;
        if (calibrated) {
            new SendDataTask(data.getBytes()).execute();
        }
    }

    public void showValues(Double PITCH) {
        mTextView.setText(PITCH.toString());
    }
    public class SendDataTask extends AsyncTask<Void, Void, Void> {

        byte[] mData;

        public SendDataTask(byte[] data) {
            mData = data;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mPacket.setData(mData);
                mSocket.send(mPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private class ConnectTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                mPacket.setAddress(InetAddress.getByName(params[0]));
                mPacket.setPort(3000);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}


