import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;
/**
 * @author Daniel Komjago
 */
/**
 * Klasa zarządzająca dżwiękiem
 */

class SoundController {
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
    static int getSfxVolume()
    {
        return sfxVolume;
    }

    /**
     * Ustawinie poziomu głośności efektów dżwiękowych
     * @param volume poziom głośności efektów dżwiękowych
     */

    static void setSfxVolume(int volume)
    {
        sfxVolume=volume;
    }

    /**
     *Zwracanie poziomu głośności muzyki
     * @return poziom głośności muzyki
     */
    static int getMusicVolume()
    {
        return musicVolume;
    }

    /**
     * Ustawinie poziomu głośności muzyki
     * @param volume poziom głośności muzyki
     */

    static void setMusicVolume(int volume)
    {
        musicVolume=volume;
    }

    /**
     * Metoda zwracająca stan wyciszenia
     * @return stan wyciszenia
     */
    static Boolean isMuted(){
        return muted;
    }

    /**
     * Wyciszenie dżwięków
     * @param flag wyciszyć(tak/nie)
     */
    static void mute(Boolean flag){
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
    static synchronized void playSound(final String filename) {
        new Thread(new Runnable() {
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
    static synchronized void playMusic() {
        new Thread(new Runnable() {
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
