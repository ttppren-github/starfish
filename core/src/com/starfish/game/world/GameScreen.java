package com.starfish.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.starfish.game.Assets;
import com.starfish.game.BaseStage;
import com.starfish.game.FreetypeFontWrap;
import com.starfish.game.Starfish;
import com.starfish.game.world.GameStage.IPlayStateListener;

public class GameScreen implements Screen {
	private final String TAG = "Game screen";
	private final String RETURN = "返回主菜单";
	private final String CONTINUE = "继续";
	private final String SWITCH = "选择关卡";

	private final int GAME_STATE_RUNNING = 1;
	private final int GAME_STATE_PAUSED = 2;
	private final int GAME_STATE_GAME_FAIL = 3;
	private final int GAME_STATE_GAME_PASS = 4;
	private final int GAME_STATE_STOPPED = 5;

	private Starfish game;
	private GameStage gameStage;
	private BaseStage uiStage;
	private int state;

	private ImageButton btnStop;
	private Button btnStart;
	private Button btnBack, btnSwitch;
	private FreetypeFontWrap font;

	public GameScreen(final Starfish game) {
		this.game = game;
		state = GAME_STATE_STOPPED;

		uiStage = new BaseStage();
		gameStage = new GameStage(game.recognizerCtrl);
		gameStage.setPlayCtrlListener(playStateListener);

		Assets assets = Assets.getInstance();

		btnStop = new ImageButton(assets.skin, Assets.BtnReturn);
		btnStop.setPosition(Assets.VIRTUAL_WIDTH - btnStop.getWidth(), 20);
		gameStage.addActor(btnStop);
		btnStop.addListener(clickLinster);

		Pixmap pixmap = new Pixmap(480, 800, Pixmap.Format.RGBA8888);
		pixmap.setColor(1f, 1f, 1f, 0.7f);
		pixmap.fillRectangle(0, 0, 480, 800);
		Texture tx = new Texture(pixmap);
		uiStage.addActor(new Image(tx));

		font = new FreetypeFontWrap();
		LabelStyle labelStyle = new LabelStyle(font.getFont(CONTINUE + RETURN
				+ SWITCH), Color.BLACK);

		// Switch
		btnSwitch = new Button(assets.skin, Assets.Btn);
		btnSwitch.add(new Label(SWITCH, labelStyle));
		btnSwitch.setPosition(120, 400);
		uiStage.addActor(btnSwitch);
		btnSwitch.addListener(clickLinster);

		btnStart = new Button(assets.skin, Assets.Btn);
		btnStart.add(new Label(CONTINUE, labelStyle));
		btnStart.setPosition(120, 320);
		uiStage.addActor(btnStart);
		btnStart.addListener(clickLinster);

		btnBack = new Button(assets.skin, Assets.Btn);
		btnBack.add(new Label(RETURN, labelStyle));
		btnBack.setPosition(120, 240);
		uiStage.addActor(btnBack);
		btnBack.addListener(clickLinster);
	}

	@Override
	public void render(float delta) {
		uiStage.act(delta);

		if (GAME_STATE_RUNNING == state) {
			gameStage.act(delta);
		}

		gameStage.draw();
		if (GAME_STATE_PAUSED == state) {
			uiStage.draw();
		}

		if (GAME_STATE_GAME_FAIL == state) {
			game.setScreen(game.gameFailScreen);
		}

		if (GAME_STATE_GAME_PASS == state) {
			game.setScreen(game.gamePassScreen);
		}
	}

	@Override
	public void resize(int width, int height) {
		// Gdx.app.log("Resize", "new width=" + width + " hei=" + height);

		gameStage.getViewport().update(width, height, true);
		uiStage.getViewport().update(width, height, true);
	}

	@Override
	public void hide() {
		if (null != game.recognizerCtrl) {
			game.recognizerCtrl.stopRecognizer();
		}

		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
		state = GAME_STATE_PAUSED;

		if (null != game.recognizerCtrl) {
			game.recognizerCtrl.stopRecognizer();
		}
	}

	private void stop(Screen screen) {
		state = GAME_STATE_STOPPED;

		if (null != game.recognizerCtrl) {
			game.recognizerCtrl.stopRecognizer();
		}

		game.setScreen(screen);
	}

	@Override
	public void resume() {
		state = GAME_STATE_RUNNING;

		if (null != game.recognizerCtrl) {
			game.recognizerCtrl.startRecognizer();
		}
	}

	@Override
	public void show() {
		state = GAME_STATE_RUNNING;
		gameStage.reset();

		InputMultiplexer inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(gameStage);
		inputMultiplexer.addProcessor(uiStage);
		inputMultiplexer.addProcessor(new ScreenInputHandler());
		Gdx.input.setInputProcessor(inputMultiplexer);

		if (null != game.recognizerCtrl) {
			game.recognizerCtrl.startRecognizer();
		}
	}

	@Override
	public void dispose() {
		gameStage.dispose();
		uiStage.dispose();
		font.dispose();

		Gdx.app.log(TAG, "dispose()");
	}

	public void setText(String text) {
		if (GAME_STATE_RUNNING == state) {
			gameStage.setWordCloudText(text);
		}
	}

	public void setVolume(int vol) {
		if (GAME_STATE_RUNNING == state) {
			gameStage.setVolume(vol);
		}
	}

	private IPlayStateListener playStateListener = new IPlayStateListener() {
		@Override
		public void gameOver() {
			state = GAME_STATE_GAME_FAIL;
			Gdx.app.log(TAG, "Game Over");
		}

		@Override
		public void gamePass() {
			state = GAME_STATE_GAME_PASS;
			Gdx.app.log(TAG, "Game Pass");
		}
	};

	private class ScreenInputHandler implements InputProcessor {

		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Keys.BACK || keycode == Keys.BACKSPACE) {
				if (GAME_STATE_RUNNING == state) {
					pause();
				} else {
					resume();
				}
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

	private ClickListener clickLinster = new ClickListener() {

		@Override
		public void clicked(InputEvent event, float x, float y) {
			if (GAME_STATE_RUNNING == state) {
				if (event.getListenerActor() == btnStop) {
					pause();
					return;
				}
			}

			if (GAME_STATE_PAUSED == state) {
				if (event.getListenerActor() == btnSwitch) {
					stop(game.swichScreen);
					return;
				}

				if (event.getListenerActor() == btnStart) {
					resume();
					return;
				}

				if (event.getListenerActor() == btnBack) {
					stop(game.mainScreen);
					return;
				}
			}
		}
	};
}
