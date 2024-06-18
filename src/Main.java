import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static final int x = 1280;
    static final int y = 720;
    static JMenuBar jMenuBar;
    static JFrame curFrame;
    static String curPath;
    static int i = 0;
    static ArrayList<File> pictures;
    static Triple<JLabel, BufferedImage, JLabel> label;
    static JScrollPane firstView;

    public static void main(String[] args) throws Exception {
        Menu.directory = "D:\\Users\\User\\Pictures\\Avka"; //add / for linux
        curFrame = new JFrame("FastTome");
        pictures = FileRead.getFiles(Menu.directory);
        int prevI = 0;
        boolean display = true;
        boolean init = true;
        KeyListenerMenu keyListenerMenu = new KeyListenerMenu();
        firstView = new JScrollPane();
        label = new Triple<>(new JLabel(), new BufferedImage(x,y,BufferedImage.TYPE_INT_RGB), new JLabel());
        curFrame.setSize(x, y);
        curFrame.setPreferredSize(new Dimension(x,y));
        curFrame.setLayout(new BorderLayout());
        curFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        curFrame.setLocationRelativeTo(null);
        String curKey = KeyListenerMenu.key;
        if(!pictures.isEmpty()) {
            label = Image.ParseImageF(label.first, pictures.get(i).toString(), x, y);
        }else{
            ImageIcon icon = new ImageIcon(label.second);
            label.first.setIcon(icon);
        }

        jMenuBar = Menu.getMenu();
        curFrame.setJMenuBar(jMenuBar);
        jMenuBar.setLocation(1000, 0);

        while (display) {

            if(!Menu.directory.equals("none") || Menu.viewChanged){

                if(!Menu.directory.equals("none")){
                    pictures = FileRead.getFiles(Menu.directory);
                    curPath = Menu.directory;
                    curFrame.remove(label.third);
                    Menu.directory = "none";
                    i = 0;
                }

                if(Menu.view == 0){
                    if(!pictures.isEmpty()) {
                        label = Image.ParseImageF(label.first, pictures.get(i).toString(), x, y);
                    }else{
                        ImageIcon icon = new ImageIcon(label.second);
                        label.first.setIcon(icon);
                    }

                    if(Menu.viewChanged){
                        curFrame.remove(firstView);
                        curFrame.add(label.first,BorderLayout.CENTER);
                        curFrame.add(label.third,BorderLayout.SOUTH);
                        curFrame.pack();
                        Menu.viewChanged = false;
                    }

                    curFrame.setVisible(true);

                }else if (Menu.view == 1){
                    firstView = Menu.getFirstView(pictures);


                    if(Menu.viewChanged){
                        curFrame.remove(label.first);
                        curFrame.remove(label.third);
                        curFrame.add(firstView);
                        curFrame.pack();
                        Menu.viewChanged = false;
                    }

                    curFrame.setVisible(true);
                }

            }

            if (init) {
                curFrame.addKeyListener(keyListenerMenu);
                curFrame.setVisible(true);
                curFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                init = false;
            }

            if(Menu.view == 0){
                if (!curKey.equals("none")) {
                    curFrame.remove(label.third);
                    if (curKey.equals("RIGHT") && i < pictures.size() - 1) {
                        i++;
                        label = Image.ParseImageF(label.first, pictures.get(i).toString(), x, y);
                        KeyListenerMenu.key = "none";
                        curFrame.setVisible(true);
                    } else if (curKey.equals("LEFT") && i > 0) {
                        i--;
                        label = Image.ParseImageF(label.first, pictures.get(i).toString(), x, y);
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
                    prevI = i;
                    curFrame.add(label.third,BorderLayout.SOUTH);
                    curFrame.pack();
                    curFrame.setVisible(true);
                }
            }else if (Menu.view == 1){
                if (prevI != i){
                    prevI = i;
                    Menu.view = 0;
                    Menu.viewChanged = true;
                    Menu.view0.setState(true);
                    Menu.view1.setState(false);
                }
            }

            if(!Menu.rename.equals("none")){
                Pair<Boolean, File> r = FileRead.rename(pictures.get(i));
                pictures.set(i,r.second);
                Image.curImage = pictures.get(i);
                label.third.setText(r.second.getName());
                if(!r.first){
                    JOptionPane.showMessageDialog(curFrame, "File exists",
                            "I/O error", JOptionPane.ERROR_MESSAGE);
                }
            }

            if(curFrame.hasFocus()){
                curFrame.requestFocus();
            }
            curKey = KeyListenerMenu.key;
        }
    }
}
