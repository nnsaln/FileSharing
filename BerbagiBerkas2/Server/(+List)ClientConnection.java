package filesharing;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLIENTConnection implements Runnable {

    private ArrayList<CLIENTConnection> alThread;
    private Socket clientSocket;
    private BufferedReader in = null;
    private SocketAddress sa = null;
    //private String namaUser;
    private Object stdin;
    private SocketAddress tmp;

  
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
            DataInputStream clientData = new DataInputStream(clientSocket.getInputStream());
            while ((clientSelection = in.readLine()) != null) {
                System.out.println("ulangi");
                for (int i = 0; i < alThread.size(); i++)
                {
                    System.out.print(alThread.get(i).getSA());
                }
                System.out.print("\n");
                switch (clientSelection) {
                    case "1":
                        //receiveFile();
                        try {
                                

                                String fileName = clientData.readUTF();
                                OutputStream output = new FileOutputStream(fileName);
                                long size = clientData.readLong();
                                byte[] buffer = new byte[1024];
                                while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                                    output.write(buffer, 0, bytesRead);
                                    size -= bytesRead;
                                }

                                output.close();
                                //clientData.close();

                                System.out.println("File "+fileName+" received from client.");

                            } catch (IOException ex) {
                                System.err.println("Client error. Connection closed.");
                            }
                        break;
                    case "2":
                        String outGoingFileName;
                        while ((outGoingFileName = in.readLine()) != null) {
                            sendFile(outGoingFileName);
                        }
                        break;
                    case "-1":
                        in.close();
                        break;
                   case "3":
                        //List User
                        String tmp = null;
                        for (int i = 0; i < alThread.size(); i++)
                        {
                              tmp = getClisock().getInetAddress().toString();
                              //System.out.println("Isi tmp:"+tmp);
                        }
                  			File file = new File("ListUser.txt");
                   
                  			// if file doesnt exists, then create it
                  			if (!file.exists()) {
                  				file.createNewFile();
                  			}
                   
                  			FileWriter fw = new FileWriter(file.getAbsoluteFile());
                  			BufferedWriter bw = new BufferedWriter(fw);
                  			bw.write(tmp);
                  			bw.close();
                        break;
                       
                    case "4":
                        //Ganti path folder sesuai server
                        File folder = new File("C:/Users/An Nisa/Documents/NetBeansProjects/FileSharing/");
                        File[] listOfFiles = folder.listFiles();

                            for (int i = 0; i < listOfFiles.length; i++) {
                              if (listOfFiles[i].isFile()) {
                                System.out.println("File " + listOfFiles[i].getName());
                              } else if (listOfFiles[i].isDirectory()) {
                                System.out.println("Directory " + listOfFiles[i].getName());
                              }
                            }
                        break;   
                    default:
                        System.out.println("Incorrect command received.");
                        //in.close();
                        break;
                }
                
            }
            clientData.close();
            in.close();
            System.out.println("in ditutup");
            synchronized(alThread)
            {
                alThread.remove(this);
            }

        } catch (IOException ex) {
            Logger.getLogger(CLIENTConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendFile(String fileName) {
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
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            System.out.println("File "+fileName+" sent to client.");
        } catch (Exception e) {
            System.err.println("File does not exist!");
        } 
    }

    SocketAddress getSA() {
        return sa; 
        //return namaUser;
    }
    private Socket getClisock() {
        return clientSocket;
    }


}
