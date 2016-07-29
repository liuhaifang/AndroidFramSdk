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
import android.view.SurfaceHolder;

import com.frame.sdk.app.FrameApplication;

import java.io.File;
import java.io.IOException;

/**
 * 视频播放类
 */
public class VideoPlayer {
    private static MediaPlayer media;
    private static VideoPlayer instance;
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
    private static Visualizer visualizer;

    public static VideoPlayer getInstance(SurfaceHolder surfaceHolder){
        if(instance==null)
            instance=new VideoPlayer(surfaceHolder);
        return instance;
    }
    private  VideoPlayer(SurfaceHolder surfaceHolder) {
        if (media == null) {
            media = new MediaPlayer();
            media.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stop();
                }
            });
            media.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if (icallback != null)
                        icallback.error();
                    return false;
                }
            });
            media.setScreenOnWhilePlaying(true);
            media.setDisplay(surfaceHolder);
        }
        state = MEDIA_IDLE;

        audioManager = (AudioManager) FrameApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
        mSensorManager = (SensorManager) FrameApplication.getInstance().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        powerManager = (PowerManager) FrameApplication.getInstance().getSystemService(Context.POWER_SERVICE);
            visualizer = new Visualizer(media.getAudioSessionId());
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
            visualizer.setDataCaptureListener(
                    new Visualizer.OnDataCaptureListener() {
                        @Override
                        public void onWaveFormDataCapture(Visualizer visualizer,
                                                          byte[] waveform, int samplingRate) {
                            // 这里添加获得数据的处理 byte[] 数组 更新出去，并画图。这里可以把这个
                            // 数组传到RunOnMusic里去
                            // visualView.updateVisualizer(waveform);
                            //震动
                            LogUtils.d("onWaveFormDataCapture  waveform=" + DataTypeConverUtil.printBytes(waveform));
                            if (icallback != null)
                                icallback.onWaveFormDataCapture(waveform);
                        }

                        @Override
                        public void onFftDataCapture(Visualizer visualizer,
                                                     byte[] fft, int samplingRate) {
                            LogUtils.d("onFftDataCapture  waveform=" + DataTypeConverUtil.printBytes(fft));
                            if (icallback != null)
                                icallback.onFftDataCapture(fft);
//                        byte[] model = new byte[fft.length / 2 + 1];
//                        model[0] = (byte) Math.abs(fft[1]);
//                        int j = 1;
//
//                        for (int i = 2; i < 18; ) {
//                            model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
//                            i += 2;
//                            j++;
//                        }
//                        visualView.updateVisualizer(model);
                        }
                    }, Visualizer.getMaxCaptureRate() / 2, true, true);
        }

    public void setCallback(IPlayCallback callback) {
        this.icallback = callback;
    }

//    public static VideoPlayer getInstance(SurfaceHolder surfaceHolder) {
//        if (instance == null)
//            instance = new VideoPlayer(surfaceHolder);
//        return instance;
//    }


    public void palyVideo(String videoPath) {
        LogUtils.i("palyVideo videoPath=" + videoPath);
        state = MEDIA_IDLE;
        if (media != null && media.isPlaying()) {
            stop();
        }
        File cachefile = new File(videoPath);
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
         * 播放错误
         */
        public void error();

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

        /**
         *
         */
        public void onFftDataCapture(byte[] data);

        public void onWaveFormDataCapture(byte[] data);
    }

    public void stop() {
        LogUtils.i("stop ");
        closeScreen();
        mSensorManager.unregisterListener(sensorEventListener);
        state = MEDIA_IDLE;
        if (icallback != null)
            icallback.stop();
        if (media != null && media.isPlaying()) {
            media.stop();
            closeScreen();
        }
        visualizer.setEnabled(false);
    }

    public void release() {
//        if (media != null) media.release();
        if(visualizer!=null) visualizer.release();
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
                keepScreen();
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

}
