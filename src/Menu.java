import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Menu{
    static String directory = "none";
    static int view = 0;
    static boolean viewChanged = true;

    public static JMenuBar getMenu(){
        JMenuBar jMenuBar = new JMenuBar();

        JMenu jMenu = new JMenu("View");
        JMenu jMenu1 = new JMenu("Open new directory");
        JMenu jMenu2 = new JMenu("Rename");
        JMenu jMenu3 = new JMenu("Add tag");
        JMenu jMenu4 = new JMenu("Collections");

        JCheckBoxMenuItem view0 = new JCheckBoxMenuItem("Image",true);
        JCheckBoxMenuItem view1 = new JCheckBoxMenuItem("List");

        view0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view1.setState(false);
                view0.setState(true);
                view = 0;
                viewChanged = true;
            }
        });

        view1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view0.setState(false);
                view1.setState(true);
                view = 1;
                viewChanged = true;
            }
        });

        jMenu.add(view0);
        jMenu.add(view1);

        JMenuItem jMenuItem = new JMenuItem("Open");
        JMenuItem jMenuItem1 = new JMenuItem("Rename");
        JMenuItem jMenuItem2 = new JMenuItem("Add");
        JMenu jMenuItem3 = new JMenu("Add to collection");
        JMenuItem jMenuItem4 = new JMenuItem("Sample Collection");

        jMenu1.add(jMenuItem);
        jMenu2.add(jMenuItem1);
        jMenu3.add(jMenuItem2);
        jMenu4.add(jMenuItem3);
        jMenu4.add(jMenuItem4);

        jMenuItem.addActionListener(e -> Menu.openDirDialog());

        jMenuItem1.addActionListener(e -> System.out.println("open"));

        jMenuItem2.addActionListener(e -> System.out.println("open"));

        jMenuItem3.addActionListener(e -> System.out.println("open"));

        jMenuBar.add(jMenu);
        jMenuBar.add(jMenu1);
        jMenuBar.add(jMenu2);
        jMenuBar.add(jMenu3);
        jMenuBar.add(jMenu4);

        return jMenuBar;
    }

    public static Pair<JScrollPane,JPanel> getFirstView(int size){
        JPanel fv = new JPanel();
        JScrollPane scroll = new JScrollPane();

        fv.setLayout(new GridLayout(size,12));

        scroll.add(fv);

        return new Pair<>(scroll,fv);
    }

    public static void openDirDialog(){
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("ApproveSelection")){
                    directory = jFileChooser.getSelectedFile().toString();
                }
            }
        });

        jFileChooser.setSize(400,400);

        JDialog dialog = new JDialog();

        jFileChooser.showOpenDialog(dialog);

        jFileChooser.setVisible(true);
    }
}
