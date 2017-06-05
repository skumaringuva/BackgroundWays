package com.sheshu.backgroundways;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements CallbackInterface {
    private static final String TAG = "BackgroundWays";
    long startTime = 0;
    Utils mUtils;
    @BindView(R.id.start_button)
    Button StartButton;
    @BindView(R.id.type_spinner)
    Spinner typeSpinner;
    String[] typeArrayStrings;
    View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int selection = typeSpinner.getSelectedItemPosition();
            if (selection > 0) {
                Log.e(TAG, "SELECTED item: " + selection + " " + typeArrayStrings[selection]);
                startBackgroundSerial(selection);
            } else {
                startTime = System.currentTimeMillis();
                String[] result = Utils.timeConsumingActivity();
                gotResult(result, Utils.TYPE_DIRECT);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        typeArrayStrings = getResources().getStringArray(R.array.background_type);
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, typeArrayStrings);
        typeSpinner.setAdapter(stringArrayAdapter);
        StartButton.setOnClickListener(mButtonListener);
        mUtils = new Utils(this);
        // startBackgroundSerial(Utils.TYPE_THREAD);
    }
    void startBackgroundSerial(int type) {
        startTime = System.currentTimeMillis();
        //mUtils.run(Utils.TYPE_ASYNC_TASK);
        mUtils.run(type);
    }
    @Override
    public void gotResult(String[] result, int messageFrom) {
        long endTime = System.currentTimeMillis();
        Log.d(TAG, "Time taken ms: " + (endTime - startTime) + " GOT RESULT: " + Arrays.toString(result) + " messageFrom: " + typeArrayStrings[messageFrom]);
    }
}








