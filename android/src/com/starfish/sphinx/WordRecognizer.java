package com.starfish.sphinx;

import static java.lang.String.format;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.FsgModel;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;

public class WordRecognizer {

	protected static final String TAG = WordRecognizer.class.getSimpleName();

	private static final int BUFFER_SIZE = 1024;

	private final Decoder decoder;

	private Thread recognizerThread;
	private final Handler mainHandler = new Handler(Looper.getMainLooper());
	private final Collection<RecognitionListener> listeners = new HashSet<RecognitionListener>();
	private final Collection<VolumeListener> volListeners = new HashSet<VolumeListener>();

	private final int sampleRate;

	protected WordRecognizer(Config config) {
		sampleRate = (int) config.getFloat("-samprate");
		if (config.getFloat("-samprate") != sampleRate)
			throw new IllegalArgumentException("sampling rate must be integer");
		decoder = new Decoder(config);
	}

	/**
	 * Adds listener.
	 */
	public void addListener(RecognitionListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public void addListener(VolumeListener listener) {
		synchronized (volListeners) {
			volListeners.add(listener);
		}
	}

	/**
	 * Removes listener.
	 */
	public void removeListener(RecognitionListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	public void removeListener(VolumeListener listener) {
		synchronized (volListeners) {
			volListeners.remove(listener);
		}
	}

	/**
	 * Starts recognition. Does nothing if recognition is active.
	 * 
	 * @return true if recognition was actually started
	 */
	public boolean startListening(String searchName) {
		if (null != recognizerThread)
			return false;

		Log.i(TAG, format("Start recognition \"%s\"", searchName));
		decoder.setSearch(searchName);
		recognizerThread = new RecognizerThread();
		recognizerThread.start();
		return true;
	}

	private boolean stopRecognizerThread() {
		if (null == recognizerThread)
			return false;

		try {
			recognizerThread.interrupt();
			recognizerThread.join();
		} catch (InterruptedException e) {
			// Restore the interrupted status.
			Thread.currentThread().interrupt();
		}

		recognizerThread = null;
		return true;
	}

	/**
	 * Stops recognition. All listeners should receive final result if there is
	 * any. Does nothing if recognition is not active.
	 * 
	 * @return true if recognition was actually stopped
	 */
	public boolean stop() {
		boolean result = stopRecognizerThread();
		if (result)
			Log.i(TAG, "Stop recognition");

		return result;
	}

	/**
	 * Cancels recogition. Listeners do not recevie final result. Does nothing
	 * if recognition is not active.
	 * 
	 * @return true if recognition was actually canceled
	 */
	public boolean cancel() {
		boolean result = stopRecognizerThread();
		if (result) {
			Log.i(TAG, "Cancel recognition");
			mainHandler.removeCallbacksAndMessages(null);
		}

		return result;
	}

	/**
	 * Gets name of the currently active search.
	 * 
	 * @return active search name or null if no search was started
	 */
	public String getSearchName() {
		return decoder.getSearch();
	}

	public void addFsgSearch(String searchName, FsgModel fsgModel) {
		decoder.setFsg(searchName, fsgModel);
	}

	/**
	 * Adds searches based on JSpeech grammar.
	 * 
	 * @param name
	 *            search name
	 * @param file
	 *            JSGF file
	 */
	public void addGrammarSearch(String name, File file) {
		Log.i(TAG, format("Load JSGF %s", file));
		decoder.setJsgfFile(name, file.getPath());
	}

	/**
	 * Adds search based on N-gram language model.
	 * 
	 * @param name
	 *            search name
	 * @param file
	 *            N-gram model file
	 */
	public void addNgramSearch(String name, File file) {
		Log.i(TAG, format("Load N-gram model %s", file));
		decoder.setLmFile(name, file.getPath());
	}

	/**
	 * Adds search based on a single phrase.
	 * 
	 * @param name
	 *            search name
	 * @param phrase
	 *            search phrase
	 */
	public void addKeywordSearch(String name, String phrase) {
		decoder.setKws(name, phrase);
	}

	private final class RecognizerThread extends Thread {
		@Override
		public void run() {
			double sum = 0;
			double amplitude;
			int engergy;

			AudioRecord recorder = new AudioRecord(
					AudioSource.VOICE_RECOGNITION, sampleRate,
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, 8192); // TODO:calculate
															// properly
		
			decoder.startUtt(null);
			recorder.startRecording();

			short[] buffer = new short[BUFFER_SIZE];
			boolean vadState = decoder.getVadState();

			while (!interrupted()) {
				int nread = recorder.read(buffer, 0, buffer.length);
				if (-1 == nread) {
					throw new RuntimeException("error reading audio buffer");
				} else if (nread > 0) {

					// compute voice volume
					sum = 0;
					for (int i = 0; i < nread; i++) {
						sum += buffer[i] * buffer[i];
					}
					amplitude = sum / nread;
					engergy = (int) Math.sqrt(amplitude);
					mainHandler.post(new VolumeEvent(engergy));
					// Log.d(TAG, "energy=" + engergy + " amplitude=" +
					// amplitude);

					decoder.processRaw(buffer, nread, false, false);
					if (decoder.getVadState() != vadState) {
						vadState = decoder.getVadState();
						mainHandler.post(new VadStateChangeEvent(vadState));
					}

					Hypothesis hypothesis = decoder.hyp();
					if (null != hypothesis && decoder.getVadState() == false) {
						// if (null != hypothesis) {
						// recorder.stop();
						recorder.read(buffer, 0, buffer.length);
						// recorder.release();
						decoder.processRaw(buffer, nread, false, false);
						decoder.endUtt();

						hypothesis = decoder.hyp();
						if (null != hypothesis)
							mainHandler.post(new ResultEvent(hypothesis, true));

						// recorder = new
						// AudioRecord(AudioSource.VOICE_RECOGNITION,
						// sampleRate,
						// AudioFormat.CHANNEL_IN_MONO,
						// AudioFormat.ENCODING_PCM_16BIT,
						// 8192); // TODO:calculate properly

						decoder.startUtt(null);
						recorder.startRecording();
					}
				}
			}

			recorder.stop();
			//int nread = recorder.read(buffer, 0, buffer.length);
			recorder.release();
			//decoder.processRaw(buffer, nread, false, false);
			decoder.endUtt();
/*	
			// Remove all pending notifications.
			mainHandler.removeCallbacksAndMessages(null);
			final Hypothesis hypothesis = decoder.hyp();
			if (null != hypothesis)
				mainHandler.post(new ResultEvent(hypothesis, true));
		*/
			
			}
	}

	private abstract class RecognitionEvent implements Runnable {
		public void run() {
			RecognitionListener[] emptyArray = new RecognitionListener[0];
			for (RecognitionListener listener : listeners.toArray(emptyArray))
				execute(listener);
		}

		protected abstract void execute(RecognitionListener listener);
	}

	private class VadStateChangeEvent extends RecognitionEvent {
		private final boolean state;

		VadStateChangeEvent(boolean state) {
			this.state = state;
		}

		@Override
		protected void execute(RecognitionListener listener) {
			if (state)
				listener.onBeginningOfSpeech();
			else
				listener.onEndOfSpeech();
		}
	}

	private class ResultEvent extends RecognitionEvent {
		protected final Hypothesis hypothesis;
		private final boolean finalResult;

		ResultEvent(Hypothesis hypothesis, boolean finalResult) {
			this.hypothesis = hypothesis;
			this.finalResult = finalResult;
		}

		@Override
		protected void execute(RecognitionListener listener) {
			if (finalResult)
				listener.onResult(hypothesis);
			else
				listener.onPartialResult(hypothesis);
		}
	}

	public interface VolumeListener {
		void onVolumeChange(int engergy);
	}

	private class VolumeEvent implements Runnable {
		int energy;

		VolumeEvent(int energy) {
			this.energy = energy;
		}

		public void run() {
			VolumeListener[] emptyArray = new VolumeListener[0];
			for (VolumeListener listener : volListeners.toArray(emptyArray))
				execute(listener);
		}

		protected void execute(VolumeListener listener) {
			listener.onVolumeChange(energy);
		}
	}
}
