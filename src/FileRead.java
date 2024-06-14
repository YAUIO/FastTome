import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Map;

public class FileRead {
    static Pair<Map<String, ArrayList<String>>, Map<String, ArrayList<String>>> imgData;

    public static List<File> getFiles(String path) {
        path += "\\";

        List<File> pictures = new ArrayList<>();
        File folder = new File(path);
        if (folder.listFiles() != null) {
            Stream<File> files = Stream.of(folder.listFiles());
            pictures = files.filter(File::isFile)
                    .filter(f -> f.getName().endsWith(".jpg") || f.getName().endsWith(".png") || f.getName().endsWith(".jpeg"))
                    .collect(Collectors.toList());
        }

        File data = new File(path + ".fasttomedata");
        File collections = new File("src\\.ftcollections");

        if (!collections.exists()) {
            try {
                collections.createNewFile();
            } catch (IOException e) {
                //System.out.println(e.getStackTrace() + " Metadata file wasn't created");
            }
        }

        if (!data.exists()) {
            try {
                data.createNewFile();
            } catch (IOException e) {
                //System.out.println(e.getStackTrace() + " Metadata file wasn't created");
            }
        } else {
            imgData = readData(path);
        }

        return pictures;
    }

    private static Pair<Map<String, ArrayList<String>>, Map<String, ArrayList<String>>> readData(String path) {
        Map<String, ArrayList<String>> tags = new HashMap<>();
        Map<String, ArrayList<String>> collections = new HashMap<>();

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
            System.out.println(e.getStackTrace() + " Metadata file wasn't found");
        }

        try {
            BufferedReader bc = new BufferedReader(new FileReader("src\\.ftcollections"));
            String buf;
            ArrayList<String> ab;
            int i, c;
            while (bc.ready()) {
                ab = new ArrayList<>();
                buf = bc.readLine();
                i = buf.indexOf("Collections:") + 5;
                c = i;
                while (c != buf.length() - 1) {
                    i = c + 1;
                    c = buf.indexOf(" ", c + 1);
                    ab.add(buf.substring(i, c));
                }
                collections.put(buf.substring(0, buf.indexOf(" ")), ab);
            }
            bc.close();
        } catch (IOException e) {
            System.out.println(e.getStackTrace() + " Collections file wasn't found");
        }

        return new Pair<>(tags, collections);
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

        if (imgData.first.containsKey(a.getName())) {

            try {
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
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }


            imgData.first.put(r.getName(), imgData.first.get(a.getName()));
            imgData.first.remove(a.getName());
        }

        if (imgData.second.containsKey(a.getName())) {

            try {
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
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }


            imgData.second.put(r.getAbsolutePath(), imgData.second.get(a.getAbsolutePath()));
            imgData.second.remove(a.getAbsolutePath());
        }


        return new Pair<>(a.renameTo(r), r);
    }
}
