package edu.ncsu.soc.rms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RingerManagerService extends Service {
  private static final String TAG = "RingerManagerService";

  @Override
  public IBinder onBind(Intent arg0) {
    return null;
  }

  @Override
  public void onCreate() {
    Log.i(TAG, "Service created in Ringer manager");
    
    
    
    
  }

}
