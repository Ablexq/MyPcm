package com.xq.pcmsample.util;

import android.media.MediaRecorder;

import java.io.IOException;

/**
 * 录制管理类
 * MediaRecorder类是Android sdk提供的一个专门用于音视频录制，一般利用手机麦克风采集音频，摄像头采集图片信息。
 * <p>
 * [Android音频开发之MediaRecorder/MediaPlayer](http://www.cnblogs.com/whoislcj/p/5477678.html)
 */

public class MediaRecorderManager {

    private MediaRecorder mRecorder;
    private static MediaRecorderManager mInstance;

    /**
     * 获取单例引用
     */
    public static MediaRecorderManager getInstance() {
        if (mInstance == null) {
            synchronized (MediaRecorderManager.class) {
                if (mInstance == null) {
                    mInstance = new MediaRecorderManager();
                }
            }
        }
        return mInstance;
    }


    /**
     * 开始录制
     */
    public void start(String filePath) {
        try {
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置音频采集方式
                //设置保存文件格式为MP4
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);//设置音频输出格式
                //设置声音数据编码格式,音频通用格式是AAC
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//设置音频编码方式
                //设置采样频率,44100是所有安卓设备都支持的频率,频率越高，音质越好，当然文件越大
                mRecorder.setAudioSamplingRate(44100);
                //设置编码频率
                mRecorder.setAudioEncodingBitRate(96000);
            }
            //设置录音保存的文件
            mRecorder.setOutputFile(filePath);//设置录音文件输出路径
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放录音资源
     */
    public void stop() {
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRecorder = null;
    }

}