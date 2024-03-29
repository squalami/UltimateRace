/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package UltimateRace;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


/**
 *
 * @author brian
 */
public class NetworkC {
    ServerSocket serv1;
    Socket s1;
    ObjectInputStream in;
    ObjectOutputStream out;
    BufferedReader d;
    boolean isS;
    
    //for multiconnection servers;
    ServerSocket servs[];
    Socket connectionList[];
    ObjectInputStream InList [];
    ObjectOutputStream outList[];
    
    
    NetworkC(String ip){
        if(ip.equalsIgnoreCase("none")){
            //System.out.println("oooo");
            isS=true;
             try {
                 serv1=new ServerSocket(8001);
             } catch (IOException ex) {
                 System.out.println("Could not create server socket");
                 System.exit(1);
             }
             try {
                s1=serv1.accept();
             }catch (IOException ex) {
                 System.out.println("could not bind socket");
                 System.exit(1);
            }
             //System.out.println("coonnection made");
            try {
                in=new ObjectInputStream(s1.getInputStream());
            } catch (IOException ex) {
               System.out.println("huh");
               System.exit(1);
            }
            try {
                out=new ObjectOutputStream(s1.getOutputStream());
            } catch (IOException ex) {
                System.out.println("could not create output stream");
                System.exit(1);
            } 
        }
        else{
            isS=false;
            //System.out.println("hhhhhh");
            try {
                s1=new Socket(ip,8001);
            } catch (UnknownHostException ex) {
                System.out.println("Host unknown");
                System.exit(1);
            } catch (IOException ex) {
                System.out.println("issue creating socket");
                System.exit(1);
            }
            try {
                out=new ObjectOutputStream(s1.getOutputStream());
            } catch (IOException ex) {
                System.out.println("could not create input stream");
                System.exit(1);
            }
            try {
                in=new ObjectInputStream(s1.getInputStream());
            } catch (IOException ex) {
                System.out.println("could not create output stream");
                System.exit(1);
            } 
            //d=new BufferedReader(new InputStreamReader(in));
            
        }
    }
    
    //sever and multiple client connections
    NetworkC(String IP, int numConnections){
        if(IP.equalsIgnoreCase("none")){
            //server
            try {
                int i=0;
                int startPort=8001;
                connectionList=new Socket[numConnections];
                InList=new ObjectInputStream [numConnections];
                outList=new ObjectOutputStream [numConnections];
                serv1=new ServerSocket(startPort);
                //give each connection besides the last a new port
                for(i=0;i<numConnections;i++){
                    //for each all clients will connection on the same port orginal and then will be given new ports
                    
                    connectionList[i]=serv1.accept();
                    //setup the output stream temperarly to send over new port data
                    outList[i]=new ObjectOutputStream(connectionList[i].getOutputStream());
                    InList[i]=new ObjectInputStream(connectionList[i].getInputStream());

                }
            } catch (IOException ex) {
               System.out.println("issue creating socket");
                System.exit(1);
            }
        }
        else{
            try {
                s1=new Socket(IP,8001);
                in=new ObjectInputStream(s1.getInputStream());
                out=new ObjectOutputStream(s1.getOutputStream());
            } catch (UnknownHostException ex) {
                System.out.println("issue creating socket");
                System.exit(1);
            } catch (IOException ex) {
               System.out.println("issue creating socket");
                System.exit(1);
            }
        }
    }
    
    //sendData function edited to account for multiConnections
    public void sendData2(carS p, int n){
        try {
            outList[n].writeObject(p);
        } catch (IOException ex) {
             System.out.println("could not send data, connection issue");
                    //resetCon(isS);
                    System.exit(1);
        }
    }
    
    //recieveData method edited to accont for multiConnections
    public carS readData2(int n){
        carS p=new carS();
        try {
            p=(carS)InList[n].readObject();
        } catch (IOException ex) {
             System.out.println("network write issue");
            System.exit(1);
        } catch (ClassNotFoundException ex) {
            System.out.println("network write issue");
            System.exit(1);
        }
        return p;
    }
    
    //method to send data between computers
    //no longer terminates the program on read
    public void sendData(carS p){
                try {
                    out.writeObject(p);
                } catch (IOException ex) {
                    //System.err.println(ex);
                    System.out.println("could not send data, connection issue");
                    //resetCon(isS);
                    System.exit(1);
                }
    }
    //method to obtain data from a computer
    //data is read of the buffer and returned
    //No longer terminates the program on connection issue
    public carS reciveData(){
        carS p=new carS();
        try {
          p=(carS)in.readObject();
          //p=d.readLine()
        } catch (IOException ex) {
            System.out.println("network write issue, connection issue");
            //resetCon(isS);
            System.exit(1);
        } catch (ClassNotFoundException ex) {
             System.out.println("network write issue");
            System.exit(1);
        }
        return p;
    }
    
    //MEthod to end connections
    public void resetCon(boolean isServ){
        try {
            if(isServ){
                    serv1.close();
            }
            s1.close();
            in.close();
            out.close();
        } catch (IOException ex) {
            System.out.println("Could not close conenctions");
        }
        
    }
}
