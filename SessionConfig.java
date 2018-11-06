package application;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Properties;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * @author Daniel Komjago
 */
/**
 * Klasa z ustawieniami z pliku konfiguracyjnego
 */
public class SessionConfig{
	private static Logger logger = Logger.getLogger("SessionConfig");

	/**
	 * Tablica z danymi dla inicjalizacji platform
	 */
	public static ArrayList<PlatformData>  plDataList = new ArrayList();
	/**
	 * Wgrany poziom
	 */
	public static int level;
	/**
	 * Zapamiętany nick użytkownika
	 */
	public static String user;
	/**
	 * Liczba wyników do wyświetlenia
	 */
	public static int highscoreDisplayCount;
	/**
	 * Początkowe współrzędne bohatera
	 */
	public static Point2D.Float characterPos;
	/**
	 * Liczba poziomów
	 */
	public static int levelCount;
	/**
	 * Progi czasu dla punktacji
	 */
	public static TreeSet<Long> timeLimits = new TreeSet<>();
	/**
	 * Szerokość ramki
	 */
	public static int frameWidth;
	/**
	 * Wysokość ramki
	 */
	public static int frameHeight;
	/**
	 * Kolor platform
	 */
	public static Color platformColor;
	/**
	 * Częstotliwość odświeżania animacji
	 */
	public static int animationRate;
	/**
	 * Liczba żyć
	 */
	public static int lives;


	/**
	 * Pole dla przechowywania pliku konfiguracyjnego
	 */
	private static Properties config;

