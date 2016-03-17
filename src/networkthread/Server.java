package networkthread;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

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
    private ObjectInputStream isr ;
    private ObjectOutputStream osw;
    
    public Server(Socket s)
    {
        try {
            client = s;
            isr = new ObjectInputStream(client.getInputStream());
            osw = new ObjectOutputStream(client.getOutputStream());

        } catch (IOException ex) {
            System.out.println("Server can not be instantiated.");
            //Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        boolean stopOrRun = true;
        String msg = null;
        char[] msgs = null;
        
        BufferedReader bufR = new BufferedReader(new InputStreamReader(isr));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(osw)); 
        
        while(stopOrRun)
        {
            try {
                int task;
                task = bufR.read();
                
                switch (task) {
                    case 1:
                        {
                            bufR.read(msgs);
                            System.out.println(msgs);
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
                            try {
                                bufR.read(msgs);
                                System.out.println(msgs.toString());                        
                            } catch (Exception e) {
                                print("Error reading client's message!");
                            }
                            break;
                        }
                }
                
            } catch (IOException ex) {
                //Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        System.out.println("Server is going off!");
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void init(Thread t) {
        t.start();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private static void print(Object obj)
    {
        System.out.println(obj.toString());
    }
    
}
