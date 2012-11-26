package UltimateRace;
/**
 * Ultimate Race
 * 
 * F.Doan
 */

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class Background extends VanillaAARectangle {

	public Background(String sprite) {
		super(sprite);
		position = new Vector2D(-5,-5);

	}

	@Override
	public void update(long deltaMs) {
		// TODO Auto-generated method stub
		//setFrame(0);
		
	}

}
