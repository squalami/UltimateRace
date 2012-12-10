package UltimateRace;
/**
 * F.Doan
 */

import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.CollisionHandler;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class CarHitOffroadObjects implements CollisionHandler {
	
	Car car;
	RaceTrack raceTrack;
	BodyLayer<OffroadObjects> offroadLayer;
	ArrayList<PolyHolder> road;
	PolyHolder p;
	
	public CarHitOffroadObjects (Car c,BodyLayer<OffroadObjects> o, RaceTrack rt) {
		car = c;
		raceTrack = rt;
		road = rt.road;
		offroadLayer = o;
	}

	@Override
	public void findAndReconcileCollisions() {
		int rs = road.size();
		if (rs > 0) {
			int bi = road.get(0).segmentIndex;
			int ti = road.get(rs-1).segmentIndex;
			Rectangle2D carRec = car.getBoundingBox();
			for (int i = 0; i < offroadLayer.size(); i++ ) {
				OffroadObjects obj = offroadLayer.get(i);
				if (obj.isActive() && obj.locationIndex >= bi && obj.locationIndex <= ti) {
					Rectangle2D objRec = obj.getBoundingBox();
					if (carRec.intersects(objRec)) {
						raceTrack.carHitObject = true;
					}
				}
			}
		}
	}

}
