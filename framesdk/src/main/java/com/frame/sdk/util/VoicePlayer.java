package com.frame.sdk.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.Visualizer;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;

import com.frame.sdk.app.FrameApplication;

import java.io.File;
import java.io.IOException;

/**
 * 语音播放类
 */
public class VoicePlayer {
    private static MediaPlayer media;
    private static VoicePlayer instance;
    private IPlayCallback icallback;
    private String cacheName = "";
    public static int MEDIA_IDLE = 0X10;// 空闲状态
    public static int MEDIA_LOADING = 0X10 + 1;// 正在下载语音
    public static int MEDIA_PLAY = 0X10 + 2;// 正在播放
    public static int MEDIA_PAUSE = 0X10 + 3; // 暂停
    private int state;
    private AudioManager audioManager;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isFirstPlay;// 第一次播放不提示模式
    private PowerManager powerManager;
    private WakeLock wakeLock;
    private Visualizer visualizer;

    private VoicePlayer() {
        if (media == null) {
            media = new MediaPlayer();
            media.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stop();
                }
            });
            media.setScreenOnWhilePlaying(true);
        }
        state = MEDIA_IDLE;

        audioManager = (AudioManager) FrameApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
        mSensorManager = (SensorManager) FrameApplication.getInstance().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        powerManager = (PowerManager) FrameApplication.getInstance().getSystemService(Context.POWER_SERVICE);
        visualizer = new Visualizer(media.getAudioSessionId());
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
    }

    public void setCallback(IPlayCallback callback) {
        this.icallback = callback;
    }

    public static VoicePlayer getInstance() {
        if (instance == null)
            instance = new VoicePlayer();
        return instance;
    }


    public void palyVoice(String voicePath) {
        LogUtils.i("palyVoice voicePath=" + voicePath);
        state = MEDIA_IDLE;
        if (media != null && media.isPlaying()) {
            stop();
        }
        File cachefile = new File(voicePath);
        if (cachefile.exists() && cachefile.length() > 0) {// 本地播放
            cacheName = cachefile.getAbsolutePath();
            toPlayState();
        }
    }


    void toPlayState() {
        try {
            media.reset();
            media.setDataSource(cacheName);
            media.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        play();
    }

    public interface IPlayCallback {
        /**
         * 开始下载
         */
        public void loading();

        /**
         * 播放中
         */
        public void playing();

        /**
         * 停止了播放
         */
        public void stop();

        /**
         * 暂停
         */
        public void pause();
    }

    public void stop() {
        LogUtils.i("stop ");
//        closeScreen();
        mSensorManager.unregisterListener(sensorEventListener);
        state = MEDIA_IDLE;
        if (icallback != null)
            icallback.stop();
        if (media != null && media.isPlaying()) {
            media.stop();
//            closeScreen();
        }
    }

    public void pause() {
        LogUtils.i("pause ");
        mSensorManager.unregisterListener(sensorEventListener);
        state = MEDIA_PAUSE;
        if (icallback != null)
            icallback.pause();
        if (media != null) {
            if (media.isPlaying()) {
                media.pause();
            }
        }
    }

    /**
     * 继续播放
     */
    public void play() {
        LogUtils.i("continue play");
        state = MEDIA_PLAY;
        if (icallback != null)
            icallback.playing();
        if (media != null) {
            if (!media.isPlaying()) {
                mSensorManager.registerListener(sensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
                isFirstPlay = true;
                media.start();
                visualizer.setEnabled(true);
//                keepScreen();
            }
        }
    }

    public boolean isPlaying() {
        return media.isPlaying();
    }

    public int getState() {
        return state;
    }

    // public int getCurrentPosition() {
    // return media.getCurrentPosition();
    // }
    //
    // public int getDuration() {
    // return media.getDuration();
    // }

    SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (isFirstPlay) {
                isFirstPlay = false;
                return;
            }
            float range = event.values[0];
            if (range == mSensor.getMaximumRange()) {
//                ToastUtil.showToast(FrameApplication.getInstance(), "当前是扬声器模式");
                audioManager.setMode(AudioManager.MODE_NORMAL);
                // replay();
            } else {
//                ToastUtil.showToast(FrameApplication.getInstance(), "当前是听筒模式");
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                // replay();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    /**
     * 播放模式切换时重播
     */
    private void replay() {
        if (TextUtils.isEmpty(cacheName))
            return;
        media.reset();
        try {
            media.setDataSource(cacheName);
            media.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        media.start();
    }

    /**
     * 播放完毕允许关闭屏幕
     */
    private void closeScreen() {
        if (wakeLock != null)
            wakeLock.release();
        wakeLock = null;
    }

    /**
     * 播放中屏幕保持打开
     */
    private void keepScreen() {
        if (wakeLock != null)
            wakeLock.release();
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Simple_" + System.currentTimeMillis());
        wakeLock.acquire();
    }

    public Visualizer getVisualizer(){
        return visualizer;
    }
}
