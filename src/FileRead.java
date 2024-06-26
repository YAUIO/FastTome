import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Map;

public class FileRead {
    static Map<String, ArrayList<String>> imgTags;
    static Map<String, ArrayList<String>> imgColl;
    static Map<String, String> imgDesc;
    static Map<String, FileTime> imgDate;

    public static ArrayList<File> getFiles(String path) {
        path += "\\";

        List<File> pictures = new ArrayList<>();
        File folder = new File(path);
        Pattern ext = Pattern.compile(".*\\.(png|jpg|gif|bmp)");
        if (folder.listFiles() != null) {
            Stream<File> files = Stream.of(folder.listFiles());
            pictures = files.filter(File::isFile)
                    .filter(f -> f.getName().endsWith(".jpg") || f.getName().endsWith(".png") || f.getName().endsWith(".jpeg"))
                    .collect(Collectors.toList());
        }

        File data = new File(path + ".fasttomedata");
        File collections = new File("src\\.ftcollections");
        File descriptions = new File(path + ".fasttomedesc");

        if (!descriptions.exists()) {
            try {
                descriptions.createNewFile();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(Main.curFrame, "Metadata file couldn't be created",
                        "Filesystem error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (!collections.exists()) {
            try {
                collections.createNewFile();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(Main.curFrame, "Metadata file couldn't be created",
                        "Filesystem error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (!data.exists()) {
            try {
                data.createNewFile();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(Main.curFrame, "Metadata file couldn't be created",
                        "Filesystem error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Triple<Map<String, ArrayList<String>>, Map<String, ArrayList<String>>, Map<String, String>> imgData = readData(path);
            imgTags = imgData.first;
            imgColl = imgData.second;
            imgDesc = imgData.third;

            imgDate = new HashMap<>();
            for (File f : pictures) {
                imgDate.put(f.getName(), getCreationDate(f));
            }

        }

        try {
            if (!(Boolean) Files.getAttribute(Path.of(data.getPath()), "dos:hidden")) {
                Files.setAttribute(Path.of(data.getPath()), "dos:hidden", true);
            }

            if (!(Boolean) Files.getAttribute(Path.of(collections.getPath()), "dos:hidden")) {
                Files.setAttribute(Path.of(collections.getPath()), "dos:hidden", true);
            }

            if (!(Boolean) Files.getAttribute(Path.of(descriptions.getPath()), "dos:hidden")) {
                Files.setAttribute(Path.of(descriptions.getPath()), "dos:hidden", true);
            }
        } catch (IOException ioex) {
            JOptionPane.showMessageDialog(Main.curFrame, "Metadata file couldn't be hidden",
                    "Filesystem error", JOptionPane.ERROR_MESSAGE);
        }

        return (ArrayList<File>) pictures;
    }

    public static FileTime getCreationDate(File file) {
        FileTime ft = FileTime.from(0, TimeUnit.MILLISECONDS);
        try {
            ft = (FileTime) Files.getAttribute(Path.of(file.getAbsolutePath()), "creationTime");
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(Main.curFrame, "Metadata file error",
                    "Filesystem error", JOptionPane.ERROR_MESSAGE);
        }
        return ft;
    }

    public static FileTime getCreationDate(String file) {
        FileTime ft = FileTime.from(0, TimeUnit.MILLISECONDS);
        try {
            ft = (FileTime) Files.getAttribute(Path.of(file), "creationTime");
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(Main.curFrame, exception.getMessage(),
                    "Filesystem error", JOptionPane.ERROR_MESSAGE);
        }
        return ft;
    }

    public static ArrayList<File> getFilesCollection(String collection) {

        ArrayList<File> pictures = new ArrayList<>();

        Set<Map.Entry<String, ArrayList<String>>> dbEntries = getCollections().entrySet();

        for (Map.Entry<String, ArrayList<String>> n : dbEntries) {
            if (n.getValue().contains(collection)) {
                pictures.add(new File(n.getKey()));
            }
        }

        ArrayList<Triple<Map<String, ArrayList<String>>, Map<String, ArrayList<String>>, Map<String, String>>> colData = new ArrayList<>();

        HashSet<String> dirs = new HashSet<>();

        for (File f : pictures) {
            dirs.add(f.getPath().substring(0, f.getPath().length() - f.getName().length()));
        }

        String[] arr = new String[dirs.size()];

        int i = 0;

        for (String s : dirs) {
            arr[i] = s;
            i++;
        }

        for (String path : arr) {
            colData.add(readData(path));
        }

        imgTags.clear();
        imgColl.clear();
        imgDesc.clear();
        imgDate.clear();

        for (Triple<Map<String, ArrayList<String>>, Map<String, ArrayList<String>>, Map<String, String>> p : colData) {
            for (String entry : p.first.keySet()) {
                for (File f : pictures) {
                    if (f.getName().equals(entry)) {
                        imgTags.put(entry, p.first.get(entry));
                    }
                }
            }

            for (String entry : p.second.keySet()) {
                if (pictures.contains(new File(entry))) {
                    imgColl.put(entry, p.second.get(entry));
                    imgDate.put(entry,getCreationDate(entry));
                }
            }

            for (String entry : p.third.keySet()) {
                for (File f : pictures) {
                    if (f.getName().equals(entry)) {
                        imgDesc.put(entry, p.third.get(entry));
                    }
                }
            }
        }
        return pictures;
    }

    private static Triple<Map<String, ArrayList<String>>, Map<String, ArrayList<String>>, Map<String, String>> readData(String path) {
        Map<String, ArrayList<String>> tags = new HashMap<>();
        Map<String, ArrayList<String>> collections = new HashMap<>();
        Map<String, String> descriptions = new HashMap<>();

        try {

            BufferedReader br = new BufferedReader(new FileReader(path + ".fasttomedata"));
            String buf;
            ArrayList<String> ab;
            int i = 0;
            int c = 0;
            while (br.ready()) {
                ab = new ArrayList<>();
                buf = br.readLine();
                i = buf.indexOf("Tags:") + 5;
                c = i;
                while (c != buf.length() - 1) {
                    i = c + 1;
                    c = buf.indexOf(" ", c + 1);
                    ab.add(buf.substring(i, c));
                }
                tags.put(buf.substring(0, buf.indexOf(" ")), ab);
            }
            br.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(Main.curFrame, e.getMessage(),
                    "Filesystem error", JOptionPane.ERROR_MESSAGE);
        }

        collections = getCollections();

        try {
            BufferedReader br = new BufferedReader(new FileReader(path + ".fasttomedesc"));
            String buf;
            ArrayList<String> ab;
            int i = 0;
            while (br.ready()) {
                buf = br.readLine();
                i = buf.indexOf("Description:") + 13;
                descriptions.put(buf.substring(0, buf.indexOf(" ")), buf.substring(i));
            }
            br.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(Main.curFrame, e.getMessage(),
                    "Filesystem error", JOptionPane.ERROR_MESSAGE);
        }

        return new Triple<>(tags, collections, descriptions);
    }

    public static HashMap<String, ArrayList<String>> getCollections() {
        HashMap<String, ArrayList<String>> collections = new HashMap<>();
        try {
            if ((Boolean) Files.getAttribute(Path.of("src\\.ftcollections"), "dos:hidden")) {
                Files.setAttribute(Path.of("src\\.ftcollections"), "dos:hidden", false);
            }
            BufferedReader bc = new BufferedReader(new FileReader("src\\.ftcollections"));
            String buf;
            ArrayList<String> ab;
            int i, c;
            while (bc.ready()) {
                ab = new ArrayList<>();
                buf = bc.readLine();
                i = buf.indexOf("Collections:") + 12;
                c = i;
                while (c != buf.length() - 1) {
                    i = c + 1;
                    c = buf.indexOf(" ", c + 1);
                    ab.add(buf.substring(i, c));
                }
                collections.put(buf.substring(0, buf.indexOf(" ")), ab);
            }
            bc.close();
            if (!(Boolean) Files.getAttribute(Path.of("src\\.ftcollections"), "dos:hidden")) {
                Files.setAttribute(Path.of("src\\.ftcollections"), "dos:hidden", true);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(Main.curFrame, e.getMessage(),
                    "Filesystem error", JOptionPane.ERROR_MESSAGE);
        }
        return collections;
    }

    public static Pair<Boolean, File> rename(File a) {

        String nameBuf = Menu.rename;
        Menu.rename = "none";

        if (!a.exists()) {
            return new Pair<>(false, a);
        }

        File r = new File(a.getPath().substring(0, a.getPath().length() - a.getName().length()) + nameBuf);

        if (r.exists()) {
            return new Pair<>(false, a);
        }

        if (imgTags.containsKey(a.getName())) {

            try {

                if ((Boolean) Files.getAttribute(Path.of(Main.curPath + "\\.fasttomedata"), "dos:hidden")) {
                    Files.setAttribute(Path.of(Main.curPath + "\\.fasttomedata"), "dos:hidden", false);
                }

                BufferedReader br = new BufferedReader(new FileReader(Main.curPath + "\\.fasttomedata"));
                ArrayList<String> file = new ArrayList<>();
                int i = -1;
                while (br.ready()) {
                    file.add(br.readLine());
                    if (file.get(file.size() - 1).startsWith(a.getName())) {
                        i = file.size() - 1;
                    }
                }
                br.close();
                if (i != -1) {
                    file.set(i, r.getName() + file.get(i).substring(file.get(i).indexOf(' ')));

                    BufferedWriter bw = new BufferedWriter(new FileWriter(Main.curPath + "\\.fasttomedata"));

                    bw.write("");

                    for (String s : file) {
                        if (!s.isEmpty() && !s.equals(" ")) {
                            bw.append(s);
                            bw.append('\n');
                        }
                    }

                    bw.close();
                }

                if (!(Boolean) Files.getAttribute(Path.of(Main.curPath + "\\.fasttomedata"), "dos:hidden")) {
                    Files.setAttribute(Path.of(Main.curPath + "\\.fasttomedata"), "dos:hidden", true);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(Main.curFrame, ex.getMessage(),
                        "Filesystem error", JOptionPane.ERROR_MESSAGE);
            }


            imgTags.put(r.getName(), imgTags.get(a.getName()));
            imgTags.remove(a.getName());
        }

        if (imgColl.containsKey(a.getAbsolutePath())) {

            try {
                if ((Boolean) Files.getAttribute(Path.of("src\\.ftcollections"), "dos:hidden")) {
                    Files.setAttribute(Path.of("src\\.ftcollections"), "dos:hidden", false);
                }

                BufferedReader br = new BufferedReader(new FileReader("src\\.ftcollections"));
                ArrayList<String> file = new ArrayList<>();
                int i = -1;
                while (br.ready()) {
                    file.add(br.readLine());
                    if (file.get(file.size() - 1).startsWith(a.getAbsolutePath())) {
                        i = file.size() - 1;
                    }
                }
                br.close();
                if (i != -1) {
                    file.set(i, r.getAbsolutePath() + file.get(i).substring(file.get(i).indexOf(' ')));

                    BufferedWriter bw = new BufferedWriter(new FileWriter("src\\.ftcollections"));

                    bw.write("");

                    for (String s : file) {
                        if (!s.isEmpty() && !s.equals(" ")) {
                            bw.append(s);
                            bw.append('\n');
                        }
                    }

                    bw.close();
                }

                if (!(Boolean) Files.getAttribute(Path.of("src\\.ftcollections"), "dos:hidden")) {
                    Files.setAttribute(Path.of("src\\.ftcollections"), "dos:hidden", true);
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(Main.curFrame, ex.getMessage(),
                        "Filesystem error", JOptionPane.ERROR_MESSAGE);
            }


            imgColl.put(r.getAbsolutePath(), imgColl.get(a.getAbsolutePath()));
            imgColl.remove(a.getAbsolutePath());
        }

        if (imgDesc.containsKey(a.getName())) {

            try {

                if ((Boolean) Files.getAttribute(Path.of(Main.curPath + "\\.fasttomedesc"), "dos:hidden")) {
                    Files.setAttribute(Path.of(Main.curPath + "\\.fasttomedesc"), "dos:hidden", false);
                }

                BufferedReader br = new BufferedReader(new FileReader(Main.curPath + "\\.fasttomedesc"));
                ArrayList<String> file = new ArrayList<>();
                int i = -1;
                while (br.ready()) {
                    file.add(br.readLine());
                    if (file.get(file.size() - 1).startsWith(a.getName())) {
                        i = file.size() - 1;
                    }
                }
                br.close();
                if (i != -1) {
                    file.set(i, r.getName() + file.get(i).substring(file.get(i).indexOf(' ')));

                    BufferedWriter bw = new BufferedWriter(new FileWriter(Main.curPath + "\\.fasttomedesc"));

                    bw.write("");

                    for (String s : file) {
                        if (!s.isEmpty() && !s.equals(" ")) {
                            bw.append(s);
                            bw.append('\n');
                        }
                    }

                    bw.close();
                }

                if (!(Boolean) Files.getAttribute(Path.of(Main.curPath + "\\.fasttomedesc"), "dos:hidden")) {
                    Files.setAttribute(Path.of(Main.curPath + "\\.fasttomedesc"), "dos:hidden", true);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(Main.curFrame, ex.getMessage(),
                        "Filesystem error", JOptionPane.ERROR_MESSAGE);
            }


            imgDesc.put(r.getName(), imgDesc.get(a.getName()));
            imgDesc.remove(a.getName());
        }

        if(imgDate.containsKey(a.getName())){
            imgDate.put(r.getName(),imgDate.get(a.getName()));
            imgDate.remove(a.getName());
        }

        return new Pair<>(a.renameTo(r), r);
    }
}
