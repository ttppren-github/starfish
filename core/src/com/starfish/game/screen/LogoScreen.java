/**
 * 
 */
package com.starfish.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.SizeToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.starfish.game.Assets;
import com.starfish.game.BaseStage;
import com.starfish.game.Starfish;
import com.starfish.game.TtsCtrl;

/**
 * @author liufy
 * 
 */
public class LogoScreen implements Screen {
	private final String TAG = LogoScreen.class.getSimpleName();

	private Starfish game;
	private Image logo, name;
	private BaseStage stage;
	private float start;
	private final float DELAY = 3; // 3s
	private TtsCtrl player;

	/**
	 * 
	 */
	public LogoScreen(Starfish game) {
		this.game = game;
		start = 0;

		stage = new BaseStage();
		Assets.getInstance().preLoading();
		logo = new Image(Assets.getInstance().logo);
		name = new Image(Assets.getInstance().name);

		stage.addActor(logo);
		stage.addActor(name);
	}

	@Override
	public void render(float delta) {
		stage.act();
		logo.setPosition((Assets.VIRTUAL_WIDTH - logo.getWidth()) / 2,
				(Assets.VIRTUAL_HEIGHT - logo.getHeight()) * 2 / 3);
		stage.draw();

		start += delta;
		if (game.assets.update()) {
			float ret = game.assets.getProgress();
			if (ret == 1 && /* game.ttsListener != null && */start > DELAY) {
				Gdx.app.log(TAG, "loading resource over.");

				game.resourceReady();
			}
		}
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		name.setPosition((Assets.VIRTUAL_WIDTH - name.getWidth()) / 2, 100);

		Color c = logo.getColor();
		c.a = 0.4f;
		logo.setColor(c);
		AlphaAction alphaAction = new AlphaAction();
		alphaAction.setAlpha(1f);
		alphaAction.setDuration(2);
		logo.addAction(alphaAction);

		SizeToAction sizeToAction = new SizeToAction();
		sizeToAction.setDuration(2);
		sizeToAction.setSize(logo.getWidth() / 2, logo.getHeight() / 2);
		logo.addAction(sizeToAction);

		player = new TtsCtrl();
		player.load("sounds/common/welcome_starfish.ogg");
		player.speakOut();
	}

	@Override
	public void hide() {
		player.unload();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		Assets.getInstance().unLoadPreLoading();
		stage.dispose();
	}
}
