import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileReader {
    public static List<File> getFiles(String path) {
        path += "\\";

        File data = new File(path + ".fasttomedata");
        if(!data.exists()){
            try {
                data.createNewFile();
            }catch(IOException e){
                System.out.println(e.getStackTrace() + " Metadata file wasn't created");
            }
        }

        List<File> pictures = new ArrayList<>();
        File folder = new File(path);
        if(folder.listFiles() != null) {
            Stream<File> files = Stream.of(folder.listFiles());
            pictures = files.filter(File::isFile)
                    .filter(f -> f.getName().endsWith(".jpg") || f.getName().endsWith(".png") || f.getName().endsWith(".jpeg"))
                    .collect(Collectors.toList());
        }

        return pictures;
    }
}
