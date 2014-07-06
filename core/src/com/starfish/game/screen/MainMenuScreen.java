/**
 * 
 */
package com.starfish.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.starfish.game.Assets;
import com.starfish.game.BaseStage;
import com.starfish.game.FreetypeFontWrap;
import com.starfish.game.Starfish;

/**
 * @author liufy
 * 
 */
public class MainMenuScreen implements Screen {

	private BaseStage stage;
	private Starfish game;
	private Button btnStart;
	private Button btnExit;
	private Image bg;
	private FreetypeFontWrap font;

	/**
	 * 
	 */
	public MainMenuScreen(Starfish game) {
		this.game = game;

		Assets assets = Assets.getInstance();
		stage = new BaseStage();
		bg = new Image(assets.getTexture(Assets.MainMenu_Bg));
		stage.addActor(bg);

		font = new FreetypeFontWrap();
		LabelStyle labelStyle = new LabelStyle(font.getFont("开始退出"),
				Color.BLACK);

		Label lab = new Label("开始", labelStyle);
		btnStart = new Button(assets.skin, Assets.Btn);
		btnStart.add(lab);
		btnStart.setPosition(120, 320);
		stage.addActor(btnStart);
		btnStart.addListener(clicListener);

		btnExit = new Button(assets.skin, Assets.Btn);
		Label lab2 = new Label("退出", labelStyle);
		btnExit.add(lab2);
		btnExit.setPosition(120, 240);
		stage.addActor(btnExit);
		btnExit.addListener(clicListener);
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
		font.dispose();
	}

	private ClickListener clicListener = new ClickListener() {

		@Override
		public void clicked(InputEvent event, float x, float y) {
			if (event.getListenerActor() == btnStart) {
				game.setScreen(game.swichScreen);
			} else if (event.getListenerActor() == btnExit) {
				if (game.recognizerCtrl != null) {
					game.recognizerCtrl.closeGame();
				}
			}
		}

	};
}
