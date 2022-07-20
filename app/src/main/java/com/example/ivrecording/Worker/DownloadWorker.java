package com.example.ivrecording.Worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.ivrecording.callrecorder.UpdateReport;

public class DownloadWorker extends Worker {

    private final Context mcontext;
    private volatile boolean complete = false;

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mcontext = context;

    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e("TAG", "doWork: doing web api");
        UpdateReport updateReport = new UpdateReport();
        updateReport.fetchFromDb(mcontext);
        return Result.success();
    }
}