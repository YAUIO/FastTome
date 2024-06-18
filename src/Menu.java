import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Menu {
    static String directory = "none";
    static String rename = "none";
    static int view = 0;
    static boolean viewChanged = true;
    static JCheckBoxMenuItem view0 = new JCheckBoxMenuItem("Image", true);
    static JCheckBoxMenuItem view1 = new JCheckBoxMenuItem("List");

    public static JMenuBar getMenu() {
        JMenuBar jMenuBar = new JMenuBar();

        JMenu view = new JMenu("Layout");
        JMenu directory = new JMenu("Open");
        JMenu edit = new JMenu("Edit");
        JMenu info = new JMenu("Info");
        JMenu search = new JMenu("Search");


        view0.addActionListener(e -> {
            view1.setState(false);
            view0.setState(true);
            Menu.view = 0;
            viewChanged = true;
        });

        view1.addActionListener(e -> {
            view0.setState(false);
            view1.setState(true);
            Menu.view = 1;
            viewChanged = true;
        });

        view.add(view0);
        view.add(view1);

        JMenuItem openD = new JMenuItem("Directory");
        JMenuItem openC = new JMenuItem("Collection");
        JMenuItem rename1 = new JMenuItem("Rename");
        JMenuItem addTag = new JMenuItem("Add tag");
        JMenuItem tags = new JMenuItem("Tags");
        JMenuItem date = new JMenuItem("Date");
        JMenuItem removeTag = new JMenuItem("Remove tag");
        JMenuItem description = new JMenuItem("Description");
        JMenuItem descriptionEdit = new JMenuItem("Description");
        JMenuItem addTo = new JMenuItem("Add to");
        JMenuItem removeFrom = new JMenuItem("Remove from");
        JMenuItem collections = new JMenuItem("Collections");
        JMenuItem filterBy = new JMenuItem("Filter by");

        directory.add(openD);
        directory.add(openC);

        edit.add(rename1);
        edit.add(addTag);
        edit.add(removeTag);
        edit.add(addTo);
        edit.add(removeFrom);
        edit.add(descriptionEdit);

        info.add(collections);
        info.add(tags);
        info.add(description);
        info.add(date);

        search.add(filterBy);

        openD.addActionListener(e -> openDirDialog()); //change directory

        openC.addActionListener(e -> openColDialog()); //open a collection

        rename1.addActionListener(e -> openRenameDial()); //rename

        addTag.addActionListener(e -> writeTagToFile()); //add tag

        tags.addActionListener(e -> viewTagDial()); //view tags

        date.addActionListener(e -> viewDate());

        removeTag.addActionListener(e -> removeTagDial()); //remove a tag

        addTo.addActionListener(e -> addToCollection()); //add to collection

        removeFrom.addActionListener(e -> removeColDial()); //remove from collection

        collections.addActionListener(e -> viewCollections()); //view and choose collections assigned to a photo

        filterBy.addActionListener(e -> Search.filterDialogue());

        description.addActionListener(e -> viewDescription());

        descriptionEdit.addActionListener(e -> editDescription());

        jMenuBar.add(info);
        jMenuBar.add(edit);
        jMenuBar.add(view);
        jMenuBar.add(directory);
        jMenuBar.add(search);

        return jMenuBar;
    }

    private static void viewDate() {
        JDialog jd = new JDialog(Main.curFrame);
        String date = "No date found";

        if (FileRead.imgDate.get(Image.curImage.getName()) != null && FileRead.imgDate.get(Image.curImage.getName())!=null) {
            date = FileRead.imgDate.get(Image.curImage.getName()).toString();
            date = date.substring(0,date.indexOf('.'));
            date = date.replace('T',' ');
        }

        JLabel text = new JLabel(date);
        text.setPreferredSize(new Dimension(160, 60));
        text.setHorizontalTextPosition(SwingConstants.CENTER);
        text.setVerticalTextPosition(SwingConstants.CENTER);
        text.setHorizontalAlignment(SwingConstants.CENTER);
        text.setVerticalAlignment(SwingConstants.CENTER);
        jd.add(text);
        jd.setSize(new Dimension(160, 60));
        jd.setPreferredSize(new Dimension(160, 60));
        jd.setLocationRelativeTo(Main.curFrame);
        jd.setVisible(true);
        jd.requestFocus();
    }

    private static void openColDialog() {
        JDialog jd = new JDialog(Main.curFrame);
        String[] arr = new String[1];
        arr[0] = "All files";
        if (!FileRead.imgColl.values().isEmpty()) {
            HashSet<String> val = new HashSet<>();

            for (ArrayList<String> list : FileRead.imgColl.values()) {
                val.addAll(list);
            }

            arr = new String[val.size() + 1];
            arr[0] = "All files";
            int i = 1;
            for (String tag : val) {
                arr[i] = tag;
                i++;
            }
        }
        JList<String> l = new JList<>(arr);
        l.addListSelectionListener(e -> {
            jd.dispose();
            if (!l.getSelectedValue().equals("All files")) {
                Main.i = 0;

                view = 0;
                view0.setState(true);
                view1.setState(false);
                Main.curFrame.remove(Main.firstView);
                Main.curFrame.add(Main.label.first, BorderLayout.CENTER);
                Main.curFrame.add(Main.label.third, BorderLayout.SOUTH);
                Main.curFrame.pack();
                Main.curFrame.setVisible(true);

                Main.pictures = FileRead.getFilesCollection(l.getSelectedValue());
                try {
                    Main.curFrame.remove(Main.label.third);
                    Main.label = Image.ParseImageF(Main.label.first, Main.pictures.get(Main.i).toString(), Main.x, Main.y);
                    Main.curFrame.add(Main.label.third, BorderLayout.SOUTH);
                    Main.curFrame.pack();
                    Main.curFrame.setVisible(true);
                } catch (IOException ex) {
                    //System.out.println(ex.getStackTrace());
                }
            } else {
                Main.pictures = FileRead.getFiles(Main.curPath);
            }

        });
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(l);
        l.setLayoutOrientation(JList.VERTICAL);
        jd.add(scrollPane);
        jd.setSize(new Dimension(200, 40 + 20 * arr.length));
        jd.setPreferredSize(new Dimension(200, 40 + 20 * arr.length));
        jd.setLocationRelativeTo(Main.curFrame);
        jd.setVisible(true);
        jd.requestFocus();
    }

    private static void editDescription() {
        JDialog dial = new JDialog(Main.curFrame);
        JLabel l = new JLabel("Enter to save, Shift+Enter new line");
        l.setHorizontalTextPosition(SwingConstants.CENTER);
        l.setVerticalTextPosition(SwingConstants.CENTER);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setVerticalAlignment(SwingConstants.CENTER);
        dial.add(l, BorderLayout.NORTH);

        JTextArea jt = new JTextArea();
        if (FileRead.imgDesc.containsKey(Image.curImage.getName())) {
            if (!FileRead.imgDesc.get(Image.curImage.getName()).isEmpty()) {
                jt.setText(FileRead.imgDesc.get(Image.curImage.getName()).replace('/','\n'));
            }
        }
        jt.setPreferredSize(new Dimension(260, 240));
        jt.setEditable(true);
        dial.add(jt, BorderLayout.CENTER);

        jt.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getModifiersEx() == InputEvent.SHIFT_DOWN_MASK && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    jt.setText(jt.getText() + "\n");
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String description = jt.getText();
                    jt.setText("");
                    dial.dispose();

                    description = description.replace('\n', '/');

                    boolean isEmpty = FileRead.imgDesc.isEmpty();
                    boolean isRecord = FileRead.imgDesc.containsKey(Image.curImage.getName());
                    boolean isDescription = false;
                    if (isRecord) {
                        isDescription = FileRead.imgDesc.get(Image.curImage.getName()).contains(description);
                    }

                    try {

                        if((Boolean) Files.getAttribute(Path.of(Main.curPath + "\\.fasttomedesc"), "dos:hidden")){
                            Files.setAttribute(Path.of(Main.curPath + "\\.fasttomedesc"), "dos:hidden", false);
                        }

                        if (!isDescription && !isRecord) {
                            BufferedWriter bw = new BufferedWriter(new FileWriter(Main.curPath + "\\.fasttomedesc", true));
                            if (!FileRead.imgDesc.isEmpty()) {
                                bw.append('\n');
                            }
                            bw.append(Image.curImage.getName()).append(" Description: ");
                            bw.append(description);
                            FileRead.imgDesc.put(Image.curImage.getName(), description);
                            bw.close();
                        } else if (!isDescription) {
                            BufferedReader br = new BufferedReader(new FileReader(Main.curPath + "\\.fasttomedesc"));
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
                                file.set(i, Image.curImage.getName() + " Description: " + description);

                                BufferedWriter bw = new BufferedWriter(new FileWriter(Main.curPath + "\\.fasttomedesc"));

                                bw.write("");

                                for (String s : file) {
                                    if (!s.isEmpty() && !s.equals(" ")) {
                                        bw.append(s);
                                        bw.append('\n');
                                    }
                                }

                                bw.close();

                                FileRead.imgDesc.replace(Image.curImage.getName(), description);
                            }
                        }

                        if(!(Boolean) Files.getAttribute(Path.of(Main.curPath + "\\.fasttomedesc"), "dos:hidden")){
                            Files.setAttribute(Path.of(Main.curPath + "\\.fasttomedesc"), "dos:hidden", true);
                        }

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        dial.setSize(new Dimension(260, 240));
        dial.setPreferredSize(new Dimension(260, 240));
        dial.setLocationRelativeTo(Main.curFrame);
        dial.pack();
        dial.setVisible(true);
    }

    private static void viewDescription() {
        JDialog jd = new JDialog(Main.curFrame);
        String description = "No description yet";

        if (FileRead.imgDesc.get(Image.curImage.getName()) != null && !FileRead.imgDesc.get(Image.curImage.getName()).isEmpty()) {
            description = FileRead.imgDesc.get(Image.curImage.getName());
        }

        String visualText = description.replace('/','\n');

        JTextArea text = new JTextArea(visualText);
        jd.add(text);
        jd.setSize(new Dimension(200, 40 + 20 * (int)visualText.lines().count()));
        jd.setPreferredSize(new Dimension(200, 40 + 20 * (int)visualText.lines().count()));
        jd.setLocationRelativeTo(Main.curFrame);
        jd.setVisible(true);
        jd.requestFocus();
    }

    private static void removeColDial() {
        JDialog jd = new JDialog(Main.curFrame);
        String[] arr = new String[1];
        arr[0] = "none";
        if (FileRead.imgColl.get(Image.curImage.getAbsolutePath()) != null) {
            arr = new String[FileRead.imgColl.get(Image.curImage.getAbsolutePath()).size()];
            int i = 0;
            for (String tag : FileRead.imgColl.get(Image.curImage.getAbsolutePath())) {
                arr[i] = tag;
                i++;
            }
        }
        JList<String> l = new JList<>(arr);

        l.addListSelectionListener(e1 -> {
            if (!l.getSelectedValue().equals("none")) {
                try {

                    if((Boolean) Files.getAttribute(Path.of("src\\.ftcollections"), "dos:hidden")){
                        Files.setAttribute(Path.of("src\\.ftcollections"), "dos:hidden", false);
                    }

                    String tag = l.getSelectedValue();
                    jd.dispose();
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
                        int pos = file.get(i).lastIndexOf(tag);

                        if (pos != -1) {

                            FileRead.imgColl.get(Image.curImage.getAbsolutePath()).remove(tag);

                            if (!FileRead.imgColl.get(Image.curImage.getAbsolutePath()).isEmpty()) {
                                file.set(i, file.get(i).substring(0, pos - 1) + file.get(i).substring(pos + tag.length()));
                            } else {
                                file.remove(i);
                                FileRead.imgColl.remove(Image.curImage.getAbsolutePath());
                            }


                            BufferedWriter bw = new BufferedWriter(new FileWriter("src\\.ftcollections"));

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

                    if(!(Boolean) Files.getAttribute(Path.of("\\.ftcollections"), "dos:hidden")){
                        Files.setAttribute(Path.of("\\.ftcollections"), "dos:hidden", true);
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
        jd.setLocationRelativeTo(Main.curFrame);
        jd.setVisible(true);
        jd.requestFocus();
    }

    private static void viewCollections() {
        JDialog jd = new JDialog(Main.curFrame);
        String[] arr = new String[1];
        arr[0] = "none";
        if (FileRead.imgColl.get(Image.curImage.getAbsolutePath()) != null && !FileRead.imgColl.get(Image.curImage.getAbsolutePath()).isEmpty()) {
            arr = new String[FileRead.imgColl.get(Image.curImage.getAbsolutePath()).size()];
            int i = 0;
            for (String tag : FileRead.imgColl.get(Image.curImage.getAbsolutePath())) {
                arr[i] = tag;
                i++;
            }
        }
        JList<String> l = new JList<>(arr);
        l.addListSelectionListener(e -> {
            if (!l.getSelectedValue().equals("none")) {
                jd.dispose();
                Main.i = 0;

                view = 0;
                view0.setState(true);
                view1.setState(false);
                Main.curFrame.remove(Main.firstView);
                Main.curFrame.add(Main.label.first, BorderLayout.CENTER);
                Main.curFrame.add(Main.label.third, BorderLayout.SOUTH);
                Main.curFrame.pack();
                Main.curFrame.setVisible(true);

                Main.pictures = FileRead.getFilesCollection(l.getSelectedValue());
                try {
                    Main.curFrame.remove(Main.label.third);
                    Main.label = Image.ParseImageF(Main.label.first, Main.pictures.get(Main.i).toString(), Main.x, Main.y);
                    Main.curFrame.add(Main.label.third, BorderLayout.SOUTH);
                    Main.curFrame.pack();
                    Main.curFrame.setVisible(true);
                } catch (IOException ex) {
                    //System.out.println(ex.getStackTrace());
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(l);
        l.setLayoutOrientation(JList.VERTICAL);
        jd.add(scrollPane);
        jd.setSize(new Dimension(200, 40 + 20 * arr.length));
        jd.setPreferredSize(new Dimension(200, 40 + 20 * arr.length));
        jd.setLocationRelativeTo(Main.curFrame);
        jd.setVisible(true);
        jd.requestFocus();
    }

    private static void addToCollection() {
        JDialog jd = new JDialog(Main.curFrame);
        String[] arr = new String[1];
        arr[0] = "New collection";

        try {

            if((Boolean) Files.getAttribute(Path.of("src\\.ftcollections"), "dos:hidden")){
                Files.setAttribute(Path.of("src\\.ftcollections"), "dos:hidden", false);
            }

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
                if ((FileRead.imgColl.get(Image.curImage.getAbsolutePath()) != null && !FileRead.imgColl.get(Image.curImage.getAbsolutePath()).isEmpty())) {
                    if (FileRead.imgColl.get(Image.curImage.getAbsolutePath()).contains(s)) {
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

            if(!(Boolean) Files.getAttribute(Path.of("src\\.ftcollections"), "dos:hidden")){
                Files.setAttribute(Path.of("src\\.ftcollections"), "dos:hidden", true);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(" Collections file wasn't found");
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
        jd.setLocationRelativeTo(Main.curFrame);
        jd.setVisible(true);
        jd.requestFocus();
    }

    private static void writeCollection(String col) {

        boolean isRecord = FileRead.imgColl.containsKey(Image.curImage.getAbsolutePath());
        boolean isTag = false;
        if (isRecord) {
            isTag = FileRead.imgColl.get(Image.curImage.getAbsolutePath()).contains(col);
        }

        try {

            if((Boolean) Files.getAttribute(Path.of("src\\.ftcollections"), "dos:hidden")){
                Files.setAttribute(Path.of("src\\.ftcollections"), "dos:hidden", false);
            }

            if (!isTag && !isRecord) {
                BufferedWriter bw = new BufferedWriter(new FileWriter("src\\.ftcollections", true));
                if (!FileRead.imgColl.isEmpty()) {
                    bw.append('\n');
                }
                bw.append(Image.curImage.getAbsolutePath()).append(" Collections: ");
                bw.append(col);
                bw.append(' ');
                ArrayList<String> tags = new ArrayList<>();
                tags.add(col);
                FileRead.imgColl.put(Image.curImage.getAbsolutePath(), tags);
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

                    FileRead.imgColl.get(Image.curImage.getAbsolutePath()).add(col);
                }

                if(!(Boolean) Files.getAttribute(Path.of( "src\\.ftcollections"), "dos:hidden")){
                    Files.setAttribute(Path.of("src\\.ftcollections"), "dos:hidden", true);
                }

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void openNewCollectionDial() {
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
        dial.setLocationRelativeTo(Main.curFrame);
        dial.pack();
        dial.setVisible(true);
    }

    private static void viewTagDial() {
        JDialog jd = new JDialog(Main.curFrame);
        String[] arr = new String[1];
        arr[0] = "none";
        if (FileRead.imgTags.get(Image.curImage.getName()) != null && !FileRead.imgTags.get(Image.curImage.getName()).isEmpty()) {
            arr = new String[FileRead.imgTags.get(Image.curImage.getName()).size()];
            int i = 0;
            for (String tag : FileRead.imgTags.get(Image.curImage.getName())) {
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
        jd.setLocationRelativeTo(Main.curFrame);
        jd.setVisible(true);
        jd.requestFocus();
    }

    private static void removeTagDial() {
        JDialog jd = new JDialog(Main.curFrame);
        String[] arr = new String[1];
        arr[0] = "none";
        if (FileRead.imgTags.get(Image.curImage.getName()) != null) {
            arr = new String[FileRead.imgTags.get(Image.curImage.getName()).size()];
            int i = 0;
            for (String tag : FileRead.imgTags.get(Image.curImage.getName())) {
                arr[i] = tag;
                i++;
            }
        }
        JList<String> l = new JList<>(arr);

        l.addListSelectionListener(e1 -> {
            if (!l.getSelectedValue().equals("none")) {
                try {
                    if((Boolean) Files.getAttribute(Path.of(Main.curPath + "\\.fasttomedata"), "dos:hidden")){
                        Files.setAttribute(Path.of(Main.curPath + "\\.fasttomedata"), "dos:hidden", false);
                    }
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

                            FileRead.imgTags.get(Image.curImage.getName()).remove(tag);

                            if (!FileRead.imgTags.get(Image.curImage.getName()).isEmpty()) {
                                file.set(i, file.get(i).substring(0, pos - 1) + file.get(i).substring(pos + tag.length()));
                            } else {
                                file.remove(i);
                                FileRead.imgTags.remove(Image.curImage.getName());
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

                    if(!(Boolean) Files.getAttribute(Path.of(Main.curPath + "\\.fasttomedata"), "dos:hidden")){
                        Files.setAttribute(Path.of(Main.curPath + "\\.fasttomedata"), "dos:hidden", true);
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
        jd.setLocationRelativeTo(Main.curFrame);
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

    private static void openDirDialog() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.addActionListener(e -> {
            if (e.getActionCommand().equals("ApproveSelection")) {
                directory = jFileChooser.getSelectedFile().toString();
            }
        });

        jFileChooser.setSize(400, 400);

        JDialog dialog = new JDialog();

        dialog.setLocationRelativeTo(Main.curFrame);

        jFileChooser.showOpenDialog(dialog);

        jFileChooser.setVisible(true);
    }

    private static void openRenameDial() {
        JDialog dial = new JDialog(Main.curFrame);
        JLabel l = new JLabel("Type in new name (with .ext)");
        l.setHorizontalTextPosition(SwingConstants.CENTER);
        l.setVerticalTextPosition(SwingConstants.CENTER);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setVerticalAlignment(SwingConstants.CENTER);
        dial.add(l, BorderLayout.NORTH);

        JTextArea jt = new JTextArea();
        jt.setText(Image.curImage.getName());
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
        dial.setLocationRelativeTo(Main.curFrame);
        dial.pack();
        dial.setVisible(true);
    }

    private static void writeTagToFile() {
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

                    boolean isRecord = FileRead.imgTags.containsKey(Image.curImage.getName());
                    boolean isTag = false;
                    if (isRecord) {
                        isTag = FileRead.imgTags.get(Image.curImage.getName()).contains(tag);
                    }

                    try {

                        if((Boolean) Files.getAttribute(Path.of(Main.curPath + "\\.fasttomedata"), "dos:hidden")){
                            Files.setAttribute(Path.of(Main.curPath + "\\.fasttomedata"), "dos:hidden", false);
                        }

                        if (!isTag && !isRecord) {
                            BufferedWriter bw = new BufferedWriter(new FileWriter(Main.curPath + "\\.fasttomedata", true));
                            if (!FileRead.imgTags.isEmpty()) {
                                bw.append('\n');
                            }
                            bw.append(Image.curImage.getName()).append(" Tags: ");
                            bw.append(tag);
                            bw.append(' ');
                            ArrayList<String> tags = new ArrayList<>();
                            tags.add(tag);
                            FileRead.imgTags.put(Image.curImage.getName(), tags);
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

                                FileRead.imgTags.get(Image.curImage.getName()).add(tag);
                            }
                        }

                        if(!(Boolean) Files.getAttribute(Path.of(Main.curPath + "\\.fasttomedata"), "dos:hidden")){
                            Files.setAttribute(Path.of(Main.curPath + "\\.fasttomedata"), "dos:hidden", true);
                        }

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        dial.setSize(new Dimension(260, 80));
        dial.setPreferredSize(new Dimension(260, 80));
        dial.setLocationRelativeTo(Main.curFrame);
        dial.pack();
        dial.setVisible(true);
    }
}
