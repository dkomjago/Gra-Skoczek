import org.apache.commons.lang3.time.StopWatch;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.TimeUnit;
/**
 * @author Daniel Komjago
 */
/**
 * Klasa paneli wyświetlającej wyniki
 */
public class StatsPanel extends JPanel implements GameComponent {

    /**
     * Plik graficzny z obrazkiem serca
     */
    private BufferedImage heartImg;
    /**
     * Stoper
     */
    private StopWatch stopper;
    /**
     * Konstruktor klasy
     */
    StatsPanel(){
        try {
            URL url = this.getClass().getResource("images/heart.png");
            InputStream is = url.openStream();
            heartImg = ImageIO.read(is);
            is.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        stopper = new StopWatch();
        stopper.start();
    }

    /**
     * Zatrzywywanie stopera
     */
    public void pause()
    {
       stopper.suspend();
    }

    /**
     * Wznowienie stopera
     */
    public void resume(){
        if(stopper.isSuspended())
        stopper.resume();
    }
    public void paintComponent(Graphics g) {
    }

    /**
     * Rysowanie komponentu
     * @param g grafika do rysowania komponentu
     */
    void render(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Verdana",1,20));
        g.drawString(String.valueOf(stopper.getTime(TimeUnit.SECONDS))+":"+String.valueOf(stopper.getTime(TimeUnit.MILLISECONDS)%1000),1,30);
        g.drawString(String.valueOf(GameScreen.getScore()),1,60);
        for(int i = 0; i < Character.getLiveCount(); i++) {
            g.drawImage(heartImg, i*10, 0, null);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Restart stopera
     */
   void restartTime(){
        stopper.reset();
        stopper.start();
    }

    /**
     * Metoda zwracająca chwilowy czas stopera w formacie SS:sss
     * @return chwilowy czas stopera w formacie SS:sss
     */
    String getTime() {
        return String.valueOf(stopper.getTime(TimeUnit.SECONDS))+":"+String.valueOf(stopper.getTime(TimeUnit.MILLISECONDS)%1000);
    }
    /**
     * Metoda zwracająca chwilowy czas stopera w formacie Long
     * @return chwilowy czas stopera w formacie Long
     */
    long getTimeLong() {
        return stopper.getTime(TimeUnit.SECONDS);
    }

}
