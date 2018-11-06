package server;

import java.io.*;
import java.util.*;
/**
 * @author Daniel Komjago
 */
/**
 * Klasa odpowiedzialna za zarządzanie serwerem
 */
public final class ServerCommands {

    /**
     * Liczba klientów
     */
    private static int clientNumber =0;

    /**
     * Czy serwer przyjmuje połączenia
     */
    private static boolean acceptingClients= true;

    /**
     * Przechowywanie pliku konfiguracyjnego
     */
    private static Properties config;

    /**
     * Odpowiedż serwera na komendę
     * @param command komenda(żądanie)
     * @return odpowiedż serwera na żądanie
     */
    public static String serverAction(String command){
        String serverCommand = command;
        String originalCommand= command;
        System.out.println(command);

        if(command.contains("GET_LEVEL:")){
            originalCommand=command;
            serverCommand=("GET_LEVEL");
        }
        if(command.contains("GAME_SCORE:")){
            originalCommand=command;
            serverCommand="GAME_SCORE";
        }

        String serverMessage;
        switch (serverCommand){
            case "LOGIN":
                serverMessage=login();
                break;
            case "GET_SETTINGS":
                serverMessage=getSettings();
                break;
            case "GET_RULES":
                serverMessage=getRules();
                break;
            case "GET_HIGH_SCORES":
                serverMessage=getHighScores();
                break;
            case "GET_LEVEL":
                String str[] = originalCommand.split(":");
                serverMessage=getLevel(Integer.parseInt(str[1]));
                break;
            case "GAME_SCORE":
                String str1[] = originalCommand.split(":");
                String str2[] = str1[1].split("@");
                serverMessage=addScore(str2[0],Integer.parseInt(str2[1]));
                break;
            case "LOGOUT":
                serverMessage=logout();
                break;
            case "CONNECTION_CLOSED":
                serverMessage=connectionClosed();
                break;
            default:
                serverMessage="INVALID_COMMAND";
        }
        return serverMessage;
    }

    /**
     * Logowanie użytkownika
     * @return odpowiedż
     */
    private static String login(){
        String serverMessage;
        if(acceptingClients) {
            serverMessage="LOGGED_IN "+clientNumber+"\n";
            clientNumber++;
        }
        else{
            serverMessage="CONNECTION_REJECTED";
        }
        return serverMessage;
    }

    /**
     * Metoda wysyłająca konfiguracje gry z serwera
     *  @return odpowiedż
     */
    private static String getSettings(){
        StringBuilder sb = new StringBuilder();
        config = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("configs/config.properties");
            config.load(input);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        catch (NumberFormatException ex){
            ex.printStackTrace();
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        for(Object p : config.keySet())
        {
            sb.append(p.toString()+" "+config.getProperty(p.toString())+System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * Metoda wysyłająca zasady gry z  serwera
     * @return odpowiedż
     */
    private static String getRules(){
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("configs/rules.txt"))){
            String currentLine;
            while ((currentLine = br.readLine()) != null && currentLine.length() != 0) {
                sb.append(currentLine+System.lineSeparator());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return sb.toString();
    }

    /**
     * Metoda wysyłająca listę wyników z serwera
     * @return odpowiedż
     */
    private static String getHighScores(){
        Properties highscores = new Properties();
        TreeMap<Integer,String> scoreTab = new TreeMap<>(Collections.reverseOrder());
        InputStream input = null;
        try {
            input = new FileInputStream("configs/highscores.properties");

            highscores.load(input);
            for(Object p : highscores.keySet())
            {
                scoreTab.put(Integer.valueOf(highscores.getProperty(p.toString())),p.toString());
            }
        }
        catch (IOException ex)
        {
            System.out.println("Wypisanie wynikow nie powiodlo sie, brak pliku");
            ex.printStackTrace();
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        for(Object p : highscores.keySet())
        {
            scoreTab.put(Integer.valueOf(highscores.getProperty(p.toString())),p.toString());
        }
        String message = printFirstEntries(Integer.parseInt(config.getProperty("highscoreDisplayCount")),scoreTab);
        return message;
    }

    /**
     * Metoda wypisująca wskazaną liczbę wyników z tablicy
     * @param max liczba wyników
     * @param source pełna tablica wyników
     * @return łańcuch z wynikami
     */
    public static String printFirstEntries(int max, SortedMap<Integer,String> source) {
        int count = 0;
        StringBuilder resultBuilder = new StringBuilder();
        for (Map.Entry<Integer,String> entry:source.entrySet()) {
            if (count >= max) break;
            resultBuilder.append(entry.getValue());
            resultBuilder.append(" ");
            resultBuilder.append(entry.getKey());
            resultBuilder.append(System.lineSeparator());
            count++;
        }
        return resultBuilder.toString();
    }

    /**
     * Metoda wysyłająca stan poziomów z serwera
     * @param levelNumber numer poziomu
     * @return odpowiedż
     */
    private static String getLevel(int levelNumber){
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("configs/Level"+levelNumber +".cfg"))){
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                sb.append(currentLine+System.lineSeparator());
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Metoda dodająca wynik do listy na serwerze
     * @param name nick użytkownika
     * @param hs wynik
     * @return odpowiedż
     */
    private static String addScore(String name, int hs){
        String response;
        Properties highscores = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("configs/highscores.properties");
            highscores.load(input);
        }
        catch (IOException ex)
        {
            System.out.println("Wypisanie wynikow nie powiodlo sie, brak pliku");
            ex.printStackTrace();
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        if (highscores.getProperty(name,"FAIL").equals("FAIL")) {
            highscores.setProperty(name, String.valueOf(hs));
        }
        else if (Integer.valueOf(highscores.getProperty(name)) < hs)
            highscores.setProperty(name, String.valueOf(hs));
        OutputStream output;
        try {
            output = new FileOutputStream("configs/highscores.properties");
            highscores.store(output, null);
        }
        catch (IOException io) {
            response="GAME_SCORE_SAVE_FAILED";
            io.printStackTrace();
            return response;
        }
        response="GAME_SCORE_SAVED";
        return response;
    }

    /**
     * Wylogowanie
     * @return odpowiedż
     */
    private static String logout(){
        return "LOGGEDOUT";
    }

    /**
     * Likwidacja połączenia
     * @return odpowiedż
     */
    private static String connectionClosed(){
        return "CLOSE_CONNECTION_NOW";
    }


}
