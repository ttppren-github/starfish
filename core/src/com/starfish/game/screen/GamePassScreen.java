/**
 * 
 */
package com.starfish.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.starfish.game.Assets;
import com.starfish.game.BaseStage;
import com.starfish.game.FreetypeFontWrap;
import com.starfish.game.Starfish;
import com.starfish.game.world.WordPool;

/**
 * @author liufy
 * 
 */
public class GamePassScreen implements Screen {
	private final String RETRY = "再来一次";
	private final String RETURN = "主菜单";
	private Starfish game;

	private BaseStage stage;
	private Button btnRetry;
	private Button btnBack;

	/**
	 * 
	 */
	public GamePassScreen(Starfish game) {
		this.game = game;
		stage = new BaseStage();

		FreetypeFontWrap font = new FreetypeFontWrap();
		LabelStyle labelStyle = new LabelStyle(font.getFont(RETURN + RETRY),
				Color.BLACK);

		btnRetry = new Button(Assets.getInstance().skin, Assets.Btn);
		btnRetry.add(new Label(RETRY, labelStyle));
		btnRetry.setPosition(120, 240);
		btnRetry.addListener(clickListener);

		btnBack = new Button(Assets.getInstance().skin, Assets.Btn);
		btnBack.add(new Label(RETURN, labelStyle));
		btnBack.setPosition(120, 160);
		btnBack.addListener(clickListener);

		stage.addActor(btnRetry);
		stage.addActor(btnBack);
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
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
		stage.dispose();
	}

	private ClickListener clickListener = new ClickListener() {

		@Override
		public void clicked(InputEvent event, float x, float y) {
			if (event.getListenerActor() == btnRetry) {
				WordPool.getInstance().reload();
				game.setScreen(game.gameScreen);
			} else if (event.getListenerActor() == btnBack) {
				game.setScreen(game.mainScreen);
			}
		}

	};
}
