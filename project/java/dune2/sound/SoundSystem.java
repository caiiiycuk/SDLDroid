package com.gamesinjs.dune2.sound;

import java.io.File;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.gamesinjs.dune2.Globals;


public class SoundSystem extends Thread {
	
	private static final int NO_MUSIC = 0;

	private MediaPlayer mediaPlayer;
	
	private int musicPlaying;
	
	private static SoundSystem instance;
	
	private static boolean alive;
	
	private static boolean paused;
	
	static {
		alive = true;
		paused = false;
	}
	
	private SoundSystem() {
		setName("SoundSystemThread");
		setDaemon(true);
		
		musicPlaying = 0;
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setLooping(false);
		//mediaPlayer.setVolume(0.5f, 0.5f);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}

	private void play(int musicToPlay) {
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
		while (alive) {
			try {
				if (!paused) {
					play(musicToPlay());
				}
				
				sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}
		
		mediaPlayer.reset();
		Log.i("OpenDUNE", "Closing SoundSystem Thread");
	}
	
	public static void init() {
		instance = new SoundSystem();
		instance.start();
	}
	
	private static native int musicToPlay();

	public static void free() {
		alive = false;
	}

	public static void onPause() {
		paused = true;
		
		if (instance != null) {
			instance.play(NO_MUSIC);
		}
	}

	public static void onResume() {
		paused = false;
		
		if (instance != null) {
			instance.play(musicToPlay());
		}
	}
	
}
