package com.starfish.game.world.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.starfish.game.Assets;

public class WordCloudView extends BaseView {

	private Rectangle bound;
	private String text;

	public WordCloudView() {
		bound = new Rectangle();
		position = new Vector2();
	}

	public void setBound(Rectangle bound) {
		this.bound = bound;
	}

	public void setPostion(Vector2 position) {
		this.position = position;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void render(Batch batch) {
		batch.draw(assets.getTexture(Assets.CLOUD), bound.x, bound.y,
				bound.width, bound.height);
		if (text != null && text.length() > 0) {
			assets.getFont().setColor(Color.WHITE);
			assets.getFont().draw(batch, text, position.x, position.y);
		}
	}
}
