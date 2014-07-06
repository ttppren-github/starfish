package com.starfish.game.icontrol;

public interface IGameControl {
	// Recognizer
	public void startRecognizer();

	public void stopRecognizer();

	public void loadGrammar(String file);

	// UI Contrl
	public void closeGame();

	public boolean checkFirstRun();

}
