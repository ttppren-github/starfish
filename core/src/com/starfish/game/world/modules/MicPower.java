package com.starfish.game.world.modules;

public class MicPower extends BaseObject {

	private static final float BOB_WIDTH = 80f;
	private static final float BOB_HEIGHT = 80f;

	public int volume, a, b;

	public MicPower(float x, float y) {
		super(x, y, BOB_WIDTH, BOB_HEIGHT);
	}

	public void update(float deltaTime, int msb) {
		b = msb;
		volume = (9 * a + b) / 10;
		a = b;
	}
}
