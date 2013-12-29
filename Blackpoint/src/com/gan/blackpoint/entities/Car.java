package com.gan.blackpoint.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;

public class Car {
	private Body chassis, leftWheel, rightWheel;
	private WheelJoint leftAxis, rightAxis;
	
	
	public Car(World world, FixtureDef chassisFixtureDef, FixtureDef wheelFixtureDef, float x, float y, float width, float height) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(x,y);
		
		// chassis
		PolygonShape chassisShape = new PolygonShape();
		chassisShape.set(new float[] {-width / 2, -height / 2, width /2 , -height / 2, width / 2 * .4f, height / 2,  -width / 2 * .8f, height / 2 * .8f }); //counter clockwise order
		chassisFixtureDef.shape = chassisShape;
		
		chassis = world.createBody(bodyDef);
		chassis.createFixture(chassisFixtureDef);
		
		// Wheels
		CircleShape wheelShape = new CircleShape();
		wheelShape.setRadius(height / 3.5f);
		
		wheelFixtureDef.shape = wheelShape;
		
		leftWheel = world.createBody(bodyDef);
		leftWheel.createFixture(wheelFixtureDef);
		
		rightWheel = world.createBody(bodyDef);
		rightWheel.createFixture(wheelFixtureDef);
		
		// Axels
		WheelJointDef axisDef = new WheelJointDef();
		axisDef.bodyA = chassis;
		axisDef.bodyB = leftWheel;
		axisDef.localAnchorA.set(-width /2 * .75f + wheelShape.getRadius(), -height / 2 * 1.25f);
		axisDef.frequencyHz = chassisFixtureDef.density; 
		axisDef.localAxisA.set(Vector2.Y);
		
		leftAxis = (WheelJoint) world.createJoint(axisDef);
		
		// right
		axisDef.bodyB = rightWheel;
		axisDef.localAnchorA.x *= -1;
		rightAxis = (WheelJoint) world.createJoint(axisDef);
		
		
		
		
		
		
		
	}
	
}
