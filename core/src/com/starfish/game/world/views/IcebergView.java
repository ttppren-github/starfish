package com.starfish.game.world.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.starfish.game.Assets;

public class IcebergView extends BaseView {

	public IcebergView(Rectangle bounds) {
		bound = bounds;
	}

	@Override
	public void render(Batch batch) {
		float x = position.x;
		float y = position.y;

		batch.draw(assets.getTexture(Assets.ICEBERG), x, y, bound.width,
				bound.height);

		batch.draw(assets.getTexture(Assets.Ruler), 340, y);
	}

}
