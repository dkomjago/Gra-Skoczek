import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * @author Daniel Komjago
 */
/**
 * Klasa zarządzania wynikami
 */
public class Highscores extends JOptionPane{
	private static Logger logger = Logger.getLogger("Highscores");
	/**
	 * plik properties wyników
	 */
	private static Properties highscores;
	/**
	 * Tablica z wynikami dla wypisywania
	 */
	private static TreeMap<Integer,String> scoreTab = new TreeMap<>();
	/**
	 * Liczba wyników do wyświetlenia
	 */
	private static int displayCount = SessionConfig.highscoreDisplayCount;

	/**
	 * Wstępna inicjalizacja klasy
	 */
	static void initialize(){
		highscores = new Properties();
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
			logger.log(Level.SEVERE, ex.getMessage());
			ex.printStackTrace();
			OutputStream output = null;
			try {
				output = new FileOutputStream("configs/highscores.properties");
				highscores.store(output, null);
			}
			catch (IOException io) {
				io.printStackTrace();
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
	}

	/**
	 * Otwieranie dialogu z wynikami
	 */
	 static void open(){
		scoreTab= new TreeMap<>(Collections.reverseOrder());
		for(Object p : highscores.keySet())
		{
			scoreTab.put(Integer.valueOf(highscores.getProperty(p.toString())),p.toString());
		}
		String message = printFirstEntries(displayCount,scoreTab);
		if(Client.isOnline()){
			try {
				OutputStream os = Client.getSocket().getOutputStream();
				PrintWriter pw = new PrintWriter(os, true);
				pw.println("GET_HIGH_SCORES");
				InputStream is = Client.getSocket().getInputStream();
				StringBuilder resultBuilder = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String currentLine;
				while ((currentLine = br.readLine()) != null  && currentLine.length() != 0) {
					resultBuilder.append(currentLine+System.lineSeparator());
				}
				JOptionPane.showMessageDialog(CanvasManager.getInstance(),resultBuilder.toString(),"HighScores",JOptionPane.PLAIN_MESSAGE);
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		else
		JOptionPane.showMessageDialog(CanvasManager.getInstance(),message);
	}

	/**
	 * Dodawanie wyniku
	 * @param name nick użytkownika
	 * @param hs wynik
	 */
	 static void create(String name, int hs) {
		if(Client.isOnline()){
			try {
				OutputStream os = Client.getSocket().getOutputStream();
				PrintWriter pw = new PrintWriter(os, true);
				pw.println("GAME_SCORE:"+name+"@"+hs);
				InputStream is = Client.getSocket().getInputStream();;
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line = br.readLine();
				JOptionPane.showMessageDialog(CanvasManager.getInstance(),line,"Server Info",JOptionPane.INFORMATION_MESSAGE);
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		else {
			if (highscores.getProperty(name, "FAIL").equals("FAIL")) {
				highscores.setProperty(name, String.valueOf(hs));
			} else if (Integer.valueOf(highscores.getProperty(name)) < hs)
				highscores.setProperty(name, String.valueOf(hs));
			OutputStream output;
			try {
				output = new FileOutputStream("configs/highscores.properties");
				highscores.store(output, null);
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}

	/**
	 * Metoda wypisująca wskazaną liczbę wyników z tablicy
	 * @param max liczba wyników
	 * @param source pełna tablica wyników
	 * @return łańcuch z wynikami
	 */
	static String printFirstEntries(int max, SortedMap<Integer,String> source) {
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
}
