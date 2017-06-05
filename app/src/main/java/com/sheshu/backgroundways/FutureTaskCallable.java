package com.sheshu.backgroundways;

import android.os.Handler;

import java.util.concurrent.Callable;

/**
 * Created by Sheshu on 6/4/17.
 */
public class FutureTaskCallable implements Callable<String[]> {
    private final Handler mHandler;
    @Override
    public String[] call() throws Exception {
        String[] result = Utils.timeConsumingActivity();
        Utils.postToHandler(result, mHandler, Utils.TYPE_FUTURE_TASK);
        return result;
    }
    FutureTaskCallable(Handler handler) {
        mHandler = handler;
    }
}
