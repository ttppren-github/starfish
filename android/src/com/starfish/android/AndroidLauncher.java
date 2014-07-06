package com.starfish.android;

import static edu.cmu.pocketsphinx.Assets.syncAssets;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.starfish.game.Starfish;
import com.starfish.game.icontrol.IGameControl;
import com.starfish.sphinx.WordRecognizer;
import com.starfish.sphinx.WordRecognizer.VolumeListener;
import com.starfish.sphinx.WordRecognizerSetup;

import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;

public class AndroidLauncher extends AndroidApplication {
	private static final String DIGITS_SEARCH = "digits";
	private WordRecognizer recognizer;
	private Starfish game;
	private AutoUpdateApk aua;
	private File appDir;
	private Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		game = new Starfish();
		initialize(game, cfg);

		// Add delay for display logging screen quickly
		Handler handler = new Handler();
		handler.postDelayed(initVoiceRun, 500);
	}

	@Override
	protected void onDestroy() {
		if (aua != null) {
			aua.stop();
		}

		if (recognizer != null) {
			recognizer.cancel();
		}

		System.exit(0);
		super.onDestroy();
	}

	RecognitionListener recognitionListener = new RecognitionListener() {
		@Override
		public void onBeginningOfSpeech() {
		}

		@Override
		public void onEndOfSpeech() {
		}

		@Override
		public void onPartialResult(Hypothesis hypothesis) {
			String text = hypothesis.getHypstr();

			game.setRecognize(text);
			Log.d("Pengui", "text=" + text);
		}

		@Override
		public void onResult(Hypothesis hypothesis) {
			String text = hypothesis.getHypstr();
			game.setRecognize(text);
			Log.d("Pengui", "text=" + text);
		}
	};

	private VolumeListener listener = new VolumeListener() {
		@Override
		public void onVolumeChange(int energy) {
			// Log.d("Pengui", "energy=" + energy);
			game.setVolume(energy);
		}
	};

	private IGameControl ttsListener = new IGameControl() {

		@Override
		public void startRecognizer() {
			recognizer.startListening(DIGITS_SEARCH);
		}

		@Override
		public void stopRecognizer() {
			recognizer.stop();
		}

		@Override
		public void closeGame() {
			finish();
		}

		@Override
		public void loadGrammar(String file) {
			if (file == null || file.isEmpty()) {
				return;
			}

			File digitsGrammar = new File(appDir, file);
			recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
		}

		@Override
		public boolean checkFirstRun() {
			SharedPreferences preferences = getSharedPreferences("config.xml",
					MODE_PRIVATE);
			int count = preferences.getInt("count", 0);
			if (0 == count) {
				preferences.edit().putInt("count", 1).commit();

				Intent intent = new Intent(ctx, Welcome.class);
				startActivity(intent);

				return true;
			}

			return false;
		}
	};

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}

		return super.onKeyUp(keyCode, event);
	}

	private void initVoice() {
		AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
		am.setParameters("noise_suppression=auto");

		try {
			appDir = syncAssets(getApplicationContext());
		} catch (IOException e) {
			throw new RuntimeException("failed to synchronize assets", e);
		}

		recognizer = (WordRecognizer) WordRecognizerSetup
				.defaultSetup()
				// .setAcousticModel(new File(appDir, "models/hmm/en-us-semi"))
				// .setAcousticModel(new File(appDir,
				// "models/hmm/hub4wsj_sc_8k"))
				.setAcousticModel(
						new File(appDir, "models/acoustic/hub4wsj_sc_8k"))
				// .setAcousticModel(new File(appDir,
				// "models/acoustic/tidigits"))
				.setDictionary(new File(appDir, "models/lm/en_US/cmu07a.dic"))
				// .setDictionary(new File(appDir,
				// "models/acoustic/dict/digits.dict"))
				// .setDictionary(new File(appDir, "models/lm/en/turtle.dic"))
				.setRawLogDir(appDir).setKeywordThreshold(1e-5f)
				.getRecognizer();
		recognizer.addListener(recognitionListener);
		recognizer.addListener(listener);

		// Start to check new version
		aua = new AutoUpdateApk(getApplicationContext());
		aua.start();

		// here for waiting this run over in Logging Screen
		game.setTtsListener(ttsListener);
	}

	private Runnable initVoiceRun = new Runnable() {

		@Override
		public void run() {
			initVoice();
		}

	};
}