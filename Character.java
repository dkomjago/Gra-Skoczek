package application;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.Timer;;

/**
 * Klasa bohatera
 */
public class Character extends JComponent implements ActionListener, GameComponent{

	/**
	 * @author Daniel Komjago
	 */

	/**
	 * Plik graficzny bohatera
	 */
	private static BufferedImage characterImg;
	private static BufferedImage animation0;
	private static BufferedImage animation1;
	private static BufferedImage animation2;
	private static BufferedImage animation3;
	private static BufferedImage animation4;
	/**
	 * stan animacji bohatera
	 */
	private int animationState;
	/**
	 * Zarządzanie animacją
	 */
	private Animation animationController;
	/**
	 * Chwilowa liczba żyć
	 */
	static private int lives;
	/**
	 * Początkowa liczba żyć
	 */
	private final int INITIAL_LIVES = SessionConfig.lives;
	/**
	 * Częstotliwość animacji
	 */
	private final int DELAY = 1000/SessionConfig.animationRate;
	/**
	 * Szerokość bohatera
	 */
	private int width = 15;
	/**
	 * wysokość bohatera
	 */
    private int height = 30;
	/**
	 * współrzędne bohatera
	 */
    private int x=0, y=0;
	/**
	 * Stan kolizji z góry
	 */
    private Boolean yuCollision=false;
	/**
	 * Stan kolizji z dołu
	 */
    private Boolean ydCollision=false;
	/**
	 * Stan kolizji po lewej stronie
	 */
    private Boolean xlCollision=false;
	/**
	 * Stan kolizji po prawej stronie
	 */
    private Boolean xrCollision=false;
	/**
	 * Stan skoku
	 */
    private Boolean jumping=false;
	/**
	 * Stan uziemienia
	 */
    private Boolean landed=false;
	/**
	 * Zmienna przechowywująca maksymalną pozycję skoku
	 */
    private int jumpPos;
	/**
	 * Kierunek ruchu w prawą stronę
	 */
    private Boolean moveRight=false;
	/**
	 * Kierunek ruchu w lewą stronę
	 */
    private Boolean moveLeft=false;
	/**
	 * Tajmer animacji
	 */
    private Timer animationTimer;

	/**
	 * Prędkość ruchu bohatera
	 */
    private final int moveSpeed=2;
	/**
	 * Moc grawitacji
	 */
    private final int gravity=4;
	/**
	 * Prędkość skoku
	 */
    private final int jumpSpeed=5;
	/**
	 * Wysokość skoku
	 */
    private final int jumpHeight=100;

	/**
	 * Platforma na której znajduje się bohater
	 */
    private Platform currentPlatform;
	/**
	 * Tymczasowe przechowywanie platformy na której znajdował się bohater
	 */
	private Platform temp;

	/**
	 * Podklasa odpowiedzialna za animację
	 */
	 class Animation implements ActionListener, GameComponent{
		/**
		 * Tajmer animacji
		 */
	 	private Timer animationTimer;
		/**
		 * Inicjalizacja animacji
		 */
		public void Initialize(){
		animationTimer = new Timer(1000/8, this);
		animationTimer.start();
		}

		/**
		 * Zatrzymanie animacji
		 */
		public void Pause()
		{
			animationTimer.stop();
		}

		/**
		 * Wznowienie animacji
		 */
		public void Resume()
		 {
			animationTimer.start();
		 }

		public void actionPerformed(ActionEvent e) {
			if(moveLeft||moveRight)
			{
				switch(animationState){
					case 0:
						animationState=1;
						characterImg=animation1;
						break;
					case 1:
						animationState=2;
						characterImg=animation2;
						break;
					case 2:
						animationState=3;
						characterImg=animation1;
						break;
					case 3:
						animationState=4;
						characterImg=animation3;
						break;
					case 4:
						animationState=5;
						characterImg=animation4;
						break;
					case 5:
						animationState=0;
						characterImg=animation3;
						break;
					default:
						animationState=0;
						characterImg=animation0;
						break;
				}
			}
			else{
				animationState=0;
				characterImg=animation0;
			}
		}
	}

