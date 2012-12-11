/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UltimateRace;

/**
 *
 * @author brian
 * mod by F.Doan
 */
import java.awt.geom.Rectangle2D;
import jig.engine.physics.vpe.CollisionHandler;


public class CarHitCar implements CollisionHandler {
	Car car1;
	Car car2;
	double cx1;
	double cy1;
	double cx2;
	double cy2;

	public CarHitCar(Car c1, Car c2){
		car1=c1;
		car2=c2;
	}
	@Override
	public void findAndReconcileCollisions() {
		if(car1.isActive() && car2.isActive()){
			Rectangle2D car1Box=car1.getBoundingBox();
			Rectangle2D car2Box=car2.getBoundingBox();
			if(car1Box.intersects(car2Box)){
				car1.carCrash = true;
				car2.carCrash = true;
				//determine if the which direction the car will be pushed
				cx1 = car1Box.getCenterX();
				cy1 = car1Box.getCenterY();
				cx2 = car2Box.getCenterX();
				cy2 = car2Box.getCenterY();
				double xDif = Math.abs(cx1-cx2);
				double yDif = Math.abs(cy1-cy2);
				car1.crashXpos = xDif;
				car1.crashYpos = yDif;
				car2.crashXpos = xDif;
				car2.crashYpos = yDif;
				if (cx1 < cx2) {
					if (cy1 == cy2) {
						car1.crashDir = Car.CrashDir.LEFT;
						car2.crashDir = Car.CrashDir.RIGHT;
					} else if (cy1 < cy2) {
						car1.crashDir = Car.CrashDir.DOWNLEFT;
						car2.crashDir = Car.CrashDir.UPRIGHT;
					} else {
						car1.crashDir = Car.CrashDir.UPLEFT;
						car2.crashDir = Car.CrashDir.DOWNRIGHT;
					}
				} else if (cx1 == cx2) {
					if (cy1 == cy2) {
                        // should never happen?
					} else if (cy1 < cy2) {
						car1.crashDir = Car.CrashDir.UP;
						car2.crashDir = Car.CrashDir.DOWN;
					} else {
						car1.crashDir = Car.CrashDir.DOWN;
						car2.crashDir = Car.CrashDir.UP;
					}

				} else { // cx1 > cx2
					if (cy1 == cy2) {
						car2.crashDir = Car.CrashDir.LEFT;
						car1.crashDir = Car.CrashDir.RIGHT;
					} else if (cy1 < cy2) {
						car2.crashDir = Car.CrashDir.DOWNLEFT;
						car1.crashDir = Car.CrashDir.UPRIGHT;
					} else {
						car2.crashDir = Car.CrashDir.UPLEFT;
						car1.crashDir = Car.CrashDir.DOWNRIGHT;
					}
				}

			}
		}
	}

}
