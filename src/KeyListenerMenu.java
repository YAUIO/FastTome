import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyListenerMenu implements KeyListener {
    public static String key = "none";

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            key = "RIGHT";
        }else if (e.getKeyCode() == KeyEvent.VK_LEFT){
            key = "LEFT";
        }else if (e.getKeyCode() == KeyEvent.VK_UP){
            key = "UP";
        }else if (e.getKeyCode() == KeyEvent.VK_DOWN){
            key = "DOWN";
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(!key.equals("none")){
            key = "none";
        }
    }
}
