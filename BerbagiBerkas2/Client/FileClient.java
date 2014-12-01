package filesharingclient;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import static java.io.DataInputStream.readUTF;
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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileClient {

    private ArrayList<CLIENTConnection> alThread;
    private static Socket sock;
    private static String fileName;
    private static BufferedReader stdin;
    private static PrintStream os;
    private static String servaddr;
    private static int servport;
    //private ArrayList<namaUser>;
    
    public static void main(String[] args) throws IOException {
        BufferedReader servdetail = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Masukkan alamat server yang dituju: ");
        servaddr = servdetail.readLine();
        System.out.print("Masukkan port server yang dituju: ");
        servport = Integer.parseInt(servdetail.readLine());
        
        try {
            sock = new Socket(servaddr, servport);
            System.out.println("Terhubung dengan server " + servaddr + " pada port " + servport);
            stdin = new BufferedReader(new InputStreamReader(System.in));
            
        } catch (Exception e) {
            System.err.println("Cannot connect to the server, try again later.");
            System.exit(1);
        }
        int bytesRead;
        boolean exit = false;
        InputStream in = sock.getInputStream();
        DataInputStream clientData = new DataInputStream(in);
        DataInputStream clientData2 = new DataInputStream(in);
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
                    fileName = stdin.readLine();
                    os.println(fileName);
                    try 
                    {
                            fileName = clientData.readUTF();
                            OutputStream output = new FileOutputStream(fileName);
                            long size = clientData.readLong();
                            byte[] buffer = new byte[1024];
                            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                                output.write(buffer, 0, bytesRead);
                                output.flush();
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
                    String user;
                    System.out.print("List User:\n");
                    int size = clientData.readInt();
                    for (int i = 0; i < size; i++)
                    {
                        System.out.println(clientData.readUTF());
                    }
                    System.out.println("selesai list user");
                    break;
                case 4:
                    os.println("4");
                    String list;
                    System.out.print("List Files:\n");
                    int sizeList = clientData2.readInt();
                    for (int i = 0; i < sizeList; i++)
                    {
                        System.out.println(clientData2.readUTF());
                        //readUTF().clear();
                        
                    }
                    
                    System.out.println("selesai list files");
                    break;
                case 5:
                    os.println("5");
                    System.err.print("Enter file name: ");
                    fileName = stdin.readLine();
                    os.println(fileName);
                    try 
                    {
                            fileName = clientData.readUTF();
                            OutputStream output = new FileOutputStream(fileName);
                            long size2 = clientData.readLong();
                            byte[] buffer = new byte[1024];
                            while (size2 > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size2))) != -1) {
                                output.write(buffer, 0, bytesRead);
                                output.flush();
                                size2 -= bytesRead;
                                
                            }
                            output.close();
                            System.out.println("File "+fileName+" received from Server.");
                    } catch (IOException ex) {
                            Logger.getLogger(CLIENTConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                    
                case 6:
                    os.println("6");
                    exit = true;
                    break;
                default:
                    //os.println("7");
                    System.out.println("Incorrect command received.");
                    break;
                }
                if (exit) break;
            }
        in.close();
        } catch (Exception e) {
            System.err.println("not valid input" + e.getMessage());
        }
        sock.close();
    }

    public static String selectAction() throws IOException {
        System.out.println("1. Upload file.");
        System.out.println("2. Download file.");
        System.out.println("3. List Active User.");
        System.out.println("4. List File.");
        System.out.println("5. Broadcast File.");
        System.out.println("6. Leave Community.");
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
            //dos.close();
            System.out.println("File "+fileName+" sent to Server.");
           
        } catch (Exception e) {
            System.err.println("File does not exist!:" + e.getMessage());
        }
    }
}
