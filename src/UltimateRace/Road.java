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

public class Road extends VanillaAARectangle {

	public ArrayList<PolyHolder> road = new ArrayList<PolyHolder>();
	int curIndex = 0;
	J2DShapeEngine shapeEngine;
	boolean update = false;

	public Road(String rsc, GameFrame gameframe) {
		super(rsc);
		// TODO Auto-generated constructor stub
		shapeEngine = new J2DShapeEngine(gameframe);
	}

	@Override
	public void update(long deltaMs) {
		// TODO Auto-generated method stub
		update = true;
	}

    @Override
	public void render(RenderingContext rc) {
		if (road.size() > 0 && update) {
			for (PolyHolder p: road) {
				shapeEngine.renderFilledPolygon(rc, p.C, p.points);
				curIndex = p.segmentIndex;
			}
		}
        update = false;
	}
}
