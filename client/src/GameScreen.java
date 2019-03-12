import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author Daniel Komjago
 */

/**
 * Klasa planszy gry
 */
public class GameScreen extends JPanel implements GameComponent{

    /**
     * Lista platform
     */
	static ArrayList<Platform> platforms;
    /**
     * Wynik gry
     */
	private static int score=0;
    /**
     * Zmienna przechowywająca bohatera
     */
	static Character character;
    /**
     * Numer poziomu
     */
	private int level;
    /**
     * Zmienna przechowywająca panel z wynikiem i czasem
     */
	StatsPanel statsPanel;
    /**
     * Obecny stan pauzy
     */
	private Boolean paused=false;

    /**
     * Plik graficzny z pustą gwiazdką
     */
    private static BufferedImage star0;
    /**
     * Plik graficzny z pokolorowaną gwiazdką
     */
    private static BufferedImage star1;

    /**
     * Próg czasowy dla punktacji najwyższy
     */
    private  static long timeLimit1;
    /**
     * Próg czasowy dla punktacji średni
     */
    private  static long timeLimit2;
    /**
     * Próg czasowy dla punktacji najniższy
     */
    private  static long timeLimit3;

    /**
     * Konstruktor planszy gry
     */
	GameScreen(){
        try {
            URL url = this.getClass().getResource("images/star0.png");
            InputStream is = url.openStream();
            star0 = ImageIO.read(is);
            url = this.getClass().getResource("images/star1.png");
            is = url.openStream();
            star1 = ImageIO.read(is);
            is.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),"pause");
        getActionMap().put("pause", new pauseAction("pause","pause/Unpause the game","esc"));
		statsPanel = new StatsPanel();
		add(statsPanel);
		statsPanel.setBounds(200, 0,300, 50);
	    platforms = new ArrayList<>();
	    setBackground(Color.white);
        setLayout(null);
	    level=SessionConfig.level;
	    SessionConfig.loadLevel(level);
}

    /**
     * Odświerzenie planszy
     */
	void refresh(){
	    score=0;
	    character=null;
	    level=SessionConfig.level;
	    SessionConfig.loadLevel(SessionConfig.level);
	    loadLevel();
	    resume();
    }

    /**
     * Wgrywanie poziomu
     */

    private void loadLevel(){
	    statsPanel.restartTime();
		for(PlatformData pl : SessionConfig.plDataList) {
		Platform p = new Platform(pl.xPos,pl.yPos);
		add(p);
		platforms.add(p);
		}
		if(character == null) {
			character = new Character();
			add(character);
		}
		character.setPosition((int)SessionConfig.characterPos.getX(),(int)SessionConfig.characterPos.getY());
	}

    /**
     * Zmiana poziomu na następny
     */
    private  void nextLevel(){
        BufferedImage result = new BufferedImage(
                3*star0.getWidth(), star0.getHeight(), //work these out
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = result.getGraphics();

        timeLimit1=(long)SessionConfig.timeLimits.toArray()[0];
        timeLimit2=(long)SessionConfig.timeLimits.toArray()[1];
        timeLimit3=(long)SessionConfig.timeLimits.toArray()[2];
        if(statsPanel.getTimeLong()<timeLimit1) {
            score+=900;
            for(int i=0;i<3;i++) {
                g.drawImage(star1, i*star1.getWidth(),0, null);
            }
        }
        else if(statsPanel.getTimeLong()<timeLimit2) {
            score+=600;
            g.drawImage(star1, 0,0, null);
            g.drawImage(star1, star1.getWidth(),0, null);
            g.drawImage(star0,2*star0.getWidth(), 0, null);
        }
        else if(statsPanel.getTimeLong()<timeLimit3) {
            score+=300;
            g.drawImage(star1, 0,0, null);
            g.drawImage(star0, star0.getWidth(),0, null);
            g.drawImage(star0, 2*star0.getWidth(), 0, null);
        }
        else {
            for(int i=0;i<3;i++) {
                g.drawImage(star0, i*star0.getWidth(), 0, null);
            }
        }
        score+=1000;
        pause();
        JOptionPane.showMessageDialog(CanvasManager.getInstance(),"Your score: "+score+System.lineSeparator()+
                "Your time: "+ statsPanel.getTime(),"You Beat The Level",JOptionPane.PLAIN_MESSAGE,new ImageIcon(result));
        level+=1;
        if(level>SessionConfig.levelCount){
            gameOver();
        }
        else {
            SessionConfig.loadLevel(level);
            loadLevel();
            resume();
        }
    }

    /**
     * Dodawanie do wyniku punktów za platformę
     * @param p platforma za którą dodaje się punkty
     */
	void addScorePlatform(Platform p){
		score+=100;
		remove(p);
		platforms.remove(p);
		if(platforms.isEmpty())
		    nextLevel();
	}

    /**
     * Metoda zwracająca tymczasowy wynik
     * @return wynik
     */
	static int getScore(){
		return score;
	}

    /**
     * Metoda wywoływana w końcu gry
     */
    private  void gameOver(){
	    if(!paused)
	    pause();
        JOptionPane gameOverPane = new JOptionPane();
        Highscores.create(SessionConfig.user,score);
        gameOverPane.showMessageDialog(CanvasManager.getInstance(),"GAME OVER","Game Over",JOptionPane.INFORMATION_MESSAGE);
        CanvasManager.getInstance().changeScreen("Menu");
	}

    /**
     * Powtórzenie poziomu
     */
	void retry(){
		for(Platform platform : platforms)
		{
			remove(platform);
		}
		platforms = new ArrayList<>();
		character.Reset();
		statsPanel.restartTime();
        if(Character.getLiveCount()<=0) {
            gameOver();
            return;
        }
        loadLevel();
	}
	
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setDoubleBuffered(true);
        CanvasManager.getInstance().resize(g);
    }

    /**
     * Rysowanie planszy
     * @param g grafika dla rysowania
     */
    public void render(Graphics g){
        if(paused) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Verdana",1,20));
            g.drawString("PAUSED",200,250);
        }
        repaint();
    }

    /**
     * Zatrzymanie
     */
    public void pause(){
        paused=true;
        statsPanel.pause();
        character.pause();
        for(Platform p : platforms)
        {
            p.pause();
        }
    }

    /**
     * Wznowienie
     */
    public void resume(){
        paused=false;
        statsPanel.resume();
        character.resume();
        for(Platform p : platforms)
        {
            p.resume();
        }
    }

    /**
     * Metoda zwracająca obecny stan pauzy
     * @return stan pauzy
     */
    Boolean isPaused(){
        return paused;
    }

    /**
     * Podklasa czynności - Pauza
     */
    class pauseAction extends AbstractAction {
        pauseAction(String text,String desc, String mnemonic) {
            super(text);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }
        public void actionPerformed(ActionEvent e) {
            if(!paused) {
                pause();
            }
            else
                resume();

        }
    }
}
