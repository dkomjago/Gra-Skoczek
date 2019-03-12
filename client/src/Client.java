import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Properties;

/**
 * @author Daniel Komjago
 */

/**
 * Główna klasa applikacji klienta
 */
public class Client {

    /**
     * IP adres serwera
     */
    private static String IPAddress;
    /**
     * Port serwera
     */
    private static int Port;
    /**
     * stan offline/online
     */
    private static Boolean online;
    /**
     * Socket serwera
     */
    private static Socket serverSocket;

    /**
     * Główna metoda klienta
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                runGame(connectToServer());
            }
        });
    }

    /**
     * Uruchomienie gry
     * @param socket Socket serwera
     */
        private static void runGame (Socket socket) {
            Object[] options={
                    "Online","Offline"
            };
            switch(JOptionPane.showOptionDialog(null, "Online or offline?", "Do you want to play online?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[1])){
                case JOptionPane.YES_OPTION:
                    serverSocket = socket;
                    if(serverSocket!=null) {
                        online=true;
                        getSettings(serverSocket);
                    }
                    else {
                        options[0]="Yes";
                        options[1]="No";
                        System.out.println("Offilne");
                        switch(JOptionPane.showOptionDialog(null, "Connection could not be established. Do you want to play offline?", "Do you want to play offline?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[1])){
                            case JOptionPane.YES_OPTION:
                                online=false;
                                break;
                            case JOptionPane.NO_OPTION:
                                System.exit(0);
                                break;
                            default:
                                System.exit(0);
                                break;
                        }
                    }
                    break;
                case JOptionPane.NO_OPTION:
                    online=false;
                    break;
                default:
                    System.exit(0);
                    break;
            }
            SessionConfig.read();
            CanvasManager.getInstance();
        }


    /**
     * Żądanie ustawień gry serwara
     * @param serverSocket socket serwera
     */
        private static void getSettings(Socket serverSocket){
                Properties prop = new Properties();
                OutputStream output = null;
                try {
                    OutputStream os = serverSocket.getOutputStream();
                    output = new FileOutputStream("configs/onlineConfig.properties");
                    PrintWriter pw = new PrintWriter(os, true);
                    pw.println("GET_SETTINGS");
                    InputStream is = serverSocket.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String currentLine;
                    while ((currentLine = br.readLine()) != null && currentLine.length() != 0) {
                        String[] set = currentLine.split(" ");
                        prop.setProperty(set[0], set[1]);
                    }
                    prop.store(output, null);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                finally {
                    if (output != null) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        }


    /**
     * Połączenie się z serwerem
     * @return socket serwera
     */
    private static Socket connectToServer() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("configs/ipconfig.cfg"));
            IPAddress=br.readLine();
            Port=Integer.parseInt(br.readLine());
            Socket serverSocket = new Socket(IPAddress, Port);
            OutputStream os = serverSocket.getOutputStream();
            PrintWriter pw = new PrintWriter(os, true);
            pw.println("LOGIN");
            InputStream is = serverSocket.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            if(br.readLine().contains("LOGGED_IN")){
                return serverSocket;
            }
            else{
                return null;
            }
        }
        catch (Exception e) {
            System.out.println("Connection could not be opened..");
            System.out.println("error: "+e);
        }
        return null;
    }

    /**
     * Metoda zwracająca stan połączenia
     * @return stan połączenia
     */
    static Boolean isOnline(){
        if(online)
            return true;
        else
            return false;
    }

    /**
     * Metoda ządająca od serwera Soket
     * @return socket serwera
     */
    static Socket getSocket()
    {
        return serverSocket;
    }

}
