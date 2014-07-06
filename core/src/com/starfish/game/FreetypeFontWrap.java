package com.starfish.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FreetypeFontWrap {
	private BitmapFont freetypeBitmapFont;

	public BitmapFont getFont(String text) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("data/DroidSansFallbackFull.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 36;
		parameter.characters = text;

		freetypeBitmapFont = generator.generateFont(parameter);
		generator.dispose(); // don't forget to dispose to avoid memory
								// leaks!

		return freetypeBitmapFont;
	}
	
	public BitmapFont getFont(String text, int size) {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("data/DroidSansFallbackFull.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		parameter.characters = text;

		freetypeBitmapFont = generator.generateFont(parameter);
		generator.dispose(); // don't forget to dispose to avoid memory
								// leaks!

		return freetypeBitmapFont;
	}

	public void dispose() {
		freetypeBitmapFont.dispose();
	}
}
