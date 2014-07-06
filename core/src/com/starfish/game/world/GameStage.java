/**
 * 
 */
package com.starfish.game.world;

import com.starfish.game.Assets;
import com.starfish.game.BaseStage;
import com.starfish.game.TtsCtrl;
import com.starfish.game.icontrol.IGameControl;
import com.starfish.game.world.modules.Iceberg;
import com.starfish.game.world.modules.MicPower;
import com.starfish.game.world.modules.WordCloud;

/**
 * @author liufy
 * 
 */
public class GameStage extends BaseStage {
	// private static final String TAG = "World Control";
	private static final float BOB_BEGIN = 0.2f;
	private static final float BOB_OVER = 0.9f;
	private static final float ICEBERG_Y = 0.36f;

	public int outValue;

	protected final WordCloud bob;
	protected final Iceberg iceberg;
	protected final MicPower micPower;
	private WorldRender render;
	private int temperature = 0;
	private IPlayStateListener playStateListener;
	private int volume;
	private IGameControl recognizerCtrl;
	private String wordFromMic;
	private TtsCtrl[] goodS;
	private int correctCnt;
	private final String[] soundR = { "sounds/common/good.ogg",
			"sounds/common/great.ogg", "sounds/common/lovely.ogg",
			"sounds/common/wonderful.ogg", "sounds/common/awesome.ogg" };

	public interface IPlayStateListener {
		public void gameOver();

		public void gamePass();
	}

	/**
	 * 
	 */
	public GameStage(IGameControl ttsListener) {
		wordFromMic = new String();
		this.bob = new WordCloud(Assets.VIRTUAL_WIDTH / 2,
				Assets.VIRTUAL_HEIGHT * BOB_BEGIN);
		this.iceberg = new Iceberg(0, Assets.VIRTUAL_HEIGHT * ICEBERG_Y);
		this.micPower = new MicPower(Assets.VIRTUAL_WIDTH - 80,
				(float) (Assets.VIRTUAL_HEIGHT * 0.9));

		render = new WorldRender(this);
		this.recognizerCtrl = ttsListener;

		goodS = new TtsCtrl[5];
		for (int i = 0; i < soundR.length; i++) {
			goodS[i] = new TtsCtrl();
			goodS[i].load(soundR[i]);
		}
		correctCnt = 0;
	}

	public void reset() {
		bob.reset();
		temperature = 0;
		iceberg.reset();
		outValue = 0;
	}

	@Override
	public void draw() {
		batch.begin();
		render.render(batch);
		batch.end();

		super.draw();
	}

	@Override
	public void act(float deltaTime) {
		updateBob(deltaTime);
		updateIceberg(deltaTime);
		micPower.update(deltaTime, volume);

		super.act(deltaTime);
	}

	public void setWordCloudText(String text) {
		if (!bob.isSpeaking()) {
			wordFromMic = text;
		}
	}

	public void setPlayCtrlListener(IPlayStateListener listener) {
		playStateListener = listener;
	}

	public void setVolume(int vol) {
		this.volume = vol;
	}

	private void updateBob(float deltaTime) {
		// First check word pool state
		if (bob.pool.checkOver()) {
			if (WordCloud.BOB_STATE_HIT != bob.state) {
				bob.state = WordCloud.BOB_STATE_IDLE;
				gamePass();
			}
		} else if (WordCloud.BOB_STATE_IDLE == bob.state && !bob.isSpeaking()) {
			bob.reset();
			wordFromMic = "";
			iceberg.start();
			if (null != recognizerCtrl) {
				recognizerCtrl.startRecognizer();
			}
		}

		if (WordCloud.BOB_STATE_FLYING == bob.state) {
			// Check word
			if (bob.getWord().matches(wordFromMic)) {
				bob.hit();
				correctCnt++;
				iceberg.stop();
			} else if (bob.position.y > Assets.VIRTUAL_HEIGHT * BOB_OVER) {
				// word miss, first speak out, then renew word
				if (null != recognizerCtrl) {
					recognizerCtrl.stopRecognizer();
				}

				bob.FlyOut();
				bob.speak();
				temperature += 1;

				correctCnt = 0;
				outValue += bob.getWord().replace(" ", "").length();
				iceberg.stop();
			}
		}

		if (WordCloud.BOB_STATE_HIT == bob.state) {
			if (bob.position.x + bob.bounds.width < 0) {
				bob.FlyOut();
			}
		}

		if (bob.state == WordCloud.BOB_STATE_HIT
				|| WordCloud.BOB_STATE_FLYING == bob.state) {
			bob.update(deltaTime);
		}

		if (correctCnt > 1) {
			goodS[correctCnt / 4].speakOut();
		}

	}

	private void updateIceberg(float deltaTime) {
		if (iceberg.bounds.height > 0) {
			iceberg.update(deltaTime, temperature);
		} else {
			gameFaill();
		}
	}

	private void gameFaill() {
		if (playStateListener == null) {
			throw new NullPointerException();
		}

		playStateListener.gameOver();
	}

	private void gamePass() {
		if (playStateListener == null) {
			throw new NullPointerException();
		}

		playStateListener.gamePass();
	}

	@Override
	public void dispose() {
		for (int i = 0; i < goodS.length; i++) {
			goodS[i].unload();
		}

		render.dispose();
		super.dispose();
	}

}
