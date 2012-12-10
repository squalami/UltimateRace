package UltimateRace;
/**
 * F.Doan
 */

import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.CollisionHandler;
import java.awt.geom.Rectangle2D;

public class CarHitOil implements CollisionHandler {
	
	Car car;
	RaceTrack raceTrack;
	BodyLayer<Oil> oilLayer;
	Oil sleekOil;


	public CarHitOil(Car c, BodyLayer<Oil> b, RaceTrack rt) {
		oilLayer = b;
		car = c;
		raceTrack = rt;
	}

	@Override
	public void findAndReconcileCollisions() {
		for(int i=0; i < oilLayer.size(); i++) {
			sleekOil = oilLayer.get(i);
			if (sleekOil.isActive()) {
				Rectangle2D oilRec = sleekOil.getBoundingBox();
				Rectangle2D carRec = car.getBoundingBox();
				if (oilRec.intersects(carRec)) {
					if (oilRec.getCenterX() > carRec.getCenterX())
					    raceTrack.skidOffleft = true;
					else
						raceTrack.skidOffright = true;
				}
			}
		}
	}

}
