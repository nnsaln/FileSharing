package shareserver;

import FileEvent.FileEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ASUS-PC
 */
public class ThreadClient implements Runnable {
    private Socket socket;
    private ArrayList<ThreadClient> alThread;
    private File dstFile = null;
    private FileOutputStream fileOutputStream = null;
    private DataInputStream dis = null;
    private SocketAddress sa = null;
    private FileEvent fileEvent;
    private ObjectInputStream inputStream = null;
        
    
    public ThreadClient(Socket socket, ArrayList<ThreadClient> alThread) throws SocketException
    {
        this.socket = socket;
        this.alThread=alThread;
        this.sa = socket.getRemoteSocketAddress();
        //System.out.println(sockClient.getSendBufferSize() + " " + sockClient.getReceiveBufferSize());
    }
    
    /*
     * Reading the FileEvent object and copying the file to disk.
     */
    /*public void downloadFile() {
        try {
            fileEvent = (FileEvent) inputStream.readObject();
            if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
                System.out.println("Error occurred ..So exiting");
                System.exit(0);
            }
            String outputFile = fileEvent.getDestinationDirectory() + fileEvent.getFilename();
            if (!new File(fileEvent.getDestinationDirectory()).exists()) {
                new File(fileEvent.getDestinationDirectory()).mkdirs();
            }
            dstFile = new File(outputFile);
            fileOutputStream = new FileOutputStream(dstFile);
            fileOutputStream.write(fileEvent.getFileData());
            fileOutputStream.flush();
            fileOutputStream.close();
            System.out.println("Output file : " + outputFile + " is successfully saved ");
            Thread.sleep(3000);
            System.exit(0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/
    
    @Override
    public void run()
    {
        try {
            /*bos=new BufferedOutputStream(getSockClient().getOutputStream());
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
            }*/
            inputStream = new ObjectInputStream(socket.getInputStream());
            fileEvent = (FileEvent) inputStream.readObject();
            if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
                System.out.println("Error occurred ..So exiting");
                System.exit(0);
            }
            String outputFile = fileEvent.getDestinationDirectory() + fileEvent.getFilename();
            if (!new File(fileEvent.getDestinationDirectory()).exists()) {
                new File(fileEvent.getDestinationDirectory()).mkdirs();
            }
            dstFile = new File(outputFile);
            fileOutputStream = new FileOutputStream(dstFile);
            fileOutputStream.write(fileEvent.getFileData());
            fileOutputStream.flush();
            fileOutputStream.close();
            System.out.println("Output file : " + outputFile + " is successfully saved ");
            Thread.sleep(3000);
            System.exit(0);
            getSockClient().close();
            synchronized(this.alThread)
            {
                this.alThread.remove(this);
            }
        } catch (IOException ex) {
            Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*public void send(String msg) throws IOException
    {
        //this.bos.write(msg.getBytes());
        //this.bos.flush();
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
        return socket;
    }

    /**
     * @param sockClient the sockClient to set
     */
    public void setSockClient(Socket sockClient) {
        this.socket = socket;
    }
    
}
