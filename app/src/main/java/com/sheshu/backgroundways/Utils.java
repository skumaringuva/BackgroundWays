package com.sheshu.backgroundways;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sheshu on 6/3/17.
 */
public class Utils {
    public static final int TYPE_DIRECT = 0;
    public static final int TYPE_THREAD = 1;
    public static final int TYPE_ASYNC_TASK = 2;
    public static final int TYPE_INTENT_SERVICE = 3;
    public static final int TYPE_LOADER = 4;
    public static final int TYPE_JOB_SCHEDULER = 5;
    public static final int TYPE_GCM_NETWORK_MANAGER = 6;
    public static final int TYPE_COUNT_DOWN_TIMER = 7;
    public static final int TYPE_FUTURE_TASK = 8;
    public static final int TYPE_THREAD_PPOL_EXECUTOR = 9;
    public static final int TYPE_SERVICE_WITH_AIDL = 10;
    public static final int TYPE_SERVICE_WITH_BINDER_CONNECTION = 11;
    public static final String MESSAGE_KEY_TAG = "result";
    static final String MESSAGE_KEY_FROM = "message_from";
    static final String RESULT_RECEIVER_KEY = "result_receiver";
    private static final String TAG = "BackgroundWays";
    final public Handler myHandler;
    Activity mActivity;
    Utils(Activity aMainActivity) {
        mActivity = aMainActivity;
        myHandler = new Handler(mActivity.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String[] result = msg.getData().getStringArray(MESSAGE_KEY_TAG);
                int messageFrom = msg.getData().getInt(MESSAGE_KEY_FROM);
                ((CallbackInterface) mActivity).gotResult(result, messageFrom);
            }
        };
    }
    public static void postToHandler(String[] result, Handler handler, int messageFrom) {
        Message msgObj = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putStringArray(MESSAGE_KEY_TAG, result);
        b.putInt(MESSAGE_KEY_FROM, messageFrom);
        msgObj.setData(b);
        handler.sendMessage(msgObj);
    }
    public static String[] timeConsumingActivity() {
        int count = 1000000;
        String test = "hello,How,Are,You";
        String[] result = null;
        for (int i = 0; i < count; i++) {
            result = test.split(",");
        }
        return result;
    }
    String[] run(int type) {
        String[] result = null;
        switch (type) {
            case TYPE_THREAD:
                runInThread();
                break;
            case TYPE_ASYNC_TASK:
                runInAsyncTask();
                break;
            case TYPE_INTENT_SERVICE:
                runInIntentService();
                break;
            case TYPE_LOADER:
                runInLoader();
                break;
            case TYPE_JOB_SCHEDULER:
                runInJobScheduler();
                break;
            case TYPE_GCM_NETWORK_MANAGER:
                runInGCMNetworkManager();
                break;
            case TYPE_COUNT_DOWN_TIMER:
                runInCountDownTimer();
                break;
            case TYPE_FUTURE_TASK:
                runInFutureTask();
                break;
            case TYPE_THREAD_PPOL_EXECUTOR:
                runInThreadPoolExecutor();
                break;
            case TYPE_SERVICE_WITH_AIDL:
                runInServiceWithAidl();
                break;
            case TYPE_SERVICE_WITH_BINDER_CONNECTION:
                runInServiceWithConnection();
                break;
            default:
                Log.d(TAG, " run without any thread directly, thats what user want");
                return timeConsumingActivity();
        }
        return null;
    }
    private void runInThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                String[] result = timeConsumingActivity();
                postToHandler(result, myHandler, TYPE_THREAD);
            }
        };
        thread.start();
    }
    private void runInAsyncTask() {
        LongAsyncTask mytask = new LongAsyncTask(myHandler, TYPE_ASYNC_TASK);
        mytask.execute();
    }
    private void runInIntentService() {
        final ResultReceiver myResultReceiver = new ResultReceiver(myHandler) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                //super.onReceiveResult(resultCode, resultData);
                String[] result = resultData.getStringArray(MESSAGE_KEY_TAG);
                postToHandler(result, myHandler, TYPE_INTENT_SERVICE);
            }
        };
        Intent intent = new Intent();
        intent.setClass(mActivity, LongIntentService.class);
        intent.putExtra(RESULT_RECEIVER_KEY, myResultReceiver);
        mActivity.startService(intent);
    }
    private void runInLoader() {
        LoaderHelper myLoaderHelper = new LoaderHelper(mActivity, myHandler, LoaderHelper.TYPE_ASYNC_TASK_LOADER);
        myLoaderHelper.start();
    }
    private void runInJobScheduler() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            ComponentName serviceComponent = new ComponentName(mActivity, MyJobService.class);
            JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
            builder.setMinimumLatency(1); // wait at least
            builder.setOverrideDeadline(3); // maximum delay
            JobScheduler jobScheduler = mActivity.getSystemService(JobScheduler.class);
            jobScheduler.schedule(builder.build());
        } else
            Log.e(TAG, "SORRY CANT RUN BELOW API 23");
    }
    private void runInGCMNetworkManager() {
        final ResultReceiver myResultReceiver = new ResultReceiver(myHandler) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                //super.onReceiveResult(resultCode, resultData);
                String[] result = resultData.getStringArray(MESSAGE_KEY_TAG);
                postToHandler(result, myHandler, TYPE_GCM_NETWORK_MANAGER);
            }
        };
        GcmNetworkManager manager = GcmNetworkManager.getInstance(mActivity);
        Bundle b = new Bundle();
        b.putParcelable(RESULT_RECEIVER_KEY, myResultReceiver);
        OneoffTask task = new OneoffTask.Builder()
                .setService(MyGCMTaskService.class)
                .setExecutionWindow(0L, 1L)
                .setTag("MyGCMTask")
                .setRequiredNetwork(Task.NETWORK_STATE_UNMETERED)
                .setExtras(b)
                .build();
        manager.schedule(task);
    }
    private void runInCountDownTimer() {
        new CountDownTimer(2, 1) {
            // Somehow this method runs in mainthread, spoiling the basic job.
            // May be useful for delayed execution.
            public void onTick(long millisUntilFinished) {
                String[] result = Utils.timeConsumingActivity();
                ((CallbackInterface) mActivity).gotResult(result, TYPE_COUNT_DOWN_TIMER);
            }
            public void onFinish() {
            }
        }.start();
    }
    private void runInFutureTask() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        FutureTask task1 = new FutureTask(new FutureTaskCallable(myHandler));
        executor.execute(task1);
      /*  try {
            // do we really need to wait? or hook it to the app destroy?
            //executor.awaitTermination(1000, TimeUnit.MILLISECONDS);

            //task1.get()
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
    }
    private void runInThreadPoolExecutor() {
        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String[] result = Utils.timeConsumingActivity();
                postToHandler(result, myHandler, TYPE_THREAD_PPOL_EXECUTOR);
            }
        });
    }
    IMyAidlInterface mIRemoteService;
    private void runInServiceWithAidl() {
        ServiceConnection mConnection = new ServiceConnection() {
            // Called when the connection with the service is established
            public void onServiceConnected(ComponentName className, IBinder service) {
                // Following the example above for an AIDL interface,
                // this gets an instance of the IRemoteInterface, which we can use to call on the service
                mIRemoteService = IMyAidlInterface.Stub.asInterface(service);
                try {
                    // Somehow this callback blocks the main thread.
                    // Is it supposed to run in remote process as we mentioned :remote for process in manifest?
                    String[] result = mIRemoteService.runAsyncTask();
                    ((CallbackInterface) mActivity).gotResult(result, TYPE_SERVICE_WITH_AIDL);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            // Called when the connection with the service disconnects unexpectedly
            public void onServiceDisconnected(ComponentName className) {
                Log.e(TAG, "Service has unexpectedly disconnected");
                mIRemoteService = null;
            }
        };
        Intent intent = new Intent(mActivity, MyAidlService.class);
        intent.setAction(IMyAidlInterface.class.getName());
        mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    private void runInServiceWithConnection() {
    }
    private void runInAlarmManager() {
    }
    private void runInNotificationManager() {
    }
}
