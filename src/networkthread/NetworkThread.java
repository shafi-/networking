/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkthread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mash
 */
public class NetworkThread {

    public static ServerSocket ss ;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            boolean cont = true;
            try {
                ss = new ServerSocket(5555);
                print("Success!!! Server is running on port 5555. ");
                
            } catch (IOException iOException) {
                print("Server is not started. Error!!! ");
                Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, iOException);
                cont = false;
            }
            
            while(cont)
            {
                print("Wait for new connection...");
                Socket s = ss.accept();
                Server server = new Server(s);
                Thread t = new Thread(server,"client");
                t.start();
            }
            
        } catch (IOException ex) {
            System.out.println("Server initialization problem! Try again");
            //Logger.getLogger(NetworkThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private static void print(Object obj)
    {
        System.out.println(obj.toString());
    }
    
}
