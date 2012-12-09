package UltimateRace;


import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.CollisionHandler;


import java.awt.Rectangle;

public class CarHitOffroadObjects implements CollisionHandler {
	
	Car car;
	RaceTrack raceTrack;
	BodyLayer<OffroadObjects> offroadLayer;
	Rectangle carRectangle;
	RoadSegment segment;
	
	public CarHitOffroadObjects (Car c,BodyLayer<OffroadObjects> o, RaceTrack rt) {
		car = c;
		raceTrack = rt;
		
	}

	@Override
	public void findAndReconcileCollisions() {

	}

}
