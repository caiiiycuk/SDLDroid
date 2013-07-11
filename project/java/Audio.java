/*
Simple DirectMedia Layer
Java source code (C) 2009-2012 Sergii Pylypenko
  
This software is provided 'as-is', without any express or implied
warranty.  In no event will the authors be held liable for any damages
arising from the use of this software.

Permission is granted to anyone to use this software for any purpose,
including commercial applications, and to alter it and redistribute it
freely, subject to the following restrictions:
  
1. The origin of this software must not be misrepresented; you must not
   claim that you wrote the original software. If you use this software
   in a product, an acknowledgment in the product documentation would be
   appreciated but is not required. 
2. Altered source versions must be plainly marked as such, and must not be
   misrepresented as being the original software.
3. This notice may not be removed or altered from any source distribution.
*/

package net.sourceforge.clonekeenplus;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.media.AudioTrack;
import android.media.AudioManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import java.io.*;
import android.util.Log;
import java.util.concurrent.Semaphore;



class AudioThread
{

	private MainActivity mParent;
	private AudioTrack mAudio;
	private byte[] mAudioBuffer;
	private int mVirtualBufSize;

	public AudioThread(MainActivity parent)
	{
		mParent = parent;
		mAudio = null;
		mAudioBuffer = null;
		nativeAudioInitJavaCallbacks();
	}
	
