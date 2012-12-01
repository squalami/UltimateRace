/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UltimateRace;

/**
 *
 * @author brian
 * Notes: in order for this class to work correctly as a sendable object all elements must be serializable
 *  some objects added will not work, Strings, varied length arrays, Vector2D,....
 * So for safety keep to basic element types
 */

//////////////////////////////////////////////
//Our network data container                //
//  well need changed per our project       //
//////////////////////////////////////////////
class carS implements java.io.Serializable{
        //used mainly to denote the end of track
        //true means more tracks to come, false means last track
        
        //this is for the car data
        RoadSegment p1;
        int lap;
        long runTime;
        int state;
        
        //for postion calculations
        double carDist;
        //map data stuff
        //////////////////
        //add data here//
        ////////////////
        //will assume maps can have up to 3 objects
        
    }