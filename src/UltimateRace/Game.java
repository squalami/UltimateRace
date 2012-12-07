package UltimateRace;
/**
 * CS447/547 Project 2 by F.Doan
 * 
 */

import UltimateRace.Car.State;
import java.util.logging.Level;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.audio.jsound.AudioStream;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.VanillaPhysicsEngine;
import jig.engine.util.Vector2D;
import java.awt.event.KeyEvent;
import java.util.Scanner;
import jig.engine.audio.jsound.AudioClip;



public class Game extends StaticScreenGame {

	static final int WORLD_WIDTH = 640;
	static final int WORLD_HEIGHT = 480;
	static int level = 1;
	static boolean turnLeft = false;
	static boolean turnRight = false;
	static boolean speedUp = false;
	static boolean applybreak = false;
	static boolean runUpdate = false;
	static boolean gameIsRun = false;
	static boolean standRevup = false;
	
	static long iniTime = 0;

	static final String XMLFILE = "resources/sprites.xml";
	static final String SPRITE_SHEET = "resources/sprites.png";

	VanillaPhysicsEngine physics;
	AudioStream music;

	//new stuff needed for more than 2 playe multi
	Car TrueCar;        //since we have may cars now we will use this variable to refer to the cleints actual car
	int carNum;
        Car otherCar1;      //These are needed to simplyfy code in networking. see networking code bellow
        Car otherCar2;
       
	Car car1;
	Car car2;
	Car car3;           

	RaceTrack raceTrack;
	Road road;
	Grass grass;
	Rumbles rumbles;
	Background background;
	SpeedBoost boost;

	static Vector2D car1Pos;   // used for network race track update
	static Vector2D car2Pos;

	BodyLayer<Car> car1Layer;
	BodyLayer<Car> car2Layer;
	BodyLayer<Car> car3Layer;
	
	BodyLayer<RaceTrack> rtLayer;
	BodyLayer<RaceTrack> rumbleLayer;
	BodyLayer<Grass> grassLayer;
	BodyLayer<Road> roadLayer;
	BodyLayer<Rumbles> rbLayer;
	BodyLayer<SpeedBoost> boostLayer;

	BodyLayer<Background> backgroundLayer;
	NetworkC GameNet;
	boolean isServ;
	boolean isNet=false;
	String IP;
	RoadSegment carRoad;
	CarHitRumbles hitRumbles;
	CarHitGrass hitGrass;

	VanillaPhysicsEngine checkCollisions = new VanillaPhysicsEngine();
	

