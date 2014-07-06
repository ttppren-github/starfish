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
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.starfish.game.Assets;
import com.starfish.game.BaseStage;
import com.starfish.game.FreetypeFontWrap;
import com.starfish.game.Starfish;
import com.starfish.game.TtsCtrl;
import com.starfish.game.icontrol.ITtsCtrl;
import com.starfish.game.world.WordPool;

/**
 * @author liufy
 * 
 */
public class GameFailScreen implements Screen {
	private final String TAG = GameFailScreen.class.getSimpleName();
	private final String RETURN = "选择关卡";
	private final String RESTART = "重玩";
	private final String VOICE = "发音";
	private Starfish gameMain;
	private BaseStage stage;
	private Button btnBack, btnRestart, btnVoice;
	private List list;
	private TextArea tx;
	private String selectedWord;
	private ITtsCtrl speaker;

	/**
	 * 
	 */
	public GameFailScreen(Starfish game) {
		this.gameMain = game;
		selectedWord = "";

		stage = new BaseStage();

		FreetypeFontWrap font = new FreetypeFontWrap();
		LabelStyle labelStyle = new LabelStyle(font.getFont(RETURN + RESTART
				+ VOICE), Color.BLACK);

		btnBack = new Button(Assets.getInstance().skin, Assets.Btn);
		btnBack.add(new Label(RETURN, labelStyle));
		btnBack.addListener(clickListener);

		btnRestart = new Button(Assets.getInstance().skin, Assets.Btn);
		btnRestart.add(new Label(RESTART, labelStyle));
		btnRestart.addListener(clickListener);

		btnVoice = new Button(Assets.getInstance().skin, Assets.Btn);
		btnVoice.add(new Label(VOICE, labelStyle));
		btnVoice.addListener(clickListener);

		list = new List(Assets.getInstance().skin, Assets.ListView);
		final ScrollPane scroller = new ScrollPane(list);

		TextFieldStyle tfStyle = new TextFieldStyle();
		tfStyle.font = Assets.getInstance().getFont();
		tfStyle.fontColor = Color.RED;
		tx = new TextArea("", tfStyle);
		tx.setBounds(40, 640, 400, 120);

		final Table tableRoot = new Table();
		tableRoot.setFillParent(true);
		tableRoot.row().spaceTop(20);
		tableRoot.add(scroller);
		tableRoot.row().spaceTop(20);
		tableRoot.add(btnVoice);
		tableRoot.row();
		tableRoot.add(btnBack);
		tableRoot.row();
		tableRoot.add(btnRestart);

		tableRoot.pad(160, 20, 40, 20);

		stage.addActor(tx);
		stage.addActor(tableRoot);

		speaker = new TtsCtrl();
	}

	@Override
	public void render(float delta) {
		stage.act(delta);

		String str = (String) list.getSelected();
		if (!str.matches(selectedWord)) {
			TextFieldStyle style = new TextFieldStyle();
			FreetypeFontWrap font = new FreetypeFontWrap();

			selectedWord = str;
			String text = WordPool.getInstance().getText(selectedWord);
			tx.clear();

			style.font = font.getFont(replayHZ(text), 24);
			style.fontColor = Color.BLACK;
			tx.setStyle(style);
			tx.setText(text);
		}

		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		list.setItems(WordPool.getInstance().getFailWords());

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
		Gdx.app.log(TAG, "dispose()");
	}

	private ClickListener clickListener = new ClickListener() {
		@Override
		public void clicked(InputEvent event, float x, float y) {
			if (event.getListenerActor() == btnBack) {
				gameMain.setScreen(gameMain.swichScreen);
			} else if (event.getListenerActor() == btnRestart) {
				WordPool.getInstance().reload();
				gameMain.setScreen(gameMain.gameScreen);
			} else if (event.getListenerActor() == btnVoice) {
				if (speaker != null) {
					String path = "sounds/" + WordPool.getInstance().getStage()
							+ "/" + selectedWord.replace(" ", "") + ".ogg";
					speaker.unload();
					speaker.load(path);
					speaker.speakOut();
				}
			}
		}
	};

	private String replayHZ(final String text) {
		String ret = "";
		StringBuffer strBuf = new StringBuffer();
		char c;
		int j;

		if (text == null) {
			return ret;
		}

		for (int i = 0; i < text.length(); i++) {
			c = text.charAt(i);
			for (j = 0; j < strBuf.length(); j++) {
				if (c == strBuf.charAt(j)) {
					break;
				}
			}

			if (j == strBuf.length()) {
				strBuf.append(c);
			}
		}

		ret = strBuf.toString();
		return ret;
	}
}
