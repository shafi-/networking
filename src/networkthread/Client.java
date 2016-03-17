/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkthread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author mash
 */
public class Client {
    private static Socket client;
    private static final String host = "localhost";
    private static int port = 5555;
    private static Scanner sc = new Scanner(System.in);
    private static String msg;
    private static String name;
    private static OutputStream os;
    private static InputStream is;
    private static byte[] b;
    private static BufferedReader bufR;
    private static PrintWriter prw;
    
    public static void main(String[] args)
    {
        try {
            
            try {
                client = new Socket(host, port);
                print("Connected with Server through : "+ host + ":"+port);
            } catch (IOException iOException) {
                print("Client connection is not established.");
            }
            os = client.getOutputStream();
            is = client.getInputStream();
            bufR = new BufferedReader(new InputStreamReader(is));
            prw = new PrintWriter(new OutputStreamWriter(os));
            
            while(true)
            {    
                task();
            }
            
        } catch (IOException ex) {
            System.out.println("Error in Client.main()");
            //Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private static void task() {
        try {
            msg = sc.next();
            
            prw.write(msg);
            prw.flush();
            
            msg = bufR.readLine();
            System.out.println("Server message: "+b.toString());
            
        } catch (IOException ex) {
            print("Error in sending/receiving message to/from server.");
//Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void print(Object obj)
    {
        System.out.println(obj.toString());
    }
}
