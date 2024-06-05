import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Map;

public class FileReader {
    static ArrayList<ArrayList<String>> tags;
    public static List<File> getFiles(String path) {
        path += "\\";

        List<File> pictures = new ArrayList<>();
        File folder = new File(path);
        if(folder.listFiles() != null) {
            Stream<File> files = Stream.of(folder.listFiles());
            pictures = files.filter(File::isFile)
                    .filter(f -> f.getName().endsWith(".jpg") || f.getName().endsWith(".png") || f.getName().endsWith(".jpeg"))
                    .collect(Collectors.toList());
        }

        File data = new File(path + ".fasttomedata");
        if(!data.exists()){
            try {
                data.createNewFile();
            }catch(IOException e){
                //System.out.println(e.getStackTrace() + " Metadata file wasn't created");
            }
        }else{
            readData(pictures);
        }

        return pictures;
    }

    private static Pair<Map<String,ArrayList<String>>,Map<String,ArrayList<String>>> readData(List<File> pictures) {
        Map<String, ArrayList<String>> tags = new HashMap<>();
        Map<String, ArrayList<String>> collections = new HashMap<>();
        Pair<Map<String,ArrayList<String>>,Map<String,ArrayList<String>>> p = new Pair<>(tags,collections);


        return p;
    }

    public static Pair<Boolean,File> rename(File a){

        String nameBuf = Menu.rename;
        Menu.rename = "none";

        if(!a.exists()){
            return new Pair<>(false,a);
        }

        File r = new File(a.getPath().substring(0,a.getPath().length()-a.getName().length()) + nameBuf);

        if(r.exists()){
            return new Pair<>(false,a);
        }

        return new Pair<>(a.renameTo(r),r);
    }
}
