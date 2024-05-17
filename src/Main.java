import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Main {
    static JMenuBar jMenuBar;

    public static void main(String[] args) throws Exception {
        String path = "D:\\Users\\User\\Pictures\\Avka"; //add / for linux
        int x = 1280;
        int y = 720;

        ArrayList<String> picNames = FileReader.getFiles(path);

        int i = 0;
        boolean display = true;
        KeyListenerMenu keyListenerMenu = new KeyListenerMenu();
        JFrame curFrame = new JFrame("FastTome");
        Pair<JLabel, BufferedImage> label = new Pair<>(new JLabel(), new BufferedImage(x,y,BufferedImage.TYPE_INT_RGB));
        curFrame.setSize(x, y);
        curFrame.setLayout(new FlowLayout());
        curFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String curKey = KeyListenerMenu.key;
        if(!picNames.isEmpty()) {
            label = Image.ParseImageF(label.first, path, picNames.get(i), x, y);
        }else{
            ImageIcon icon = new ImageIcon(label.second);
            label.first.setIcon(icon);
        }

        jMenuBar = Menu.getMenu();
        curFrame.setJMenuBar(jMenuBar);
        jMenuBar.setLocation(1000, 0);
        curFrame.add(label.first);

        while (display) {

            if(!Menu.directory.equals("none")){
                path = Menu.directory;
                Menu.directory = "none";
                picNames = FileReader.getFiles(path);
                i = 0;
                if(!picNames.isEmpty()) {
                    label = Image.ParseImageF(label.first, path, picNames.get(i), x, y);
                }else{
                    ImageIcon icon = new ImageIcon(label.second);
                    label.first.setIcon(icon);
                }
                curFrame.setVisible(true);
            }

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

            if(!Menu.isNeedsFocus()){
                curFrame.requestFocus();
            }
            curKey = KeyListenerMenu.key;
        }
    }
}
