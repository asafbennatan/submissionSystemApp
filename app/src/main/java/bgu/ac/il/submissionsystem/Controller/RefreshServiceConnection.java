package bgu.ac.il.submissionsystem.Controller;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Asaf on 07/01/2016.
 */
public class RefreshServiceConnection implements ServiceConnection{
    private AtomicBoolean mBound;

    public RefreshServiceConnection() {
        mBound=new AtomicBoolean(false);
    }

    @Override
    public void onServiceConnected(ComponentName className,
                                   IBinder service) {

        mBound.set(true);
    }



    @Override
    public void onServiceDisconnected(ComponentName arg0) {
        mBound.set(false);
    }

    public boolean isBound() {
        return mBound.get();
    }
}
