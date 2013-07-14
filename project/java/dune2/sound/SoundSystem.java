package com.gamesinjs.dune2.sound;

import java.io.File;
import java.io.IOException;

import com.gamesinjs.dune2.Globals;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;


public class SoundSystem extends Thread {
	
	private MediaPlayer mediaPlayer;
	
	private int musicPlaying;
	
	private static SoundSystem instance;
	
	private SoundSystem() {
		setName("SoundSystemThread");
		setDaemon(true);
		
		musicPlaying = 0;
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setLooping(false);
		//mediaPlayer.setVolume(0.5f, 0.5f);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}

	private void tick() {
		int musicToPlay = musicToPlay();
		if (musicPlaying != musicToPlay) {
			File dataDir = new File(Globals.DataDir);
			File music = new File(dataDir, "music/opendune_"+musicToPlay+".mid");
			
			Log.i("OpenDUNE", "Should play " + music.getAbsoluteFile() + ", exists: " + music.exists());
			
			mediaPlayer.reset();
			
			if (music.exists()) {
				try {
					mediaPlayer.setDataSource(music.getAbsoluteFile().toString());
					mediaPlayer.prepare();
					mediaPlayer.start();
				} catch (Exception e) {
					Log.e("OpenDUNE", "Unable to play music", e);
				}
			}
			
			musicPlaying = musicToPlay;
		}
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				tick();
				sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	public static void init() {
		instance = new SoundSystem();
		instance.start();
	}
	
	private static native int musicToPlay();
	
}
