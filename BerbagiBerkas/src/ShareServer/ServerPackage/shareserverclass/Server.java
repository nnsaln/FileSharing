package shareserver;

import FileEvent.FileEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private ObjectInputStream inputStream = null;
    private FileEvent fileEvent;
    private File dstFile = null;
    private FileOutputStream fileOutputStream = null;

    public Server() {

    }

    /**
     * Accepts socket connection
     */
    public void doConnect() {
        try {
            ArrayList<ThreadClient> allThread = new ArrayList<>();
            serverSocket = new ServerSocket(4445);
            System.out.println("Menunggu panggilan...");
            socket = serverSocket.accept();
            System.out.println(socket.getInetAddress().toString()+" masuk\r\n");
                synchronized(allThread)
                {
                    ThreadClient tc = new ThreadClient(socket,allThread);
                    allThread.add(tc);
                    Thread t = new Thread(tc);
                    t.start();
                }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Server server = new Server();
        server.doConnect();
        //server.downloadFile();
    }
}

