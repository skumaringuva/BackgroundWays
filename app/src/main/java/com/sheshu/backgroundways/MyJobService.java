package com.sheshu.backgroundways;

import android.app.job.JobParameters;
import android.app.job.JobService;

/**
 * Created by Sheshu on 6/4/17.
 */
public class MyJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        return true;
    }
    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
