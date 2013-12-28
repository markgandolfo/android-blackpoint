package com.gan.blackpoint;

import com.badlogic.gdx.Game;
import com.gan.blackpoint.screens.Settings;

public class Blackpoint extends Game {
	
	public static final String 	TITLE 	= "Blackpoint", 
								VERSION = "0.0.0.0.1 alpha";
	
	@Override
	public void create() {
		setScreen(new Settings());
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}
