package com.vincent.qr_scanner.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

import com.vincent.qr_scanner.R;

import java.io.Closeable;

/**
 * 声响管理器
 * @Author: Vincent
 * @CreateAt: 2021/01/11 17:39
 */
public class BeepManager implements MediaPlayer.OnErrorListener, Closeable {
    private static final long VIBRATE_DURATION = 200L;

    private final Context context;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private boolean playBeep;
    private boolean vibrate;

    public BeepManager(Context context) {
        this.context = context;
        this.mediaPlayer = null;
        updatePrefs();
    }

    public void setVibrate(boolean vibrate){
        this.vibrate = vibrate;
    }

    public void setPlayBeep(boolean playBeep){
        this.playBeep = playBeep;
    }

    private synchronized void updatePrefs() {
        if (mediaPlayer == null) {
            mediaPlayer = buildMediaPlayer(context);
        }
        if(vibrator == null){
            vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    public synchronized void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private MediaPlayer buildMediaPlayer(Context context) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor file = context.getResources().openRawResourceFd(R.raw.beep);
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepare();
            return mediaPlayer;
        } catch (Exception e) {
            e.printStackTrace();
            mediaPlayer.release();
            return null;
        }
    }

    @Override
    public synchronized boolean onError(MediaPlayer mp, int what, int extra) {
        close();
        updatePrefs();
        return true;
    }

    @Override
    public synchronized void close() {
        try{
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
