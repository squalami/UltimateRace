package UltimateRace;

/**
 * UltimateRace: Car Object 
 * F. Doan
 * 
 */

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;


public class Car extends VanillaAARectangle {
	
	public enum State { STRAIGHT, UP, DOWN }
	
	double speed = 0;
	Vector2D startPos;
	RoadSegment curSegment;
	int lap = 0;
	long elapsedTime = 0;
	
	public State state = State.STRAIGHT;
	
	public Car(String sprite, Vector2D pos) {
		super(sprite);
		// TODO Auto-generated constructor stub
		position = pos;
		startPos = pos;

	}

	@Override
	public void update(long deltaMs) {
		updateFrames(deltaMs);
				
	}

	private void updateFrames(long deltaMs) {
		
		if (state == State.UP) {
			setFrame(1);
		} else if (state == State.DOWN) {
			setFrame(2);
		} else {
			setFrame(0);
		}

	}
	

}