	public Game() {
		super(WORLD_WIDTH, WORLD_HEIGHT, false);

		physics = new VanillaPhysicsEngine();
		//music = new AudioStream("resources/RhythmSphere3.mp3");
		ResourceFactory.getFactory().loadSheet(SPRITE_SHEET, XMLFILE);

		background = new Background(SPRITE_SHEET + "#background");
		backgroundLayer = new AbstractBodyLayer.IterativeUpdate<Background>();
		backgroundLayer.add(background);
		gameObjectLayers.add(backgroundLayer);

		road = new Road(SPRITE_SHEET + "#trans",gameframe);
		rumbles = new Rumbles(SPRITE_SHEET + "#trans",gameframe);
		grass = new Grass(SPRITE_SHEET + "#trans",gameframe);
		
		boost = new SpeedBoost(SPRITE_SHEET + "#boost", gameframe);

		car1 = new Car(SPRITE_SHEET + "#redCar",
				new Vector2D((WORLD_WIDTH/2 - 180),WORLD_HEIGHT-70),
				SPRITE_SHEET + "#fire",
				SPRITE_SHEET + "#smoke",
				SPRITE_SHEET + "#cutgrass");
		car1Pos = car1.getPosition();

		car2 = new Car(SPRITE_SHEET + "#greenCar",
				new Vector2D((3*WORLD_WIDTH/4 - 200),WORLD_HEIGHT-70),
				SPRITE_SHEET + "#fire",
				SPRITE_SHEET + "#smoke",
				SPRITE_SHEET + "#cutgrass");
		
		car2Pos = car2.getPosition();
		
		car3 = new Car(SPRITE_SHEET + "#orangeCar",
				new Vector2D((3*WORLD_WIDTH/4 - 60),WORLD_HEIGHT-70),
				SPRITE_SHEET + "#fire",
				SPRITE_SHEET + "#smoke",
				SPRITE_SHEET + "#cutgrass");
		
		
		if(isNet){
                    
                    
                    //new multiplayer setup for more than 3 players
                   /*
                        Scanner s2=new Scanner(System.in);
                        System.out.println("Host or client: ");
                        IP=s2.nextLine();
                        if(IP.equalsIgnoreCase("host")){
                            System.out.println("Number of other players: ");
                            int playerNum=s2.nextInt();
                            GameNet=new NetworkC("none",playerNum);
                            isServ=true;
                            carS s1;
                            s1=new carS();
                            s1.carNum=2;
                            GameNet.sendData2(s1,0);
                            s1.carNum=3;
                            GameNet.sendData2(s1,1);
                        }
                        else{
                            System.out.println("Host ip address: ");
                            IP=s2.nextLine();
                            GameNet=new NetworkC(IP,0);
                            isServ=false;
                            carS s1;
                            s1=GameNet.reciveData();
                            carNum=s1.carNum;
                            if(s1.carNum==2){
                                otherCar1=car1;
                                otherCar2=car3;
                                raceTrack = new RaceTrack(SPRITE_SHEET + "#trans",gameframe,
						car2,  // this to be modified in network mode
						grass,
						road,
						rumbles
						);
                                TrueCar=car2;
                                 //TO do setup 2nd car stuff
                            }
                            else{                                
                                otherCar1=car1;
                                otherCar2=car2;
                                raceTrack = new RaceTrack(SPRITE_SHEET + "#trans",gameframe,
						car3,  // this to be modified in network mode
						grass,
						road,
						rumbles
						);
                                TrueCar=car3;
                                //TO do setup 3rd car stuff
                            }
                        }
                   */
			System.out.println("Please enter none for host or an ip for client: ");
			Scanner s=new Scanner(System.in);
			IP=s.nextLine();
			GameNet=new NetworkC(IP);
			if(IP.equalsIgnoreCase("none")){
				isServ=true;
				raceTrack = new RaceTrack(SPRITE_SHEET + "#trans",gameframe,
						car1,  // this to be modified in network mode
						grass,
						road,
						rumbles
						);
			}
			else{
				isServ=false;
				raceTrack = new RaceTrack(SPRITE_SHEET + "#trans",gameframe,
						car2,  // this to be modified in network mode
						grass,
						road,
						rumbles
						);
			}
		}
		else
			raceTrack = new RaceTrack(SPRITE_SHEET + "#trans",gameframe,
					car1,  // this to be modified in network mode
					grass,
					road,
					rumbles
					);

		rtLayer = new AbstractBodyLayer.IterativeUpdate<RaceTrack>();
		rtLayer.add(raceTrack);
		gameObjectLayers.add(rtLayer);
		physics.manageViewableSet(rtLayer);
		
		boostLayer = new AbstractBodyLayer.IterativeUpdate<SpeedBoost>();
		boostLayer.add(boost);
		gameObjectLayers.add(boostLayer);
		physics.manageViewableSet(boostLayer);
		
		roadLayer = new AbstractBodyLayer.IterativeUpdate<Road>();
		roadLayer.add(road);
		gameObjectLayers.add(roadLayer);
		physics.manageViewableSet(roadLayer);

		rbLayer = new AbstractBodyLayer.IterativeUpdate<Rumbles>();
		rbLayer.add(rumbles);
		gameObjectLayers.add(rbLayer);
		physics.manageViewableSet(rbLayer);

		grassLayer = new AbstractBodyLayer.IterativeUpdate<Grass>();
		grassLayer.add(grass);
		gameObjectLayers.add(grassLayer);
		physics.manageViewableSet(grassLayer);

		car1Layer = new AbstractBodyLayer.IterativeUpdate<Car>();
		car1Layer.add(car1);		
		car2Layer = new AbstractBodyLayer.IterativeUpdate<Car>();
		car2Layer.add(car2);
		car3Layer = new AbstractBodyLayer.IterativeUpdate<Car>();
		car3Layer.add(car3);

		gameObjectLayers.add(car1Layer);
		physics.manageViewableSet(car1Layer);
		gameObjectLayers.add(car2Layer);
		physics.manageViewableSet(car2Layer);
		gameObjectLayers.add(car3Layer);
		physics.manageViewableSet(car3Layer);

		hitRumbles = new CarHitRumbles(car1,rumbles);
		hitGrass = new CarHitGrass(car1,grassLayer);
		if (isNet) {
			if (isServ) {
				gameObjectLayers.add(new GameUI(car1));
			} else {
				gameObjectLayers.add(new GameUI(car2));
			}
		} else {
			gameObjectLayers.add(new GameUI(car1));
		}


	}

