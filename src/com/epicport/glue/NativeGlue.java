package com.epicport.glue;

import android.util.Log;

public class NativeGlue extends Thread {

	public static final int GM_UNKNOWN = -1;
	public static final int GM_IN_GAME = 1;

	private static NativeGlue instance;
	private static GameModeChangeListener onChangeListener;

	static {
		instance = new NativeGlue();
		onChangeListener = null;
	}

	private boolean alive;
	private int mode;

	private NativeGlue() {
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

	private static native int gameMode();

	public static native void buyResource(int resourceId, int count);
	
}
