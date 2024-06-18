import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Search {
    public static void filterDialogue() {
        JDialog dial = new JDialog(Main.curFrame);
        JLabel l = new JLabel("AND is &, OR is |");
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
                    String filter = jt.getText();
                    filter += " ";
                    jt.setText("");
                    search(filter);
                    dial.dispose();
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
            if (bi[i]==0){
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

        if (filter.contains("date")) {
            conditions.add(
                    f -> {
                        if (FileRead.imgDate.containsKey(f.getName())) {
                            if (FileRead.imgDate.get(f.getName()) != null) {
                                String date = FileRead.imgDate.get(f.getName()).toString();
                                date = date.substring(0, date.indexOf('T'));
                                String rhs = filter.substring(filter.indexOf("date") + 7, filter.indexOf(' ', filter.indexOf("date") + 8));
                                char sign = filter.charAt(filter.indexOf("date") + 5);
                                return compareDate(date, rhs, sign);
                            }
                        }
                        return false;
                    }
            );
        }

        if (filter.contains("tag")) {
            conditions.add(
                    f -> {
                        if (FileRead.imgTags.containsKey(f.getName())) {
                            if (!FileRead.imgTags.get(f.getName()).isEmpty()) {
                                ArrayList<String> tags = FileRead.imgTags.get(f.getName());
                                return tags.contains(filter.substring(filter.indexOf("tag") + 4, filter.indexOf(' ', filter.indexOf("tag") + 4)));
                            }
                        }
                        return false;
                    }
            );
        }

        if (filter.contains("collection")) {
            conditions.add(
                    f -> {
                        if (FileRead.imgColl.containsKey(f.getAbsolutePath())) {
                            if (!FileRead.imgColl.get(f.getAbsolutePath()).isEmpty()) {
                                ArrayList<String> collections = FileRead.imgColl.get(f.getAbsolutePath());
                                return collections.contains(filter.substring(filter.indexOf("collection") + 10, filter.indexOf(' ', filter.indexOf("collection") + 10)));
                            }
                        }
                        return false;
                    }
            );
        }

        if (filter.contains("description")) {
            conditions.add(
                    f -> {
                        if (FileRead.imgDesc.containsKey(f.getName())) {
                            if (!FileRead.imgDesc.get(f.getName()).isEmpty()) {
                                String description = FileRead.imgDesc.get(f.getName());
                                return description.contains(filter.substring(filter.indexOf("description") + 12, filter.indexOf(' ', filter.indexOf("description") + 12)));
                            }
                        }
                        return false;
                    }
            );
        }

        String mode = "and";

        if (filter.contains("|")){
            mode = "or";
        }

        HashSet<File> filteredPhotos = new HashSet<>(Main.pictures);

        if(mode.equals("and")) {
            for (Predicate<File> pred : conditions) {

                buf.addAll(filteredPhotos);

                filteredPhotos.clear();

                filteredPhotos.addAll(buf.stream()
                        .filter(pred)
                        .toList());

                System.out.println(filteredPhotos);

                buf.clear();

            }
        }else{
            for (Predicate<File> pred : conditions) {

                filteredPhotos.addAll(Main.pictures.stream()
                        .filter(pred)
                        .toList());

            }
        }

        if(!filteredPhotos.isEmpty()) {

            Main.pictures.clear();

            List<File> pics;

            pics = filteredPhotos.stream()
                    .sorted(Comparator.comparingInt(a -> a.getName().charAt(0)*10+a.getName().charAt(1)))
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

            try {
                Main.curFrame.remove(Main.label.third);
                if (Main.pictures.size() > Main.i) {
                    Main.label = Image.ParseImageF(Main.label.first, Main.pictures.get(Main.i).toString(), Main.x, Main.y);
                } else {
                    Main.label.second = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                }
                Main.curFrame.add(Main.label.third, BorderLayout.SOUTH);
                Main.curFrame.pack();
                Main.curFrame.setVisible(true);
            } catch (IOException ex) {
                //System.out.println(ex.getStackTrace());
            }

        }else{
            JOptionPane.showMessageDialog(Main.curFrame, "No such photos in current scope",
                    "Search error", JOptionPane.ERROR_MESSAGE);
        }

    }
}