	public int fillBuffer()
	{
		if( mParent.isPaused() )
		{
			try{
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
		else
		{
			//if( Globals.AudioBufferConfig == 0 ) // Gives too much spam to logcat, makes things worse
			//	mAudio.flush();

			mAudio.write( mAudioBuffer, 0, mVirtualBufSize );
		}
		
		return 1;
	}
	
	public int initAudio(int rate, int channels, int encoding, int bufSize)
	{
			if( mAudio == null )
			{
					channels = ( channels == 1 ) ? AudioFormat.CHANNEL_CONFIGURATION_MONO : 
													AudioFormat.CHANNEL_CONFIGURATION_STEREO;
					encoding = ( encoding == 1 ) ? AudioFormat.ENCODING_PCM_16BIT :
													AudioFormat.ENCODING_PCM_8BIT;

					mVirtualBufSize = bufSize;

					if( AudioTrack.getMinBufferSize( rate, channels, encoding ) > bufSize )
						bufSize = AudioTrack.getMinBufferSize( rate, channels, encoding );

					if(Globals.AudioBufferConfig != 0) {    // application's choice - use minimal buffer
						bufSize = (int)((float)bufSize * (((float)(Globals.AudioBufferConfig - 1) * 2.5f) + 1.0f));
						mVirtualBufSize = bufSize;
					}
					mAudioBuffer = new byte[bufSize];

					mAudio = new AudioTrack(AudioManager.STREAM_MUSIC,
												rate,
												channels,
												encoding,
												bufSize,
												AudioTrack.MODE_STREAM );
					//mAudio.setStereoVolume(0.5f, 0.5f);
					mAudio.play();
			}
			return mVirtualBufSize;
	}
	
	public byte[] getBuffer()
	{
		return mAudioBuffer;
	}
	
	public int deinitAudio()
	{
		if( mAudio != null )
		{
			mAudio.stop();
			mAudio.release();
			mAudio = null;
		}
		mAudioBuffer = null;
		return 1;
	}
	
	public int initAudioThread()
	{
		// Make audio thread priority higher so audio thread won't get underrun
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		return 1;
	}
	
	public int pauseAudioPlayback()
	{
		if( mAudio != null )
		{
			mAudio.pause();
		}
		if( mRecordThread != null )
		{
			mRecordThread.pauseRecording();
		}
		return 1;
	}

	public int resumeAudioPlayback()
	{
		if( mAudio != null )
		{
			mAudio.play();
		}
		if( mRecordThread != null )
		{
			mRecordThread.resumeRecording();
		}
		return 1;
	}

	private native int nativeAudioInitJavaCallbacks();

	// ----- Audio recording -----

	private RecordingThread mRecordThread = null;
	private AudioRecord mRecorder = null;
	private int mRecorderBufferSize = 0;

	private byte[] startRecording(int rate, int channels, int encoding, int bufsize)
	{
		if( mRecordThread == null )
		{
			mRecordThread = new RecordingThread();
			mRecordThread.start();
		}
		if( !mRecordThread.isStopped() )
		{
			Log.i("SDL", "SDL: error: application already opened audio recording device");
			return null;
		}

		mRecordThread.init(bufsize);

		int channelConfig = ( channels == 1 ) ? AudioFormat.CHANNEL_IN_MONO :
										AudioFormat.CHANNEL_IN_STEREO;
		int encodingConfig = ( encoding == 1 ) ? AudioFormat.ENCODING_PCM_16BIT :
										AudioFormat.ENCODING_PCM_8BIT;

		int minBufDevice = AudioRecord.getMinBufferSize(rate, channelConfig, encodingConfig);
		int minBufferSize = Math.max(bufsize * 8, minBufDevice + (bufsize - (minBufDevice % bufsize)));
		Log.i("SDL", "SDL: app opened recording device, rate " + rate + " channels " + channels + " sample size " + (encoding+1) + " bufsize " + bufsize + " internal bufsize " + minBufferSize);
		if( mRecorder == null || mRecorder.getSampleRate() != rate ||
			mRecorder.getChannelCount() != channels ||
			mRecorder.getAudioFormat() != encodingConfig ||
			mRecorderBufferSize != minBufferSize )
		{
			if( mRecorder != null )
				mRecorder.release();
			mRecorder = null;
			try {
				mRecorder = new AudioRecord(AudioSource.DEFAULT, rate, channelConfig, encodingConfig, minBufferSize);
				mRecorderBufferSize = minBufferSize;
			} catch (IllegalArgumentException e) {
				Log.i("SDL", "SDL: error: failed to open recording device!");
				return null;
			}
		}
		else
		{
			Log.i("SDL", "SDL: reusing old recording device");
		}
		mRecordThread.startRecording();
		return mRecordThread.mRecordBuffer;
	}

	private void stopRecording()
	{
		if( mRecordThread == null || mRecordThread.isStopped() )
		{
			Log.i("SDL", "SDL: error: application already closed audio recording device");
			return;
		}
		mRecordThread.stopRecording();
		Log.i("SDL", "SDL: app closed recording device");
	}

	private class RecordingThread extends Thread
	{
		private boolean stopped = true;
		byte[] mRecordBuffer;
		private Semaphore waitStarted = new Semaphore(0);
		private boolean sleep = false;

		RecordingThread()
		{
			super();
		}

		void init(int bufsize)
		{
			if( mRecordBuffer == null || mRecordBuffer.length != bufsize )
				mRecordBuffer = new byte[bufsize];
		}

		public void run()
		{
			while( true )
			{
				waitStarted.acquireUninterruptibly();
				waitStarted.drainPermits();
				stopped = false;
				sleep = false;

				while( !sleep )
				{
					int got = mRecorder.read(mRecordBuffer, 0, mRecordBuffer.length);
					if( got != mRecordBuffer.length )
					{
						// Audio is stopped here, sleep a bit.
						try{
							Thread.sleep(1000);
						} catch (InterruptedException e) {}
					}
					else
					{
						//Log.i("SDL", "SDL: nativeAudioRecordCallback with len " + mRecordBuffer.length);
						nativeAudioRecordCallback();
						//Log.i("SDL", "SDL: nativeAudioRecordCallback returned");
					}
				}

				stopped = true;
				mRecorder.stop();
			}
		}

		public void startRecording()
		{
			mRecorder.startRecording();
			waitStarted.release();
		}
		public void stopRecording()
		{
			sleep = true;
			while( !stopped )
			{
				try{
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}
		public void pauseRecording()
		{
			if( !stopped )
				mRecorder.stop();
		}
		public void resumeRecording()
		{
			if( !stopped )
				mRecorder.startRecording();
		}
		public boolean isStopped()
		{
			return stopped;
		}
	}

	private native void nativeAudioRecordCallback();
}
