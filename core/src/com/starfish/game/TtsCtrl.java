package com.starfish.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.starfish.game.icontrol.ITtsCtrl;

public class TtsCtrl implements ITtsCtrl {
	private final String TAG = TtsCtrl.class.getSimpleName();
	private Music music;

	public TtsCtrl() {
		music = null;
	}

	public void load(String src) {
		music = null;
		try {
			music = Gdx.audio.newMusic(Gdx.files.internal(src));
			music.setVolume(1.0f);
		} catch (Exception e) {
			Gdx.app.log(TAG, "load error: " + src);
		}

	}

	public void unload() {
		if (null != music) {
			music.stop();
			music.dispose();
			music = null;
		}
	}

	@Override
	public void speakOut() {
		if (null != music) {
			music.play();
		}
	}

	@Override
	public boolean isSpeaking() {
		if (null != music && music.isPlaying()) {
			return true;
		}

		return false;
	}

}
