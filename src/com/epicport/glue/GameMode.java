package com.epicport.glue;

import android.util.Log;

public class GameMode extends Thread {

	public static final int GM_UNKNOWN = 0;
	public static final int GM_IN_GAME = 1;

	private static GameMode instance;
	private static GameModeChangeListener onChangeListener;

	static {
		instance = new GameMode();
		onChangeListener = null;
	}

	private boolean alive;
	private int mode;

	private GameMode() {
		alive = true;
		mode = -1;

		setName("Game mode listen thread");
		setDaemon(true);
	}

	@Override
	public void run() {
		while (alive) {
			try {
				int gameMode = gameMode();

				if (gameMode != mode) {
					mode = gameMode;
					fireModeChanged();
				}

				sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}

		Log.i("native-glue", "Closing game mode listen thread");
	}

	private void fireModeChanged() {
		Log.d("native-glue", "Game mode now is " + mode);
		if (onChangeListener != null) {
			onChangeListener.onGameModeChanged(mode);
		}
	}

	public static void listen() {
		Log.i("native-glue", "GameMode thread inited");
		instance.start();
	}

	public static void dispose() {
		instance.alive = false;
	}

	public static void setGameModeChangeListener(GameModeChangeListener listener) {
		onChangeListener = listener;

		if (instance != null) {
			onChangeListener.onGameModeChanged(instance.mode);
		}
	}

	private static int gameMode() {
		return GM_IN_GAME;
	}
	
	public static void allocateUnits(int unitType, long[] units) {
		
	}

	public static void placeUnits(long[] units) {
		
	}

	public static void freeUnits(long[] units) {
		
	}
	
//	private static native int gameMode();
//
//	public static native void allocateUnits(int unitType, long[] units);
//
//	public static native void placeUnits(long[] units);
//
//	public static native void freeUnits(long[] units);
//	
//	public static native void offsetMode(boolean enabled);

}
