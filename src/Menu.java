import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Menu {
    static String directory = "none";
    static String rename = "none";
    static int view = 0;
    static boolean viewChanged = true;
    static JCheckBoxMenuItem view0 = new JCheckBoxMenuItem("Image", true);
    static JCheckBoxMenuItem view1 = new JCheckBoxMenuItem("List");

    public static JMenuBar getMenu() {
        JMenuBar jMenuBar = new JMenuBar();

        JMenu jMenu = new JMenu("View");
        JMenu jMenu1 = new JMenu("Open new directory");
        JMenu jMenu2 = new JMenu("Rename");
        JMenu jMenu3 = new JMenu("Tags");
        JMenu jMenu4 = new JMenu("Collections");


        view0.addActionListener(e -> {
            view1.setState(false);
            view0.setState(true);
            view = 0;
            viewChanged = true;
        });

        view1.addActionListener(e -> {
            view0.setState(false);
            view1.setState(true);
            view = 1;
            viewChanged = true;
        });

        jMenu.add(view0);
        jMenu.add(view1);

        JMenuItem jMenuItem = new JMenuItem("Open");
        JMenuItem jMenuItem1 = new JMenuItem("Rename");
        JMenuItem jMenuItem2 = new JMenuItem("Add");
        JMenuItem jMenuItem21 = new JMenuItem("View");
        JMenuItem jMenuItem22 = new JMenuItem("Remove");
        JMenuItem jMenuItem3 = new JMenuItem("Add to");
        JMenuItem jMenuItem4 = new JMenuItem("View");

        jMenu1.add(jMenuItem);
        jMenu2.add(jMenuItem1);
        jMenu3.add(jMenuItem2);
        jMenu3.add(jMenuItem21);
        jMenu3.add(jMenuItem22);
        jMenu4.add(jMenuItem3);
        jMenu4.add(jMenuItem4);

        jMenuItem.addActionListener(e -> Menu.openDirDialog()); //change directory

        jMenuItem1.addActionListener(e -> Menu.openRenameDial()); //rename

        jMenuItem2.addActionListener(e -> Menu.writeTagToFile()); //add tag

        jMenuItem21.addActionListener(e -> viewTagDial()); //view tags

        jMenuItem22.addActionListener(e -> removeTagDial()); //remove a tag

        jMenuItem3.addActionListener(e -> addToCollection()); //add to collection

        jMenuItem4.addActionListener(e -> viewCollections()); //sample collection

        jMenuBar.add(jMenu);
        jMenuBar.add(jMenu1);
        jMenuBar.add(jMenu2);
        jMenuBar.add(jMenu3);
        jMenuBar.add(jMenu4);

        return jMenuBar;
    }

    public static void viewCollections() {
        JDialog jd = new JDialog(Main.curFrame);
        String[] arr = new String[1];
        arr[0] = "none";

        try {
            BufferedReader bc = new BufferedReader(new FileReader("src\\.ftcollections"));
            String buf;
            HashSet<String> coll = new HashSet<>();
            int i, c;
            while (bc.ready()) {
                buf = bc.readLine();
                i = buf.indexOf("Collections:") + 12;
                c = i;
                while (c != buf.length() - 1) {
                    i = c + 1;
                    c = buf.indexOf(" ", c + 1);
                    coll.add(buf.substring(i, c));
                }
            }
            bc.close();

            arr = new String[coll.size()];

            i = 0;

            for (String s : coll) {
                arr[i] = s;
                i++;
            }

        } catch (IOException e) {
            System.out.println(e.getStackTrace() + " Collections file wasn't found");
        }

        JList<String> l = new JList<>(arr);
        l.addListSelectionListener(e -> {
            jd.dispose();

            //todo make view

        });
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(l);
        l.setLayoutOrientation(JList.VERTICAL);
        jd.add(scrollPane);
        jd.setSize(new Dimension(200, 40 + 20 * arr.length));
        jd.setPreferredSize(new Dimension(200, 40 + 20 * arr.length));
        jd.setVisible(true);
        jd.requestFocus();
    }


    public static void addToCollection() {
        JDialog jd = new JDialog(Main.curFrame);
        String[] arr = new String[1];
        arr[0] = "New collection";

        try {
            BufferedReader bc = new BufferedReader(new FileReader("src\\.ftcollections"));
            String buf;
            HashSet<String> coll = new HashSet<>();
            int i, c;
            while (bc.ready()) {
                buf = bc.readLine();
                i = buf.indexOf("Collections:") + 12;
                c = i;
                while (c != buf.length() - 1) {
                    i = c + 1;
                    c = buf.indexOf(" ", c + 1);
                    coll.add(buf.substring(i, c));
                }
            }
            bc.close();

            ArrayList<String> toRemove = new ArrayList<>();

            for (String s : coll) {
                if ((FileRead.imgData.second.get(Image.curImage.getAbsolutePath()) != null && !FileRead.imgData.second.get(Image.curImage.getAbsolutePath()).isEmpty())) {
                    if (FileRead.imgData.second.get(Image.curImage.getAbsolutePath()).contains(s)) {
                        toRemove.add(s);
                    }
                }
            }

            coll.removeAll(toRemove);

            arr = new String[coll.size() + 1];

            arr[0] = "New collection";

            i = 1;

            for (String s : coll) {
                arr[i] = s;
                i++;
            }

        } catch (IOException e) {
            System.out.println(e.getStackTrace() + " Collections file wasn't found");
        }

        JList<String> l = new JList<>(arr);
        l.addListSelectionListener(e -> {

            jd.dispose();
            if (l.getSelectedIndex() == 0) {
                openNewCollectionDial();
            } else {
                writeCollection(l.getSelectedValue());
            }

        });
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(l);
        l.setLayoutOrientation(JList.VERTICAL);
        jd.add(scrollPane);
        jd.setSize(new Dimension(200, 40 + 20 * arr.length));
        jd.setPreferredSize(new Dimension(200, 40 + 20 * arr.length));
        jd.setVisible(true);
        jd.requestFocus();
    }

    public static void writeCollection(String col) {

        boolean isRecord = FileRead.imgData.second.containsKey(Image.curImage.getAbsolutePath());
        boolean isTag = false;
        if (isRecord) {
            isTag = FileRead.imgData.second.get(Image.curImage.getAbsolutePath()).contains(col);
        }

        try {
            if (!isTag && !isRecord) {
                BufferedWriter bw = new BufferedWriter(new FileWriter("src\\.ftcollections", true));
                if (!FileRead.imgData.second.isEmpty()) {
                    bw.append('\n');
                }
                bw.append(Image.curImage.getAbsolutePath()).append(" Collections: ");
                bw.append(col);
                bw.append(' ');
                ArrayList<String> tags = new ArrayList<>();
                tags.add(col);
                FileRead.imgData.second.put(Image.curImage.getAbsolutePath(), tags);
                bw.close();
            } else if (!isTag) {
                BufferedReader br = new BufferedReader(new FileReader("src\\.ftcollections"));
                ArrayList<String> file = new ArrayList<>();
                int i = -1;
                while (br.ready()) {
                    file.add(br.readLine());
                    if (file.get(file.size() - 1).startsWith(Image.curImage.getAbsolutePath())) {
                        i = file.size() - 1;
                    }
                }
                br.close();
                if (i != -1) {
                    file.set(i, file.get(i) + col + " ");

                    BufferedWriter bw = new BufferedWriter(new FileWriter("src\\.ftcollections"));

                    bw.write("");

                    for (String s : file) {
                        if (!s.isEmpty() && !s.equals(" ")) {
                            bw.append(s);
                            bw.append('\n');
                        }
                    }

                    bw.close();

                    FileRead.imgData.second.get(Image.curImage.getAbsolutePath()).add(col);
                }
            }

        } catch (IOException ex) {
            System.out.println(ex.getStackTrace());
        }
    }

    public static void openNewCollectionDial() {
        JDialog dial = new JDialog(Main.curFrame);
        JLabel l = new JLabel("Type in new collection (no whitespaces)");
        l.setHorizontalTextPosition(SwingConstants.CENTER);
        l.setVerticalTextPosition(SwingConstants.CENTER);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setVerticalAlignment(SwingConstants.CENTER);
        dial.add(l, BorderLayout.NORTH);

        JTextArea jt = new JTextArea();
        jt.setPreferredSize(new Dimension(260, 80));
        jt.setEditable(true);
        dial.add(jt, BorderLayout.CENTER);

        jt.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String tag = jt.getText();
                    jt.setText("");
                    dial.dispose();
                    writeCollection(tag);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        dial.setSize(new Dimension(260, 80));
        dial.setPreferredSize(new Dimension(260, 80));
        dial.pack();
        dial.setVisible(true);
    }

    public static void viewTagDial() {
        JDialog jd = new JDialog(Main.curFrame);
        String[] arr = new String[1];
        arr[0] = "none";
        if (FileRead.imgData.first.get(Image.curImage.getName()) != null && !FileRead.imgData.first.get(Image.curImage.getName()).isEmpty()) {
            arr = new String[FileRead.imgData.first.get(Image.curImage.getName()).size()];
            int i = 0;
            for (String tag : FileRead.imgData.first.get(Image.curImage.getName())) {
                arr[i] = tag;
                i++;
            }
        }
        JList<String> l = new JList<>(arr);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(l);
        l.setLayoutOrientation(JList.VERTICAL);
        jd.add(scrollPane);
        jd.setSize(new Dimension(200, 40 + 20 * arr.length));
        jd.setPreferredSize(new Dimension(200, 40 + 20 * arr.length));
        jd.setVisible(true);
        jd.requestFocus();
    }

    public static void removeTagDial() {
        JDialog jd = new JDialog(Main.curFrame);
        String[] arr = new String[1];
        arr[0] = "none";
        if (FileRead.imgData.first.get(Image.curImage.getName()) != null) {
            arr = new String[FileRead.imgData.first.get(Image.curImage.getName()).size()];
            int i = 0;
            for (String tag : FileRead.imgData.first.get(Image.curImage.getName())) {
                arr[i] = tag;
                i++;
            }
        }
        JList<String> l = new JList<>(arr);

        l.addListSelectionListener(e1 -> {
            if (!l.getSelectedValue().equals("none")) {
                try {
                    String tag = l.getSelectedValue();
                    jd.dispose();
                    BufferedReader br = new BufferedReader(new FileReader(Main.curPath + "\\.fasttomedata"));
                    ArrayList<String> file = new ArrayList<>();
                    int i = -1;
                    while (br.ready()) {
                        file.add(br.readLine());
                        if (file.get(file.size() - 1).startsWith(Image.curImage.getName())) {
                            i = file.size() - 1;
                        }
                    }
                    br.close();

                    if (i != -1) {
                        int pos = file.get(i).lastIndexOf(tag);

                        if (pos != -1) {

                            FileRead.imgData.first.get(Image.curImage.getName()).remove(tag);

                            if (!FileRead.imgData.first.get(Image.curImage.getName()).isEmpty()) {
                                file.set(i, file.get(i).substring(0, pos - 1) + file.get(i).substring(pos + tag.length()));
                            } else {
                                file.remove(i);
                                FileRead.imgData.first.remove(Image.curImage.getName());
                            }


                            BufferedWriter bw = new BufferedWriter(new FileWriter(Main.curPath + "\\.fasttomedata"));

                            bw.write("");

                            i = 0;

                            while (i < file.size()) {
                                if (!file.get(i).isEmpty()) {
                                    bw.append(file.get(i));
                                }
                                if (i + 1 < file.size() && !file.get(i + 1).isEmpty()) {
                                    bw.append('\n');
                                }
                                i++;
                            }

                            bw.close();
                        }

                    }

                } catch (IOException ex) {
                    //System.out.println(ex.getMessage());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(l);
        l.setLayoutOrientation(JList.VERTICAL);
        jd.add(scrollPane);
        jd.setSize(new Dimension(200, 40 + 20 * arr.length));
        jd.setPreferredSize(new Dimension(200, 40 + 20 * arr.length));
        jd.setVisible(true);
        jd.requestFocus();
    }

    public static JScrollPane getFirstView(List<File> l) {
        String[] arr = new String[l.size()];
        int i = 0;
        while (i < l.size()) {
            arr[i] = l.get(i).getName();
            i++;
        }
        JList<String> list = new JList<>(arr);
        list.addListSelectionListener(e -> Main.i = e.getFirstIndex());
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(list);
        list.setLayoutOrientation(JList.VERTICAL);
        return scrollPane;
    }

    public static void openDirDialog() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.addActionListener(e -> {
            if (e.getActionCommand().equals("ApproveSelection")) {
                directory = jFileChooser.getSelectedFile().toString();
            }
        });

        jFileChooser.setSize(400, 400);

        JDialog dialog = new JDialog();

        jFileChooser.showOpenDialog(dialog);

        jFileChooser.setVisible(true);
    }

    public static void openRenameDial() {
        JDialog dial = new JDialog(Main.curFrame);
        JLabel l = new JLabel("Type in new name (with .ext)");
        l.setHorizontalTextPosition(SwingConstants.CENTER);
        l.setVerticalTextPosition(SwingConstants.CENTER);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setVerticalAlignment(SwingConstants.CENTER);
        dial.add(l, BorderLayout.NORTH);

        JTextArea jt = new JTextArea();
        jt.setPreferredSize(new Dimension(100, 80));
        jt.setEditable(true);
        dial.add(jt, BorderLayout.CENTER);

        jt.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    rename = jt.getText();
                    jt.setText("");
                    dial.dispose();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        dial.setSize(new Dimension(200, 80));
        dial.setPreferredSize(new Dimension(200, 80));
        dial.pack();
        dial.setVisible(true);
    }

    public static void writeTagToFile() {
        JDialog dial = new JDialog(Main.curFrame);
        JLabel l = new JLabel("Type in new tag (no whitespaces)");
        l.setHorizontalTextPosition(SwingConstants.CENTER);
        l.setVerticalTextPosition(SwingConstants.CENTER);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setVerticalAlignment(SwingConstants.CENTER);
        dial.add(l, BorderLayout.NORTH);

        JTextArea jt = new JTextArea();
        jt.setPreferredSize(new Dimension(260, 80));
        jt.setEditable(true);
        dial.add(jt, BorderLayout.CENTER);

        jt.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String tag = jt.getText();
                    jt.setText("");
                    dial.dispose();

                    boolean isRecord = FileRead.imgData.first.containsKey(Image.curImage.getName());
                    boolean isTag = false;
                    if (isRecord) {
                        isTag = FileRead.imgData.first.get(Image.curImage.getName()).contains(tag);
                    }

                    try {
                        if (!isTag && !isRecord) {
                            BufferedWriter bw = new BufferedWriter(new FileWriter(Main.curPath + "\\.fasttomedata", true));
                            if (!FileRead.imgData.first.isEmpty()) {
                                bw.append('\n');
                            }
                            bw.append(Image.curImage.getName()).append(" Tags: ");
                            bw.append(tag);
                            bw.append(' ');
                            ArrayList<String> tags = new ArrayList<>();
                            tags.add(tag);
                            FileRead.imgData.first.put(Image.curImage.getName(), tags);
                            bw.close();
                        } else if (!isTag) {
                            BufferedReader br = new BufferedReader(new FileReader(Main.curPath + "\\.fasttomedata"));
                            ArrayList<String> file = new ArrayList<>();
                            int i = -1;
                            while (br.ready()) {
                                file.add(br.readLine());
                                if (file.get(file.size() - 1).startsWith(Image.curImage.getName())) {
                                    i = file.size() - 1;
                                }
                            }
                            br.close();
                            if (i != -1) {
                                file.set(i, file.get(i) + tag + " ");

                                BufferedWriter bw = new BufferedWriter(new FileWriter(Main.curPath + "\\.fasttomedata"));

                                bw.write("");

                                for (String s : file) {
                                    if (!s.isEmpty() && !s.equals(" ")) {
                                        bw.append(s);
                                        bw.append('\n');
                                    }
                                }

                                bw.close();

                                FileRead.imgData.first.get(Image.curImage.getName()).add(tag);
                            }
                        }

                    } catch (IOException ex) {
                        System.out.println(ex.getStackTrace());
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        dial.setSize(new Dimension(260, 80));
        dial.setPreferredSize(new Dimension(260, 80));
        dial.pack();
        dial.setVisible(true);
    }
}
