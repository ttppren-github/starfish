package com.starfish.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class BaseStage extends Stage {
	protected Batch batch;
	public Assets assests;

	public BaseStage() {
		super(new StretchViewport(Assets.VIRTUAL_WIDTH, Assets.VIRTUAL_HEIGHT));

		batch = getBatch();
		assests = Assets.getInstance();
	}

}
