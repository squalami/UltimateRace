/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UltimateRace;

/**
 *
 * @author brian
 */
import java.awt.geom.Rectangle2D;
import jig.engine.physics.vpe.CollisionHandler;


public class CarHitCar implements CollisionHandler {
    Car car1;
    Car car2;
    double dx = 0.005;
    double centrifugal  = 0.3;   
    RaceTrack track;
    
    public CarHitCar(Car c1, Car c2,RaceTrack r1){
        car1=c1;
        car2=c2;
        track=r1;
    }
    @Override
    public void findAndReconcileCollisions() {
       Rectangle2D car1Box=car1.getBoundingBox();
       Rectangle2D car2Box=car2.getBoundingBox();
       if(car2.isActive()){
            if(car1Box.intersects(car2Box)){
                //determine if the which direction the car will be pushed
                if(car1.getPosition().getX()<car2.getPosition().getX()){
                    track.carX=track.carX - dx - (dx * car1.speed * centrifugal);
                }
                else if(car1.getPosition().getX()>car2.getPosition().getX()){
                    track.carX=track.carX+dx+(dx*car1.speed*centrifugal);
                }
                else{
                    double p=Math.random();
                    if(p>.5){
                        track.carX=track.carX - dx - (dx * car1.speed * centrifugal);
                    }
                    else{
                         track.carX=track.carX+dx+(dx*car1.speed*centrifugal);
                    }
                }
                if(car1.getPosition().getY()>car2.getPosition().getY()){
                    car1.speed=car1.speed+0.005;
                }
                else{
                    car1.speed=car1.speed-0.005;
                    if(car1.speed<0){
                        car1.speed=0;
                    }
                }
           }
       }
    }
    
}
