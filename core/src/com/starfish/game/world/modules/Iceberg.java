package com.starfish.game.world.modules;

import com.starfish.game.Assets;

public class Iceberg extends ActiveObject {

	public int mHeight;

	private final int MELT_VELOCITY = 1;
	private static final float BOB_WIDTH = Assets.VIRTUAL_WIDTH;
	private static final float BOB_HEIGHT = 270f;
	private static final int HEIGHT = 8848;
	private int ve;
	private float heightSetp;

	float stateTime;

	public Iceberg(float x, float y) {
		super(x, y, BOB_WIDTH, BOB_HEIGHT);
		stateTime = 0;
		ve = MELT_VELOCITY;

		heightSetp = HEIGHT / BOB_HEIGHT;
	}

	public void update(float deltaTime, int temperature) {
		float tmp = (float) Math.sqrt(temperature);
		float dis = tmp * deltaTime * ve;
		this.bounds.height -= dis;

		mHeight = (int) (heightSetp * this.bounds.height);
		stateTime += deltaTime;
	}

	public void stop() {
		ve = 0;
	}

	public void start() {
		ve = MELT_VELOCITY;
	}

	public void reset() {
		this.bounds.height = BOB_HEIGHT;
	}
}
