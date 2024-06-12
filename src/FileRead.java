import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Map;

public class FileRead {
    static Pair<Map<String,ArrayList<String>>,Map<String,ArrayList<String>>> imgData;
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
        File collections = new File("src\\.ftcollections");

        if(!collections.exists()){
            try {
                collections.createNewFile();
            }catch(IOException e){
                //System.out.println(e.getStackTrace() + " Metadata file wasn't created");
            }
        }

        if(!data.exists()){
            try {
                data.createNewFile();
            }catch(IOException e){
                //System.out.println(e.getStackTrace() + " Metadata file wasn't created");
            }
        }else{
            imgData = readData(pictures, path);
        }

        return pictures;
    }

    private static Pair<Map<String,ArrayList<String>>,Map<String,ArrayList<String>>> readData(List<File> pictures, String path) {
        Map<String, ArrayList<String>> tags = new HashMap<>();
        Map<String, ArrayList<String>> collections = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(path + ".fasttomedata"));
            String buf;
            ArrayList<String> ab;
            int i = 0;
            int c = 0;
            while (br.ready()){
                ab = new ArrayList<>();
                buf = br.readLine();
                i = buf.indexOf("Tags:") + 5;
                c = i;
                while (c != buf.length()-1){
                    i = c+1;
                    c = buf.indexOf(" ",c+1);
                    ab.add(buf.substring(i,c));
                }
                tags.put(buf.substring(0,buf.indexOf(" ")),ab);
            }
            br.close();
        }catch (IOException e){
            System.out.println(e.getStackTrace() + " Metadata file wasn't found");
        }

        try {
            BufferedReader bc = new BufferedReader(new FileReader("src\\.ftcollections"));
            String buf;
            ArrayList<String> ab;
            int i,c;
            while (bc.ready()){
                ab = new ArrayList<>();
                buf = bc.readLine();
                i = buf.indexOf("Collections:") + 5;
                c = i;
                while (c != buf.length()-1){
                    i = c+1;
                    c = buf.indexOf(" ",c+1);
                    ab.add(buf.substring(i,c));
                }
                tags.put(buf.substring(0,buf.indexOf(" ")),ab);
            }
            bc.close();
        }catch (IOException e){
            System.out.println(e.getStackTrace() + " Collections file wasn't found");
        }

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
