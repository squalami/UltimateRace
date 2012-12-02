package UltimateRace;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;

import jig.engine.FontResource;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.ViewableLayer;

public class GameUI implements ViewableLayer {
	
	Car car;
	FontResource redFont = ResourceFactory.getFactory().getFontResource(new Font("Sans Serif", Font.BOLD, 14),
			Color.red, null );
	
	public GameUI(Car c) {
		// TODO Auto-generated constructor stub
		car = c;
	}

	@Override
	public void render(RenderingContext rc) {
		int speed = (int)(car.speed * 45);
		redFont.render("Position:  "+car.RacePos, rc, 
				AffineTransform.getTranslateInstance(10,20));
		redFont.render("Lap:  "+car.lap, rc, 
				AffineTransform.getTranslateInstance(150,20));
		redFont.render("Time Elapsed (s):  "+car.elapsedTime, rc, 
				AffineTransform.getTranslateInstance(250,20));
		redFont.render("Speed(mph):  "+speed, rc, 
				AffineTransform.getTranslateInstance(470,20));
		
	}
	
	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setActivation(boolean a) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void update(long deltaMs) {
		// TODO Auto-generated method stub
		
	}

}
