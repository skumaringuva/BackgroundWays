package com.sheshu.backgroundways;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

/**
 * Created by Sheshu on 6/3/17.
 */
public class LongIntentService extends IntentService {
    public LongIntentService(String name) {
        super(name);
    }
    public LongIntentService() {
        super("");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        ResultReceiver resultReceiver = intent.getParcelableExtra(Utils.RESULT_RECEIVER_KEY);
        String[] result = Utils.timeConsumingActivity();
        Bundle b = new Bundle();
        b.putStringArray(Utils.MESSAGE_KEY_TAG, result);
        resultReceiver.send(1, b);
    }
}
