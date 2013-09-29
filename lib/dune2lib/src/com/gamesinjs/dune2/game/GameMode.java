package com.gamesinjs.dune2.game;

import android.util.Log;

public class GameMode extends Thread {

	public static final int GM_MENU = 0;
	public static final int GM_NORMAL = 1;
	public static final int GM_RESTART = 2;
	public static final int GM_PICKHOUSE = 3;
	
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
		
		Log.i("OpenDUNE", "Closing game mode listen thread");
	}

	private void fireModeChanged() {
		if (onChangeListener != null) {
			onChangeListener.onGameModeChanged(mode);
		}
	}

	public static void listen() {
		Log.i("Dune_2", "GameMode thread inited");
		instance.start();
	}

	private static native int gameMode();

	public static void dispose() {
		instance.alive = false;
	}
	
	public static void setGameModeChangeListener(GameModeChangeListener listener) {
		onChangeListener = listener;
		
		if (instance != null) {
			onChangeListener.onGameModeChanged(instance.mode);
		}
	}

//	private static void updateVisibility() {
//	if (view != null) {
//		class Callback implements Runnable {
//			public void run() {
//				if (canShow) {
//					view.setVisibility(View.VISIBLE);
//				} else {
//					view.setVisibility(View.GONE);
//				}
//			}
//		}
//		
//		activity.runOnUiThread(new Callback());
//	}
//}
	
}
