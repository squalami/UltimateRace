package UltimateRace;

import java.awt.geom.AffineTransform;

import jig.engine.GameFrame;
import jig.engine.RenderingContext;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class SpeedBoost extends VanillaAARectangle{
	public SpeedBoost(String sprite, GameFrame gameframe) {
		super(sprite);
		position = new Vector2D((640/2 - 120),480-70);
	}
	


	@Override
	public void update(long deltaMs) {
		// TODO Auto-generated method stub
		
	}
	
	public void render(final RenderingContext rc) {
		if (!active) {
			return;
		}

		AffineTransform at = AffineTransform.getTranslateInstance(position
				.getX(), position.getY());
		super.render(rc, at);

		if (renderMarkup) {
			imgBoundingRectangle.get(0).render(rc, at);
		}
	}
	
}
