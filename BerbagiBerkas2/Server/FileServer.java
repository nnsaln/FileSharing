package serverberbagiberkas2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class FileServer {
    private static int servport;
    private static ServerSocket serverSocket;
    private static Socket clientSocket = null;
    private static ArrayList<CLIENTConnection> alThread = new ArrayList();

    public static void main(String[] args) throws IOException {
        System.out.print("Masukkan port yg ingin digunakan: ");
        BufferedReader port = new BufferedReader(new InputStreamReader(System.in));
        servport = Integer.parseInt(port.readLine());
        try {
            serverSocket = new ServerSocket(servport);
            System.out.println("Server berjalan pada port " + servport + ".");
            System.out.println("Menunggu permintaan...");
        } catch (Exception e) {
            System.err.println("Port already in use.");
            System.exit(1);
        }

        while (true) {
            try {
                clientSocket = serverSocket.accept();
                System.out.println("Accepted connection : " + clientSocket);
                
                Thread t;
                CLIENTConnection curr = new CLIENTConnection(clientSocket, alThread);
                t = new Thread(curr);
                t.start();
                synchronized(alThread)
                {
                    alThread.add(curr);
                    for (int i = 0; i < alThread.size(); i++)
                    {
                        alThread.get(i).setAL(alThread);
                    }
                }

            } catch (Exception e) {
                System.err.println("Error in connection attempt.");
            }
        }
        //serverSocket.close();
    }

}
