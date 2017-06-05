package com.sheshu.backgroundways;

import android.os.AsyncTask;
import android.os.Handler;

/**
 * Created by Sheshu on 6/4/17.
 */
class LongAsyncTask extends AsyncTask<Void, Void, String[]> {
    private final Handler mHandler;
    private int mType;
    LongAsyncTask(Handler handler, int type) {
        mHandler = handler;
        mType = type;
    }
    @Override
    protected String[] doInBackground(Void... params) {
        String[] result = Utils.timeConsumingActivity();
        return result;
    }
    @Override
    protected void onPostExecute(String[] result) {
        Utils.postToHandler(result, mHandler, mType);
    }
}

