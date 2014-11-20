/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threadserver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author kromatin
 */

class Handler extends Thread
{
    private Socket clisock = null;
    //public Socket[] sockArr = new Socket[10];
    private ArrayList<Handler> sockArr;
    public Handler(){}
    public Handler(Socket clisock, ArrayList<Handler> sockArr)
    {
        this.clisock = clisock;
        this.sockArr = sockArr;
    }
    
    public void setArr(ArrayList<Handler> sockArr)
    {
        this.sockArr = sockArr;
    }
    
    @Override
    public void run()
    {
        try {
            System.out.println("Client masuk: " + getClisock().getInetAddress());
            BufferedWriter res = new BufferedWriter(new OutputStreamWriter(getClisock().getOutputStream()));
            BufferedReader req = new BufferedReader(new InputStreamReader(getClisock().getInputStream()));
            String tmp;
            
            while(true)
            {
                for (int i = 0; i < this.sockArr.size(); i++)
                {
                    System.out.print(sockArr.get(i).getClisock().getInetAddress() + " - ");
                    
                }
                System.out.print("\n");
                if ("exit".equals((tmp = req.readLine()))) break;
                tmp = getClisock().getInetAddress() + ": " + tmp;
                res.write("Balasan server: " + tmp + "\n");
                res.flush();
            }   
            res.close();
            req.close();
            getClisock().close();
            synchronized(this.sockArr)
            {
                sockArr.remove(this);
            }
        } catch (IOException ex) {
            Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Client " + getClisock().getInetAddress() + " terputus");
    }

    /**
     * @return the clisock
     */
    public Socket getClisock() {
        return clisock;
    }
    
}


public class ThreadServer {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        int maxcli = 10;
        ServerSocket servsock = new ServerSocket(33445);
        Handler t = new Handler();
        //Socket[] sockArr = new Socket[maxcli];
        ArrayList<Handler> sockArr = new ArrayList();
        while(true)
        {
            
            Socket clisock = servsock.accept();
            if (sockArr.size() <= 3)
            {
                t = new Handler(clisock, sockArr);
                t.start();
                synchronized(sockArr)
                {
                    sockArr.add(t);
                    for (int i = 0; i < sockArr.size(); i++)
                        sockArr.get(i).setArr(sockArr);
                }
            }
            else clisock.close();
        }
        System.out.println("Selesai");
        servsock.close();
    }
    
}