	/**
	 * Wpisać domyślne wartości do pliku konfiguracyjnego
	 */
	public static void writeDefaults() {
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			output = new FileOutputStream("configs/config.properties");

			prop.setProperty("level", "1");
			prop.setProperty("levelCount", "2");
			prop.setProperty("user", "user");
			prop.setProperty("highscoreDisplayCount", "10");
			prop.setProperty("soundMuted", "false");
			prop.setProperty("sfxVolume", "60");
			prop.setProperty("musicVolume","60");
			prop.setProperty("frameWidth", "500");
			prop.setProperty("frameHeight", "500");
			prop.setProperty("platformColor","yellow");
            prop.setProperty("animationRate","120");
			prop.setProperty("lives", "3");
			prop.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
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
	 * Wczytać plik konfiguracyjny
	 */
	public static void read() {
		config = new Properties();
		InputStream input = null;

		try {
			if(Client.isOnline()){
				input = new FileInputStream("configs/onlineConfig.properties");

				config.load(input);
				level= Integer.parseInt(config.getProperty("level"));
				levelCount = Integer.parseInt(config.getProperty("levelCount"));
				user = config.getProperty("user");
				highscoreDisplayCount = Integer.parseInt(config.getProperty("highscoreDisplayCount"));
				frameWidth=Integer.parseInt(config.getProperty("frameWidth"));
				frameHeight=Integer.parseInt(config.getProperty("frameHeight"));
				animationRate=Integer.parseInt(config.getProperty("animationRate"));
				lives=Integer.parseInt(config.getProperty("lives"));
                try {
                    Field field = Class.forName("java.awt.Color").getField(config.getProperty("platformColor"));
                    platformColor = (Color)field.get(null);
                } catch (Exception e) {
                    platformColor = null;
                }
				SoundController.Mute(Boolean.valueOf(config.getProperty("soundMuted")));
				SoundController.SetSfxVolume(Integer.valueOf(config.getProperty("sfxVolume")));
				SoundController.SetMusicVolume(Integer.valueOf(config.getProperty("musicVolume")));
			}
			input = new FileInputStream("configs/config.properties");

			config.load(input);
			level= Integer.parseInt(config.getProperty("level"));
			levelCount = Integer.parseInt(config.getProperty("levelCount"));
			user = config.getProperty("user");
			highscoreDisplayCount = Integer.parseInt(config.getProperty("highscoreDisplayCount"));
			frameWidth=Integer.parseInt(config.getProperty("frameWidth"));
			frameHeight=Integer.parseInt(config.getProperty("frameHeight"));
            animationRate=Integer.parseInt(config.getProperty("animationRate"));
			lives=Integer.parseInt(config.getProperty("lives"));
            try {
                Field field = Class.forName("java.awt.Color").getField(config.getProperty("platformColor"));
                platformColor = (Color)field.get(null);
            } catch (Exception e) {
                platformColor = null;
            }
			SoundController.Mute(Boolean.valueOf(config.getProperty("soundMuted")));
			SoundController.SetSfxVolume(Integer.valueOf(config.getProperty("sfxVolume")));
			SoundController.SetMusicVolume(Integer.valueOf(config.getProperty("musicVolume")));

		}
		catch (IOException ex){
			logger.log(Level.SEVERE, ex.getMessage());
			ex.printStackTrace();
			if(!Client.isOnline())
			writeDefaults();
		}
		catch (NumberFormatException ex){
			logger.log(Level.SEVERE, ex.getMessage());
			ex.printStackTrace();
            if(!Client.isOnline())
			writeDefaults();
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
	 * Zapisywanie ustawień dżwięku
	 * @param soundMuted czy dżwięk jest wyciszony
	 * @param sfxVolume  poziom głośności efektów dżwiękowych
	 * @param musicVolume poziom głośności muzyki
	 */
	public static void SaveSettings(Boolean soundMuted, int sfxVolume, int musicVolume)
	{
		OutputStream output = null;

		try {
			output = new FileOutputStream("configs/config.properties");

			config.setProperty("soundMuted", soundMuted.toString());
			config.setProperty("sfxVolume", String.valueOf(sfxVolume));
			config.setProperty("musicVolume",String.valueOf(musicVolume));
			config.setProperty("user", user);
			config.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
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
	 * Wczytywanie poziomu z pliku
	 * @param lvl numer poziomu
	 */
	public static void LoadLevel(int lvl) {
		try {
			BufferedReader br;
			plDataList = new ArrayList();
			// Open the file
			if(Client.isOnline()){
				OutputStream os = Client.getSocket().getOutputStream();
				PrintWriter pw = new PrintWriter(os, true);
				pw.println("GET_LEVEL:"+lvl);
				InputStream is = Client.getSocket().getInputStream();
				br = new BufferedReader(new InputStreamReader(is));
			}
			else {
				FileInputStream fstream = new FileInputStream("configs/Level" + lvl + ".cfg");
				br = new BufferedReader(new InputStreamReader(fstream));
			}

			String strLine;

			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				String[] strSplit = strLine.split(" ");
				if(strSplit[0].equals("%")) continue;
				else if(strSplit[0].equals("CHARACTER")) {
					characterPos = new Point2D.Float(Integer.valueOf(strSplit[1]),Integer.valueOf(strSplit[2]));
					continue;
				}
				else if(strSplit[0].equals("TIMELIMITS")) {
					timeLimits.add(Long.valueOf(strSplit[1]));
					timeLimits.add(Long.valueOf(strSplit[2]));
					timeLimits.add(Long.valueOf(strSplit[3]));
					continue;
				}
				plDataList.add(new PlatformData(Integer.parseInt(strSplit[0]),Integer.parseInt(strSplit[1])));
			}

			//Close the input stream
			br.close();
		}
		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}

	/**
	 * Wczytywanie zasad gry z pliku
	 */
	public static void GetRules(){
		if (Client.isOnline()) {
			try {
				OutputStream os = Client.getSocket().getOutputStream();
				PrintWriter pw = new PrintWriter(os, true);
				pw.println("GET_RULES");
				InputStream is = Client.getSocket().getInputStream();
				StringBuilder sb = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String currentLine;
				while ((currentLine = br.readLine()) != null && currentLine.length() != 0) {
					sb.append(currentLine+System.lineSeparator());
				}
				JOptionPane.showMessageDialog(CanvasManager.getInstance(), sb.toString());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		else{
			try {
				FileInputStream fstream = new FileInputStream("configs/rules.cfg");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				StringBuilder sb = new StringBuilder();
				String currentLine;
				while ((currentLine = br.readLine()) != null && currentLine.length() != 0) {
					sb.append(currentLine+System.lineSeparator());
				}
				JOptionPane.showMessageDialog(CanvasManager.getInstance(), sb.toString());
			}
			catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
