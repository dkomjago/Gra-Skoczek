package application;
/**
 * @author Daniel Komjago
 */

/**
 * Interfejs dla komponentów gry
 */
public interface GameComponent {
    /**
     * Zatrzymanie komponentu
     */
    void Pause();
    /**
     * Wznowienie komponentu
     */
    void Resume();
}
