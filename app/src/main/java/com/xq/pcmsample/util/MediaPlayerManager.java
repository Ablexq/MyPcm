package com.xq.pcmsample.util;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;

/**
 * 播放管理类：
 *
 * [Android音频开发之MediaRecorder/MediaPlayer](http://www.cnblogs.com/whoislcj/p/5477678.html)
 *
 */

public class MediaPlayerManager {

    private static MediaPlayerManager mInstance;
    private MediaPlayer player;

    /**
     * 获取单例引用
     */
    public static MediaPlayerManager getInstance() {
        if (mInstance == null) {
            synchronized (MediaPlayerManager.class) {
                if (mInstance == null) {
                    mInstance = new MediaPlayerManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 播放录音
     */
    public boolean play(String url) {
        return play(url, new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer arg0) {
                stop();
            }
        }, new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                stop();
                return false;
            }
        });
    }

    /**
     * 播放录音
     */
    private boolean play(String url, MediaPlayer.OnCompletionListener completionListener, MediaPlayer.OnErrorListener errorListener) {
        stop();
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        try {
            if (player == null) {
                player = new MediaPlayer();
                player.setDataSource(url);
                player.setVolume(0.7f, 0.7f);
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.prepare();
                player.start();
                player.setOnCompletionListener(completionListener);
                player.setOnErrorListener(errorListener);
                return true;
            }
        } catch (Exception e) {
            stop();
        }
        return false;
    }

    /**
     * 释放资源
     */
    public void stop() {
        if (player != null) {
            try {
                player.stop();
                player.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                player = null;
            }
        }
    }

}