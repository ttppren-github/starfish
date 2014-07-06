
package com.starfish.game.world.modules;

import com.badlogic.gdx.math.Vector2;

public class ActiveObject extends BaseObject  {
	public final Vector2 velocity;
	public final Vector2 accel;

	public ActiveObject (float x, float y, float width, float height) {
		super(x, y, width, height);
		velocity = new Vector2();
		accel = new Vector2();
	}
}