	public void update(long deltaMs) {
		super.update(deltaMs);
		
                //new mulitplayer stuff
                carS np1[]=new carS[2];
                carS np2[]=new carS[2];
                
                //normal 2 player stuff
                carS p1;
		carS p2;
		int j;
		int k;
		int k2;
		double car1Dist;
		double car2Dist; 
		double calcX;
		RoadSegment segOrg=new RoadSegment(State.STRAIGHT,false);
		if(isNet){
			if(isServ){
                                //more than 1 car stuff
                              /*
                                //reciving data 
                                np1[0]=GameNet.readData2(0);
                                np1[1]=GameNet.readData2(1);
                                //sending the data
                                np2[0]=new carS();
                                np2[0].p1=raceTrack.findSegment(raceTrack.curZpos+raceTrack.carZ);
                                np2[0].lap=car1.lap;
                                np2[0].runTime=car1.elapsedTime;
                                if(car1.state==State.DOWN){
					np2[0].state=1;
				}
				else if(car1.state==State.STRAIGHT){
					np2[0].state=2;
				}
				else
					np2[0].state=3;
				np2[0].carDist=car1.getPosition().getY()-np1[0].p1.lowerScreenY;
                                np2[0].carNum=1;
                                np2[1]=np1[1];
                                GameNet.sendData2(np2[0],0);
                                GameNet.sendData2(np2[1],0);
                                np2[1]=np1[0];
                                GameNet.sendData2(np2[0],1);
                                GameNet.sendData2(np2[1],1);
                                
                                //start calcualtions
                                car2.setActivation(false);
                                car3.setActivation(false);
                                int car2Seg;
                                int car3Seg;
                                carS car2Data;      
                                carS car3Data;
                                int carsFound=0;
                                 car2Data=np1[0];
                                 car3Data=np1[1];
                                 
                                int currCarIndex=np2[0].p1.index;
                                for(int i=0;i<raceTrack.drawDistance;i++){
                                    if(car2Data.p1.index==currCarIndex){
                                        car2.setActivation(true);
                                        carsFound++;
                                        //do ofther car2 stuff
                                    }
                                    if(car3Data.p1.index==currCarIndex){
                                        car3.setActivation(true);
                                        carsFound++;
                                        //do other car 3 stuff
                                    }
                                    if(carsFound==2){
                                        break;
                                    }
                                    else{
                                        currCarIndex++;
                                        if(currCarIndex>raceTrack.totalIndex)
                                            currCarIndex=0;
                                    }
                                }
                                car2.curSegment=car2Data.p1;
                                car2.elapsedTime=car2Data.runTime;
                                car2.lap=car2Data.lap;
                                if(car2Data.state==1){
					car2.state=State.DOWN;
				}
				else if(car2Data.state==2){
					car2.state=State.STRAIGHT;
				}
				else
					car2.state=State.UP;
                                car3.curSegment=car3Data.p1;
                                car3.elapsedTime=car3Data.runTime;
                                car3.lap=car3Data.lap;
                                if(car3Data.state==1){
					car3.state=State.DOWN;
				}
				else if(car3Data.state==2){
					car3.state=State.STRAIGHT;
				}
				else
					car3.state=State.UP;
                                //DO positioning
                                Postioning(car1,car2,car3,np2[0].p1,car2.currSegment,car3.currSegment);
                             */
				p1=GameNet.reciveData();
				car2Dist=p1.carDist;
				p2=new carS();
				p2.p1=raceTrack.findSegment(raceTrack.curZpos+raceTrack.carZ);
				j=p2.p1.index;
				k=p1.p1.index+1;
				k2=p1.p1.index;
				p2.lap=car1.lap;
				p2.runTime=car1.elapsedTime;
				if(car1.state==State.DOWN){
					p2.state=1;
				}
				else if(car1.state==State.STRAIGHT){
					p2.state=2;
				}
				else
					p2.state=3;
				p2.carDist=car1.getPosition().getY()-p1.p1.lowerScreenY;
				car1Dist=p2.carDist;
				GameNet.sendData(p2);
				car2.setActivation(false);
				for(int i=0;i<raceTrack.drawDistance-30;i++){
					if(j==k2){
						segOrg=raceTrack.track.segments.get(j);

						//System.out.println(p1.p1.index+","+j);
						car2.setActivation(true);

						calcX=car2.startPos.getX()-p1.p1.lowerScreenX;
						//System.out.println(segOrg.lowerScreenX);
						double pp=(segOrg.upperScreenX+segOrg.upperScreenW-segOrg.lowerScreenX+segOrg.lowerScreenW);
						double ppp=(p2.p1.upperScreenX+p2.p1.upperScreenW-p2.p1.lowerScreenX+p2.p1.lowerScreenW);
						calcX=calcX*(pp/ppp);
						car2.scal2H=(segOrg.grassRight-segOrg.grassLeft)/(p2.p1.grassRight-p2.p1.grassLeft);
						car2.currWidth=(int)(car2.getWidth()*car2.scal2H);
						car2.currHeight=(int)(car2.getHeight()*car2.scal2H);
						//car2.setPosition(new Vector2D(segOrg.lowerScreenX,car2.startPos.getY()));
						if(calcX<0)
							car2.setCenterPosition(new Vector2D(segOrg.lowerScreenX+calcX+car2.getWidth()/2,segOrg.lowerScreenY-car2.currHeight));
						else
							car2.setCenterPosition(new Vector2D(segOrg.lowerScreenX+calcX+car2.getWidth()/2,segOrg.lowerScreenY-car2.currHeight));
						//will use segment resizing factor to determine car resizing


						//car2.scal2W=segOrg.lowerScreenW/p2.p1.lowerScreenW;
						break;
					}
					else{
						j++;
						if(j>raceTrack.totalIndex){
							j=0;
						}
					}
				}

				car2.curSegment=p1.p1;
				car2.elapsedTime=p1.runTime;
				car2.lap=p1.lap;
				if(p1.state==1){
					car2.state=State.DOWN;
				}
				else if(p1.state==2){
					car2.state=State.STRAIGHT;
				}
				else
					car2.state=State.UP;
				car2.curSegment=segOrg;
				//finding postion use lap numbe first
				if(car1.lap>car2.lap){
					car1.RacePos=1;
					car2.RacePos=2;
				}
				else if(car1.lap<car2.lap){
					car2.RacePos=1;
					car1.RacePos=2;
				}
				else{
					//same lap use segment number
					if(p2.p1.index>segOrg.index){
						car1.RacePos=1;
						car2.RacePos=2;
					}
					else if(p2.p1.index<segOrg.index){
						car2.RacePos=1;
						car1.RacePos=2;
					}
					else{
						//same segment, will use distance to end of segment in the y for position
						if(car1Dist<car2Dist){
							car2.RacePos=1;
							car1.RacePos=2;
						}
						else{
							car1.RacePos=1;
							car2.RacePos=2;
						}
					}
				}        
				//carRoad.roadCenter=p1.p1.roadCenter;


			}
			else{
                            
                                //more than 2 player stuff
                            /*
                                np1[0]=new carS();
                                np1[0].p1=raceTrack.findSegment(raceTrack.curZpos+raceTrack.carZ);
                                np1[0].lap=TrueCar.lap;
                                np1[0].runTime=TrueCar.elapsedTime;
                                if(TrueCar.state==State.DOWN){
					np1[0].state=1;
				}
				else if(TrueCar.state==State.STRAIGHT){
					np1[0].state=2;
				}
				else
					np1[0].state=3;
                                np1[0].carDist=TrueCar.getPosition().getY()-np1[0].p1.lowerScreenY;
                                np1[0].carNum=carNum;
                                GameNet.sendData(np1[0]);
                                np2[0]=GameNet.reciveData();
                                np2[1]=GameNet.reciveData();
                                
                                otherCar1.setActivation(false);
                                otherCar2.setActivation(false);
                                carS other1Data;      
                                carS other2Data;
                                int carsFound=0;
                                if(np2[0].carNum<np2[1].carNum){
                                    other1Data=np2[0];
                                    other2Data=np2[1];
                                }
                                else{
                                    other1Data=np2[1];
                                    other2Data=np2[0];
                                }
                                int currCarIndex=np1[0].p1.index;
                                 for(int i=0;i<raceTrack.drawDistance;i++){
                                    if(other1Data.p1.index==currCarIndex){
                                        otherCar1.setActivation(true);
                                        carsFound++;
                                        //do ofther car2 stuff
                                    }
                                    if(other2Data.p1.index==currCarIndex){
                                        otherCar2.setActivation(true);
                                        carsFound++;
                                        //do other car 3 stuff
                                    }
                                    if(carsFound==2){
                                        break;
                                    }
                                    else{
                                        currCarIndex++;
                                        if(currCarIndex>raceTrack.totalIndex)
                                            currCarIndex=0;
                                    }
                                }
                                otherCar1.curSegment=other1Data.p1;
                                otherCar1.elapsedTime=other1Data.runTime;
                                otherCar1.lap=other1Data.lap;
                                if(other1Data.state==1){
					otherCar1.state=State.DOWN;
				}
				else if(other1Data.state==2){
					otherCar1.state=State.STRAIGHT;
				}
				else
					otherCar1.state=State.UP;
                                otherCar2.curSegment=other2Data.p1;
                                otherCar2.elapsedTime=other2Data.runTime;
                                otherCar2.lap=other2Data.lap;
                                if(other2Data.state==1){
					otherCar2.state=State.DOWN;
				}
				else if(other2Data.state==2){
					otherCar2.state=State.STRAIGHT;
				}
				else
					otherCar2.state=State.UP;
                                //DO positioning
                                Postioning(Truecar,otherCar1,otherCar2,np1[0].p1,otherCar1.currSegment,otherCar2.currSegment);
                             */
				//send ovet all needed data
				p1=new carS();
				p1.p1=raceTrack.findSegment(raceTrack.curZpos+raceTrack.carZ);
				p1.lap=car2.lap;
				p1.runTime=car2.elapsedTime;
				if(car2.state==State.DOWN){
					p1.state=1;
				}
				else if(car2.state==State.STRAIGHT){
					p1.state=2;
				}
				else
					p1.state=3;
				p1.carDist=car2.getPosition().getY()-p1.p1.lowerScreenY;
				car2Dist=p1.carDist;
				GameNet.sendData(p1);
				p2=GameNet.reciveData();
				car1Dist=p2.carDist;
				j=p1.p1.index;
				k=p2.p1.index+1;
				k2=p2.p1.index;
				car1.setActivation(false);
				//go through all visable segments to find if car is on one
				for(int i=0;i<raceTrack.drawDistance-30;i++){
					if(j==k2){
						//segment indexes match, so we grab the segment
						segOrg=raceTrack.track.segments.get(j);
						car1.setActivation(true);

						//calculate the x distance travel on other cars screen
						calcX=car1.startPos.getX()-p2.p1.lowerScreenX;

						//get lenght of segment car will be put one
						double pp=(segOrg.upperScreenX+segOrg.upperScreenW-segOrg.lowerScreenX+segOrg.lowerScreenW);
						//get length of orginal segment
						double ppp=(p2.p1.upperScreenX+p2.p1.upperScreenW-p2.p1.lowerScreenX+p2.p1.lowerScreenW);

						//times x distance by ratio to get actual distance the car will travel on the new segment
						calcX=calcX*(pp/ppp);
						car1.scal2H=(segOrg.grassRight-segOrg.grassLeft)/(p2.p1.grassRight-p2.p1.grassLeft);
						car1.currWidth=(int)(car1.getWidth()*car1.scal2H);
						car1.currHeight=(int)(car1.getHeight()*car1.scal2H);
						//if calcX< o car is to the left of x mid, else its to the right
						if(calcX<0)
							car1.setCenterPosition(new Vector2D(segOrg.lowerScreenX+calcX+car1.getWidth()/2,segOrg.lowerScreenY-car1.currHeight));
						else
							car1.setCenterPosition(new Vector2D(segOrg.lowerScreenX+calcX+car1.getWidth()/2,segOrg.lowerScreenY-car1.currHeight));


						//car1.scal2W=segOrg.lowerScreenW/p1.p1.lowerScreenW;
						//System.out.println(car1.scal2H+","+car2.scal2W);
						break;
					}
					else{
						j++;
						if(j>raceTrack.totalIndex){
							j=0;
						}
					}
				}
				//set car1 values what we got
				car1.curSegment=p2.p1;
				car1.elapsedTime=p2.runTime;
				car1.lap=p2.lap;
				if(p2.state==1){
					car1.state=State.DOWN;
				}
				else if(p2.state==2){
					car1.state=State.STRAIGHT;
				}
				else
					car1.state=State.UP;
				car1.curSegment=segOrg;

				if(car1.lap>car2.lap){
					car1.RacePos=1;
					car2.RacePos=2;
				}
				else if(car1.lap<car2.lap){
					car2.RacePos=1;
					car1.RacePos=2;
				}
				else{
					//same lap use segment number
					if(segOrg.index>p1.p1.index){
						car1.RacePos=1;
						car2.RacePos=2;
					}
					else if(segOrg.index<p1.p1.index){
						car2.RacePos=1;
						car1.RacePos=2;
					}
					else{
						//same segment, will use distance to end of segment in the y for position
						if(car1Dist<car2Dist){
							car2.RacePos=1;
							car1.RacePos=2;
						}
						else{
							car1.RacePos=1;
							car2.RacePos=2;
						}
					}
				}


			}

		}
		car1Pos = car1.getPosition();
		car2Pos = car2.getPosition();
		checkUserInput ();
		CollisionHandlers(deltaMs);
		//music.loop(0.05, 25);
		
		if (!gameIsRun) {
			iniTime = System.currentTimeMillis();
			gameIsRun = true;
		}

	}

