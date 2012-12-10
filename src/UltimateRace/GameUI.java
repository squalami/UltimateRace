package UltimateRace;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.util.List;
import jig.engine.FontResource;
import jig.engine.ImageResource;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.ViewableLayer;

public class GameUI implements ViewableLayer {
	Car car;
	
	List<ImageResource> bg2 = ResourceFactory.getFactory().getFrames(
			Game.SPRITE_SHEET + "#bg2");
	List<ImageResource> n1 = ResourceFactory.getFactory().getFrames(
			Game.SPRITE_SHEET + "#number1");
	List<ImageResource> n2 = ResourceFactory.getFactory().getFrames(
			Game.SPRITE_SHEET + "#number2");
	List<ImageResource> n3 = ResourceFactory.getFactory().getFrames(
			Game.SPRITE_SHEET + "#number3");
	List<ImageResource> go = ResourceFactory.getFactory().getFrames(
			Game.SPRITE_SHEET + "#go");
	List<ImageResource> youWin = ResourceFactory.getFactory().getFrames(
			Game.SPRITE_SHEET + "#youwin");

	FontResource redFont = ResourceFactory.getFactory().getFontResource(new Font("Sans Serif", Font.BOLD, 14),
			Color.red, null );
	FontResource whiteBig = ResourceFactory.getFactory().getFontResource(new Font("Sans Serif", Font.BOLD, 18),
			Color.white, null );
	FontResource whiteHeader = ResourceFactory.getFactory().getFontResource(new Font("Sans Serif", Font.BOLD, 14),
			Color.white, null );
	FontResource whiteFont = ResourceFactory.getFactory().getFontResource(new Font("Sans Serif", Font.BOLD, 12),
			Color.white, null );
	FontResource whiteReg = ResourceFactory.getFactory().getFontResource(new Font("Sans Serif", Font.BOLD, 11),
			Color.white, null );
	
	long time = 0;
	int displayCount = 0;
	boolean count1 = false;
	boolean count2 = false;
	boolean count3 = false;
	boolean displayGo = false;

	public GameUI(Car c) {
		// TODO Auto-generated constructor stub
		car = c;
	}

	@Override
	public void render(RenderingContext rc) {
		if (car.isWin) {
			int loc = 4*40;
			youWin.get(0).render(rc, 
					AffineTransform.getTranslateInstance(loc,loc-15));
		}
		if (Game.startGame) {
			int X = 8 * 40 - 20;
			int Y = 6 * 40 - 25;
			if (!count1 && !count2 && !count3 && !displayGo) count1 = true;
			
			if (count1) {
				n1.get(0).render(rc, 
						AffineTransform.getTranslateInstance(X,Y));
			}
			if (count2) {
				n2.get(0).render(rc, 
						AffineTransform.getTranslateInstance(X-7,Y));
			}
			if (count3) {
				n3.get(0).render(rc, 
						AffineTransform.getTranslateInstance(X-5,Y));
			}
			if (displayGo) {
				go.get(0).render(rc, 
						AffineTransform.getTranslateInstance(X-15,Y));				
			}
		}

		if (Game.displayNextLevel) {
			for(int c=4; c < 12; c++) {
				for (int r=5; r < 8; r++) {
					bg2.get(0).render(rc, 
							AffineTransform.getTranslateInstance(c*40,r*40));	
					
				}
			}
			whiteBig.render("Press F1 to play next level", rc, AffineTransform.getTranslateInstance(6*40-35,6*40));
		}
		
		if (Game.displayMenu) {
			int X = 160;
			for(int c=3; c < 13; c++) {
				for (int r=2; r < 8; r++) {
					bg2.get(0).render(rc, 
							AffineTransform.getTranslateInstance(c*40,r*40));	
					
				}
			}
			whiteHeader.render("Key Stroke Tutorial:", rc, AffineTransform.getTranslateInstance(6*40,3*30));
			whiteFont.render("1. Arrow keys: to control the car.",rc, AffineTransform.getTranslateInstance(X,4*30));
			whiteFont.render("2. Space-Bar: check out car engine performance.",rc, AffineTransform.getTranslateInstance(X,5*30));			
			whiteFont.render("3. Press F1 to start.",rc, AffineTransform.getTranslateInstance(X,6*30));
			whiteBig.render("A Game by:",rc, AffineTransform.getTranslateInstance(X+80,7*30));
			whiteBig.render("Fred Doan - Brian Lamb - Perry Miller",rc, AffineTransform.getTranslateInstance(X,8*30));
			whiteBig.render("WSU - Fall 2012.",rc, AffineTransform.getTranslateInstance(X+90,9*30));
		}

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
		if (count1 || count2 || count3 || displayGo) {
			time++;
		}
		
		if (time > 115 && count1) {
			count1 = false;
			time = 0;
			count2 = true;
		}
		if (time > 115 && count2) {
			count2 = false;
			time = 0;
			count3 = true;
		}
		if (time > 90 && count3) {
			count3 = false;
			time = 0;
			displayGo = true;
		}
		if (time > 45 && displayGo) {
			displayGo = false;
			time = 0;
			Game.startGame = false;
			Game.gameIsRun = true;
			Game.iniTime = System.currentTimeMillis();
		}
	}

}
