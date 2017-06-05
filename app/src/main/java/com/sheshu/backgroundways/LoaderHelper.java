package com.sheshu.backgroundways;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * Created by Sheshu on 6/4/17.
 */
public class LoaderHelper implements LoaderManager.LoaderCallbacks {
    public static final int TYPE_CURSOR_LOADER = 1;
    public static final int TYPE_ASYNC_TASK_LOADER = 2;
    private static final String TAG = "LoaderHelper";
    private final Handler mHandler;
    private Activity mActivity;
    private int mType;
    LoaderHelper(Activity aActivity, Handler handler, int type) {
        mActivity = aActivity;
        mHandler = handler;
        mType = type;
    }
    public void start() {
        Loader loader = mActivity.getLoaderManager().initLoader(100, null, this);
        loader.forceLoad();
        // loader.startLoading();
    }
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (mType == TYPE_ASYNC_TASK_LOADER) {
            MyAsyncTaskLoader myLoader = new MyAsyncTaskLoader(mActivity, mHandler);
            return myLoader;
        } else {
        }
        return null;
    }
    @Override
    public void onLoadFinished(Loader loader, Object data) {
        Log.e(TAG, " " + data);
        ((CallbackInterface) mActivity).gotResult((String[]) data, Utils.TYPE_LOADER);
    }
    @Override
    public void onLoaderReset(Loader loader) {
    }
    static class MyAsyncTaskLoader extends AsyncTaskLoader<String[]> {
        static Handler mHandler;
        public MyAsyncTaskLoader(Context context, Handler handler) {
            super(context);
            mHandler = handler;
        }
        @Override
        public String[] loadInBackground() {
            String[] result = Utils.timeConsumingActivity();
            return result;
        }
    }
    //class MyCursorLoader extends CursorLoader<>
}
