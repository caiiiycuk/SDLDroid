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
import android.widget.TextView;
import android.util.Log;
import java.io.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.StatFs;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.Collections;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import java.lang.String;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Button;
import android.view.View;
import android.widget.LinearLayout;
import android.text.Editable;
import android.text.SpannedString;
import android.content.Intent;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.util.DisplayMetrics;
import android.net.Uri;
import java.util.concurrent.Semaphore;
import java.util.Arrays;
import android.graphics.Color;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.Sensor;
import android.widget.Toast;


class SettingsMenuMisc extends SettingsMenu
{
	static class DownloadConfig extends Menu
	{
		String title(final MainActivity p)
		{
			return p.getResources().getString(R.string.storage_question);
		}
		void run (final MainActivity p)
		{
			long freeSdcard = 0;
			long freePhone = 0;
			try
			{
				StatFs sdcard = new StatFs(Environment.getExternalStorageDirectory().getPath());
				StatFs phone = new StatFs(Environment.getDataDirectory().getPath());
				freeSdcard = (long)sdcard.getAvailableBlocks() * sdcard.getBlockSize() / 1024 / 1024;
				freePhone = (long)phone.getAvailableBlocks() * phone.getBlockSize() / 1024 / 1024;
			}
			catch(Exception e) {}

			final CharSequence[] items = { p.getResources().getString(R.string.storage_phone, freePhone),
											p.getResources().getString(R.string.storage_sd, freeSdcard),
											p.getResources().getString(R.string.storage_custom) };
			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(p.getResources().getString(R.string.storage_question));
			builder.setSingleChoiceItems(items, Globals.DownloadToSdcard ? 1 : 0, new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					dialog.dismiss();

					if( item == 2 )
						showCustomDownloadDirConfig(p);
					else
					{
						Globals.DownloadToSdcard = (item != 0);
						Globals.DataDir = Globals.DownloadToSdcard ?
										Settings.SdcardAppPath.getPath(p) :
										p.getFilesDir().getAbsolutePath();
						goBack(p);
					}
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}
		static void showCustomDownloadDirConfig(final MainActivity p)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(p.getResources().getString(R.string.storage_custom));

			final EditText edit = new EditText(p);
			edit.setFocusableInTouchMode(true);
			edit.setFocusable(true);
			edit.setText(Globals.DataDir);
			builder.setView(edit);

			builder.setPositiveButton(p.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					Globals.DataDir = edit.getText().toString();
					dialog.dismiss();
					showCommandLineConfig(p);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}
		static void showCommandLineConfig(final MainActivity p)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(p.getResources().getString(R.string.storage_commandline));

			final EditText edit = new EditText(p);
			edit.setFocusableInTouchMode(true);
			edit.setFocusable(true);
			edit.setText(Globals.CommandLine);
			builder.setView(edit);

			builder.setPositiveButton(p.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					Globals.CommandLine = edit.getText().toString();
					dialog.dismiss();
					goBack(p);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}
	}

