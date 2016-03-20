/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkthread;

import java.io.Serializable;


/**
 *
 * @author mash
 */
class OfflineMessage implements Serializable{
    String from;
    String to;
    String text;

    public OfflineMessage(String frm, String rcv, String msg) {
        from = frm;
        to = rcv;
        text = msg;
    }
    
}
