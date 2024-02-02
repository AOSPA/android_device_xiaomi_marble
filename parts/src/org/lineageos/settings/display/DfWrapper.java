/*
 * Copyright (C) 2023 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.display;

import android.os.IHwBinder.DeathRecipient;
import android.util.Log;

import vendor.xiaomi.hardware.displayfeature.V1_0.IDisplayFeature;

public class DfWrapper {

    private static final String TAG = "DisplayFeatureWrapper";

    private static IDisplayFeature mDisplayFeature;

    private static DeathRecipient mDeathRecipient = (cookie) -> {
        dlog("serviceDied");
        mDisplayFeature = null;
    };

    public static IDisplayFeature getDisplayFeature() {
        if (mDisplayFeature == null) {
            dlog("getDisplayFeature: mDisplayFeature=null");
            try {
                mDisplayFeature = IDisplayFeature.getService();
                mDisplayFeature.asBinder().linkToDeath(mDeathRecipient, 0);
            } catch (Exception e) {
                Log.e(TAG, "getDisplayFeature failed!", e);
            }
        }
        return mDisplayFeature;
    }

    public static void setDisplayFeature(DfParams params) {
        final IDisplayFeature displayFeature = getDisplayFeature();
        if (displayFeature == null) {
            Log.e(TAG, "setDisplayFeatureParams: displayFeature is null!");
            return;
        }
        dlog("setDisplayFeatureParams: " + params);
        try {
            displayFeature.setFeature(0, params.mode, params.value, params.cookie);
        } catch (Exception e) {
            Log.e(TAG, "setDisplayFeatureParams failed!", e);
        }
    }

    private static void dlog(String msg) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, msg);
        }
    }

    public static class DfParams {
        /* displayfeature parameters */
        final int mode, value, cookie;

        public DfParams(int mode, int value, int cookie) {
            this.mode = mode;
            this.value = value;
            this.cookie = cookie;
        }

        public String toString() {
            return "DisplayFeatureParams(" + mode + ", " + value + ", " + cookie + ")";
        }
    }
}
