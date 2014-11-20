/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ASUS-PC
 */
public class ThreadClient implements Runnable {
    private Socket sockClient;
    private ArrayList<ThreadClient> alThread;
    private BufferedReader br = null;
    private BufferedOutputStream bos = null;
    private DataInputStream dis = null;
    private SocketAddress sa = null;
        
    
    public ThreadClient(Socket sockClient, ArrayList<ThreadClient> alThread)
    {
        this.sockClient=sockClient;
        this.alThread=alThread;
        this.sa = sockClient.getRemoteSocketAddress();
    }
    
    @Override
    public void run()
    {
        try {
            bos=new BufferedOutputStream(getSockClient().getOutputStream());
            dis=new DataInputStream(getSockClient().getInputStream());
            bos.write("Selamat datang...\r\n".getBytes());
            bos.flush();
            br = new BufferedReader(new InputStreamReader(dis));
            String msg;
            while((msg = br.readLine()) != null)
            {
                msg=this.sa.toString() + ":" + msg+"\r\n";
                this.sendMessage(msg);
                bos.flush();
            }
            System.out.println("Koneksi terputus");
            bos.close();
            getSockClient().close();
            synchronized(this.alThread)
            {
                this.alThread.remove(this);
            }
        } catch (IOException ex) {
            Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void send(String msg) throws IOException
    {
        this.bos.write(msg.getBytes());
        this.bos.flush();
    }
    
    public synchronized void sendMessage(String msg) throws IOException
    {
        for(int i=0;i<this.alThread.size();i++)
        {
            ThreadClient tc = this.alThread.get(i);
            tc.send(msg);
        }
    }

    /**
     * @return the sockClient
     */
    public Socket getSockClient() {
        return sockClient;
    }

    /**
     * @param sockClient the sockClient to set
     */
    public void setSockClient(Socket sockClient) {
        this.sockClient = sockClient;
    }
    
}
