package com.jiebao.baqiang.global;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

import com.jiebao.baqiang.R;


public class BeepManager {

	private final long VIBRATE_DURATION = 200L;

	private boolean playBeep = false;
	private boolean vibrate = false;

	private Context mContext;
	private int loadId1;
	private int loadId2;
	private SoundPool mSoundPool;
	private Vibrator mVibrator;

	public BeepManager(Context context, boolean playBeep, boolean vibrate) {
		super();
		this.mContext = context;
		this.playBeep = playBeep;
		this.vibrate = vibrate;

		initial();
	}

	public void setPlayBeep(boolean playBeep) {
		this.playBeep = playBeep;
	}

	public void setVibrate(boolean vibrate) {
		this.vibrate = vibrate;
	}

	private void initial() {
		if (null == mSoundPool) {
			mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		}
		loadId1 = mSoundPool.load(mContext, R.raw.beep, 1);
		loadId2  = mSoundPool.load(mContext, R.raw.beep1, 1);

		mVibrator = (Vibrator) mContext
				.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void play() {
		if (playBeep) {
			mSoundPool.play(loadId1, 1f, 1f, 1, 0, 1f);
		}
		if (vibrate) {
			mVibrator.vibrate(VIBRATE_DURATION);
		}
	}

	public void notifySound() {
		if (playBeep) {
			mSoundPool.play(loadId2, 1f, 1f, 1, 0, 1f);
		}
		if (vibrate) {
			mVibrator.vibrate(VIBRATE_DURATION);
		}
	}
}