        //3 car base race postioning finding, will try to far car farthest ahead then use 2 car for other 2
        public void Positioning(Car c1,Car c2,Car c3,RoadSegment seg1,RoadSegment seg2,RoadSegment seg3){
            if(c1.lap>c2.lap){
                if(c1.lap>c3.lap){
                    c1.RacePos=1;
                    doPos(c2,c3,seg2,seg3);
                    c2.RacePos++;
                    c3.RacePos++;
                }
                else if(c1.lap<c3.lap){
                    c3.RacePos=1;
                    c1.RacePos=2;
                    c2.RacePos=3;
                }
                else{
                    c2.RacePos=3;
                    doPos(c1,c3,seg1,seg3);

                }
            }
            else if(c1.lap<c2.lap){
                if(c2.lap>c3.lap){
                        c2.RacePos=1;
                        doPos(c1,c3,seg1,seg3);
                        c1.RacePos++;
                        c3.RacePos++;
                }
                else if(c2.lap<c3.lap){
                    c3.RacePos=1;
                    c1.RacePos=3;
                    c2.RacePos=2;
                }
                else{
                    c1.RacePos=3;
                    doPos(c2,c3,seg2,seg3);
                }
            }
            else{
                if(c3.lap>c2.lap){
                    c3.RacePos=1;
                    doPos(c1,c2,seg1,seg2);
                    c1.RacePos++;
                    c2.RacePos++;
                }
                else if(c2.lap>c3.lap){
                    c3.RacePos=3;
                    doPos(c1,c2,seg1,seg2);
                }
                else{
                    //Laps same so we do segments
                    if(seg1.index>seg2.index){
                        if(seg1.index>seg3.index){
                            c1.RacePos=1;
                            doPos(c2,c3,seg2,seg3);
                            c2.RacePos++;
                            c3.RacePos++;
                        }
                        else if(seg1.index<seg3.index){
                            c3.RacePos=1;
                            c1.RacePos=2;
                            c2.RacePos=3;
                        }
                        else{
                            c2.RacePos=3;
                            doPos(c1,c3,seg1,seg3);
                        }
                    }
                    else if(seg1.index<seg2.index){
                        if(seg2.index>seg3.index){
                            c2.RacePos=1;
                            doPos(c1,c3,seg1,seg3);
                            c1.RacePos++;
                            c3.RacePos++;
                        }
                        else if(seg2.index<seg3.index){
                            c3.RacePos=1;
                            c2.RacePos=2;
                            c1.RacePos=3;
                        }
                        else{
                            c2.RacePos=3;
                            doPos(c1,c3,seg1,seg3);
                        }
                    }
                    else{
                        if(seg3.index>seg2.index){
                            c3.RacePos=1;
                            doPos(c1,c2,seg1,seg2);
                            c1.RacePos++;
                            c2.RacePos++;
                        }
                        else if(seg3.index<seg2.index){
                            c3.RacePos=3;
                            doPos(c1,c2,seg1,seg2);
                        }
                        else{
                            //same lap and segment now we look at postion
                          if(seg1.upperScreenY>seg2.upperScreenY){
                            if(seg1.upperScreenY>seg3.upperScreenY){
                                c1.RacePos=1;
                                doPos(c2,c3,seg2,seg3);
                                c3.RacePos++;
                                c2.RacePos++;
                            }
                            else if(seg1.upperScreenY<seg3.upperScreenY){
                                c3.RacePos=1;
                                c1.RacePos=2;
                                c2.RacePos=3;
                            }
                            else{
                                c2.RacePos=3;
                                doPos(c1,c3,seg1,seg3);
                            }
                        }
                        else if(seg1.upperScreenY<seg2.upperScreenY){
                            if(seg2.upperScreenY>seg3.upperScreenY){
                                c2.RacePos=1;
                                doPos(c1,c3,seg1,seg3);
                                c1.RacePos++;
                                c3.RacePos++;
                            }
                            else if(seg2.upperScreenY<seg3.upperScreenY){
                                c3.RacePos=1;
                                c2.RacePos=2;
                                c1.RacePos=3;
                            }
                            else{
                                c2.RacePos=3;
                                doPos(c1,c3,seg1,seg3);
                            }
                        }
                        else{
                            if(seg3.upperScreenY>seg2.lowerScreenY){
                                c3.RacePos=1;
                                doPos(c1,c2,seg1,seg2);
                                c1.RacePos++;
                                c2.RacePos++;
                            }
                            else if(seg3.upperScreenY<seg2.upperScreenY){
                                c3.RacePos=3;
                                doPos(c1,c2,seg1,seg2);
                            }
                            else{
                                c1.RacePos=1;
                                c2.RacePos=1;
                                c3.RacePos=1;
                            }
                        }
                    }
                }
            }
        }
     }
        
