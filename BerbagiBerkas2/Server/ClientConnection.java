package filesharing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLIENTConnection implements Runnable {

    private ArrayList<CLIENTConnection> alThread;
    private String serverDirPath = "C:/Users/An Nisa/Documents/NetBeansProjects/FileSharing/";
    private Socket clientSocket;
    private BufferedReader in = null;
    private SocketAddress sa = null;
    private BufferedOutputStream bos = null;
    private String namaUser;
    private Object stdin;
    private String listFiles;
    //private Object osList;

  
    public CLIENTConnection(Socket client, ArrayList<CLIENTConnection> alThread) throws SocketException
    {
        this.clientSocket = client;
        this.alThread=alThread;
        this.sa = client.getRemoteSocketAddress();
        //this.namaUser=namaUser;
    }
    
    public void setAL(ArrayList<CLIENTConnection> alThread)
    {
        this.alThread = alThread;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));
            String clientSelection;
            int bytesRead;
            boolean exit = false;
            DataInputStream clientData = new DataInputStream(clientSocket.getInputStream());
            DataInputStream clientData2 = new DataInputStream(clientSocket.getInputStream());
            while ((clientSelection = in.readLine()) != null) {
                System.out.println("ulangi");
                for (int i = 0; i < alThread.size(); i++)
                {
                    System.out.print(alThread.get(i).getSA());
                }
                System.out.print("\n");
                switch (clientSelection) {
                    case "1":
                        try {
                                String fileName = clientData.readUTF();
                                OutputStream output = new FileOutputStream(fileName);
                                long size = clientData.readLong();
                                byte[] buffer = new byte[1024];
                                while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                                    output.write(buffer, 0, bytesRead);
                                    output.flush();
                                    size -= bytesRead;
                                }
                                output.close();
                                System.out.println("File "+fileName+" received from client.");
                            } catch (IOException ex) {
                                System.err.println("Client error. Connection closed.");
                            }
                        break;
                    case "2":
                        String outGoingFileName;
                        while ((outGoingFileName = in.readLine()) != null) {
                            outGoingFileName = this.serverDirPath + outGoingFileName;
                            sendFile(outGoingFileName, this.clientSocket);
                        }
                        break;
                    case "-1":
                        in.close();
                        break;
                    case "3":
                        OutputStream os = clientSocket.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(os);
                        dos.writeInt(alThread.size());
                        dos.flush();
                  
                        for (int i = 0; i < alThread.size(); i++)
                        {
                            System.out.println(alThread.get(i).getSocket().getInetAddress().toString());
                            try{dos.writeUTF(alThread.get(i).getSocket().getInetAddress().toString());
                            dos.flush();}
                            catch (Exception err)
                            {
                                System.out.println(err.getMessage());
                            }
                        }
                        //dos.close();
                        System.out.println("selesai list");
                        //clientData.reset();
                        break;
                    case "4":
                        File folder = new File("C:/Users/An Nisa/Documents/NetBeansProjects/FileSharing/");
                        File[] listOfFiles = folder.listFiles();
                        System.out.println("List of Files:");
                        
                            OutputStream osList;
                            osList = clientSocket.getOutputStream();
                            DataOutputStream dosList = new DataOutputStream(osList);
                            dosList.writeInt(listOfFiles.length);
                            dosList.flush();
                            for (int j = 0; j < listOfFiles.length; j++)
                            {

                                try
                                {
                                    listFiles = listOfFiles[j].getName();
                                    dosList.writeUTF(listFiles);
                                    dosList.flush();

                                }

                                catch (Exception err)
                                {
                                    System.out.println(err.getMessage());
                                }
                            }
                            //dosList.close();
                            System.out.println("selesai list");    
                        
                        
                        //clientData.reset();
                        break;   
                    
                    case "5":
                        String outGoingFileName2;
                        while ((outGoingFileName2 = in.readLine()) != null) {
                            outGoingFileName2 = this.serverDirPath + outGoingFileName2;
                            sendBroadcast(outGoingFileName2);
                        }
                        break;
                        

                    case "6":
                        exit = true;
                        break;
                    default:
                        System.out.println("Incorrect command received.");
                        break;
                }
                if (exit) break;
                
            }
            clientData.close();
            in.close();
            System.out.println(clientSocket.getInetAddress().toString() + " ditutup");
            synchronized(alThread)
            {
                alThread.remove(this);
            }
            clientSocket.close();

        } catch (IOException ex) {
            Logger.getLogger(CLIENTConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void send(String fileName) throws IOException
    {
       try {
            //handle file read
            File myFile = new File(fileName);
            byte[] mybytearray = new byte[(int) myFile.length()];

            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //bis.read(mybytearray, 0, mybytearray.length);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);

            //handle file send over socket
            OutputStream os = clientSocket.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(myFile.getName());
            dos.flush();
            dos.writeLong(mybytearray.length);
            dos.flush();
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            //dos.close();
            System.out.println("File "+fileName+" sent to client.");
        } catch (Exception e) {
            System.err.println("File does not exist!");
        } 
    }
    
    public synchronized void sendBroadcast(String fileName) throws IOException
    {
        for(int i=0;i<this.alThread.size();i++)
        {
            CLIENTConnection cc = this.alThread.get(i);
            cc.send(fileName);
        }
    }

    public void sendFile(String fileName, Socket clientSocket) {
        try {
            //handle file read
            File myFile = new File(fileName);
            byte[] mybytearray = new byte[(int) myFile.length()];

            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //bis.read(mybytearray, 0, mybytearray.length);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);

            //handle file send over socket
            OutputStream os = clientSocket.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(myFile.getName());
            dos.flush();
            dos.writeLong(mybytearray.length);
            dos.flush();
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            //dos.close();
            System.out.println("File "+fileName+" sent to client.");
        } catch (Exception e) {
            System.err.println("File does not exist!");
        } 
    }
    
    
    SocketAddress getSA() {
        return sa; 
    }
    
    Socket getSocket()
    {
        return this.clientSocket;
    }

}
