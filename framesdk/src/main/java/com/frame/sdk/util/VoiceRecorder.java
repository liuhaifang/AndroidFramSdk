package com.frame.sdk.util;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;

import com.frame.sdk.app.FrameApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 录音工具类
 */
public class VoiceRecorder {

    private Context ctx;
    private IRecordCallback icallback;
    public static String filePath;

    private static int sampleRate = 8000;
    private static int chanel = AudioFormat.CHANNEL_IN_MONO;
    private static int encodeBytes = AudioFormat.ENCODING_PCM_16BIT;
    static final int bufferSize = AudioRecord.getMinBufferSize(sampleRate,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    /**
     * 控制是否正常结束，非正常结束，不需要播放结束声音
     */
//    private boolean endsuc = true;

    int audioState = 0;// 空闲状态
    private AudioRecord audioRecord;
    // private Thread thRecord; //录音线程
    public static Object lock = new Object();
    // 结束繁忙有2种方式，一种用户点击结束2秒之后，应用退出或进入后台
    public boolean isbusy = false;// 控制线程是否还是忙（很重要），用于控制用户频繁的重复点击开启线程

    private PowerManager powerManager;
    private WakeLock wakeLock;

    public VoiceRecorder(Context context) {
        this.ctx = context;
        powerManager = (PowerManager) FrameApplication.getInstance().getSystemService(Context.POWER_SERVICE);
    }

    public void setCallback(IRecordCallback callback) {
        icallback = callback;
    }

    /**
     * 按钮点击，调用此开始录制
     */
    public boolean startRecord(String filePath) {
        if (isbusy)
            return false;
        this.filePath = filePath;
        if (audioState != 0) {
            LogUtils.i("voicetool 非0 状态开始了录制");
            return false;
        }
        isbusy = true;
        audioState = 1;
        new Thread(recordRunnable).start(); // 每次录音都是新线程哦
        keepScreen();
        icallback.beginRecord();
        return true;
    }

    /**
     * 停止播放,是否正常停止
     */
    public void endRecord() {
        closeScreen();
//        endsuc = allright;
        audioState = 0;
        // spPool.play(2, 1, 1, 0, 0, 1);
//        if (allright) {
//
//        }
        // if (audioState != 1 || !allright) // 非正常结束，不需要播放结束音
        // return;
        //
        icallback.endRecord(filePath);
        isbusy = false;
    }

    private Runnable recordRunnable = new Runnable() {

        @Override
        public void run() {
            audioStart();
        }
    };

    // /录制语音开始
    void audioStart() {
        // 多次录制也应该重复使用资源
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, chanel, encodeBytes, bufferSize);
        // Log.i(MacroDef.LOG_TAG,
        // "inistate state is "+String.valueOf(audioRecord.getState()));

        byte[] buffer = new byte[bufferSize];
        audioRecord.startRecording();

        // String path1 = HttpTool.GetFolder() + RECORD_NAME;
        // String path2 = HttpTool.GetFolder() + PLAY_NAME;
        try {
            FileOutputStream fos = null;
            if (!TextUtils.isEmpty(filePath)) {
                File file = new File(filePath);
                if (file.exists())
                    file.delete();
                file.createNewFile();
                fos = new FileOutputStream(file);
            }


            while (audioState == 1) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);

                if (bufferReadResult == AudioRecord.ERROR_INVALID_OPERATION) {
                    LogUtils.i("read error");
                } else {
                    int v = 0;
                    // 将 buffer 内容取出，进行平方和运算
                    for (int i = 0; i < buffer.length; i++) {
                        v += buffer[i] * buffer[i];
                    }
                    // 平方和除以数据总长度，得到音量大小。
//                    double mean = v / (double) bufferReadResult;
//                    int volume =  (Math.abs((int)(v /(float)bufferReadResult)/10000) >> 1);
                    int volume = (int) (v /(float)bufferReadResult);
                    double fenbei = 10 * Math.log10(volume);
                    if (icallback != null) {
                        icallback.yinLiang(volume,fenbei);
                    }
//                    LogUtils.d("buffer=="+DataTypeConverUtil.printBytes(buffer));
//                    LogUtils.d("v="+v+",bufferReadResult="+bufferReadResult+",volume="+volume+",fenbei="+fenbei);
                }
                if (fos != null)
                    fos.write(buffer);
            }
            if (fos != null) fos.close();
            audioRecord.stop();
            audioRecord.release();

            // 文件转换完了才跳转
            audioState = 0;
            // ToNext(); //暂不跳转，测试播放
            // trackPlay();

            synchronized (lock) {
                lock.notifyAll();
            }

        } catch (IOException e) {
            // throw new IllegalStateException("Failed to create " +
            // file.toString());
            // UtilTools.showT(RecordingActivity.this, "录音失败,没有权限");
            e.printStackTrace();
        }
        isbusy = false;

    }

    public interface IRecordCallback {
        public void endRecord(String filePath);

        public void beginRecord();

        /**
         * 子线程调用
         */
        public void yinLiang(int volume,double fenbei);

    }

    /**
     * 录音完毕允许关闭屏幕
     */
    private void closeScreen() {
        if (wakeLock != null)
            wakeLock.release();
        wakeLock = null;
    }

    /**
     * 录音中屏幕保持打开
     */
    private void keepScreen() {
        if (wakeLock != null)
            wakeLock.release();
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Recorder_" + System.currentTimeMillis());
        wakeLock.acquire();
    }
}
