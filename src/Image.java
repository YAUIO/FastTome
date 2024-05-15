import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

public class Image {
    public static JLabel ParseImageF(JLabel lbl, String path, String image, int x, int y) throws IOException
    {
        path = path + "\\" + image;
        BufferedImage img= ImageIO.read(new File(path));
        int newWidth = img.getWidth();
        int newHeight = img.getHeight();
        double multiplier = 1;
        if(img.getWidth()>x || img.getHeight()>y){
            if(x/img.getWidth()<y/img.getHeight()){
                multiplier = (double) x /img.getWidth();
            }else{
                multiplier = (double) y /img.getHeight();
            }
            multiplier-=0.04;
            newWidth = (int)(newWidth * multiplier);
            newHeight = (int)(newHeight * multiplier);
        }

        img = org.imgscalr.Scalr.resize(img, org.imgscalr.Scalr.Method.BALANCED, newWidth, newHeight);
        ImageIcon icon=new ImageIcon(img);
        lbl.setIcon(icon);
        lbl.setSize(newWidth,newHeight);
        return lbl;
    }

    public static JLabel reScale(String mode, JLabel label){
        if(mode.equals("UP")){

        }
        return label;
    }
}
