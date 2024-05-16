import javax.swing.*;

public class Menu{
    public static JMenuBar getMenu(){
        JMenuBar jMenuBar = new JMenuBar();
        jMenuBar.setLocation(0,0);
        JMenu jMenu = new JMenu("Open new directory");
        JMenu jMenu1 = new JMenu("Rename");
        JMenu jMenu2 = new JMenu("Add tag");

        jMenuBar.add(jMenu);
        jMenuBar.add(jMenu1);
        jMenuBar.add(jMenu2);

        return jMenuBar;
    }
}
