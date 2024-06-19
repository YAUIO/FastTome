import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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

    static HashMap<String, Integer> curKey;
    static int prevI = 0;

    public static void main(String[] args) {
        Menu.openDirDialog();
        curFrame = new JFrame("FastTome");
        boolean display = true;
        boolean init = true;
        KeyListenerMenu keyListenerMenu = new KeyListenerMenu();
        firstView = new JScrollPane();
        label = new Triple<>(new JLabel(), new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB), new JLabel());
        curFrame.setSize(x, y);
        curFrame.setPreferredSize(new Dimension(x, y));
        curFrame.setLayout(new BorderLayout());
        curFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        curFrame.setLocationRelativeTo(null);
        curKey = new HashMap<>();
        curKey.put("UP", 0);
        curKey.put("DOWN", 0);
        curKey.put("LEFT", 0);
        curKey.put("RIGHT", 0);

        jMenuBar = Menu.getMenu();
        curFrame.setJMenuBar(jMenuBar);
        jMenuBar.setLocation(1000, 0);

        Runnable changeImg = () -> {
            while (true) {
                if (Menu.view == 0) {
                    if ((curKey.get("RIGHT") > 0 && i < pictures.size() - 1) || (curKey.get("LEFT") > 0 && i > 0)) {
                        curFrame.remove(label.third);
                        if (i - curKey.get("LEFT") > -1) {
                            i -= curKey.get("LEFT");
                        } else {
                            i = 0;
                        }
                        if (i + curKey.get("RIGHT") < pictures.size() - 1) {
                            i += curKey.get("RIGHT");
                        } else {
                            i = pictures.size() - 1;
                        }
                        curKey.replace("LEFT", 0);
                        curKey.replace("RIGHT", 0);
                        label = Image.ParseImageF(label.first, pictures.get(i).toString(), x, y);
                        prevI = i;
                        curFrame.add(label.third, BorderLayout.SOUTH);
                        curFrame.pack();
                        curFrame.setVisible(true);
                    }
                    if (curKey.get("UP") > 0) {
                        label = Image.reScale("UP", label, curKey.get("UP"));
                        curKey.replace("UP", 0);
                        curFrame.pack();
                        curFrame.setVisible(true);
                    }
                    if (curKey.get("DOWN") > 0) {
                        label = Image.reScale("DOWN", label, curKey.get("DOWN"));
                        curKey.replace("DOWN", 0);
                        curFrame.pack();
                        curFrame.setVisible(true);
                    }
                }
            }
        };

        Thread changeThread = new Thread(changeImg);
        pictures = FileRead.getFiles(Menu.directory);
        Main.curPath = Menu.directory;
        Menu.directory = "none";
        changeThread.start();

        if (!pictures.isEmpty()) {
            label = Image.ParseImageF(label.first, pictures.get(i).toString(), x, y);
        } else {
            ImageIcon icon = new ImageIcon(label.second);
            label.first.setIcon(icon);
        }

        while (display) {

            if (!Menu.directory.equals("none") || Menu.viewChanged) {

                if (!Menu.directory.equals("none")) {
                    final String dir = Menu.directory;
                    Thread parseDirectory = new Thread(() -> Main.pictures = FileRead.getFiles(dir));
                    parseDirectory.start();
                    while (parseDirectory.isAlive()){
                        JOptionPane.showMessageDialog(curFrame, "Parsing photos",
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                    Main.curPath = Menu.directory;
                    Main.curFrame.remove(Main.label.third);
                    Menu.directory = "none";
                    i = 0;
                }

                if (Menu.view == 0) {
                    if (!pictures.isEmpty()) {
                        label = Image.ParseImageF(label.first, pictures.get(i).toString(), x, y);
                        curFrame.add(label.third, BorderLayout.SOUTH);
                    } else {
                        ImageIcon icon = new ImageIcon(label.second);
                        label.first.setIcon(icon);
                    }

                    if (Menu.viewChanged) {
                        curFrame.remove(firstView);
                        curFrame.add(label.first, BorderLayout.CENTER);
                        curFrame.pack();
                        Menu.viewChanged = false;
                    }

                    curFrame.setVisible(true);

                } else if (Menu.view == 1) {
                    firstView = Menu.getFirstView(pictures);


                    if (Menu.viewChanged) {
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

            if (Menu.view == 1) {
                if (prevI != i) {
                    prevI = i;
                    Menu.view = 0;
                    Menu.viewChanged = true;
                    Menu.view0.setState(true);
                    Menu.view1.setState(false);
                }
            }

            if (!Menu.rename.equals("none")) {
                Pair<Boolean, File> r = FileRead.rename(pictures.get(i));
                pictures.set(i, r.second);
                Image.curImage = pictures.get(i);
                label.third.setText(r.second.getName());
                if (!r.first) {
                    JOptionPane.showMessageDialog(curFrame, "File exists",
                            "I/O error", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (curFrame.hasFocus()) {
                curFrame.requestFocus();
            }

            if (!KeyListenerMenu.key.equals("none") ) {
                if(KeyListenerMenu.key.equals("RIGHT") && i < pictures.size()-1){
                    curKey.replace(KeyListenerMenu.key, curKey.get(KeyListenerMenu.key) + 1);
                }else if(KeyListenerMenu.key.equals("LEFT") && i > 0){
                    curKey.replace(KeyListenerMenu.key, curKey.get(KeyListenerMenu.key) + 1);
                }else if (KeyListenerMenu.key.equals("UP") || KeyListenerMenu.key.equals("DOWN")){
                    curKey.replace(KeyListenerMenu.key, curKey.get(KeyListenerMenu.key) + 1);
                }
                KeyListenerMenu.key = "none";
            }
        }
    }
}
