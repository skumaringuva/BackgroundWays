package com.sheshu.backgroundways;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

/**
 * Created by Sheshu on 6/4/17.
 */
public class MyGCMTaskService extends GcmTaskService {
    private static final String TAG = "MyGCMTaskService";
    @Override
    public int onRunTask(TaskParams taskParams) {
        Bundle b = taskParams.getExtras();
        ResultReceiver resultReceiver = b.getParcelable(Utils.RESULT_RECEIVER_KEY);
        // To send the message back, we are using result receiver.
        // The purpose of this task service is not similar to Intentservice.
        // This way is used to judge all methods on equal platform. Not for its actual purpose.
        String[] result = Utils.timeConsumingActivity();
        b = new Bundle();
        b.putStringArray(Utils.MESSAGE_KEY_TAG, result);
        try {
            resultReceiver.send(1, b);
        } catch (NullPointerException npe) {
            Log.e(TAG, "GCM Networkmanager task NPE");
        }
        return 0;
    }
}
