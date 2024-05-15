import javax.swing.*;
import javax.swing.text.html.ImageView;
import java.awt.*;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = "D:\\Users\\User\\Pictures";
        int x = 1280;
        int y = 720;

        ArrayList<String> picNames = FileReader.getFiles(path);

        int i = 0;
        boolean display = true;
        KeyListenerMenu keyListenerMenu = new KeyListenerMenu();
        Frame curFrame = ParseImage.ParseImageF(path,picNames.get(i),x,y);
        String curKey = KeyListenerMenu.key;

        while(display == true){
            if(!curFrame.isVisible()){
                curFrame.addKeyListener(keyListenerMenu);
                curFrame.setVisible(true);
            }

            if(!curKey.equals("none")){
                if (curKey.equals("RIGHT") && i<picNames.size()-1){
                    i++;
                    curFrame.setVisible(false);
                    curFrame = ParseImage.ParseImageF(path,picNames.get(i),x,y);
                    KeyListenerMenu.key = "none";
                }else if (curKey.equals("LEFT") && i>1){
                    i--;
                    curFrame.setVisible(false);
                    curFrame = ParseImage.ParseImageF(path,picNames.get(i),x,y);
                    KeyListenerMenu.key = "none";
                }
            }

            curFrame.requestFocus();
            curKey = KeyListenerMenu.key;
        }
    }
}
