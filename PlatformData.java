package application;
/**
 * @author Daniel Komjago
 */
/**
 * Klasa przechowywująca dane platformy
 */
public class PlatformData {
    /**
     * współrzędna x
     */
    public int xPos;
    /**
     * współrzędna y
     */
    public int yPos;

    /**
     * Konstruktor
     * @param x współrzędna x
     * @param y współrzędna y
     */
    public PlatformData(int x, int y){
        xPos = x;
        yPos = y;
    }
}