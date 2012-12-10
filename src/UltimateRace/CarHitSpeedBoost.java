package UltimateRace;

import java.util.ArrayList;
import java.awt.Rectangle;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.CollisionHandler;

public class CarHitSpeedBoost implements CollisionHandler {
	
	Car car;
	RaceTrack raceTrack;
	BodyLayer<SpeedBoost> boostLayer;
	SpeedBoost booster;
	ArrayList<PolyHolder> road;
	Rectangle sbRec;


	public CarHitSpeedBoost(Car c, BodyLayer<SpeedBoost> b, RaceTrack rt) {
		boostLayer = b;
		car = c;
		raceTrack = rt;
		road = rt.road;
	}
	
	@Override
	public void findAndReconcileCollisions() {
		for(int i=0; i < boostLayer.size(); i++) {
			booster = boostLayer.get(i);
			if (booster.isActive()) {
				sbRec = new Rectangle((int)booster.getPosition().getX(),(int)booster.getPosition().getY(),
						               booster.getWidth(),booster.getHeight());
				if (sbRec.contains(car.getCenterPosition().getX(),car.getCenterPosition().getY())) {
					raceTrack.startFireTimer = true;
					//System.out.println("Just hit a booster");
				}
			}
		}
		
	}

}
