package filesharingclient;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileClient {

    private ArrayList<CLIENTConnection> alThread;
    private ArrayList<FileClient> ipThread;
    private static Socket sock;
    private static String fileName;
    private static BufferedReader stdin;
    private static PrintStream os;
    private SocketAddress sa = null;
    //private ArrayList<namaUser>;
    public FileClient(Socket sock, ArrayList<FileClient> ipThread) throws SocketException
    {
        this.sock = sock;
        this.ipThread=ipThread;
        this.sa = sock.getRemoteSocketAddress();
        //this.namaUser=namaUser;
    }
    
    public SocketAddress getIP() {
        return sa; 
        //return namaUser;
    }
    
      public void setIP(ArrayList<FileClient> ipThread)
    {
        this.ipThread = ipThread;
    }
    
    
   
    public static void main(String[] args) throws IOException {
        try {
            sock = new Socket("localhost", 4444);
            stdin = new BufferedReader(new InputStreamReader(System.in));
            
        } catch (Exception e) {
            System.err.println("Cannot connect to the server, try again later.");
            System.exit(1);
        }
        int bytesRead;
        InputStream in = sock.getInputStream();
        DataInputStream clientData = new DataInputStream(in);
        os = new PrintStream(sock.getOutputStream());

        try {
            while(true)
            {
                switch (Integer.parseInt(selectAction())) {
                case 1:
                    os.println("1");
                    sendFile();
                    break;
                case 2:
                    os.println("2");
                    System.err.print("Enter file name: ");
                    String path;
                    //sur ini pake list folder dari serverku, kalo mau nyoba pake folder servermu sur
                    path = "C:/Users/An Nisa/Documents/NetBeansProjects/FileSharing/";
                    fileName = path + stdin.readLine();
                    //System.out.print(fileName);
                    os.println(fileName);
                    try 
                    {
                            fileName = clientData.readUTF();
                            OutputStream output = new FileOutputStream(fileName);
                            long size = clientData.readLong();
                            byte[] buffer = new byte[1024];
                            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                                output.write(buffer, 0, bytesRead);
                                size -= bytesRead;
                            }
                            output.close();
                            System.out.println("File "+fileName+" received from Server.");
                    } catch (IOException ex) {
                            Logger.getLogger(CLIENTConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case 3:
                  os.println("3");
                  System.out.print("List User:\n");
                  //sur ini pake list folder dari serverku, kalo mau nyoba pake folder servermu sur
                  BufferedReader list = new BufferedReader(new FileReader("C:/Users/An Nisa/Documents/NetBeansProjects/FileSharing/ListUser.txt"));
                  String line;
                    while((line = list.readLine()) != null)
                    {
                        System.out.println(line);
                    }
                    list.close();
                  break;
                  
                case 4:
                  os.println("4");
                  //sur ini pake list folder dari serverku, kalo mau nyoba pake folder servermu sur
                  File folder = new File("C:/Users/An Nisa/Documents/NetBeansProjects/FileSharing/");
                        File[] listOfFiles = folder.listFiles();
                            System.out.println("List of Files:");
                            for (int i = 0; i < listOfFiles.length; i++) {
                              if (listOfFiles[i].isFile()) 
                              { 
                                System.out.println(listOfFiles[i].getName());
                              } else if (listOfFiles[i].isDirectory()) {
                                //System.out.println("Directory " + listOfFiles[i].getName());
                                continue;
                              }
                            }
                        break;   
               
                default:
                    os.println("9");
                    System.out.println("Incorrect command received.");
                    break;
                }
            }
        in.close();
        } catch (Exception e) {
            System.err.println("not valid input");
        }


        sock.close();
    }

    public static String selectAction() throws IOException {
        System.out.println("\n");
        System.out.println("1. Upload file.");
        System.out.println("2. Download file.");
        System.out.println("3. List Active User.(Refresh)");
        System.out.println("4. List Directory.");
        System.out.print("\nMake selection: ");

        return stdin.readLine();
    }

    public static void sendFile() {
        try {
            
            System.err.print("Enter file name: ");
            fileName = stdin.readLine();
            System.out.println(fileName);
            File myFile = new File(fileName);
            byte[] mybytearray = new byte[(int) myFile.length()];

            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //bis.read(mybytearray, 0, mybytearray.length);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);

            OutputStream os = sock.getOutputStream();

            //Sending file name and file size to the server
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeUTF(myFile.getName());
            dos.writeLong(mybytearray.length);
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            System.out.println("File "+fileName+" sent to Server.");
           
        } catch (Exception e) {
            System.err.println("File does not exist!:" + e.getMessage());
        }
    }
   /* 
    public void listUser()
    {
        for(int i=0; i<alThread.size(); i++)
        {
            System.out.print(alThread.get(i).getNamaClient().getInetAddress() + " - ");
            
        }
    }
*/
    //public static void receiveFile(String fileName) {
        
    //}
}
