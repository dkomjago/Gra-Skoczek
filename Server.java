package server;


import java.io.BufferedReader;
import java.io.Console;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Daniel Komjago
 */
/**
 * Główna klasa serwera
 */
public class Server {

    /**
     * Port serwera
     */
    private int serverPort;

        public static void main(String[] args) {
            new Server().runServer();
        }

    /**
     * Uruchomienie serwera
     */
    public void runServer() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("configs/ipconfig.cfg"));
            serverPort = Integer.parseInt(br.readLine());
            ServerSocket serverSocket = new ServerSocket(serverPort);
            System.out.println("Server is up! Waiting for connections...");
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ServerThread(socket)).start();
            }
        }
        catch (IOException e){
            System.out.println("Unable to start the server");
            System.err.println(e);
        }
    }

}
