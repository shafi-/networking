package networkthread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mash
 *
 */
public class Server implements Runnable {

    private Socket client;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private int isAuthenticated;
    private ArrayList<Socket> friendRequestList;
    private ArrayList<Socket> friendList;
    private final int userId;
    private String userName;
    private String password;
    private String instruction;
    private boolean isRegistered = false;
    
    public Server(Socket s, int id) {
        print("Create new Server instance...");
        client = s;
        userId = id;
        isAuthenticated = 0;
    }

    @Override
    public void run() {
        initialize();
        boolean stopOrRun = true;
        String msg = null;
        sendMessage("Welcome!");
        
        checkOfflieMsg();
        
        while (stopOrRun) {
            instruction = (String) getMessage();
            print("Instruction from client: " + instruction);

            switch (instruction) {
                case "register": {
                    register();
                    break;
                }
                case "login": {
                    logIn();
                    break;
                }
                case "logout": {
                    logOut();
                    break;
                }
                case "receivemsgto": {  //receiveMessage
                    String msgto = (String) getMessage();
                    
                    Socket chatWith;
                    
                    //check if friend then check if online 
                    
                    for (userPublicData upd : NetworkThread.connections) {
                        if(upd.userName.equals(msgto))
                        {
                            chatWith = upd.socket;
                            sendMessage("Connection established with "+upd.userName);
                            msg = (String) getMessage();
                            sendMessageTo(chatWith,msg);
                        }
                        else
                        {
                            msg = (String) getMessage();
                            storeOfflineMsg(msg,upd.userName);
                        }
                    }
                    print(msg);
                    break;
                }
                case "sendfreq": {

                    break;
                }
                
                case "dmsg": {
                    String data = (String)getMessage();
                    String[] dataN = data.split("$%");
                }
                case "freqs": {
                    ShowRequests();
                    break;
                }
                case "online": {
                    showOnlineUsers();
                    break;
                }
                
                case "friends": {
                    showFriendList();
                    break;
                }
                case "reject": {
                    break;
                }
                case "accept": {
                    acceptFriendRequest();
                    break;
                }
                case "sendfile": {
                    String fileName = (String) getMessage();
                    recieveFile(fileName);
                    break;
                }

                case "exit": {
                    sendMessage("Disconnected!!!");
                    closeStreams();
                    stopOrRun = false;
                }
                
                case "chatwith": {
                    String to = (String) getMessage();
                }
                default: {
                    print("Server got undefined input. Try again.["+instruction+"]");
                }
            }

        }
    }

    private void print(Object obj) {
        System.out.println("Client[" + userId + "] :" + obj.toString());
    }

    private boolean sendMessage(String message) {
        try {
            outputStream.writeObject(message);
            outputStream.flush();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private Object getMessage() {
        try {
            return inputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean initialize() {
        try {
            outputStream = new ObjectOutputStream(client.getOutputStream());
            inputStream = new ObjectInputStream(client.getInputStream());
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private void closeStreams() {
        try {
            inputStream.close();
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void register() {
        String usrName = (String) getMessage();
        String pass = (String) getMessage(); 
        
        print("Got userName and password.");

        if (isAlreadyRegistered(usrName)) {
            sendMessage("You have already registerred. Please log in.");
        } 
        
        else {
        
            NetworkThread.connections.get(userId-1).userName = usrName;
            this.userName = usrName;
            this.password = pass;
            userData ud = new userData();
            ud.userId = userId;
            ud.userName = usrName;
            ud.password = pass;
            NetworkThread.allUsers.add(ud);
            
            sendMessage(usrName + " is registered with password: " + pass);
            isRegistered = true;
        }
    }

    private void logIn() {
        String usrName = (String) getMessage();
        String pass = (String) getMessage();

        if (isAlreadyRegistered(usrName)) {
            if (Auth(usrName, pass)) {
                isAuthenticated = 1;
                sendMessage("Success");
            } else {
                sendMessage("Username/Password didn't matched.");
            }
        } else {
            sendMessage("Please register first.");
        }
    }

    private void logOut() {
        isAuthenticated = 0;
        sendMessage("You logged out successfully.");
        
        //Remove user from online list

        int index = NetworkThread.connections.indexOf(this);
        
        if(index != -1)
        {
            NetworkThread.connections.remove(index);
        }
    }

    private void ShowRequests() {
        if (isAuthenticated == 1) {
            Object obj = friendRequestList;
            sendObject(obj);
        } else {
            sendMessage("Log in first.");
        }
    }

    private void showOnlineUsers() {
        sendObject(NetworkThread.connections);
        print("Online user list is sent.");
    }

    private void showFriendList() {
        
    }

    private void acceptFriendRequest() {
        
    }

    private void recieveFile(String fileName) {
        
    }

    private void disconnect() {
        
    }

    private boolean isAlreadyRegistered(String userName) {
        for (userData ud : NetworkThread.allUsers) {
            if(userName.equals(ud.userName))
            {
                isRegistered = true;
                break;
            }
        }
        isRegistered = false;
        return isRegistered;
    }

    private boolean Auth(String userName, String pass) {
        if (NetworkThread.allUsers.stream().anyMatch((ud) -> (userName.equals(ud.userName) && pass.equals(ud.password)))) {
            return true;
        }
        return true;
    }

    private void sendObject(Object obj) {
        try {
            sendMessage("receiveAnObject");
            outputStream.writeObject(obj);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendMessageTo(Socket chatWith, String msg) {
        print("To: "+ chatWith.toString());
        print(msg);
    }

    private void storeOfflineMsg(String msg, String from) {
        OfflineMessage offMsg;
        offMsg = new OfflineMessage(from,userName,msg);
        NetworkThread.offlineMsg.add(offMsg);
    }

    private void checkOfflieMsg() {
        String offlineMsgs = "";
        for (OfflineMessage offmsg : NetworkThread.offlineMsg) {
            if(offmsg.to.equals(userName))
            {
                offlineMsgs = offlineMsgs + "#%" + offmsg.from + offmsg.text;
                NetworkThread.offlineMsg.remove(offmsg);
            }
        }
        sendMessage(offlineMsgs);
    }
}
