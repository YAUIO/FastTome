import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu{
    static boolean needsFocus = false;
    static String directory = "none";

    public static JMenuBar getMenu(){
        JMenuBar jMenuBar = new JMenuBar();

        JMenu jMenu = new JMenu("Open new directory");
        JMenu jMenu1 = new JMenu("Rename");
        JMenu jMenu2 = new JMenu("Add tag");

        JMenuItem jMenuItem = new JMenuItem("Open");
        JMenuItem jMenuItem1 = new JMenuItem("Rename");
        JMenuItem jMenuItem2 = new JMenuItem("Add");

        jMenu.add(jMenuItem);
        jMenu1.add(jMenuItem1);
        jMenu2.add(jMenuItem2);

        jMenuItem.addActionListener(e -> Menu.openDirDialog());

        jMenuItem1.addActionListener(e -> System.out.println("open"));

        jMenuItem2.addActionListener(e -> System.out.println("open"));

        jMenuBar.add(jMenu);
        jMenuBar.add(jMenu1);
        jMenuBar.add(jMenu2);

        return jMenuBar;
    }

    public static void openDirDialog(){
        needsFocus = true;
        JDialog dialog = new JDialog();
        JTextField textField = new JTextField();
        JButton submit = new JButton("Submit");
        dialog.add(textField);
        dialog.setLayout(new GridLayout());
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(600,100);

        textField.setEnabled(true);
        textField.setEditable(true);
        textField.setSize(500,100);
        textField.requestFocusInWindow();
        textField.setText("FUCK THIS LANGUAGE");

        submit.setSize(100,100);
        dialog.add(submit);

        dialog.setVisible(true);
        if(needsFocus){
            dialog.requestFocus();
        }
        submit.addActionListener(e -> {
            dialog.setVisible(false);
            needsFocus = false;
            System.out.println(needsFocus);
            directory = textField.getText();
        });
    }

    public static boolean isNeedsFocus() {
        return needsFocus;
    }
}
