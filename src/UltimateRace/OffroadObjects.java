package UltimateRace;
/**
 * Ultimate Race
 * F.Doan
 */

import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import jig.engine.RenderingContext;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class OffroadObjects extends VanillaAARectangle{

	RaceTrack raceTrack;
	RoadSegment segment;
	double xPos;
	double yPos;
	double scaleFactor = 1;
	int locationIndex;
	boolean leftSide = false;
	ArrayList<PolyHolder> road;
	PolyHolder ph;

	public OffroadObjects(String sprite, RaceTrack rt, int i, boolean b) {
		super(sprite);
		// TODO Auto-generated constructor stub
		//System.out.println(" obj sprite"+sprite);
		raceTrack = rt;
		locationIndex = i;
		leftSide = b;
		active = false;
		road = rt.road;
		updateObject();
	}

	@Override
	public void update(long deltaMs) {
		updateObject();
	}

	public void updateObject() {
		int rs = road.size();
		if (rs > 0) {
			int bi = road.get(0).segmentIndex;
			int ti = road.get(rs-1).segmentIndex;
			
			if (locationIndex >= bi && locationIndex <= ti) {
				
				double dn = road.get(0).w * 0.2;
				double tn = road.get(rs-1).w;
				double ws = this.getWidth();
				//System.out.println("bottom index: "+bi+", top index: "+ti+", check cur index: "+locationIndex+", scaleFactor: "+scaleFactor);
				
				//PolyHolder p;
				for (PolyHolder p: road) {
					
					//if (locationIndex < 150) System.out.println(" p.segmentIndex: "+p.segmentIndex+" vs. locationIndex: "+locationIndex);
					if (locationIndex == p.segmentIndex) {
						if (p.xL < 0 || p.xR < 0) continue;
						scaleFactor = p.w /dn;
						yPos = p.yB;
						double width = (25 + ws) * scaleFactor;
						if (leftSide) {
							xPos = p.xL - width * 2.5;
						} else {
							xPos = p.xR + width * 0.9;
						}
						if (xPos < 0) continue;
						active = true;
						this.setCenterPosition(new Vector2D(xPos,yPos));
						//System.out.println("xPos: "+xPos+", yPos: "+yPos+", p.xL: "+p.xL+", p.xR:"+p.xR);

					}
				}
			} else {
				active = false;
			}
		}

	}
	
	@Override
	public void render(RenderingContext rc) {			
		if (active) {
			AffineTransform at = AffineTransform.getTranslateInstance(xPos,yPos);
			at.scale(scaleFactor,scaleFactor);
			super.render(rc,at);
			if(renderMarkup){
				imgBoundingRectangle.get(0).render(rc, at);
			}
		}
	}


}
