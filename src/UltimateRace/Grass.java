package UltimateRace;
/**
 * Ultimate Race
 * Grass.java
 * 
 * F.Doan
 */

import java.util.ArrayList;
import jig.engine.GameFrame;
import jig.engine.RenderingContext;
import jig.engine.j2d.J2DShapeEngine;
import jig.engine.physics.vpe.VanillaAARectangle;


public class Grass extends VanillaAARectangle {

	public ArrayList<PolyHolder> grasses = new ArrayList<PolyHolder>();
	J2DShapeEngine shapeEngine;
	
	public Grass(String rsc, GameFrame gameframe) {
		super(rsc);
		// TODO Auto-generated constructor stub
		shapeEngine = new J2DShapeEngine(gameframe);
	}

	@Override
	public void update(long deltaMs) {
		// TODO Auto-generated method stub
		//System.out.println("grass array:"+grasses.size());
		
	}

    @Override
	public void render(RenderingContext rc) {
		if (grasses.size() > 0) {
			for (PolyHolder p: grasses) {
				shapeEngine.renderFilledPolygon(rc, p.C, p.points);
			}
		}

	}

}
