package com.starfish.game.world.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.starfish.game.Assets;

public abstract class BaseView {
	Assets assets;
	Vector2 position;
	Rectangle bound;

	public BaseView() {
		assets = Assets.getInstance();
		position = new Vector2();
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public void setBound(Rectangle bound) {
		this.bound = bound;
	}

	abstract public void render(Batch batch);
}
