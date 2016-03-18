package networkthread;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mash
 * 
 */

public class Server implements Runnable{

    private Socket client;
    private ObjectInputStream inputStream ;
    private ObjectOutputStream outputStream;
    
    public Server(Socket s)
    {
        print("Create new Server instance...");
        client = s;
    }
 
    @Override
    public void run() {
        initialize();
        boolean stopOrRun = true;
        String msg = null;
        char[] msgs = null;
        
        while(stopOrRun)
        {
            sendMessage("Welcome ");
            msg = (String) getMessage();
            print(msg);
            sendMessage("Server got: "+msg);
            
            /* 
            int taskCode;
            
            try {    
                taskCode = (int) inputStream.readObject();
                
                switch (taskCode) {
                    case 1:
                    {
                        msg = (String) getMessage();
                        System.out.println(msg);
                        break;
                    }
                    case 0:
                    {
                        break;
                        //Send file
                    }
                    
                    case -1:
                    {
                        print("-1 is got in stream.");
                    }
                    
                    default:
                    {  
                        print(getMessage());
                        break;
                    }
                }
                
            }           
            catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            */
        }
        System.out.println("Server is going off!");
        sendMessage("Disconnected!!!");
        closeStreams();
    }

    
    private void print(Object obj)
    {
        System.out.println(obj.toString());
    }
    
    private boolean sendMessage(String message){
        try {
            outputStream.writeObject(message);
            outputStream.flush();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    private Object getMessage()
    {
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
    
    
    private void closeStreams(){
        try {
            inputStream.close();        
            outputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
