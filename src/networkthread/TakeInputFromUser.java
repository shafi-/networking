/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networkthread;

import java.util.Scanner;

/**
 *
 * @author mash
 */
public class TakeInputFromUser implements Runnable {
    Scanner sc = new Scanner(System.in);
    String instruction = "No input";
    public int hasInput = 0;
    public TakeInputFromUser()
    {
        System.out.println("Waiting for input.");
    }
    @Override
    public void run() {
        System.out.println("Ready to take input.");
        instruction = sc.nextLine();
        System.out.println("Input paisi.");
        hasInput = 1;
    }
    
}
