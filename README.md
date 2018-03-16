
参考：

[Android音频处理——通过AudioRecord去保存PCM文件进行录制，播放，停止，删除功能](http://blog.csdn.net/qq_26787115/article/details/53078951)

[Android音频开发之AudioTrack实时播放](http://www.cnblogs.com/whoislcj/p/5477229.html)

AudioRecord和AudioTrack类是Android获取和播放音频流的重要类，放置在android.media包中。与该包中的MediaRecorder和MediaPlayer类不同，AudioRecord和AudioTrack类在获取和播放音频数据流时无需通过文件保存和文件读取，可以动态地直接获取和播放音频流，在实时处理音频数据流时非常有用。

当然，如果用户只想录音后写入文件或从文件中取得音频流进行播放，那么直接使用MediaRecorder和MediaPlayer类是首选方案，因为这两个类使用非常方便，而且成功率很高。


1.AudioRecord与MediaRecorder一样用来录制音频的

2.AudioRecord可以对录制的数据进行实时的处理，
比如降噪，除杂，或者将音频进行实时传输，比如IP电话，对讲功能等操作。

3.AudioRecord比MediaRecorder更接近底层，录制的音频是PCM格式的，
只能用AudioTraker进行播放，或者将PCM数据转换成amr,wav等格式播放。


AudioRecord，以pulling方式通过read()方法对缓冲区的数据进行轮询，来实时的获取缓冲区的数据。
AudioTrack，专门用来播放PCM数据.

其实在Android中录音可以用MediaRecord录音，操作比较简单。
但是不能对音频进行处理。考虑到项目中做的是实时语音只能选择AudioRecord进行录音。

方法介绍
---
```
public AudioRecord(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat,
            int bufferSizeInBytes)

public AudioTrack(int streamType, int sampleRateInHz, int channelConfig, int audioFormat,
            int bufferSizeInBytes, int mode)

static public int getMinBufferSize(int sampleRateInHz, int channelConfig, int audioFormat)
```
参数介绍
---
audioSource

该参数指的是音频采集的输入源，可选的值以常量的形式定义在 MediaRecorder.AudioSource 类中，
常用的值包括：
DEFAULT（默认），
VOICE_RECOGNITION（用于语音识别，等同于DEFAULT），
MIC（由手机麦克风输入），
VOICE_COMMUNICATION（用于VoIP应用）等等。

streamType

音频管理策略，该参数的可选的值以常量的形式定义在 AudioManager 类中，主要包括：
STREAM_VOCIE_CALL：电话声音
STREAM_SYSTEM：系统声音
STREAM_RING：铃声
STREAM_MUSCI：音乐声
STREAM_ALARM：警告声
STREAM_NOTIFICATION：通知声

sampleRateInHz

采样率，音频的采样频率，每秒钟能够采样的次数，采样率越高，音质越高。
给出的实例是44100、22050、11025但不限于这几个参数。
例如要采集低质量的音频就可以使用4000、8000等低采样率。
注意，目前44100Hz是唯一可以保证兼容所有Android手机的采样率。

channelConfig

通道数的配置，可选的值以常量的形式定义在 AudioFormat 类中，常用的是
CHANNEL_IN_MONO（单通道）
CHANNEL_IN_STEREO（双通道）

audioFormat

编码制式和采样大小：采集来的数据当然使用PCM编码(脉冲代码调制编码，即PCM编码。
PCM通过抽样、量化、编码三个步骤将连续变化的模拟信号转换为数字编码。)
当然采样大小越大，那么信息量越多，音质也越高，
现在主流的采样大小都是16bit，在低质量的语音传输的时候8bit足够了。
可选的值也是以常量的形式定义在 AudioFormat 类中，常用的是
ENCODING_PCM_16BIT（16bit）
ENCODING_PCM_8BIT（8bit），
注意，前者是可以保证兼容所有Android手机的。

bufferSizeInBytes

配置的是 AudioTrack 内部的音频缓冲区的大小，该缓冲区的值不能低于一帧“音频帧”（Frame）的大小，
一帧音频帧的大小计算如下：
int size = 采样率 x 位宽 x 采样时间 x 通道数
采集数据需要的缓冲区的大小，如果不知道最小需要的大小可以在getMinBufferSize()查看。
AudioTrack 类提供了一个帮助你确定这个 bufferSizeInBytes 的函数，原型如下：
int getMinBufferSize(int sampleRateInHz, int channelConfig, int audioFormat);

mode

AudioTrack 提供了两种播放模式，一种是 static 方式，一种是 streaming 方式，
前者需要一次性将所有的数据都写入播放缓冲区，简单高效，通常用于播放铃声、系统提醒的音频片段; 后者则是按照一定的时间间隔不间断地写入音频数据，理论上它可用于任何音频播放的场景。
在 AudioTrack 类中，一个是 MODE_STATIC，另一个是 MODE_STREAM


```
int bufferSize = AudioTrack.getMinBufferSize(8000,
                                AudioFormat.CHANNEL_OUT_MONO,
                                AudioFormat.ENCODING_PCM_16BIT);
AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                                8000,
                                AudioFormat.CHANNEL_OUT_MONO,
                                AudioFormat.ENCODING_PCM_16BIT,
                                bufferSize * 2,
                                AudioTrack.MODE_STREAM);

byte[] tempBuffer = new byte[bufferSize];
audioTrack.play();
audioTrack.write(tempBuffer, 0, readCount);

audioTrack.stop();
audioTrack.release();
```

```
int bufferSize = AudioRecord.getMinBufferSize(8000,
                                AudioFormat.CHANNEL_IN_MONO,
                                AudioFormat.ENCODING_PCM_16BIT);
AudioRecord mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                8000,
                                AudioFormat.CHANNEL_IN_MONO,
                                AudioFormat.ENCODING_PCM_16BIT,
                                bufferSize * 2);

mRecorder.startRecording();
byte[] tempBuffer = new byte[bufferSize];
bytesRecord = mRecorder.read(tempBuffer, 0, bufferSize);

mRecorder.stop();
mRecorder.release();
```

权限
---
```
<!-- 录音权限 -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<!-- 往SDCard写入数据权限 -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<!-- 在SDCard中创建与删除文件权限 -->
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
<!-- 录视频权限 -->
<uses-permission android:name="android.permission.CAMERA" />
```


MediaRecorder和AudioRecord区别：
---
MediaRecorder和AudioRecord都可以录制音频，区别是MediaRecorder录制的音频文件是经过压缩后的，需要设置编码器。并且录制的音频文件可以用系统自带的Music播放器播放。
而AudioRecord录制的是PCM格式的音频文件，需要用AudioTrack来播放，AudioTrack更接近底层。
PCM可能更加可以理解为音频的源文件

AudioRecord优缺点
--
主要是实现边录边播以及对音频的实时处理,这个特性让他更适合在语音方面有优势
优点：语音的实时处理，可以用代码实现各种音频的封装
缺点：输出是PCM格式文件，如果保存成音频文件，是不能够被播放器播放的，所以必须先写代码实现数据编码以及压缩

MediaRecorder优缺点：
--
已经集成了录音、编码、压缩等，支持少量的录音音频格式，大概有,aac,amr,3gp等
优点：集成，直接调用相关接口即可，代码量小
缺点：无法实时处理音频；输出的音频格式不是很多，例如没有输出mp3格式文件




AudioRecord常量
---
```
/**
 *  表明audioRecord状态 初始化失败。
 */
public static final int STATE_UNINITIALIZED = 0;

/**
 *  表明AudioRecord状态 准备好使用了
 */
public static final int STATE_INITIALIZED   = 1;

/**
 * 表明AudioRecord 记录状态 还没记录
 */
public static final int RECORDSTATE_STOPPED = 1;

/**
 * 表明AudioRecord 记录状态 正在记录
 */
public static final int RECORDSTATE_RECORDING = 3;// matches SL_RECORDSTATE_RECORDING



/**
 * 表示成功操作。
 */
public  static final int SUCCESS                               = AudioSystem.SUCCESS;



/* AudioRecord.read()方法读取成功返回字节数，读取失败则返回以下： */
/**
 * 表示泛型操作失败。
 */
public  static final int ERROR                                 = AudioSystem.ERROR;

/**
 * 表示由于使用无效值而导致的失败。
 */
public  static final int ERROR_BAD_VALUE                       = AudioSystem.BAD_VALUE;

/**
 * 表示由于使用方法不当而导致的失败。
 */
public  static final int ERROR_INVALID_OPERATION               = AudioSystem.INVALID_OPERATION;

/**
 * 一个错误代码，指示报告它的对象不再有效，需要重新创建。
 */
public  static final int ERROR_DEAD_OBJECT                     = AudioSystem.DEAD_OBJECT;

```




