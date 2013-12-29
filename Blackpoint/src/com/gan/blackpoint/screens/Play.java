package com.gan.blackpoint.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.gan.blackpoint.InputController;
import com.gan.blackpoint.entities.Car;

public class Play implements Screen {

	private World world;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera camera;
	private Sprite boxSprite;
	private SpriteBatch batch;
	private Body box;
	private Array<Body> tmpBodies = new Array<Body>();
	
	private Car car;
	
	private final float PIXELS_TO_METERS = 15f,			// how many pixels to a meter
						TIME_STEP = 1 / 60f, 			// 60 fps
						SPEED = 500f,					// speed constant
						MIN_ZOOM = .25f,				// How far in should we be able to zoom
						ANGULAR_MOMENTUM = .50f;
	
	private final int 	VELOCITY_ITERATIONS = 8,		// copied from box2d example
						POSITION_ITERATIONS = 3;		// copied from box2d example
	
	private Vector2 movement = new Vector2();
	private float spin = 0;
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0,0,0,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
//		car.applyForceToCenter(movement, true);
//		box.applyAngularImpulse(spin, true);

		camera.position.set(car.getChassis().getPosition().x,  car.getChassis().getPosition().y, 0);

		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		world.getBodies(tmpBodies);
		for(Body body : tmpBodies) {
			if(body.getUserData() instanceof Sprite) {
				Sprite sprite = (Sprite) body.getUserData();
				sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
				sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
				sprite.draw(batch);
			}
		}
		batch.end();
		
		debugRenderer.render(world,  camera.combined);
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width / PIXELS_TO_METERS;
		camera.viewportHeight = height / PIXELS_TO_METERS;
	}

	@Override
	public void show() {
		world = new World(new Vector2(0, -9.81f), true);
		debugRenderer = new Box2DDebugRenderer();
		batch = new SpriteBatch();

		camera = new OrthographicCamera();

		
		BodyDef bodyDef = new BodyDef();
		FixtureDef fixtureDef = new FixtureDef();
		FixtureDef wheelFixtureDef = new FixtureDef();
		
		// Car 
		fixtureDef.density = 5;
		fixtureDef.friction = .4f;
		fixtureDef.restitution = .3f;
		
		wheelFixtureDef.density = fixtureDef.density - .5f;
		wheelFixtureDef.friction = 1;
		wheelFixtureDef.restitution = .4f;
		
		car = new Car(world, fixtureDef, wheelFixtureDef, 0, 3, 3, 1.5f);
		
		Gdx.input.setInputProcessor(new InputMultiplexer(new InputController() {
			@Override
			public boolean keyDown(int keycode) {
				switch(keycode) {
					case Keys.ESCAPE:
						((Game) Gdx.app.getApplicationListener()).setScreen(new LevelMenu());
						break;
				}
				return false;	
			}
			
			@Override
			public boolean scrolled(int amount) {
				if(amount == -1 && camera.zoom <= MIN_ZOOM) {
					camera.zoom = MIN_ZOOM;
				} else {
					camera.zoom += amount / PIXELS_TO_METERS;
				}
				return false;
			}
		}, car));
		
		
		// GROUND
		// Body Definition
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(0,0); 	// put it on the ground
		
		// Shape
		ChainShape groundShape = new ChainShape();
		groundShape.createChain(new Vector2[] {new Vector2(-200,0), new Vector2(200,0) });
		
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