	static class OptionalDownloadConfig extends Menu
	{
		boolean firstStart = false;
		OptionalDownloadConfig()
		{
			firstStart = true;
		}
		OptionalDownloadConfig(boolean firstStart)
		{
			this.firstStart = firstStart;
		}
		String title(final MainActivity p)
		{
			return p.getResources().getString(R.string.downloads);
		}
		void run (final MainActivity p)
		{
			String [] downloadFiles = Globals.DataDownloadUrl;
			final boolean [] mandatory = new boolean[downloadFiles.length];
			
			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(p.getResources().getString(R.string.downloads));

			CharSequence[] items = new CharSequence[downloadFiles.length];
			for(int i = 0; i < downloadFiles.length; i++ )
			{
				items[i] = new String(downloadFiles[i].split("[|]")[0]);
				if( items[i].toString().indexOf("!") == 0 )
					items[i] = items[i].toString().substring(1);
				if( items[i].toString().indexOf("!") == 0 )
				{
					items[i] = items[i].toString().substring(1);
					mandatory[i] = true;
				}
			}

			if( Globals.OptionalDataDownload == null || Globals.OptionalDataDownload.length != items.length )
			{
				Globals.OptionalDataDownload = new boolean[downloadFiles.length];
				boolean oldFormat = true;
				for( int i = 0; i < downloadFiles.length; i++ )
				{
					if( downloadFiles[i].indexOf("!") == 0 )
					{
						Globals.OptionalDataDownload[i] = true;
						oldFormat = false;
					}
				}
				if( oldFormat )
				{
					Globals.OptionalDataDownload[0] = true;
					mandatory[0] = true;
				}
			}

			builder.setMultiChoiceItems(items, Globals.OptionalDataDownload, new DialogInterface.OnMultiChoiceClickListener()
			{
				public void onClick(DialogInterface dialog, int item, boolean isChecked) 
				{
					Globals.OptionalDataDownload[item] = isChecked;
					if( mandatory[item] && !isChecked )
					{
						Globals.OptionalDataDownload[item] = true;
						((AlertDialog)dialog).getListView().setItemChecked(item, true);
					}
				}
			});
			builder.setPositiveButton(p.getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					dialog.dismiss();
					goBack(p);
				}
			});
			if( firstStart )
			{
				builder.setNegativeButton(p.getResources().getString(R.string.show_more_options), new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int item) 
					{
						dialog.dismiss();
						menuStack.clear();
						new MainMenu().run(p);
					}
				});
			}
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}
	}

	static class AudioConfig extends Menu
	{
		String title(final MainActivity p)
		{
			return p.getResources().getString(R.string.audiobuf_question);
		}
		void run (final MainActivity p)
		{
			final CharSequence[] items = {	p.getResources().getString(R.string.audiobuf_verysmall),
											p.getResources().getString(R.string.audiobuf_small),
											p.getResources().getString(R.string.audiobuf_medium),
											p.getResources().getString(R.string.audiobuf_large) };

			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(R.string.audiobuf_question);
			builder.setSingleChoiceItems(items, Globals.AudioBufferConfig, new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					Globals.AudioBufferConfig = item;
					dialog.dismiss();
					goBack(p);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}
	}

	static class VideoSettingsConfig extends Menu
	{
		String title(final MainActivity p)
		{
			return p.getResources().getString(R.string.video);
		}
		//boolean enabled() { return true; };
		void run (final MainActivity p)
		{
			CharSequence[] items = {
				p.getResources().getString(R.string.pointandclick_keepaspectratio),
				p.getResources().getString(R.string.video_smooth)
			};
			boolean defaults[] = { 
				Globals.KeepAspectRatio,
				Globals.VideoLinearFilter
			};

			if(Globals.SwVideoMode && !Globals.CompatibilityHacksVideo)
			{
				CharSequence[] items2 = {
					p.getResources().getString(R.string.pointandclick_keepaspectratio),
					p.getResources().getString(R.string.video_smooth),
					p.getResources().getString(R.string.video_separatethread),
				};
				boolean defaults2[] = { 
					Globals.KeepAspectRatio,
					Globals.VideoLinearFilter,
					Globals.MultiThreadedVideo
				};
				items = items2;
				defaults = defaults2;
			}

			if(Globals.Using_SDL_1_3)
			{
				CharSequence[] items2 = {
					p.getResources().getString(R.string.pointandclick_keepaspectratio),
				};
				boolean defaults2[] = { 
					Globals.KeepAspectRatio,
				};
				items = items2;
				defaults = defaults2;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(p.getResources().getString(R.string.video));
			builder.setMultiChoiceItems(items, defaults, new DialogInterface.OnMultiChoiceClickListener() 
			{
				public void onClick(DialogInterface dialog, int item, boolean isChecked) 
				{
					if( item == 0 )
						Globals.KeepAspectRatio = isChecked;
					if( item == 1 )
						Globals.VideoLinearFilter = isChecked;
					if( item == 2 )
						Globals.MultiThreadedVideo = isChecked;
				}
			});
			builder.setPositiveButton(p.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					dialog.dismiss();
					goBack(p);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}
	}

	static class ShowReadme extends Menu
	{
		String title(final MainActivity p)
		{
			return "Readme";
		}
		boolean enabled()
		{
			return true;
		}
		void run (final MainActivity p)
		{
			String readmes[] = Globals.ReadmeText.split("\\^");
			String lang = new String(Locale.getDefault().getLanguage()) + ":";
			String readme = readmes[0];
			String buttonName = "", buttonUrl = "";
			for( String r: readmes )
			{
				if( r.startsWith(lang) )
					readme = r.substring(lang.length());
				if( r.startsWith("button:") )
				{
					buttonName = r.substring("button:".length());
					if( buttonName.indexOf(":") != -1 )
					{
						buttonUrl = buttonName.substring(buttonName.indexOf(":") + 1);
						buttonName = buttonName.substring(0, buttonName.indexOf(":"));
					}
				}
			}
			readme = readme.trim();
			if( readme.length() <= 2 )
			{
				goBack(p);
				return;
			}
			TextView text = new TextView(p);
			text.setMaxLines(1000);
			text.setText(readme);
			text.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT));
			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			ScrollView scroll = new ScrollView(p);
			scroll.addView(text);
			Button ok = new Button(p);
			final AlertDialog alertDismiss[] = new AlertDialog[1];
			ok.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					alertDismiss[0].cancel();
				}
			});
			ok.setText(R.string.ok);
			LinearLayout layout = new LinearLayout(p);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.addView(scroll);
			layout.addView(ok);
			if( buttonName.length() > 0 )
			{
				Button cancel = new Button(p);
				cancel.setText(buttonName);
				final String url = buttonUrl;
				cancel.setOnClickListener(new View.OnClickListener()
				{
					public void onClick(View v)
					{
						if( url.length() > 0 )
						{
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(url));
							p.startActivity(i);
						}
						alertDismiss[0].cancel();
						System.exit(0);
					}
				});
				layout.addView(cancel);
			}
			builder.setView(layout);
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alertDismiss[0] = alert;
			alert.setOwnerActivity(p);
			alert.show();
		}
	}

	/* REMOVE GEROSCOPE AT ALL */

	static class ResetToDefaultsConfig extends Menu
	{
		String title(final MainActivity p)
		{
			return p.getResources().getString(R.string.reset_config);
		}
		boolean enabled()
		{
			return true;
		}
		void run (final MainActivity p)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle(p.getResources().getString(R.string.reset_config_ask));
			builder.setMessage(p.getResources().getString(R.string.reset_config_ask));
			
			builder.setPositiveButton(p.getResources().getString(R.string.ok), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					Settings.DeleteSdlConfigOnUpgradeAndRestart(p); // Never returns
					dialog.dismiss();
					goBack(p);
				}
			});
			builder.setNegativeButton(p.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int item) 
				{
					dialog.dismiss();
					goBack(p);
				}
			});
			builder.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
				public void onCancel(DialogInterface dialog)
				{
					goBack(p);
				}
			});
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			alert.show();
		}
	}
}

