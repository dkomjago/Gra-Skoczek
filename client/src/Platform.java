import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
/**
 * @author Daniel Komjago
 */
/**
 * Klasa platformy
 */
public class Platform extends JComponent implements ActionListener, GameComponent{
	/**
	 * Szerokość platformy
	 */
	final int pWidth = 35;
	/**
	 * Wysokość platformy
	 */
    final int pHeight = 35;
	/**
	 * Współrzędne platformy
	 */
    int xPos;
    int yPos;
	/**
	 * Częstotliwość odświeżania animacji
	 */
	private final int DELAY = 1000/60;
	/**
	 * Stan platformy odpowiadający za jej usunięcie po animacji
	 */
	private Boolean destroy=false;
	/**
	 * Zmienna przechowywająca oryginalną pozycję platformy
	 */
	private int temp;
	/**
	 * Tajmer animacji
	 */
	private Timer animationTimer;

	/**
	 * tablica z instancjami klasy
	 */
	private static ArrayList<Platform> instances = new ArrayList<Platform>();

	/**
	 * Konstruktor klasy
	 * @param x współrzędna x
	 * @param y współrzędna y
	 */
	Platform(int x, int y) {
		animationTimer = new Timer(DELAY, this);
		animationTimer.start();
        xPos = x;
        yPos = y;
        setBounds(xPos,yPos,pWidth,pHeight);
		instances.add(this);
    }

	/**
	 * Zatrzymanie animacji
	 */
	public void pause()
	{
		animationTimer.stop();
	}

	/**
	 * Wznowienie animacji
	 */
	public void resume()
	{
		animationTimer.start();
	}
	
	 public void paintComponent(Graphics g) {
	      super.paintComponent(g);
	      /*
	      g.setColor(Color.black);
	      setBounds(xPos,yPos,pWidth,pHeight);
	      g.fillRect(0,0,pWidth,pHeight);
	      */
	   }

	/**
	 * Rysowanie elementu
	 * @param g grafika dla rysowania
	 */
	void render(Graphics g) {
		g.setColor(SessionConfig.platformColor);
		g.fillRect(xPos,yPos,pWidth,pHeight);
        g.setColor(Color.BLACK);
        g.drawRect(xPos,yPos,pWidth,pHeight);
	}
	 
	 public Dimension getPreferredSize() {
		  return new Dimension(pWidth, pHeight);
		}

	/**
	 * Usunięcie platformy
	 */
		void destroy(){
			destroy=true;
			temp = yPos;
		}

	public void actionPerformed(ActionEvent e) {
	 	if(destroy) {
			yPos++;
			if(yPos-temp>10) {
				CanvasManager.gs.addScorePlatform(this);
				temp=Integer.MAX_VALUE;
			}
		}
	}

}
