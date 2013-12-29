package com.gan.blackpoint.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;
import com.badlogic.gdx.utils.Array;
import com.gan.blackpoint.InputController;

public class Play implements Screen {

	private World world;
	private Box2DDebugRenderer debugRenderer;
	private OrthographicCamera camera;
	private Sprite boxSprite;
	private SpriteBatch batch;
	private Body box;
	private Array<Body> tmpBodies = new Array<Body>();
	
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
		box.applyForceToCenter(movement, true);
		box.applyAngularImpulse(spin, true);

		camera.position.set(box.getPosition().x,  box.getPosition().y, 0);
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

		Gdx.input.setInputProcessor(new InputController() {
			@Override
			public boolean keyDown(int keycode) {
				switch(keycode) {
					case Keys.ESCAPE:
						((Game) Gdx.app.getApplicationListener()).setScreen(new LevelMenu());
						break;
					case Keys.W:
						movement.y = SPEED;
						break;
					case Keys.A:
						movement.x = -SPEED;
						break;
					case Keys.S:
						movement.y = -SPEED;
						break;
					case Keys.D:
						movement.x = SPEED;
						break;
					case Keys.Q:
						spin = ANGULAR_MOMENTUM;
						break;
					case Keys.E:
						spin = -ANGULAR_MOMENTUM;
				}
				return true;	
			}
			@Override
			public boolean keyUp(int keycode) {
				switch(keycode) {
				case Keys.W:
				case Keys.S:	
					movement.y = 0;
					break;
				case Keys.A:
				case Keys.D:	
					movement.x = 0;
					break;
				case Keys.Q:
				case Keys.E:
					spin = 0;
					break;
			}
			return true;	
			}
			
			@Override
			public boolean scrolled(int amount) {
				if(amount == -1 && camera.zoom <= MIN_ZOOM) {
					camera.zoom = MIN_ZOOM;
				} else {
					camera.zoom += amount / PIXELS_TO_METERS;
				}
				return true;
			}
		});
		
		BodyDef bodyDef = new BodyDef();
		FixtureDef fixtureDef = new FixtureDef();

		// BOX
		// body definition
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(2.25f,10);
		
		// Box Shape
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(.5f, 1); // half height, and width.. so the box will be 1m x 2m. 
		
		// box fixture
		fixtureDef.shape = boxShape;
		fixtureDef.friction = .25f;
		fixtureDef.restitution = .1f;
		fixtureDef.density = 5;		// 5kg in a square meter
				
		box = world.createBody(bodyDef);
		box.createFixture(fixtureDef);

		boxSprite = new Sprite(new Texture("img/player.png"));
		boxSprite.setSize(2, 2);
		boxSprite.setOrigin(boxSprite.getWidth() / 2, boxSprite.getHeight() / 2);
		box.setUserData(boxSprite);

		// Ball Shape
		CircleShape ballShape = new CircleShape();
		ballShape.setRadius(.5f);
		ballShape.setPosition(new Vector2(0, 1.5f));
		
		// Ball Fixture definition
		fixtureDef.shape = ballShape;
		fixtureDef.density = 3.5f; // kilograms in one square meter
		fixtureDef.friction = .25f; // 0-1; 1 means can't be moved. 0 is no friction
		fixtureDef.restitution = .8f; // how bouncy the object is.. i.e. dropped from a height of 1m, at .8f it'll jump up 80cm. 
		
		// Attach ball it to the world.
		box.createFixture(fixtureDef);
		
		ballShape.dispose();
		
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
		
		Body ground = world.createBody(bodyDef);
		ground.createFixture(fixtureDef);
		
		// other box
		bodyDef.position.y = 7;
		
		PolygonShape otherBoxShape = new PolygonShape();
		otherBoxShape.setAsBox(.25f, .25f);
		
		fixtureDef.shape = otherBoxShape;
		
		Body otherBox = world.createBody(bodyDef);
		otherBox.createFixture(fixtureDef);
		otherBoxShape.dispose();
		
		// distance joint between other box and box
		DistanceJointDef distanceJointDef = new DistanceJointDef();
		distanceJointDef.bodyA = otherBox;
		distanceJointDef.bodyB = box;
		distanceJointDef.length = 5;
		distanceJointDef.localAnchorB.set(0,0);
		
		world.createJoint(distanceJointDef);
		
		// rope joint between ground and player
		RopeJointDef ropeJointDef = new RopeJointDef();
		ropeJointDef.bodyA = ground;
		ropeJointDef.bodyB = box;
		ropeJointDef.maxLength = 4;
		ropeJointDef.localAnchorA.set(0,0);
		ropeJointDef.localAnchorB.set(0,0); 
		
//		world.createJoint(ropeJointDef);
		
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
