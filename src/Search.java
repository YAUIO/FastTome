import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public class Search {
    private static Pair<String[], int[]> count;
    private static char mode;

    public static void filterDialogue() {
        JDialog dial = new JDialog(Main.curFrame);
        JLabel l = new JLabel("Shift+Enter new line, AND is &, OR is |");
        l.setHorizontalTextPosition(SwingConstants.CENTER);
        l.setVerticalTextPosition(SwingConstants.CENTER);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setVerticalAlignment(SwingConstants.CENTER);
        dial.add(l, BorderLayout.NORTH);

        JButton openHelp = new JButton("Help");

        openHelp.addActionListener(e -> {
            JDialog jd = new JDialog(dial);
            JTextArea jt = new JTextArea("Supported:\n tag <string> \n extension <string> \n name <string> \n date < | > | = <yyyy-mm-dd/yyyy-mm/yyyy> \n description <string> \n description \"<string> <string>\" \n All the predicated are either ORed or ANDed \n Spaces between are obligatory");
            jt.setEditable(false);
            jd.add(jt, BorderLayout.CENTER);
            jd.setSize(new Dimension(260, 200));
            jd.setPreferredSize(new Dimension(260, 200));
            jd.pack();
            jd.setLocationRelativeTo(dial);
            jd.setVisible(true);
        });

        dial.add(openHelp, BorderLayout.SOUTH);

        JTextArea jt = new JTextArea();
        jt.setPreferredSize(new Dimension(260, 80));
        jt.setEditable(true);
        dial.add(jt, BorderLayout.CENTER);

        final Thread searchThread = new Thread(() -> {
            String filter = jt.getText();
            if (!filter.endsWith(" ")) {
                filter = filter + " ";
            }
            filter = filter.replace('\n', ' ');
            jt.setText("");
            dial.dispose();
            search(filter);
        });

        jt.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getModifiersEx() == InputEvent.SHIFT_DOWN_MASK && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    jt.setText(jt.getText() + "\n");
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchThread.start();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        dial.setSize(new Dimension(260, 120));
        dial.setPreferredSize(new Dimension(260, 120));
        dial.pack();
        dial.setLocationRelativeTo(Main.curFrame);
        dial.setVisible(true);
    }

    private static int[] toInt(String date) {
        int[] arr = new int[3];
        int i = 0;
        int a = 0;
        int oldVal = 0;

        while (i < 3) {
            a = date.indexOf('-', oldVal + 1);

            if (a == -1) {
                a = date.length();
            }

            if (oldVal != a) {
                if (i == 0) {
                    arr[i] = Integer.parseInt(date.substring(0, a));
                } else {
                    arr[i] = Integer.parseInt(date.substring(oldVal + 1, a));
                }
            } else {
                arr[i] = 0;
            }

            oldVal = a;
            i++;
        }

        return arr;
    }

    private static boolean compareDate(String a, String b, char sign) {
        int[] ai = toInt(a);
        int[] bi = toInt(b);

        int i = 0;

        while (i < ai.length - 1 && ai[i] == bi[i]) {
            i++;
            if (bi[i] == 0) {
                i--;
                break;
            }
        }

        if (sign == 60) {
            return ai[i] < bi[i];
        } else if (sign == 61) {
            return ai[i] == bi[i];
        } else if (sign == 62) {
            return ai[i] > bi[i];
        }

        return false;
    }

    private static void search(String filter) {
        ArrayList<Predicate<File>> conditions = new ArrayList<>();

        ArrayList<File> buf = new ArrayList<>();

        String[] filters = {"date", "tag", "name", "extension", "collection", "description"};

        count = new Pair<>(filters, new int[filters.length]);

        int i = 0;
        int ii;

        if (filter.contains("|")) {
            mode = '|';
        } else {
            mode = '&';
        }

        while (i < count.first.length) {
            ii = 0;
            count.second[i] = 0;
            while (true) {
                ii = filter.indexOf(count.first[i], ii);
                if (ii != -1) {
                    count.second[i]++;
                    ii++;
                } else {
                    break;
                }
            }
            i++;
        }

        if (filter.contains("date")) {
            conditions.add(
                    f -> {
                        if (FileRead.imgDate.containsKey(f.getName()) && FileRead.imgDate.get(f.getName()) != null) {
                            String date = FileRead.imgDate.get(f.getName()).toString();
                            date = date.substring(0, date.indexOf('T'));
                            ArrayList<String> rhs = new ArrayList<>();
                            ArrayList<Character> sign = new ArrayList<>();
                            int c = 0;
                            int o = 0;
                            int os = 0;
                            while (c < count.second[0]) {
                                o = filter.indexOf("date", o) + 7;
                                rhs.add(filter.substring(o, filter.indexOf(' ', o + 1)));

                                os = filter.indexOf("date", os) + 5;
                                sign.add(filter.charAt(os));

                                o++;
                                os += 5;

                                c++;
                            }

                            c = 1;
                            boolean r = compareDate(date, rhs.getFirst(), sign.getFirst());

                            while (c < rhs.size()) {
                                if (mode == '|') {
                                    r = r || compareDate(date, rhs.get(c), sign.get(c));
                                } else if (mode == '&') {
                                    r = r && compareDate(date, rhs.get(c), sign.get(c));
                                }
                                c++;
                            }
                            return r;
                        }
                        return false;
                    }
            );
        }

        if (filter.contains("tag")) {
            conditions.add(
                    f -> {
                        if (!FileRead.imgTags.containsKey(f.getName()) || FileRead.imgTags.get(f.getName()).isEmpty()) {
                            return false;
                        }

                        int c = 0;
                        int o = 0;
                        ArrayList<String> bufsubstr = new ArrayList<>();
                        while (c < count.second[1]) {
                            o = filter.indexOf("tag", o) + 4;
                            bufsubstr.add(filter.substring(o, filter.indexOf(' ', o)));
                            c++;
                            o++;
                        }

                        c = 1;
                        boolean r = FileRead.imgTags.get(f.getName()).contains(bufsubstr.getFirst());
                        while (c < bufsubstr.size()) {
                            if (mode == '|') {
                                r = r || FileRead.imgTags.get(f.getName()).contains(bufsubstr.get(c));
                            } else if (mode == '&') {
                                r = r && FileRead.imgTags.get(f.getName()).contains(bufsubstr.get(c));
                            }
                            c++;
                        }

                        return r;
                    }
            );
        }

        if (filter.contains("name")) {
            conditions.add(
                    f -> {
                        int c = 0;
                        int o = 0;
                        ArrayList<String> bufsubstr = new ArrayList<>();
                        while (c < count.second[2]) {
                            o = filter.indexOf("name", o) + 5;
                            bufsubstr.add(filter.substring(o, filter.indexOf(' ', o)));
                            c++;
                            o++;
                        }

                        c = 1;
                        boolean r = f.getName().substring(0, f.getName().indexOf('.')).contains(bufsubstr.getFirst());
                        while (c < bufsubstr.size()) {
                            if (mode == '|') {
                                r = r || f.getName().substring(0, f.getName().indexOf('.')).contains(bufsubstr.get(c));
                            } else if (mode == '&') {
                                r = r && f.getName().substring(0, f.getName().indexOf('.')).contains(bufsubstr.get(c));
                            }
                            c++;
                        }

                        return r;
                    }
            );
        }

        if (filter.contains("extension")) {
            conditions.add(
                    f -> {
                        int c = 0;
                        int o = 0;
                        ArrayList<String> bufsubstr = new ArrayList<>();
                        while (c < count.second[3]) {
                            o = filter.indexOf("extension", o) + 10;
                            bufsubstr.add(filter.substring(o, filter.indexOf(' ', o)));
                            c++;
                            o++;
                        }

                        c = 1;
                        boolean r = f.getName().contains(bufsubstr.getFirst());
                        while (c < bufsubstr.size()) {
                            if (mode == '|') {
                                r = r || f.getName().contains(bufsubstr.get(c));
                            } else if (mode == '&') {
                                r = r && f.getName().contains(bufsubstr.get(c));
                            }
                            c++;
                        }

                        return r;
                    }
            );
        }

        if (filter.contains("collection")) {
            conditions.add(
                    f -> {
                        if (!FileRead.imgColl.containsKey(f.getAbsolutePath()) || FileRead.imgColl.get(f.getAbsolutePath()).isEmpty()) {
                            return false;
                        }

                        int c = 0;
                        int o = 0;
                        ArrayList<String> bufsubstr = new ArrayList<>();
                        while (c < count.second[4]) {
                            o = filter.indexOf("collection", o) + 11;
                            bufsubstr.add(filter.substring(o, filter.indexOf(' ', o)));
                            c++;
                            o++;
                        }

                        c = 1;
                        boolean r = FileRead.imgColl.get(f.getAbsolutePath()).contains(bufsubstr.getFirst());
                        while (c < bufsubstr.size()) {
                            if (mode == '|') {
                                r = r || FileRead.imgColl.get(f.getAbsolutePath()).contains(bufsubstr.get(c));
                            } else if (mode == '&') {
                                r = r && FileRead.imgColl.get(f.getAbsolutePath()).contains(bufsubstr.get(c));
                            }
                            c++;
                        }

                        return r;
                    }
            );

        }

        if (filter.contains("description")) {
            conditions.add(
                    f -> {
                        if (!FileRead.imgDesc.containsKey(f.getName()) || FileRead.imgDesc.get(f.getName()).isEmpty()) {
                            return false;
                        }

                        int c = 0;
                        int o = 0;
                        ArrayList<String> bufsubstr = new ArrayList<>();
                        while (c < count.second[5]) {
                            o = filter.indexOf("description", o) + 12;

                            if(filter.indexOf('\"', o)!=-1) {
                                o++;
                                bufsubstr.add(filter.substring(o, filter.indexOf('\"', o+2)));
                            }else{
                                bufsubstr.add(filter.substring(o, filter.indexOf(' ', o)));
                            }

                            bufsubstr.set(bufsubstr.size()-1,bufsubstr.getLast().replace('\"',' '));
                            c++;
                            o++;
                        }

                        c = 1;
                        boolean r = FileRead.imgDesc.get(f.getName()).contains(bufsubstr.getFirst());
                        while (c < bufsubstr.size()) {
                            if (mode == '|') {
                                r = r || FileRead.imgDesc.get(f.getName()).contains(bufsubstr.get(c));
                            } else if (mode == '&') {
                                r = r && FileRead.imgDesc.get(f.getName()).contains(bufsubstr.get(c));
                            }
                            c++;
                        }

                        return r;
                    }
            );
        }

        HashSet<File> filteredPhotos = new HashSet<>();

        if (mode == '&') {
            filteredPhotos = new HashSet<>(Main.pictures);
            for (Predicate<File> pred : conditions) {

                buf.addAll(filteredPhotos);

                filteredPhotos.clear();

                filteredPhotos.addAll(buf.stream()
                        .filter(pred)
                        .toList());

                buf.clear();

            }
        } else {
            for (Predicate<File> pred : conditions) {

                filteredPhotos.addAll(Main.pictures.stream()
                        .filter(pred)
                        .toList());

            }
        }

        if (!filteredPhotos.isEmpty()) {

            Main.pictures.clear();

            List<File> pics;

            pics = filteredPhotos.stream()
                    .sorted(Comparator.comparingInt(a -> a.getName().charAt(0) * 10 + a.getName().charAt(1)))
                    .toList();

            Main.pictures.addAll(pics);

            Menu.view = 0;
            Main.i = 0;
            Menu.view0.setState(true);
            Menu.view1.setState(false);
            Main.curFrame.remove(Main.firstView);
            Main.curFrame.add(Main.label.first, BorderLayout.CENTER);
            Main.curFrame.add(Main.label.third, BorderLayout.SOUTH);
            Main.curFrame.pack();
            Main.curFrame.setVisible(true);

            Main.curFrame.remove(Main.label.third);
            if (Main.pictures.size() > Main.i) {
                Main.label = Image.ParseImageF(Main.label.first, Main.pictures.get(Main.i).toString(), Main.x, Main.y);
            } else {
                Main.label.second = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
            Main.curFrame.add(Main.label.third, BorderLayout.SOUTH);
            Main.curFrame.pack();
            Main.curFrame.setVisible(true);
            JOptionPane.showMessageDialog(Main.curFrame, "Search completed, " + Main.pictures.size() + " photos found",
                    "Information", JOptionPane.INFORMATION_MESSAGE);

        } else if (conditions.isEmpty()) {
            JOptionPane.showMessageDialog(Main.curFrame, "Wrong input",
                    "Search error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(Main.curFrame, "No such photos in current scope",
                    "Search error", JOptionPane.ERROR_MESSAGE);
        }


    }
}
