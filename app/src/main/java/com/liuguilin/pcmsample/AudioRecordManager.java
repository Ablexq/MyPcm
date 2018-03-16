package com.liuguilin.pcmsample;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;


/**
 * [Android音频开发之AudioRecord录音实现](https://www.cnblogs.com/whoislcj/p/5477216.html)
 * <p>
 * 其实在Android中录音可以用MediaRecord录音，操作比较简单。
 * 但是不能对音频进行处理。考虑到项目中做的是实时语音只能选择AudioRecord进行录音。
 * <p>
 * [Android音频处理——通过AudioRecord去保存PCM文件进行录制，播放，停止，删除功能]
 * (http://blog.csdn.net/qq_26787115/article/details/53078951)
 */

@SuppressWarnings("ResultOfMethodCallIgnored")
public class AudioRecordManager {
    private static final int FREQUENCY = 16000;                  //16K采集率
    private AudioRecord mRecorder;
    private DataOutputStream dos;
    private Thread recordThread;
    private boolean isStart = false;
    private static AudioRecordManager mInstance;
    private int bufferSize;

    private AudioRecordManager() {
        bufferSize = AudioRecord.getMinBufferSize(FREQUENCY,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        //一、实例化一个AudioRecord类
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                FREQUENCY,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize * 2);
    }

    /**
     * 获取单例引用
     */
    public static AudioRecordManager getInstance() {
        if (mInstance == null) {
            synchronized (AudioRecordManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioRecordManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 销毁线程方法
     */
    private void destroyThread() {
        try {
            isStart = false;
            if (null != recordThread && Thread.State.RUNNABLE == recordThread.getState()) {
                try {
                    Thread.sleep(500);
                    recordThread.interrupt();
                } catch (Exception e) {
                    recordThread = null;
                }
            }
            recordThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            recordThread = null;
        }
    }

    /**
     * 启动录音线程
     */
    private void startThread() {
        destroyThread();
        isStart = true;
        if (recordThread == null) {
            recordThread = new Thread(recordRunnable);
            recordThread.start();
        }
    }

    /**
     * 录音线程
     */
    private Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                int bytesRecord;
                //int bufferSize = 320;
                byte[] tempBuffer = new byte[bufferSize];
                if (mRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
                    stopRecord();
                    return;
                }
                //四、现在就可以开始录制了
                mRecorder.startRecording();
                //writeToFileHead();
                while (isStart) {
                    if (null != mRecorder) {
                        bytesRecord = mRecorder.read(tempBuffer, 0, bufferSize);
                        if (bytesRecord == AudioRecord.ERROR_INVALID_OPERATION
                                || bytesRecord == AudioRecord.ERROR_BAD_VALUE) {
                            continue;
                        }
                        if (bytesRecord != 0 && bytesRecord != -1) {
                            //在此可以对录制音频的数据进行二次处理 比如变声，压缩，降噪，增益等操作
                            //我们这里直接将pcm音频原数据写入文件 这里可以直接发送至服务器
                            //  对方采用AudioTrack进行播放原数据
                            dos.write(tempBuffer, 0, bytesRecord);
                        } else {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    /**
     * 保存文件
     *
     * @param path File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
     *             + "/reverseme.pcm");
     */
    private void setPath(String path) throws Exception {
        //二、创建一个文件，用于保存录制的内容
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        //三、打开一个输出流，指向创建的文件
        dos = new DataOutputStream(new FileOutputStream(file, true));
    }

    /**
     * 启动录音
     */
    public void startRecord(String path) {
        try {
            setPath(path);
            startThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        try {
            destroyThread();
            if (mRecorder != null) {
                if (mRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
                    mRecorder.stop();
                }
                if (mRecorder != null) {
                    mRecorder.release();
                }
            }
            if (dos != null) {
                dos.flush();
                dos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}