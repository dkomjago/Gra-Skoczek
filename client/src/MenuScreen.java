import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Daniel Komjago
 */
/**
 * Klasa planszy menu
 */
class MenuScreen extends JPanel{

	/**
	 * Konstruktor klasy planszy menu
	 */
	 MenuScreen() {
	    setBackground(Color.white);
	    mainMenu();
		}

	/**
	 * Inicjalizacja komponentów planszy
	 */
	private void mainMenu() {
		JPanel buttonCont = new JPanel();
	    buttonCont.setLayout(new GridLayout(5,1,50,50));
	    buttonCont.setBackground(Color.white);
	    JButton bPlay = new JButton("PLAY");
	    bPlay.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
				CanvasManager.getInstance().changeScreen("Game");
			}
	    });
	    JButton bHs = new JButton("HIGHSCORES");
	    bHs.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		Highscores.open();
	    	}
	    });
	    JButton bSettings = new JButton("SETTINGS");
	    bSettings.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
				CanvasManager.getInstance().changeScreen("Settings");
	    	}
	    });
		JButton bRules = new JButton("RULES");
		bRules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SessionConfig.getRules();
			}
		});
	    JButton bQuit = new JButton("QUIT");
	    bQuit.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		System.exit(0);
	    	}
	    });
	    buttonCont.add(bPlay);
	    buttonCont.add(bHs);
	    buttonCont.add(bSettings);
        buttonCont.add(bRules);
	    buttonCont.add(bQuit);
	    setLayout(new FlowLayout(FlowLayout.CENTER,100,100));
	    add(buttonCont);	    
	}
	
}
