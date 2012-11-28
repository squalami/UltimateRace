package UltimateRace;

import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.CollisionHandler;

public class CarHitGrass implements CollisionHandler {
	
	Car car;
	BodyLayer<Grass> grass;

	public CarHitGrass(Car c, BodyLayer<Grass> g) {
		grass = g;
		car = c;
	}
	
	@Override
	public void findAndReconcileCollisions() {
		// TODO Auto-generated method stub
		for(int i=0; i < grass.size(); i++) {
			
		}
		
	}

}
