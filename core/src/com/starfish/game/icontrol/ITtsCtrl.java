package com.starfish.game.icontrol;

public interface ITtsCtrl {

	public void load(String src);

	public void unload();

	public void speakOut();

	public boolean isSpeaking();
}
