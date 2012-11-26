package UltimateRace;

/**
 * Ultimate Race
 * 
 * F.Doan
 */

import java.util.ArrayList;
import jig.engine.GameFrame;
import jig.engine.RenderingContext;
import jig.engine.j2d.J2DShapeEngine;
import jig.engine.physics.vpe.VanillaAARectangle;

public class Rumbles extends VanillaAARectangle {
	
	public ArrayList<PolyHolder> rumbles = new ArrayList<PolyHolder>();
	J2DShapeEngine shapeEngine;

	public Rumbles(String rsc, GameFrame gameframe) {
		super(rsc);
		// TODO Auto-generated constructor stub
		shapeEngine = new J2DShapeEngine(gameframe);
	}

	@Override
	public void update(long deltaMs) {
		// TODO Auto-generated method stub
		
	}

    @Override
	public void render(RenderingContext rc) {
		if (rumbles.size() > 0) {
			for (PolyHolder p: rumbles) {
				shapeEngine.renderFilledPolygon(rc, p.C, p.points);
			}
		}

	}
}
