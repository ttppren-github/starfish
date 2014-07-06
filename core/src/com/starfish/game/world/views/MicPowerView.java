package com.starfish.game.world.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.starfish.game.Assets;

public class MicPowerView extends BaseView {
	private final int W = 128;
	private final int MAX = 10000;
	private float volume;

	public void setVolume(int vol) {
		volume = vol;
	}

	public void render(Batch batch) {
		float x = position.x;
		float y = position.y;

		float scale = MAX / volume;
		float w = W * 2 / scale;
		if (w < W / 2) {
			w = W / 2;
		}

		batch.draw(assets.getTexture(Assets.SUN), x - w / 2, y - w / 2, w, w);
		batch.draw(assets.getTexture(Assets.MIC),
				x - assets.getTexture(Assets.MIC).getWidth() / 2, y
						- assets.getTexture(Assets.MIC).getHeight() / 2);

		// Gdx.app.log("MIC", "vol=" + volume);
	}
}
