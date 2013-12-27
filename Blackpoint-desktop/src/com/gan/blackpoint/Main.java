package com.gan.blackpoint;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = Blackpoint.TITLE + " " + Blackpoint.VERSION;
		cfg.vSyncEnabled = true;
		cfg.useGL20 = true;
		cfg.width = 1080;
		cfg.height = 700;
		
		new LwjglApplication(new Blackpoint(), cfg);
	}
}
