package UltimateRace;


import jig.engine.physics.vpe.CollisionHandler;


import java.awt.Rectangle;

public class CarHitRumbles implements CollisionHandler {
	
	Car car;
	Rumbles rumble;
	Rectangle carRectangle;
	RoadSegment segment;
	
	public CarHitRumbles (Car c, Rumbles r) {
		car = c;
		rumble = r;
		
	}

	@Override
	public void findAndReconcileCollisions() {

	}

}
