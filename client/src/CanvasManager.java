import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_USHORT_555_RGB;

/**
 * @author Daniel Komjago
 */


/**
 * Klasa zarządzająca planszami i ramką
 */
class CanvasManager extends JFrame {

    /**
     * Instancja klasy
     */
	private static CanvasManager instance = null;
    /**
     * Główna plansza na której znajdują się pozostałe
     */
	private static JPanel main;
    /**
     * Rozkład plansz
     */
	private static CardLayout cl;
    /**
     * Plansza gry
     */
	 static GameScreen gs;
    /**
     * Plansza menu
     */
    static MenuScreen ms;
    /**
     * Plansza ustawień
     */
    static SettingsScreen ss;
    /**
     * Szeriokość ramki gry
     */
	private static int frameWidth = 500;
    /**
     * Wysokość ramki gry
     */
	private static int frameHeight = 500;
    /**
     * Grafika dla rysowania obrazu
     */
    static Graphics2D g2;
    /**
     * Całkowity obraz planszy gry
     */
    private BufferedImage displayImage;

    /**
     * Konstruktor klasy
     */

	private CanvasManager(){
	    frameWidth=SessionConfig.frameWidth;
        frameHeight=SessionConfig.frameHeight;
		setTitle("Skoczek");
	    setSize(new Dimension(frameWidth, frameHeight));
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    main = new JPanel(new CardLayout());
	    ms= new MenuScreen();
	    gs = new GameScreen();
	    ss = new SettingsScreen();
        Highscores.initialize();
	    main.add(ms,"Menu");
	    main.add(gs,"Game");
	    main.add(ss,"Settings");
	    add(main);
	    cl = (CardLayout)main.getLayout();
        setVisible(true);
        SoundController.playMusic();
	}

    /**
     * Metoda dla uzyskania instancji klasy
     * @return instancja klasy
     */

	public static CanvasManager getInstance(){
	      if(instance == null) {
	         instance = new CanvasManager();
	      }
	      return instance;
	   }

    /**
     * Metoda zmieniająca planszę
     * @param name  nazwa planszy na którą się zmienia
     */
	public void changeScreen(String name){
	    if(name.equals("Game"))
	        gs.refresh();
		cl.show(main, name);;
	}

    /**
     * Metoda skalująca i rysująca obraz na planszy
     * @param g grafika wymagająca skalowania
     */
	    public void resize(Graphics g){
            displayImage = new BufferedImage(500, 500, TYPE_USHORT_555_RGB);
			float scale = Math.min((float) gs.getWidth() / displayImage.getWidth(), (float) gs.getHeight() / displayImage.getHeight());
			int displayWidth = (int) (displayImage.getWidth() * scale);
			int displayHeight = (int) (displayImage.getHeight() * scale);
            g2 = displayImage.createGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, displayWidth*2, displayHeight*2);
            for(Platform pl : GameScreen.platforms) {
                pl.render(g2);
            }
            GameScreen.character.render(g2);
            gs.statsPanel.render(g2);
            gs.render(g2);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(displayImage, gs.getWidth()/2 - displayWidth/2, gs.getHeight()/2 - displayHeight/2, displayWidth, displayHeight, null);
		}

}