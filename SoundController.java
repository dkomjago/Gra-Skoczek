package application;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
/**
 * @author Daniel Komjago
 */
/**
 * Klasa zarządzająca dżwiękiem
 */

public class SoundController {
    /**
     * Poziom głośności efektów dżwiękowych
     */
    private static int sfxVolume =80;
    /**
     * Poziom głośności muzyki
     */
    private static int musicVolume =60;
    /**
     * Stan wyciszenia dżwięku
     */
    private static Boolean muted=false;
    /**
     * Odcinek z muzyką
     */
    private static Clip music;

    /**
     * Zwracanie poziomu głośności efektów dżwiękowych
     * @return poziom głośności efektów dżwiękowych
     */
    public static int GetSfxVolume()
    {
        return sfxVolume;
    }

    /**
     * Ustawinie poziomu głośności efektów dżwiękowych
     * @param volume poziom głośności efektów dżwiękowych
     */

    public static void SetSfxVolume(int volume)
    {
        sfxVolume=volume;
    }

    /**
     *Zwracanie poziomu głośności muzyki
     * @return poziom głośności muzyki
     */
    public static int GetMusicVolume()
    {
        return musicVolume;
    }

    /**
     * Ustawinie poziomu głośności muzyki
     * @param volume poziom głośności muzyki
     */

    public static void SetMusicVolume(int volume)
    {
        musicVolume=volume;
    }

    /**
     * Metoda zwracająca stan wyciszenia
     * @return stan wyciszenia
     */
    public static Boolean isMuted()
    {
        if(muted)
            return true;
        else
            return false;
    }

    /**
     * Wyciszenie dżwięków
     * @param flag wyciszyć(tak/nie)
     */
    public static void Mute(Boolean flag)
    {
        muted=flag;
        if(music!= null) {
            if (isMuted()) {
                music.stop();
            } else
                playMusic();
        }
    }

    /**
     * Odtwarzanie pliku z dżwiękiem
     * @param filename ścieżka do pliku
     */
    public static synchronized void playSound(final String filename) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResourceAsStream("sfx/"+filename));
                    clip.open(inputStream);
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(sfxVolume-80);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Odtwaraznie muzyki
     */
    public static synchronized void playMusic() {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    music = AudioSystem.getClip();
                    URL url = this.getClass().getResource("sfx/music.wav");
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(url);
                    music.open(inputStream);
                    FloatControl gainControl = (FloatControl) music.getControl(FloatControl.Type.MASTER_GAIN);
                    gainControl.setValue(musicVolume-80);
                    if(!isMuted())
                    music.loop(Clip.LOOP_CONTINUOUSLY);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }
}
