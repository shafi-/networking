/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkthread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mash
 */
public class NetworkThread {

    public static ServerSocket ss;
    transient public static ArrayList<userData> allUsers; // All users data will be here
    transient public static ArrayList<Server> connections; //Only online users will be here
    public static int userId = 0;
    public static String usersInfo = "/src/UserInfo.txt";
    transient public static ArrayList<OfflineMessage> offlineMsg;
    transient private static ArrayList<userPublicData>[] friendlist;
    transient public static int storedFileId = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            //Constants constants = new Constants();
            boolean cont = true;

            allUsers = new ArrayList<userData>();
            getSavedData();

            connections = new ArrayList<Server>();

            offlineMsg = new ArrayList<OfflineMessage>();
            collectOfflineMsg();

            try {
                ss = new ServerSocket(5555);
                print("Success!!! Server is running on port 5555. ");

            } catch (IOException iOException) {
                print("Server is not started. Error!!! ");
                Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, iOException);
                cont = false;
            }

            while (cont) {
                print("Wait for new connection...");
                Socket s = ss.accept();
                
                userId = userId + 1;
                
                Server server = new Server(s, userId);
                Thread t = new Thread(server, "client");

                try {
                    //userPublicData upd = new userPublicData(userId, "name", s);
                    connections.add(server);
                } catch (Exception ex) {
                    print("Could not be added.");
                }
                t.start();
            }

        } catch (IOException ex) {
            System.out.println("Server initialization problem! Try again");
            //Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void print(Object obj) {
        System.out.println(obj.toString());
    }

    private static void collectOfflineMsg() {
        try {
            File userDataFile = new File(getCurrentDirectory() + "/src/OfflineMsgs.txt");
            Scanner sc = new Scanner(userDataFile);
            while (sc.hasNext()) {
                userId = userId + 1;
                userData ud = new userData();
                ud.userId = sc.nextInt();
                ud.userName = sc.next();
                ud.password = sc.next();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void getSavedData() {
        try {
            File userDataFile = new File(getCurrentDirectory() + "/src/UserInfo.txt");

            Scanner sc = new Scanner(userDataFile);
            while (sc.hasNext()) {
                userId = userId + 1;
                userData ud = new userData();
                ud.userId = sc.nextInt();
                ud.userName = sc.next();
                ud.password = sc.next();
                allUsers.add(ud);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String getCurrentDirectory() {
        return System.getProperty("user.dir");
    }

}
