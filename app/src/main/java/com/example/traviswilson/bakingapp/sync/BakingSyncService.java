package com.example.traviswilson.bakingapp.sync;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by traviswilson on 7/27/17.
 */

public class BakingSyncService extends Service{
    private static final Object lock = new Object();
    private BakingSyncAdapter mAdapter;
    @Override
    public void onCreate(){
        synchronized (lock){
            if (mAdapter == null){
                mAdapter = new BakingSyncAdapter(this, true);
            }
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAdapter.getSyncAdapterBinder();
    }
}
