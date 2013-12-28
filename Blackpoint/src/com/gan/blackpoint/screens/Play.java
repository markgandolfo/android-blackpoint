package com.gan.blackpoint.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.gan.blackpoint.InputController;

public class Play implements Screen {

	private World world;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera camera;

	private final float PIXELS_TO_METERS = 15,			// how many pixels to a meter
						TIME_STEP = 1 / 60f; 			// 60 fps
	private final int 	VELOCITY_ITERATIONS = 8,		// copied from box2d example
						POSITION_ITERATIONS = 3;		// copied from box2d example
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		debugRenderer.render(world,  camera.combined);
		
		world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width / PIXELS_TO_METERS;
		camera.viewportHeight = height / PIXELS_TO_METERS;
		camera.update();
	}

	@Override
	public void show() {
		world = new World(new Vector2(0, -9.81f), true);
		debugRenderer = new Box2DDebugRenderer();

		camera = new OrthographicCamera();

		Gdx.input.setInputProcessor(new InputController() {
			@Override
			public boolean keyDown(int keycode) {
				if(keycode == Keys.ESCAPE)
					((Game) Gdx.app.getApplicationListener()).setScreen(new LevelMenu());
				return true;
			}
		});
		
		// BALL
		// Body definition
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(0,1); // x = 0, y is set to 1 meter 

		// Ball Shape
		CircleShape ballShape = new CircleShape();
		ballShape.setRadius(.5f);

		// Fixture definition
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = ballShape;
		fixtureDef.density = 2.5f; // kilograms in one square meter
		fixtureDef.friction = .25f; // 0-1; 1 means can't be moved. 0 is no friction
		fixtureDef.restitution = .8f; // how bouncy the object is.. i.e. dropped from a height of 1m, at .8f it'll jump up 80cm. 

		// Attach it to the world.
		world.createBody(bodyDef).createFixture(fixtureDef);
		
		ballShape.dispose();
		
		// GROUND
		// Body Definition
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(0,0); 	// put it on the ground
		
		// Shape
		ChainShape groundShape = new ChainShape();
		groundShape.createChain(new Vector2[] {new Vector2(-50,0), new Vector2(50,-5) });
		
		// Fixture
		fixtureDef.shape = groundShape;
		fixtureDef.friction = .5f; 
		fixtureDef.restitution = 0;
		
		world.createBody(bodyDef).createFixture(fixtureDef);
		
		groundShape.dispose();
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		world.dispose();
		debugRenderer.dispose();
		
	}

}
