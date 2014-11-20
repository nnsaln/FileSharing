/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleserver;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ASUS-PC
 */
public class SimpleServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            ArrayList<ThreadClient> allThread = new ArrayList<>();
            ServerSocket ssServer = new ServerSocket(6060);
            System.out.println("Menunggu panggilan...");
            while(true)
            {
                Socket sockClient = ssServer.accept();
                System.out.println(sockClient.getInetAddress().toString()+" masuk\r\n");
                synchronized(allThread)
                {
                    ThreadClient tc = new ThreadClient(sockClient,allThread);
                    allThread.add(tc);
                    Thread t = new Thread(tc);
                    t.start();
                }
            }
            //ssServer.close();
            
        } catch (IOException ex) {
            Logger.getLogger(SimpleServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
