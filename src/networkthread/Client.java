/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkthread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mash
 */
public class Client {

    private static String serverReply;
    private static int isLogIn = 0;
    private static int debug = 1;

    public static void main(String[] args) {

        try {
            client = new Socket(host, port);
            inputStream = new ObjectInputStream(client.getInputStream());
            outputStream = new ObjectOutputStream(client.getOutputStream());
            outputStream.flush();
            
            print("Connected with Server through : " + host + ":" + port);
        } catch (IOException iOException) {
            print("Client-Server connection could not be established.");
        }
        print(getMessage());
        handleOfflineMessage();
        while (true) {
            task();
        }

    }

    private static void task() {
        String instruction;
        //print(getMessage());
        instruction = getInstruction();
        //instruction = sc.nextLine();
        switch (instruction) {
            case "register": {
                register(instruction);
                break;
            }
            case "login": {
                logIn(instruction);
                break;
            }
            case "logout": {
                logOut(instruction);
                break;
            }
            case "sendmsgto": {
                sendMessage(instruction);
                print("Enter friend name: ");
                String messageTo = sc.nextLine();
                sendMessage(messageTo);
                msg = sc.nextLine();
                sendMessage(msg);
                break;
            }
            case "sendfreq": {
                String friend = sc.nextLine();
                sendFriendRequest(instruction,friend);
                break;
            }
            case "freqs": {
                ShowRequests(instruction);
                break;
            }
            case "online": {
                showOnlineUsers(instruction);
                break;
            }
            case "friends": {
                showFriendList(instruction);
                break;
            }
            case "reject": {
                String nameOfReq = sc.nextLine();
                rejectFriendRequest(instruction, nameOfReq);
                break;
            }
            case "accept": {
                acceptFriendRequest(instruction);
                break;
            }
            case "sendfile": {
                String fileName = sc.nextLine();
                sendFile(instruction,fileName);
                break;
            }
            
            case "exit":{
                disconnect(instruction);
            }
            default: {
                print("Undefined input. Try again.["+instruction+"]");
            }
        }
    }

    private static Object getMessage() {
        try {
            return inputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private static void print(Object obj) {
        System.out.println(obj.toString());
    }

    private static boolean sendMessage(String msg) {
        try {
            if(debug == 1){
                print("You says " + msg);
            }
            outputStream.writeObject(msg);
            outputStream.flush();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private static void closeStreams() {
        try {
            inputStream.close();
            outputStream.close();
            sc.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void register(String instruction) {
        sendMessage(instruction);
        
        String userName;
        userName = sc.nextLine();
        sendMessage(userName);

        String password;
        password = sc.nextLine();
        sendMessage(password);
        
        serverReply = (String) getMessage();
        print(serverReply);
    }

    private static void logIn(String instruction) {
        
        sendMessage(instruction);
        
        String userName;
        userName = sc.nextLine();
        sendMessage(userName);

        String password;
        password = sc.nextLine();
        sendMessage(password);
        
        serverReply = (String) getMessage();
        
        if("Success".equals(serverReply))
        {
            isLogIn = 1;
        }
        else
        {
            isLogIn = 0;
        }
        print(serverReply);
    }

    private static void logOut(String instruction) {
        if(isLogIn == 1)
        {
            sendMessage(instruction);
            serverReply = (String) getMessage();
            print(serverReply);
        }
        else
        {
            print("You are not logged in.");
        }
    }

    private static void sendFriendRequest(String instruction, String friend) {
        sendMessage(instruction);
        sendMessage(friend);
        
        serverReply = (String) getMessage();
        print(serverReply);
    }

    private static void ShowRequests(String instruction) {
        sendMessage(instruction);
        
        serverReply = (String) getMessage();
        
        print(serverReply);
    }

    private static void showOnlineUsers(String instruction) {
        sendMessage(instruction);
        
        ArrayList<userPublicData> onlineUsers = new ArrayList<userPublicData>();
        
        onlineUsers = (ArrayList<userPublicData>) getMessage();
        
        for (userPublicData onlineUser : onlineUsers) {
            print(onlineUser.userName+"   "+onlineUser.userId);
        }
        print(onlineUsers.toString());
    }

    private static void showFriendList(String instruction) {
        sendMessage(instruction);
        
        serverReply = (String) getMessage();
        
        print(serverReply);
    }

    private static void rejectFriendRequest(String instruction, String nameOfReq) {
        sendMessage(instruction);
        sendMessage(nameOfReq);
        serverReply = (String) getMessage();
        print(serverReply);
        
    //Extra
        ShowRequests("freqs");
        serverReply = (String) getMessage();
        print(serverReply);
    }

    private static void acceptFriendRequest(String instruction) {
        sendMessage(instruction);
        String friend = sc.nextLine();
        sendMessage(friend);
        serverReply = (String) getMessage();
        print(serverReply);
    }

    private static void sendFile(String instruction, String fileName) {
        sendMessage(instruction);
        transferFile(fileName);
        
        serverReply = (String) getMessage();
        print(serverReply);
    }

    private static void transferFile(String fileName) {
        //transfer file
    }

    private static void disconnect(String instruction) {
        sendMessage(instruction);
        serverReply = (String) getMessage();
        print(serverReply);
        closeStreams();
    }

   /** 
    * variables 
    */
    private static Socket client;
    private static String host = "localhost";
    private static int port = 5555;
    private static Scanner sc = new Scanner(System.in);
    private static String msg;
    private static String name;
    private static ObjectOutputStream outputStream;
    private static ObjectInputStream inputStream;

    private static String getInstruction() {
        String instruction = null;
        
        while(instruction == null)
        {
            try {
                if(inputStream.available() != 0)
                {
                    instruction = (String) getMessage();
                }
                else if(sc.hasNext())
                {
                    instruction = sc.nextLine();
                }
            } catch (IOException ex) {
                //No input from server or user-end
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instruction;
    }

    private static void handleOfflineMessage() {
        String offlineMsg = (String) getMessage();
        print(offlineMsg);
    }
}
