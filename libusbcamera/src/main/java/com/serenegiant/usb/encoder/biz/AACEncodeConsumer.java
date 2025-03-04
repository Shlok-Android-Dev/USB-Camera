package com.serenegiant.usb.encoder.biz;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Process;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AACEncodeConsumer extends Thread{
    private static final boolean DEBUG = false;
    private static final String TAG = "TMPU";
    private static final String MIME_TYPE = "audio/mp4a-latm";
    private static final long TIMES_OUT = 1000;
    private static final int SAMPLE_RATE = 8000;     // Sampling rate
    private static final int BIT_RATE = 16000;       // Bit rate
    private static final int BUFFER_SIZE = 1920;     // Minimum Cache
    private int outChannel = 1;
    private int bitRateForLame = 32;
    private int qaulityDegree = 7;
    private int bufferSizeInBytes;

    private AudioRecord mAudioRecord; // Audio Capture
    private MediaCodec mAudioEncoder;   // Audio Coding
    private OnAACEncodeResultListener listener;
    private  int mSamplingRateIndex = 0;//ADTS
    private boolean isEncoderStart = false;
    private boolean isRecMp3 = false;
    private boolean isExit = false;
    private long prevPresentationTimes = 0;
    private WeakReference<Mp4MediaMuxer> mMuxerRef;
    private MediaFormat newFormat;

    private static final int[] AUDIO_SOURCES = new int[] {
            MediaRecorder.AudioSource.DEFAULT,
            MediaRecorder.AudioSource.MIC,
            MediaRecorder.AudioSource.CAMCORDER,
    };
    /**
     * There are 13 supported frequencies by ADTS.
     **/
    public static final int[] AUDIO_SAMPLING_RATES = { 96000, // 0
            88200, // 1
            64000, // 2
            48000, // 3
            44100, // 4
            32000, // 5
            24000, // 6
            22050, // 7
            16000, // 8
            12000, // 9
            11025, // 10
            8000, // 11
            7350, // 12
            -1, // 13
            -1, // 14
            -1, // 15
    };

    private FileOutputStream fops;

    // Encoding stream result callback interface
    public interface OnAACEncodeResultListener{
        void onEncodeResult(byte[] data, int offset,
                            int length, long timestamp);
    }

    public AACEncodeConsumer(){
        for (int i=0;i < AUDIO_SAMPLING_RATES.length; i++) {
            if (AUDIO_SAMPLING_RATES[i] == SAMPLE_RATE) {
                mSamplingRateIndex = i;
                break;
            }
        }
    }

    public void setOnAACEncodeResultListener(OnAACEncodeResultListener listener){
        this.listener = listener;
    }

    public void exit(){
        isExit = true;
    }

    public synchronized void setTmpuMuxer(Mp4MediaMuxer mMuxer){
        this.mMuxerRef =  new WeakReference<>(mMuxer);
        Mp4MediaMuxer muxer = mMuxerRef.get();
        if (muxer != null && newFormat != null) {
            muxer.addTrack(newFormat, false);
        }
    }

    @Override
    public void run() {
        // Enable audio capture and encoding
        if(! isEncoderStart){
            initAudioRecord();
            initMediaCodec();
        }
        // Initialize audio file parameters
        byte[] mp3Buffer = new byte[1024];

        // There is a problem here. When the local recording is finished, nothing is written.
        while(! isExit){
            byte[] audioBuffer = new byte[2048];
            // Capture Audio
            int readBytes = mAudioRecord.read(audioBuffer,0,BUFFER_SIZE);

            if(DEBUG)
                Log.i(TAG,"Collect audio readBytes = "+readBytes);
            // Encoding Audio
            if(readBytes > 0){
                encodeBytes(audioBuffer,readBytes);
            }
        }
        // Stop audio capture and encoding
        stopMediaCodec();
        stopAudioRecord();
    }


    @TargetApi(21)
    private void encodeBytes(byte[] audioBuf, int readBytes) {
        ByteBuffer[] inputBuffers = mAudioEncoder.getInputBuffers();
        ByteBuffer[] outputBuffers = mAudioEncoder.getOutputBuffers();
        //Returns an input buffer handle of the encoder. -1 means there is no input buffer available.
        int inputBufferIndex = mAudioEncoder.dequeueInputBuffer(TIMES_OUT);
        if(inputBufferIndex >= 0){
// Bind an empty, writable input buffer inputBuffer to the client
            ByteBuffer inputBuffer  = null;
            if(!isLollipop()){
                inputBuffer = inputBuffers[inputBufferIndex];
            }else{
                inputBuffer = mAudioEncoder.getInputBuffer(inputBufferIndex);
            }
            // Write valid raw data to the input buffer and submit it to the encoder for encoding
            if(audioBuf==null || readBytes<=0){
                mAudioEncoder.queueInputBuffer(inputBufferIndex,0,0,getPTSUs(),MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            }else{
                inputBuffer.clear();
                inputBuffer.put(audioBuf);
                mAudioEncoder.queueInputBuffer(inputBufferIndex,0,readBytes,getPTSUs(),0);
            }
        }

// Returns an output buffer handle. When -1, it means there is no available output buffer.
// The mBufferInfo parameter contains the encoded data, and the timesOut parameter is the timeout waiting time.
        MediaCodec.BufferInfo  mBufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = -1;
        do{
            outputBufferIndex = mAudioEncoder.dequeueOutputBuffer(mBufferInfo,TIMES_OUT);
            if(outputBufferIndex == MediaCodec. INFO_TRY_AGAIN_LATER){
                if(DEBUG)
                    Log.i(TAG,"获得编码器输出缓存区超时");
            }else if(outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED){
                // If the API is less than 21, the APP needs to rebind the encoder's input buffer;
                // If the API is greater than 21, there is no need to process INFO_OUTPUT_BUFFERS_CHANGED
                if(!isLollipop()){
                    outputBuffers = mAudioEncoder.getOutputBuffers();
                }
            }else if(outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                // The encoder output buffer format changes, usually before storing data and only once
                // Set the mixer video track here, and start the mixer if audio has been added (to ensure audio and video synchronization)
                if(DEBUG)
                    Log.i(TAG,"编码器输出缓存区格式改变，添加视频轨道到混合器");
                synchronized (AACEncodeConsumer.this) {
                    newFormat = mAudioEncoder.getOutputFormat();
                    if(mMuxerRef != null){
                        Mp4MediaMuxer muxer = mMuxerRef.get();
                        if (muxer != null) {
                            muxer.addTrack(newFormat, false);
                        }
                    }
                }
            }else{
                // When the flag attribute is set to BUFFER_FLAG_CODEC_CONFIG, it means that the data in the output buffer has been consumed.
                if((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0){
                    if(DEBUG)
                        Log.i(TAG,"编码数据被消费，BufferInfo的size属性置0");
                    mBufferInfo.size = 0;
                }
                // Data stream end mark, end this cycle
                if((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0){
                    if(DEBUG)
                        Log.i(TAG,"数据流结束，退出循环");
                    break;
                }
                // Get a read-only output buffer inputBuffer, which contains the encoded data
                ByteBuffer mBuffer = ByteBuffer.allocate(10240);
                ByteBuffer outputBuffer = null;
                if(!isLollipop()){
                    outputBuffer  = outputBuffers[outputBufferIndex];
                }else{
                    outputBuffer  = mAudioEncoder.getOutputBuffer(outputBufferIndex);
                }
                if(mBufferInfo.size != 0){
                    // Failed to obtain the output buffer, throwing an exception
                    if(outputBuffer == null){
                        throw new RuntimeException("encodecOutputBuffer"+outputBufferIndex+"was null");
                    }
                    // Adding a video stream to the mixer
                    if(mMuxerRef != null){
                        Mp4MediaMuxer muxer = mMuxerRef.get();
                        if (muxer != null) {
                            muxer.pumpStream(outputBuffer, mBufferInfo, false);
                        }
                    }
                    // Add ADTS header to AAC stream and cache it in mBuffer
                    mBuffer.clear();
                    outputBuffer.get(mBuffer.array(), 7, mBufferInfo.size);
                    outputBuffer.clear();
                    mBuffer.position(7 + mBufferInfo.size);
                    addADTStoPacket(mBuffer.array(), mBufferInfo.size + 7);
                    mBuffer.flip();
                    // Push AAC callback to MainModelImpl
                    if(listener != null){
                        Log.i(TAG,"----->Get aac data stream<-----");
                        listener.onEncodeResult(mBuffer.array(),0, mBufferInfo.size + 7, mBufferInfo.presentationTimeUs / 1000);
                    }
                }
                // Processing is completed, and output buffer resources are released
                mAudioEncoder.releaseOutputBuffer(outputBufferIndex,false);
            }
        }while (outputBufferIndex >= 0);
    }

    private void initAudioRecord(){
        if(DEBUG)
            Log.d(TAG,"AACEncodeConsumer-->Start capturing audio");
        // Setting process priority
        Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        for (final int src: AUDIO_SOURCES) {
            try {
                mAudioRecord = new AudioRecord(src,
                        SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
                if (mAudioRecord != null) {
                    if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                        mAudioRecord.release();
                        mAudioRecord = null;
                    }
                }
            } catch (final Exception e) {
                mAudioRecord = null;
            }
            if (mAudioRecord != null) {
                break;
            }
        }
        mAudioRecord.startRecording();
    }

    private void initMediaCodec(){
        if(DEBUG)
            Log.d(TAG,"AACEncodeConsumer-->Start encoding audio");
        MediaCodecInfo mCodecInfo = selectSupportCodec(MIME_TYPE);
        if(mCodecInfo == null){
            Log.e(TAG,"The encoder does not support the "+MIME_TYPE+" type");
            return;
        }
        try{
            mAudioEncoder = MediaCodec.createByCodecName(mCodecInfo.getName());
        }catch(IOException e){
            Log.e(TAG,"Failed to create encoder"+e.getMessage());
            e.printStackTrace();
        }
        MediaFormat format = new MediaFormat();
        format.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm");
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, SAMPLE_RATE);
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, BUFFER_SIZE);
        mAudioEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mAudioEncoder.start();
        isEncoderStart = true;
    }

    private void stopAudioRecord() {
        if(DEBUG)
            Log.d(TAG,"AACEncodeConsumer-->Stop collecting audio");
        if(mAudioRecord != null){
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    private void stopMediaCodec() {
        if(DEBUG)
            Log.d(TAG,"AACEncodeConsumer-->Stop encoding audio");
        if(mAudioEncoder != null){
            mAudioEncoder.stop();
            mAudioEncoder.release();
            mAudioEncoder = null;
        }
        isEncoderStart = false;
    }

    // API>=21
    private boolean isLollipop(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    // API<=19
    private boolean isKITKAT(){
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT;
    }

    private long getPTSUs(){
        long result = System.nanoTime()/1000;
        if(result < prevPresentationTimes){
            result = (prevPresentationTimes  - result ) + result;
        }
        return result;
    }

    /**
     * Traverse all codecs and return the first encoder that matches the specified MIME type
     * Determine whether there is an encoder that supports the specified mime type
     * */
    private MediaCodecInfo selectSupportCodec(String mimeType){
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            // Determine whether it is an encoder, otherwise enter the next cycle directly
            if (!codecInfo.isEncoder()) {
                continue;
            }
            // If it is an encoder, determine whether the Mime type is supported
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

    private void addADTStoPacket(byte[] packet, int packetLen) {
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF1;
        packet[2] = (byte) (((2 - 1) << 6) + (mSamplingRateIndex << 2) + (1 >> 2));
        packet[3] = (byte) (((1 & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }


    private short[] transferByte2Short(byte[] data,int readBytes){
        // Convert byte[] to short[], reducing the array length by half
        int shortLen = readBytes / 2;
        // Assemble the byte[] array into a ByteBuffer buffer
        ByteBuffer byteBuffer = ByteBuffer.wrap(data, 0, readBytes);
        // Convert ByteBuffer to little endian and get shortBuffer
        ShortBuffer shortBuffer = byteBuffer.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        short[] shortData = new short[shortLen];
        shortBuffer.get(shortData, 0, shortLen);
        return shortData;
    }
}
