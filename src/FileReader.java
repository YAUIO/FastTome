import java.io.File;
import java.util.ArrayList;

public class FileReader {
    public static ArrayList<String> getFiles(String path) {
        ArrayList<String> fileNames = new ArrayList<String>();
        File folder = new File(path);
        File[] files = folder.listFiles();
        if(files != null) {
            for (File f : files) {
                if (f.isFile()) {
                    if (f.getName().endsWith(".jpg") ||
                            f.getName().endsWith(".png") ||
                            f.getName().endsWith(".jpeg")) {
                        fileNames.add(f.getName());
                    }
                }
            }
        }
        return fileNames;
    }
}
