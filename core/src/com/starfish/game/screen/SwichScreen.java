/**
 * 
 */
package com.starfish.game.screen;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.starfish.game.Assets;
import com.starfish.game.BaseStage;
import com.starfish.game.Starfish;
import com.starfish.game.world.WordPool;

/**
 * @author liufy
 * 
 */
public class SwichScreen implements Screen {
	private BaseStage stage;
	private Starfish gameMain;
	private Image bg;
	private ImageButton btnReturn;
	private ArrayList<ImageButton> groups;

	/**
	 * 
	 * 
	 */
	public SwichScreen(Starfish game) {
		this.gameMain = game;
		groups = new ArrayList<ImageButton>();

		Assets assets = Assets.getInstance();
		stage = new BaseStage();
		bg = new Image(assets.getTexture(Assets.SwitchScreen_Bg));
		stage.addActor(bg);

		btnReturn = new ImageButton(assets.skin, Assets.BtnReturn);
		btnReturn.setPosition(20, (float) (Assets.VIRTUAL_HEIGHT * 0.9) - 20);
		stage.addActor(btnReturn);
		btnReturn.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				gameMain.setScreen(gameMain.mainScreen);
			}
		});

		Table tab = new Table(assets.skin);
		for (int i = 0; i < 5; i++) {
			ImageButton tBtn = new ImageButton(assets.skin, Assets.BtnStar);
			tBtn.sizeBy(72, 72);
			Label lab = new Label(Integer.toString(i + 1), assets.skin,
					Assets.FONT);
			tBtn.add(lab);
			tab.add(tBtn).pad(20);
			if (i == 2) {
				tab.row();
			}

			tBtn.addListener(new ClickListener() {

				@Override
				public void clicked(InputEvent event, float x, float y) {
					int i = groups.indexOf(event.getListenerActor()) + 1;

					WordPool pool = WordPool.getInstance();
					String str = String.format("dic/%dstage_dic.json", i);
					pool.loadJson(str);

					if (gameMain.recognizerCtrl != null) {
						String gram = String.format(
								"models/grammar/words/%dstage.gram", i);
						gameMain.recognizerCtrl.loadGrammar(gram);
					}

					gameMain.setScreen(gameMain.gameScreen);
				}
			});
			groups.add(tBtn);
		}
		tab.add(new Image(assets.getTexture(Assets.LockStar)));
		tab.setPosition((float) (Assets.VIRTUAL_WIDTH - tab.getWidth()) / 2,
				(float) (Assets.VIRTUAL_HEIGHT * 0.65 - tab.getHeight()));
		stage.addActor(tab);
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
		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(new ScreenInputHandler());
		Gdx.input.setInputProcessor(inputMultiplexer);
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
		groups.clear();
		stage.dispose();
	}

	private class ScreenInputHandler implements InputProcessor {

		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Keys.BACK || keycode == Keys.BACKSPACE) {
				gameMain.setScreen(gameMain.mainScreen);
			}
			return false;
		}

		@Override
		public boolean keyUp(int keycode) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean keyTyped(char character) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer,
				int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean scrolled(int amount) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
