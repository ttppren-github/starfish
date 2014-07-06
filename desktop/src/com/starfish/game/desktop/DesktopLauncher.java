package com.starfish.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.starfish.game.Starfish;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Starfish";
		config.width = 480;
		config.height = 800;
		new LwjglApplication(new Starfish(), config);
	}
}