        //Two car calculations
        public void doPos(Car c1,Car c2,RoadSegment seg1,RoadSegment seg2){
            if(c1.lap>c2.lap){
                c1.RacePos=1;
		c2.RacePos=2;
            }
            else if(c1.lap<c2.lap){
                c2.RacePos=1;
		c1.RacePos=2;
            }
            else{
		//same lap use segment number
                if(seg1.index>seg2.index){
                    c1.RacePos=1;
                    c2.RacePos=2;
                }
		else if(seg1.index<seg2.index){
                    c2.RacePos=1;
                    c1.RacePos=2;
		}
		else{
                    //same segment, will use current segments upper x value;
                    if(seg1.upperScreenY>seg2.upperScreenY){
                        c2.RacePos=2;
			c1.RacePos=1;
                    }
                    else{
                        c1.RacePos=1;
			c2.RacePos=2;
                    }
		}
            }
        }
        
	/**
	 * Render the shapes, and possible shape markup
	 */
	@Override
	public void render(RenderingContext rc) {		
		super.render(rc);
	}

	public void checkUserInput () {
		turnLeft = keyboard.isPressed(KeyEvent.VK_LEFT);
		turnRight = keyboard.isPressed(KeyEvent.VK_RIGHT);
		speedUp = keyboard.isPressed(KeyEvent.VK_UP);
		applybreak = keyboard.isPressed(KeyEvent.VK_DOWN);
		standRevup = keyboard.isPressed(KeyEvent.VK_SPACE);
	}

	public void CollisionHandlers(long deltaMs) {
		checkCollisions.registerCollisionHandler(hitRumbles);
		checkCollisions.registerCollisionHandler(hitGrass);
		//checkCollisions.applyLawsOfPhysics(deltaMs);
	}

	public static void main(String[] args) {
		//jig.engine.lwjgl.LWResourceFactory.makeCurrentResourceFactory();

		ResourceFactory.getJIGLogger().setLevel(Level.WARNING);
		ResourceFactory.getJIGLogger().getHandlers()[0].setLevel(Level.WARNING);

		Game a;
		a = new Game();
		a.run();
		a.gameframe.getConsole().appendLineToPrompt("fps histogram");

	}

}
