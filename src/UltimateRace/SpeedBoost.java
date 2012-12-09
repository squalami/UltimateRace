package UltimateRace;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import jig.engine.RenderingContext;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class SpeedBoost extends VanillaAARectangle{

	double xPos;
	double yPos;
	double scaleFactor = 1;
	int locationIndex;
	ArrayList<PolyHolder> road;

	public SpeedBoost(String sprite, RaceTrack rt, int i) {
		super(sprite);
		locationIndex = i;
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
			
			if (locationIndex > bi && locationIndex < ti) {
				
				double dn = road.get(0).w;
				for (PolyHolder p: road) {
					
					if (locationIndex == p.segmentIndex) {
						scaleFactor = p.w /dn;
						yPos = p.yB;
						xPos = p.w/2 + p.xL ;
						active = true;
						this.setCenterPosition(new Vector2D(xPos,yPos));

					}
				}
			} else {
				active = false;
			}
		}

	}
	
	public void render(final RenderingContext rc) {
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
