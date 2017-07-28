package com.example.traviswilson.bakingapp.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by traviswilson on 7/27/17.
 */

public class BakingAuthenticatorService extends Service {
    private BakingAuthenticator mAuthenticator;
    @Override
    public void onCreate(){
        mAuthenticator = new BakingAuthenticator(this);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
