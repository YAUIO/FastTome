import javax.swing.*;
import javax.swing.plaf.MenuBarUI;
import javax.swing.text.html.ImageView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {
    static JMenuBar jMenuBar;

    public static void main(String[] args) throws Exception {
        String path = "D:\\Users\\User\\Pictures";
        int x = 1280;
        int y = 720;

        ArrayList<String> picNames = FileReader.getFiles(path);

        int i = 0;
        boolean display = true;
        KeyListenerMenu keyListenerMenu = new KeyListenerMenu();
        JFrame curFrame = new JFrame();
        curFrame.setSize(x, y);
        curFrame.setLayout(new FlowLayout());
        curFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String curKey = KeyListenerMenu.key;
        Pair<JLabel, BufferedImage> label = Image.ParseImageF(new JLabel(), path, picNames.get(i), x, y);
        jMenuBar = Menu.getMenu();
        curFrame.setJMenuBar(jMenuBar);
        jMenuBar.setLocation(1000, 0);
        curFrame.add(label.first);

        while (display == true) {
            if (!curFrame.isVisible()) {
                curFrame.addKeyListener(keyListenerMenu);
                curFrame.setVisible(true);
                curFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }

            if (!curKey.equals("none")) {
                if (curKey.equals("RIGHT") && i < picNames.size() - 1) {
                    i++;
                    label = Image.ParseImageF(label.first, path, picNames.get(i), x, y);
                    KeyListenerMenu.key = "none";
                    curFrame.setVisible(true);
                } else if (curKey.equals("LEFT") && i > 0) {
                    i--;
                    label = Image.ParseImageF(label.first, path, picNames.get(i), x, y);
                    KeyListenerMenu.key = "none";
                    curFrame.setVisible(true);
                } else if (curKey.equals("UP")) {
                    label = Image.reScale(curKey, label);
                    KeyListenerMenu.key = "none";
                    curFrame.setVisible(true);
                } else if (curKey.equals("DOWN")) {
                    label = Image.reScale(curKey, label);
                    KeyListenerMenu.key = "none";
                    curFrame.setVisible(true);
                }
            }

            curFrame.requestFocus();
            curKey = KeyListenerMenu.key;
        }
    }
}
