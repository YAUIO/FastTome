import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class Main {
    static JMenuBar jMenuBar;

    public static void main(String[] args) throws Exception {
        Menu.directory = "D:\\Users\\User\\Pictures\\Avka"; //add / for linux
        int x = 1280;
        int y = 720;

        List<File> pictures = FileReader.getFiles(Menu.directory);

        int i = 0;
        boolean display = true;
        boolean init = true;
        KeyListenerMenu keyListenerMenu = new KeyListenerMenu();
        JFrame curFrame = new JFrame("FastTome");
        Pair<JScrollPane,JPanel> firstView = new Pair<>(new JScrollPane(), new JPanel());
        Pair<JLabel, BufferedImage> label = new Pair<>(new JLabel(), new BufferedImage(x,y,BufferedImage.TYPE_INT_RGB));
        curFrame.setSize(x, y);
        curFrame.setLayout(new BorderLayout());
        curFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                    pictures = FileReader.getFiles(Menu.directory);
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
                        curFrame.remove(firstView.first);
                        curFrame.add(label.first,BorderLayout.CENTER);
                        Menu.viewChanged = false;
                    }

                    curFrame.setVisible(true);

                }else if (Menu.view == 1){
                    firstView = Menu.getFirstView(pictures.size());
                    int c = 0;

                    if(!pictures.isEmpty()) { //fix this bs
                        System.out.println(pictures.size());
                        while (c<pictures.size()){
                            firstView.second.add(new JTextArea("PHOTO"));
                            c++;
                        }
                        curFrame.pack();
                        curFrame.setVisible(true);
                        c = 0;
                        /*while (c<pictures.size()){
                            label = Image.ParseImageF(label.first, pictures.get(i).toString(), x, y);
                            firstView.second.add(label.first);
                            c++;
                            System.out.println("p");
                        }*/
                    }else{
                        ImageIcon icon = new ImageIcon(label.second);
                        label.first.setIcon(icon);
                        firstView.second.add(label.first);
                    }


                    if(Menu.viewChanged){
                        curFrame.remove(label.first);
                        curFrame.add(firstView.first);
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
                }
            }


            curFrame.requestFocus();
            curKey = KeyListenerMenu.key;
        }
    }
}