	/**
	 * Konstruktor klasy
	 */
	public Character(){
		try {
			URL url = this.getClass().getResource("images/character0.png");
			InputStream is = url.openStream();
			animation0 = ImageIO.read(is);
			url = getClass().getResource("images/character1.png");
			is = url.openStream();
			animation1 = ImageIO.read(is);
			url = getClass().getResource("images/character2.png");
			is = url.openStream();
			animation2 = ImageIO.read(is);
			url = getClass().getResource("images/character3.png");
			is = url.openStream();
			animation3 = ImageIO.read(is);
			url = getClass().getResource("images/character4.png");
			is = url.openStream();
			animation4 = ImageIO.read(is);
			is.close();
	       } catch (IOException ex) {
	    	 ex.printStackTrace();
	       }
	        lives=INITIAL_LIVES;
		 	setBounds(0,0,10000,10000);
		 	setDoubleBuffered(true);
	     	animationTimer = new Timer(DELAY, this);
	     	animationTimer.start();
		 	setFocusable(true);
			requestFocusInWindow();
			getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_A,0,true),"lmove0");
			getActionMap().put("lmove0", new LeftActionRelease("LeftRelease","Stop moving left","ar"));
			getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D,0,true),"rmove0");
			getActionMap().put("rmove0", new RightActionRelease("RightRelease","Stop moving right","dr"));
			getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"),"lmove1");
		 	getActionMap().put("lmove1", new LeftAction("Left","Go left","a"));
		 	getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"),"rmove1");
		 	getActionMap().put("rmove1", new RightAction("Right","Go right","d"));
		 	getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"),"wmove");
		 	getActionMap().put("wmove", new JumpAction("Jump","Make a jump","w"));
		 	characterImg=animation0;
		 	animationController=new Animation();
		 	animationController.Initialize();
	}


	 public void paintComponent(Graphics g) {
	      super.paintComponent(g);
	   }

	/**
	 * Zatrzymanie animacji
	 */
	public void Pause()
	{
		animationTimer.stop();
		animationController.Pause();
	}

	/**
	 * Wznowienie animacji
	 */
	public void Resume()
	{
		animationTimer.start();
		animationController.Resume();
	}

	/**
	 * Rysowanie elementu
	 * @param g grafika do rysowania komponentu
	 */
	public void Render(Graphics g){
	 	if(!moveRight) {
			CanvasManager.getInstance().g2.drawImage(characterImg.getScaledInstance(width, height, Image.SCALE_DEFAULT), x, y, null);
		}
	 	else {
	 			BufferedImage temp;
				AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
				tx.translate(-characterImg.getWidth(null), 0);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				temp = op.filter(characterImg, null);
				CanvasManager.getInstance().g2.drawImage(temp.getScaledInstance(width, height, Image.SCALE_DEFAULT), x, y, null);
		}
		Toolkit.getDefaultToolkit().sync();
	}


	public Dimension getPreferredSize() {
		  return new Dimension(animation0.getWidth(), animation0.getHeight());
		}

	/**
	 * Ustawienie współrzędnych bohatera
	 * @param dx współrzędna x
	 * @param dy współrzędna y
	 */
	public void SetPosition(int dx, int dy){
	 	moveRight=false;
	 	moveLeft=false;
		x=dx;
		y=dy;
	}

	/**
	 * Zabespieczenie przed utknięciem w platformę
	 */
	public void Unstuck(){
		y-=1;
	}
	 
	    
	public void actionPerformed(ActionEvent e) {
		if(moveRight && !xrCollision)
			x+=moveSpeed;
		if(moveLeft && !xlCollision)
			x-=moveSpeed;
	        if(!ydCollision)
    	        y += gravity;
			if(!yuCollision){
				if(jumping){
					if(y>jumpPos){
						y-=gravity+jumpSpeed;
					}
					else
						jumping=false;
				}
			}
			else
				jumping=false;
	        ydCollision=false;
	        yuCollision=false;
	        xlCollision=false;
	        xrCollision=false;
	        Rectangle rect1 = new Rectangle(x, y, width, height+1);
	        for(Platform p : GameScreen.platforms){
	        	Rectangle rect2 = new Rectangle(p.xPos, p.yPos, p.pWidth, p.pHeight);
	        	if(rect1.intersects(rect2))
	        	{
					Collision(rect1,rect2);
	        		if(ydCollision) {
	        			if(y+height>p.yPos) {
	        				System.out.println("UNSTUCK");
							Unstuck();
						}
						landed=true;
	        			temp = currentPlatform;
						currentPlatform = p;
					}
	        	}
	        }
	        if(currentPlatform != null && temp!=null && (!ydCollision||temp!=currentPlatform)) {
	        	currentPlatform.Destroy();
				currentPlatform = null;
			}
	        if(y>500)
				CanvasManager.gs.Retry();
	        repaint();
	    }

	/**
	 * Podklasa czynności - ruch w lewą stronę
	 */
	    class LeftAction extends AbstractAction {
	        public LeftAction(String text,String desc, String mnemonic) {
	            super(text);
	            putValue(SHORT_DESCRIPTION, desc);
	            putValue(MNEMONIC_KEY, mnemonic);
	        }
	        public void actionPerformed(ActionEvent e) {
				if(!CanvasManager.gs.isPaused())
	            moveLeft=true;
	        }
	    }
	/**
	 * Podklasa czynności - ruch w prawą stronę
	 */
	    class RightAction extends AbstractAction {
	        public RightAction(String text,String desc, String mnemonic) {
	            super(text);
	            putValue(SHORT_DESCRIPTION, desc);
	            putValue(MNEMONIC_KEY, mnemonic);
	        }
	        public void actionPerformed(ActionEvent e) {
				if(!CanvasManager.gs.isPaused())
	            moveRight=true;
	        }
	    }
	/**
	 * Podklasa czynności - zakończenie ruchu w lewą stronę
	 */
	class LeftActionRelease extends AbstractAction {
		public LeftActionRelease(String text,String desc, String mnemonic) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			if(!CanvasManager.gs.isPaused())
				moveLeft=false;
		}
	}
	/**
	 * Podklasa czynności - zakończenie ruchu w prawą stronę
	 */
	class RightActionRelease extends AbstractAction {
		public RightActionRelease(String text,String desc, String mnemonic) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			if(!CanvasManager.gs.isPaused())
				moveRight=false;
		}
	}
	/**
	 * Podklasa czynności - skok
	 */
	    class JumpAction extends AbstractAction {
	        public JumpAction(String text,String desc, String mnemonic) {
	            super(text);
	            putValue(SHORT_DESCRIPTION, desc);
	            putValue(MNEMONIC_KEY, mnemonic);
	        }
	        public void actionPerformed(ActionEvent e) {
	        	if(landed && !jumping && !CanvasManager.gs.isPaused()&& temp!=null && currentPlatform!=null)
	        	{
	        		jumping=true;
	        		landed=false;
	    			jumpPos=y-jumpHeight;
	    			SoundController.playSound("jump.wav");
	        	}
	        }
	    }

	/**
	 * Reset stanu bohatera po śmierci
	 */
	    void Reset(){
			lives-=1;
			SetPosition((int)SessionConfig.characterPos.getX(),(int)SessionConfig.characterPos.getY());
			ydCollision=false;
			currentPlatform=null;
			temp=null;
		}

	/**
	 * Sprawdzienie kolizji z platformą
	 * @param r1 prostokąt wymiarów bohatera
	 * @param r2 prostokąt wymiarów platformy
	 */
	     void Collision(Rectangle r1, Rectangle r2){
	    	final int left=0,top=1,right=2,bottom=3;
	    	
	    	    Rectangle[] boxes = new Rectangle[4];
	    	    boxes[0] = new Rectangle(r1.x-1, r1.y-1, 1, r1.height);
	    	    boxes[1] = new Rectangle(r1.x, r1.y-1, r1.width, 1);
	    	    boxes[2] = new Rectangle(r1.x + r1.width, r1.y-1, 1, r1.height);
	    	    boxes[3] = new Rectangle(r1.x, r1.y+r1.height, r1.width, 1);

	    	    double greatestArea = 0;
	    	    int greatest = 0;

	    	    for( int i = 0; i<4; i++){
	    	        if(boxes[i].createIntersection(r2).getWidth()*boxes[i].createIntersection(r2).getHeight() > greatestArea){
	    	            greatestArea = boxes[i].createIntersection(r2).getWidth()*boxes[i].createIntersection(r2).getHeight();
	    	            greatest = i;
	    	        }
	    	    }
	    	    switch(greatest){
	    	    case right:
	    	    	xrCollision=true;
	    	    	break;
	    	    case left:
	    	    	xlCollision=true;
	    	    	break;
	    	    case bottom:
	    	    	ydCollision=true;
	    	    	break;
	    	    case top:
	    	    	yuCollision=true;
	    	    	break;
	    	    default:
	    	    	break;
	    	    }
	    }


	/**
	 * Zwraca liczbę żyć
	 * @return liczba żyć
	 */
	    static public int GetLiveCount(){
			return lives;
		}



}
