/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkthread;

import java.io.Serializable;
import java.net.Socket;

/**
 *
 * @author mash
 */
public class userPublicData implements Serializable {
    public String userName;
    public int userId;
    transient public Socket socket;

    public userPublicData(int id, String name, Socket s) {
        userName = name;
        userId = id;
        this.socket = s;
    }
    
}
