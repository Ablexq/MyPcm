package com.liuguilin.pcmsample;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ScrollView mScrollView;
    private TextView tv_audio_succeess;
    //pcm文件
    private File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myaudio.pcm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    //初始化View
    private void initView() {
        mScrollView = (ScrollView) findViewById(R.id.mScrollView);
        tv_audio_succeess = (TextView) findViewById(R.id.tv_audio_succeess);
        printLog("初始化成功");
        Button startRecord = findViewById(R.id.startRecord);
        Button stopRecord = findViewById(R.id.stopRecord);
        Button startTrack = findViewById(R.id.startTrack);
        Button stopTrack = findViewById(R.id.stopTrack);
        Button deleteAudio = findViewById(R.id.deleteAudio);
        startRecord.setOnClickListener(this);
        stopRecord.setOnClickListener(this);
        startTrack.setOnClickListener(this);
        stopTrack.setOnClickListener(this);
        deleteAudio.setOnClickListener(this);
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startRecord:
                AudioRecordManager.getInstance().startRecord(file.getAbsolutePath());
                printLog("开始录音");
                break;

            case R.id.stopRecord:
                AudioRecordManager.getInstance().stopRecord();
                printLog("停止录音");
                break;

            case R.id.startTrack:
                AudioTrackManager.getInstance().startPlay(file.getAbsolutePath());
                printLog("播放录音");
                break;

            case R.id.stopTrack:
                AudioTrackManager.getInstance().stopPlay();
                printLog("停止播放");
                break;

            case R.id.deleteAudio:
                deleFile();
                printLog("删除本地录音");
                break;
        }
    }

    //打印log
    private void printLog(final String resultString) {
        tv_audio_succeess.post(new Runnable() {
            @Override
            public void run() {
                tv_audio_succeess.append(resultString + "\n");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    //删除文件
    private void deleFile() {

        if (file == null) {
            return;
        }
        file.delete();
        printLog("文件删除成功");
    }

}
