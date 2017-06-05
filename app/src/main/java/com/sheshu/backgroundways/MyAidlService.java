package com.sheshu.backgroundways;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class MyAidlService extends Service {
    public MyAidlService() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // Return the interface
        return mBinder;
    }
    private final IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub() {
        @Override
        public String[] runAsyncTask() throws RemoteException {
            return Utils.timeConsumingActivity();
        }
    };
}
