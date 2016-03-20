package networkthread;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
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
    private boolean debug;
    
    public Server(Socket s, int id) {
        userId = id;
        print("Create new Server instance...");
        client = s;
        isAuthenticated = 0;
    }

    @Override
    public void run() {
        initialize();
        boolean stopOrRun = true;
        String msg = null;
        sendMessage("Welcome!");
        
        checkOfflieMsg();
        checkOfflineFile();
        
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
                case "sendmsgto": {  //receiveMessage
                    
                    String msgto = (String) getMessage();
                    
                    Socket chatWith;
                    
                    //check if friend then check if online 
                    
                    for (userPublicData upd : NetworkThread.connections) {
                        if(upd.userName.equals(msgto))
                        {
                            chatWith = upd.socket;
                            sendMessage("Connection established with "+upd.userName);
                            msg = (String) getMessage();
                            if(sendMessageTo(chatWith,msg))
                                sendMessage("Message sent to "+upd.userName);                            
                            break;
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
                    sendFriendRequest();
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
                    // Code for reject
                    rejectFriendRequest();
                    break;
                }
                case "accept": {
                    acceptFriendRequest();
                    break;
                }
                case "sendfile": {   
                    recieveFileFromClientAndSendTo();                    
                    break;
                }

                case "exit": {
                    sendMessage("Disconnected!!!");
                    removeFromOnline();
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
            if(debug)
            {
                int index = NetworkThread.allUsers.indexOf(ud);
                print("Debug: new registered user: "+ NetworkThread.allUsers.get(index).userName);
            }
            
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

    private void recieveFileFromClientAndSendTo() {
        try {
            
            String recver = (String) getMessage();
            //File file = (File) inputStream.readObject();
            byte[] content = (byte[]) inputStream.readObject();
            
            //find recver
            for(userPublicData upd : NetworkThread.connections)
            {
                if(recver.equals(upd.userName))
                {
                    ObjectOutputStream oos = new ObjectOutputStream(upd.socket.getOutputStream());
                    
                    oos.writeObject("rcvfile");
                    oos.writeObject(userName);
                    //oos.writeObject(file);
                    oos.writeObject(content);
                    break;
                }
                else
                {
                    String sender = (String) getMessage();
                    print(sender+" sent a file.");
                    StoredFile sf = new StoredFile(sender,userName, NetworkThread.storedFileId);
                    File file = new File((System.getProperty("user.dir")+"/"+NetworkThread.storedFileId ));
                    Files.write(file.toPath(),content);
                }
            }
            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void recieveFileFromFriend() {
        try {
            String sender = (String) getMessage();
            File file = (File) inputStream.readObject();
            //sendToClient();
            sendMessage("rcvfile");
            sendMessage(sender);
            outputStream.writeObject(file);
            
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
            outputStream.writeObject(obj);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean sendMessageTo(Socket chatWith, String msg) {
        
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(chatWith.getOutputStream());
            oos.writeObject(msg);
            print("To: "+ chatWith.toString()+" : "+msg);
            return true;
        } catch (IOException ex) {
            sendMessage("Message cannot be delivered.");
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                oos.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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

    private void removeFromOnline() {
        for (userPublicData upd : NetworkThread.connections) {
            if(upd.userId == userId && upd.userName.equals(userName))
            {
                NetworkThread.connections.remove(upd);
            }
        }
    }

    private void sendFriendRequest() {
        
        String user = (String) getMessage();
                    
        for ( userPublicData upd : NetworkThread.connections) {
            if(upd.userName.equals(user))
            {
                try {
                    
                    ObjectOutputStream oos = new ObjectOutputStream(upd.socket.getOutputStream());
                    oos.writeObject("rcvfreq");
                    oos.writeObject(userName);
                    print(userName + " sent a friend request to "+ upd.userName);
                    oos.close();
                
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        sendMessage("Friend request sent to " + user);
    }

    private void checkOfflineFile() {
        //Check offline message
    }

    private void rejectFriendRequest() {
        
        String user = (String) getMessage();
        
        for (userPublicData upd : NetworkThread.connections) {
            if(upd.userName.equals(user))
            {
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(upd.socket.getOutputStream());
                    oos.writeObject(userName + " rejected your friend request.");
                    oos.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        sendMessage("Reject message sent to " + user);
    }
    
    private void acceptFriendRequest() {
           
        String user = (String) getMessage();
        
        for (userPublicData upd : NetworkThread.connections) {
            if(upd.userName.equals(user))
            {
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(upd.socket.getOutputStream());
                    oos.writeObject(userName + " accepted your friend request.");
                    oos.close();
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        sendMessage("Now " + user + " you are friend");
    }
}
