/*
 * Copyright (C) 2018,2020 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.dolby;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioDeviceAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.util.Log;

import org.lineageos.settings.R;
import org.lineageos.settings.dolby.DolbyConstants.DsParam;

import java.util.Arrays;
import java.util.List;

public final class DolbyUtils {

    private static final String TAG = "DolbyUtils";
    private static final int EFFECT_PRIORITY = 100;
    private static final int VOLUME_LEVELER_AMOUNT = 2;

    private static final AudioAttributes ATTRIBUTES_MEDIA = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build();

    private static DolbyUtils mInstance;
    private DolbyAtmos mDolbyAtmos;
    private Context mContext;

    private DolbyUtils(Context context) {
        mContext = context;
        mDolbyAtmos = new DolbyAtmos(EFFECT_PRIORITY, 0);
        mDolbyAtmos.setEnabled(mDolbyAtmos.getDsOn());
    }

    public static synchronized DolbyUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DolbyUtils(context);
        }
        return mInstance;
    }

    public void onBootCompleted() {
        Log.i(TAG, "Boot completed");

        // Restore speaker virtualizer, because for some reason it isn't
        // enabled automatically at boot.
        final AudioDeviceAttributes device = mContext.getSystemService(AudioManager.class)
                .getDevicesForAttributes(ATTRIBUTES_MEDIA).get(0);
        final boolean isOnSpeaker = (device.getType() == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER);
        final boolean spkVirtEnabled = getSpeakerVirtualizerEnabled();
        dlog("isOnSpeaker=" + isOnSpeaker + " spkVirtEnabled=" + spkVirtEnabled);
        if (isOnSpeaker && spkVirtEnabled) {
            setSpeakerVirtualizerEnabled(false);
            setSpeakerVirtualizerEnabled(true);
            Log.i(TAG, "re-enabled speaker virtualizer");
        }
    }

    private void checkEffect() {
        if (!mDolbyAtmos.hasControl()) {
            Log.w(TAG, "lost control, recreating effect");
            mDolbyAtmos.release();
            mDolbyAtmos = new DolbyAtmos(EFFECT_PRIORITY, 0);
        }
    }

    public void setDsOn(boolean on) {
        checkEffect();
        dlog("setDsOn: " + on);
        mDolbyAtmos.setDsOn(on);
    }

    public boolean getDsOn() {
        boolean on = mDolbyAtmos.getDsOn();
        dlog("getDsOn: " + on);
        return on;
    }

    public void setProfile(int index) {
        checkEffect();
        dlog("setProfile: " + index);
        mDolbyAtmos.setProfile(index);
    }

    public int getProfile() {
        int profile = mDolbyAtmos.getProfile();
        dlog("getProfile: " + profile);
        return profile;
    }

    public String getProfileName() {
        String profile = Integer.toString(mDolbyAtmos.getProfile());
        List<String> profiles = Arrays.asList(mContext.getResources().getStringArray(
                R.array.dolby_profile_values));
        int profileIndex = profiles.indexOf(profile);
        dlog("getProfileName: profile=" + profile + " index=" + profileIndex);
        return profileIndex == -1 ? null : mContext.getResources().getStringArray(
                R.array.dolby_profile_entries)[profileIndex];
    }

    public void resetProfileSpecificSettings() {
        checkEffect();
        mDolbyAtmos.resetProfileSpecificSettings();
    }

    public void setPreset(String preset) {
        checkEffect();
        int[] gains = Arrays.stream(preset.split(",")).mapToInt(Integer::parseInt).toArray();
        dlog("setPreset: " + Arrays.toString(gains));
        mDolbyAtmos.setDapParameter(DsParam.GEQ_BAND_GAINS, gains);
    }

    public String getPreset() {
        int[] gains = mDolbyAtmos.getDapParameter(DsParam.GEQ_BAND_GAINS);
        dlog("getPreset: " + Arrays.toString(gains));
        String[] preset = Arrays.stream(gains).mapToObj(String::valueOf).toArray(String[]::new);
        return String.join(",", preset);
    }

    public void setHeadphoneVirtualizerEnabled(boolean enable) {
        checkEffect();
        dlog("setHeadphoneVirtualizerEnabled: " + enable);
        mDolbyAtmos.setDapParameterBool(DsParam.HEADPHONE_VIRTUALIZER, enable);
    }

    public boolean getHeadphoneVirtualizerEnabled() {
        boolean enabled = mDolbyAtmos.getDapParameterBool(DsParam.HEADPHONE_VIRTUALIZER);
        dlog("getHeadphoneVirtualizerEnabled: " + enabled);
        return enabled;
    }

    public void setSpeakerVirtualizerEnabled(boolean enable) {
        checkEffect();
        dlog("setSpeakerVirtualizerEnabled: " + enable);
        mDolbyAtmos.setDapParameterBool(DsParam.SPEAKER_VIRTUALIZER, enable);
    }

    public boolean getSpeakerVirtualizerEnabled() {
        boolean enabled = mDolbyAtmos.getDapParameterBool(DsParam.SPEAKER_VIRTUALIZER);
        dlog("getSpeakerVirtualizerEnabled: " + enabled);
        return enabled;
    }

    public void setStereoWideningAmount(int amount) {
        checkEffect();
        dlog("setStereoWideningAmount: " + amount);
        mDolbyAtmos.setDapParameterInt(DsParam.STEREO_WIDENING_AMOUNT, amount);
    }

    public int getStereoWideningAmount() {
        int amount = mDolbyAtmos.getDapParameterInt(DsParam.STEREO_WIDENING_AMOUNT);
        dlog("getStereoWideningAmount: " + amount);
        return amount;
    }

    public void setDialogueEnhancerAmount(int amount) {
        checkEffect();
        dlog("setDialogueEnhancerAmount: " + amount);
        mDolbyAtmos.setDapParameterBool(DsParam.DIALOGUE_ENHANCER_ENABLE, amount > 0);
        mDolbyAtmos.setDapParameterInt(DsParam.DIALOGUE_ENHANCER_AMOUNT, amount);
    }

    public int getDialogueEnhancerAmount() {
        boolean enabled = mDolbyAtmos.getDapParameterBool(
                DsParam.DIALOGUE_ENHANCER_ENABLE);
        int amount = enabled ? mDolbyAtmos.getDapParameterInt(
                DsParam.DIALOGUE_ENHANCER_AMOUNT) : 0;
        dlog("getDialogueEnhancerAmount: " + enabled + " amount=" + amount);
        return amount;
    }

    public void setBassEnhancerEnabled(boolean enable) {
        checkEffect();
        dlog("setBassEnhancerEnabled: " + enable);
        mDolbyAtmos.setDapParameterBool(DsParam.BASS_ENHANCER_ENABLE, enable);
    }

    public boolean getBassEnhancerEnabled() {
        boolean enabled = mDolbyAtmos.getDapParameterBool(DsParam.BASS_ENHANCER_ENABLE);
        dlog("getBassEnhancerEnabled: " + enabled);
        return enabled;
    }

    public void setVolumeLevelerEnabled(boolean enable) {
        checkEffect();
        dlog("setVolumeLevelerEnabled: " + enable);
        mDolbyAtmos.setDapParameterBool(DsParam.VOLUME_LEVELER_ENABLE, enable);
        mDolbyAtmos.setDapParameterInt(DsParam.VOLUME_LEVELER_AMOUNT,
                enable ? VOLUME_LEVELER_AMOUNT : 0);
    }

    public boolean getVolumeLevelerEnabled() {
        boolean enabled = mDolbyAtmos.getDapParameterBool(DsParam.VOLUME_LEVELER_ENABLE);
        int amount = mDolbyAtmos.getDapParameterInt(DsParam.VOLUME_LEVELER_AMOUNT);
        dlog("getVolumeLevelerEnabled: " + enabled + " amount=" + amount);
        return enabled && (amount == VOLUME_LEVELER_AMOUNT);
    }

    private static void dlog(String msg) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, msg);
        }
    }
}
