package UltimateRace;

import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.CollisionHandler;

public class CarHitRumbles implements CollisionHandler {
	
	Car car;
	BodyLayer<Rumbles> rumble;
	
	public CarHitRumbles (Car c, BodyLayer<Rumbles> r) {
		car = c;
		rumble = r;
	}

	@Override
	public void findAndReconcileCollisions() {
		// TODO Auto-generated method stub
		for (int i=0; i < rumble.size(); i++) {
			
		}
		
	}

}
