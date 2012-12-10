package UltimateRace;
/**
 * F.Doan
 */
import java.awt.geom.Rectangle2D;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.CollisionHandler;

public class CarHitSpeedBoost implements CollisionHandler {
	
	Car car;
	RaceTrack raceTrack;
	BodyLayer<SpeedBoost> boostLayer;
	SpeedBoost booster;


	public CarHitSpeedBoost(Car c, BodyLayer<SpeedBoost> b, RaceTrack rt) {
		boostLayer = b;
		car = c;
		raceTrack = rt;
	}
	
	@Override
	public void findAndReconcileCollisions() {
		for(int i=0; i < boostLayer.size(); i++) {
			booster = boostLayer.get(i);
			if (booster.isActive()) {
				Rectangle2D sbRec = booster.getBoundingBox();
				Rectangle2D carRec = car.getBoundingBox();
				if (carRec.contains(sbRec.getCenterX(),sbRec.getCenterY())) {
					raceTrack.startFireTimer = true;
				}
			}
		}
		
	}

}
