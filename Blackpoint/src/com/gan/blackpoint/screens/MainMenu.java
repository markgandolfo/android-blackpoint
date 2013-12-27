package com.gan.blackpoint.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.gan.blackpoint.Blackpoint;
import com.gan.blackpoint.tween.ActorAccessor;

public class MainMenu implements Screen {
	
	private Stage stage;
	private TextureAtlas atlas; 
	private Skin skin;
	private Table table;
	private TextButton buttonExit, buttonPlay;
	private BitmapFont white, black; // done
	private Label heading;
	private TweenManager tweenManager;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		tweenManager.update(delta);
		
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
		table.setClip(true); // work around for table.setTransform(true);
		table.setSize(width, height);
	}

	@Override
	public void show() {
		stage = new Stage();
		
		Gdx.input.setInputProcessor(stage);
		
		atlas = new TextureAtlas("ui/button.pack");
		skin = new Skin(atlas);

		table = new Table(skin);
		table.setBounds(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		white = new BitmapFont(Gdx.files.internal("font/white.fnt"), false);
		black = new BitmapFont(Gdx.files.internal("font/black.fnt"), false);		
		
		// creating button
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("button.up");
		textButtonStyle.down = skin.getDrawable("button.down");
		textButtonStyle.pressedOffsetX = 1;
		textButtonStyle.pressedOffsetY = -1;
		textButtonStyle.font = white;
		
		buttonExit = new TextButton("EXIT", textButtonStyle);
		
		buttonExit.setWidth(200);
		buttonExit.pad(15);
		buttonExit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		buttonPlay = new TextButton("PLAY", textButtonStyle);
		buttonPlay.pad(15);
		buttonPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Game) Gdx.app.getApplicationListener()).setScreen(new Levels());

				super.clicked(event, x, y);
			}
		});
		
		// creating heading
		LabelStyle headingStyle = new LabelStyle(white, Color.WHITE);
		heading = new Label(Blackpoint.TITLE, headingStyle);
		
		table.add(heading).padBottom(50);
		table.row();
		table.add(buttonPlay);
		table.getCell(buttonPlay).spaceBottom(20);
		table.row();
		table.add(buttonExit);
		stage.addActor(table);
		
		// creating animations
		tweenManager = new TweenManager();
		Tween.registerAccessor(Actor.class, new ActorAccessor());
		
		
		Timeline.createSequence().beginSequence()
			.push(Tween.to(heading, ActorAccessor.RGB, .5f).target(0, 0, 1))
			.push(Tween.to(heading, ActorAccessor.RGB, .5f).target(0, 1, 0))
			.push(Tween.to(heading, ActorAccessor.RGB, .5f).target(1, 0, 0))
			.push(Tween.to(heading, ActorAccessor.RGB, .5f).target(0, 1, 1))
			.push(Tween.to(heading, ActorAccessor.RGB, .5f).target(1, 0, 1))
			.push(Tween.to(heading, ActorAccessor.RGB, .5f).target(1, 1, 0))
			.push(Tween.to(heading, ActorAccessor.RGB, .5f).target(1, 1, 1))
			.end().repeat(Tween.INFINITY, 0).start(tweenManager);
		
		Timeline.createSequence().beginSequence()
			.push(Tween.set(buttonPlay, ActorAccessor.ALPHA).target(0))
			.push(Tween.set(buttonExit, ActorAccessor.ALPHA).target(0))
			.push(Tween.from(heading, ActorAccessor.ALPHA, .3f).target(0))
			.push(Tween.to(buttonPlay, ActorAccessor.ALPHA, .3f).target(1))
			.push(Tween.to(buttonExit, ActorAccessor.ALPHA, .3f).target(1))
			.end().start(tweenManager);
		
		// table fade-in
		Tween.from(table, ActorAccessor.ALPHA, .5f).target(0).start(tweenManager);
		Tween.from(table, ActorAccessor.Y, .5f).target(Gdx.graphics.getHeight() / 8).start(tweenManager);
	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		white.dispose();
		black.dispose();
		stage.dispose();
		atlas.dispose();
		skin.dispose();
	}

}
