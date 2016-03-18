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
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mash
 */
public class Client {

    private static String serverReply;

    public static void main(String[] args) {

        try {

            //print("Create new client...");
            client = new Socket(host, port);
            //print("Created");
            inputStream = new ObjectInputStream(client.getInputStream());
            //print("inputStream is connected");
            outputStream = new ObjectOutputStream(client.getOutputStream());
            outputStream.flush();
            //print("outputStream is connected");

            print("Connected with Server through : " + host + ":" + port);
        } catch (IOException iOException) {
            print("Client-Server connection could not be established.");
        }
        while (true) {
            task();
        }

    }

    private static void task() {
        String instruction;
        print(getMessage());
        instruction = sc.nextLine();
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
            case "sendmsg": {
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
                print("Undefined input. Try again.");
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
            print("You says " + msg);
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
        sendMessage("register");
        
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
        sendMessage("login");
        
        String userName;
        userName = sc.nextLine();
        sendMessage(userName);

        String password;
        password = sc.nextLine();
        sendMessage(password);
        
        serverReply = (String) getMessage();
        print(serverReply);
    }

    private static void logOut(String instruction) {
        sendMessage("logOut");
        serverReply = (String) getMessage();
        print(serverReply);
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
        
        serverReply = (String) getMessage();
        
        print(serverReply);
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
}
