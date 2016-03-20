/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkthread;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
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
    private static int userInput = 0;
    private static String userInstruction;
    private static ArrayList<String> friendList;
    private static ArrayList<String> friendRequest;
    static Thread t;
    static TakeInputFromUser tifu;
    
    public static void main(String[] args) {

        try {
            client = new Socket(host, port);
            initialize();
            print("Connected with Server through : " + host + ":" + port);
        } catch (IOException iOException) {
            print("Client-Server connection could not be established.");
        }
        print(getMessage());
        handleOfflineMessage();
        
        tifu = new TakeInputFromUser();
        t = new Thread(tifu);
        t.start();
        
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
                String friendname = sc.nextLine();
                sendMessage(friendname);
                print(getMessage());
                print("Enter message: ");
                msg = sc.nextLine();
                sendMessage(msg);
                print(getMessage());
                break;
            }
            case "sendfreq": {
                print("Enter user name: ");
                String friend = sc.nextLine();
                sendFriendRequest(instruction,friend);
                break;
            }
            
            case "rcvfreq": {
                String user = (String) getMessage();
                print("You have a friend request from "+user);
                print("To accept write accept. To reject write reject.");
                friendRequest.add(user);
                handleFriendRequest(user);
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
                String nameOfReq = sc.nextLine();
                acceptFriendRequest(instruction, nameOfReq );
                break;
            }
            case "sendfile": {
                sendFile(instruction);
                break;
            }
            
            case "rcvfile":{
                try {
                    String sender = (String) getMessage();
                    print(sender+" sent a file.");
                    File file = new File((System.getProperty("user.dir")+"/"+"receivedfileFrom"+sender));
                    byte[] content = (byte[]) inputStream.readObject();
                    Files.write(file.toPath(),content);
                    
                } catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
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
            isLogIn = 0;
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
        
        ArrayList<Server> onlineUsers = new ArrayList<Server>();
        
        
        onlineUsers = (ArrayList<Server>) getMessage();
        
        print("Online users: ");
        for (Server server : onlineUsers) {
            print(server.userName +"   "+server.userId);
        }
        
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
    /*        ShowRequests("freqs");
    serverReply = (String) getMessage();
    print(serverReply);*/    }

    private static void acceptFriendRequest(String instruction, String user) {
        sendMessage(instruction);
        sendMessage(user);
        serverReply = (String) getMessage();
        print(serverReply);
    }

    private static void sendFile(String instruction) {
        sendMessage(instruction);
        
        print("Enter friend's name: ");
        String recver = sc.next();
        
        print("Enter file name: ");
        String fileName = sc.next();
        
        transferFile(fileName,recver);
        
        serverReply = (String) getMessage();
        print(serverReply);
    }

    private static void transferFile(String fileName, String recver) {
        try {
            //transfer file
            sendMessage(recver);
            outputStream.writeObject(fileName);
            print("File sent");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                else if(tifu.hasInput == 1)
                {
                    instruction = tifu.instruction;
                    tifu.hasInput = 0;
                }
                //print("Debug: "+ tifu.hasInput+ " "+ tifu.instruction);
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

    private static void handleFriendRequest(String user) {
        String reply = sc.next();
        if( reply.equals("accept"))
        {
            acceptFriendRequest(reply,user);
            friendList.add(user);
            friendRequest.remove(user);
        }
        else if(reply.equals("reject"))
        {
            rejectFriendRequest(reply, user);
            friendRequest.remove(user);
        }
        else
        {
            print("Invalid option. Try again.");
            handleFriendRequest(user);
        }
    }

    private static void initialize() {
        try {
            inputStream = new ObjectInputStream(client.getInputStream());
            outputStream = new ObjectOutputStream(client.getOutputStream());
            outputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void rcvInstruction(String instruct) {
        userInput = 1;
        userInstruction = instruct;
    }
    
}
