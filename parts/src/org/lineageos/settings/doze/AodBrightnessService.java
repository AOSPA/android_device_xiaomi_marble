/*
 * Copyright (C) 2023 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.doze;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

import org.lineageos.settings.display.DfWrapper;

public class AodBrightnessService extends Service {

    private static final String TAG = "AodBrightnessService";
    private static final boolean DEBUG = true;

    private static final int SENSOR_TYPE_AOD = 33171029; // xiaomi.sensor.aod
    private static final float AOD_SENSOR_EVENT_BRIGHT = 4f;
    private static final float AOD_SENSOR_EVENT_DIM = 5f;
    private static final float AOD_SENSOR_EVENT_DARK = 3f;

    private static final int DOZE_HBM_BRIGHTNESS_THRESHOLD = 20;

    private SensorManager mSensorManager;
    private Sensor mAodSensor;
    private boolean mIsDozing, mIsDozeHbm;

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }

        @Override
        public void onSensorChanged(SensorEvent event) {
            final float value = event.values[0];
            mIsDozeHbm = (value == AOD_SENSOR_EVENT_BRIGHT);
            dlog("onSensorChanged: type=" + event.sensor.getType() + " value=" + value);
            updateDozeBrightness();
        }
    };

    private final BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_ON:
                    dlog("Received ACTION_SCREEN_ON");
                    mIsDozing = false;
                    updateDozeBrightness();
                    mSensorManager.unregisterListener(mSensorListener, mAodSensor);
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    dlog("Received ACTION_SCREEN_OFF");
                    if (Settings.Secure.getInt(getContentResolver(),
                            Settings.Secure.DOZE_ALWAYS_ON, 0) == 0) {
                        dlog("AOD is disabled by setting.");
                        mIsDozing = false;
                        break;
                    }
                    mIsDozing = true;
                    setInitialDozeHbmState();
                    mSensorManager.registerListener(mSensorListener,
                            mAodSensor, SensorManager.SENSOR_DELAY_NORMAL);
                    break;
            }
        }
    };

    public static void startService(Context context) {
         context.startServiceAsUser(new Intent(context, AodBrightnessService.class),
                UserHandle.CURRENT);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dlog("Creating service");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAodSensor = mSensorManager.getDefaultSensor(SENSOR_TYPE_AOD);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dlog("Starting service");
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStateReceiver, screenStateFilter);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        dlog("Destroying service");
        unregisterReceiver(mScreenStateReceiver);
        mSensorManager.unregisterListener(mSensorListener, mAodSensor);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setInitialDozeHbmState() {
        final int brightness = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 0);
        mIsDozeHbm = (brightness > DOZE_HBM_BRIGHTNESS_THRESHOLD);
        dlog("setInitialDozeHbmState: brightness=" + brightness + " mIsDozeHbm=" + mIsDozeHbm);
        updateDozeBrightness();
    }

    private void updateDozeBrightness() {
        dlog("updateDozeBrightness: mIsDozing=" + mIsDozing + " mIsDozeHbm=" + mIsDozeHbm);
        final int mode = !mIsDozing ? 0 : (mIsDozeHbm ? 1 : 2);
        try {
            DfWrapper.setDisplayFeature(
                    new DfWrapper.DfParams(/*DOZE_BRIGHTNESS_STATE*/ 25, mode, 0));
        } catch (Exception e) {
            Log.e(TAG, "updateDozeBrightness failed!", e);
        }
    }

    private static void dlog(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }
}
