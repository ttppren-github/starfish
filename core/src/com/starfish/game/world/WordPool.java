package com.starfish.game.world;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class WordPool {
	private final String TAG = WordPool.class.getSimpleName();
	private static WordPool instance;
	private Array<String> words;
	private JsonValue val;
	private String fileName;

	private WordPool() {
		words = new Array<String>();
	}

	public static WordPool getInstance() {
		if (null == instance) {
			instance = new WordPool();
		}

		return instance;
	}

	public void loadWords(String fileName) {
		String result = new String();
		Pattern p = Pattern.compile("\\t|\r|\n");

		result = Gdx.files.internal(fileName).readString();
		if (result.length() > 0) {
			Matcher m = p.matcher(result);
			result = m.replaceAll("");

			int end = result.indexOf("public");
			int start = result.indexOf("=");
			result = result.substring(start, end);

			end = result.indexOf(";");
			result = result.substring(1, end);

			String[] wordArray = result.split("\\|");

			for (int i = 0; i < wordArray.length; i++) {
				p = Pattern.compile("\\s*$");
				m = p.matcher(wordArray[i]);
				wordArray[i] = m.replaceAll("");

				if (wordArray[i].length() > 0) {
					words.add(wordArray[i]);
				}
			}
			Gdx.app.log(TAG, "Word get from size:" + words.size);
		}
	}

	public void loadJson(String fileName) {
		words.clear();

		JsonReader json = new JsonReader();
		val = json.parse(Gdx.files.internal(fileName));

		for (int i = 0; i < val.size; i++) {
			JsonValue t = val.get(i);
			String word = t.getString("name");
			if (word != null && word.length() > 0) {
				words.add(word);
			}
		}

		this.fileName = fileName;
	}

	public void reload() {
		words.clear();
		for (int i = 0; i < val.size; i++) {
			JsonValue t = val.get(i);
			String word = t.getString("name");
			if (word != null && word.length() > 0) {
				words.add(word);
			}
		}
	}

	public String mining() {
		return words.get(random());
	}

	public void hit(String word) {
		words.removeValue(word, true);
	}

	public boolean checkOver() {

		return words.size == 0;
	}

	public Array<String> getFailWords() {
		return words;
	}

	private int random() {
		int ret = 0;
		if (words.size > 0) {
			ret = MathUtils.random(0, words.size - 1);
		}
		return ret;
	}

	public String getText(String str) {
		String ret = "";
		for (int i = 0; i < val.size; i++) {
			JsonValue t = val.get(i);
			if (t.getString("name").matches(str)) {
				ret = t.getString("yisi");
				break;
			}
		}

		return ret;
	}

	public String getStage() {
		if (fileName != null && fileName.length() > 1) {
			return fileName.substring(4, 5);
		}

		return "0";
	}
}
