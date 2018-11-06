package application;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.sound.midi.Soundbank;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
/**
 * @author Daniel Komjago
 */
/**
 * Klasa planszy ustawień
 */
public class SettingsScreen extends JPanel{

	/**
	 * Konstruktor
	 */
	public SettingsScreen(){
		setLayout(new GridLayout(8,1,50,30));
		setBackground(Color.white);
        JLabel lTextField = new JLabel("Username:");
        lTextField.setHorizontalAlignment(SwingConstants.CENTER);
        JFormattedTextField sTextField = new JFormattedTextField(SessionConfig.user);
        sTextField.setHorizontalAlignment(SwingConstants.CENTER);
        sTextField.addPropertyChangeListener("value", new PropertyChangeListener(){
        public void propertyChange(PropertyChangeEvent e) {
            JFormattedTextField source = (JFormattedTextField)e.getSource();
            SessionConfig.user=source.getText();
        }});
		JSlider sMusic = new JSlider();
		sMusic.setMaximum(86);
		sMusic.setMinimum(0);
		sMusic.setValue(SoundController.GetMusicVolume());
		sMusic.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ev) {
				JSlider source = (JSlider)ev.getSource();
				SoundController.SetMusicVolume(source.getValue());
				if(source.getValueIsAdjusting())
					SoundController.Mute(true);
				else
					SoundController.Mute(false);
			}
		});
		JLabel lMusic = new JLabel("Music");
		lMusic.setHorizontalAlignment(SwingConstants.CENTER);
		JSlider sSfx = new JSlider();
		sSfx.setMaximum(86);
		sSfx.setMinimum(0);
		sSfx.setValue(SoundController.GetSfxVolume());
		sSfx.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent ev) {
					JSlider source = (JSlider)ev.getSource();
					SoundController.SetSfxVolume(source.getValue());
					SoundController.playSound("jump.wav");
			}
		});
		JLabel lSfx = new JLabel("Sound Effects");
		lSfx.setHorizontalAlignment(SwingConstants.CENTER);
		JToggleButton bMute = new JToggleButton("Mute" , SoundController.isMuted());
		bMute.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				if(ev.getStateChange()==ItemEvent.SELECTED){
					SoundController.Mute(true);
				} else if(ev.getStateChange()==ItemEvent.DESELECTED){
					SoundController.Mute(false);
				}
			}
		});
		JButton bBack = new JButton("Back");
	    bBack.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		SessionConfig.SaveSettings(SoundController.isMuted(),SoundController.GetSfxVolume(),SoundController.GetMusicVolume());
				CardLayout cl = (CardLayout)getParent().getLayout();
				cl.show(getParent(), "Menu");
	    	}
	    });
        add(lTextField);
        add(sTextField);
		add(bMute);
		add(lMusic);
		add(sMusic);
		add(lSfx);
		add(sSfx);
		add(bBack);
	}
}
