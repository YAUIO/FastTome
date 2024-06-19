import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

public class Image {
    private static BufferedImage origImage;
    static File curImage;

    public static Triple<JLabel, BufferedImage, JLabel> ParseImageF(JLabel lbl, String image, int x, int y){
        File imageF = new File(image);

        try {
            BufferedImage img = ImageIO.read(imageF);
        int newWidth = img.getWidth();
        int newHeight = img.getHeight();
        double multiplier;
        int bx = x;
        int by = y-100;
        if (img.getWidth() > bx || img.getHeight() > by) {
            if (bx / img.getWidth() < by / img.getHeight()) {
                multiplier = (double) bx / img.getWidth();
            } else {
                multiplier = (double) by / img.getHeight();
            }
            multiplier -= 0.04;
            newWidth = (int) (newWidth * multiplier);
            newHeight = (int) (newHeight * multiplier);
        } else if (img.getWidth() < bx || img.getHeight() < by) {
            if (bx / img.getWidth() < by / img.getHeight()) {
                multiplier = (double) bx / img.getWidth();
            } else {
                multiplier = (double) by / img.getHeight();
            }
            multiplier -= 0.1;
            newWidth = (int) (newWidth * multiplier);
            newHeight = (int) (newHeight * multiplier);
        }

        img = rescaleImg(img,newWidth,newHeight);
        curImage = imageF;
        origImage = img;
        ImageIcon icon = new ImageIcon(img);
        lbl.setIcon(icon);
        JLabel text = new JLabel(imageF.getName());
        text.setHorizontalTextPosition(SwingConstants.CENTER);
        text.setVerticalTextPosition(SwingConstants.CENTER);
        text.setHorizontalAlignment(SwingConstants.CENTER);
        text.setVerticalAlignment(SwingConstants.CENTER);
        lbl.setSize(newWidth, newHeight);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        return new Triple<>(lbl, img, text);
        }catch (IOException ex){
            JOptionPane.showMessageDialog(Main.curFrame, "Exception while reading a photo",
                    "I/O Error", JOptionPane.ERROR_MESSAGE);
        }

        JLabel text = new JLabel(image);
        text.setHorizontalTextPosition(SwingConstants.CENTER);
        text.setVerticalTextPosition(SwingConstants.CENTER);
        text.setHorizontalAlignment(SwingConstants.CENTER);
        text.setVerticalAlignment(SwingConstants.CENTER);
        return new Triple<>(lbl,new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB),text);
    }

    private static BufferedImage rescaleImg(BufferedImage img, int newWidth, int newHeight){
        BufferedImage out = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D buf = out.createGraphics();
        buf.drawImage(img, 0, 0, newWidth, newHeight, null);
        buf.dispose();
        return out;
    }

    public static Triple<JLabel, BufferedImage, JLabel> reScale(String mode, Triple<JLabel, BufferedImage, JLabel> img, int scale) {
        int newWidth = img.second.getWidth();
        int newHeight = img.second.getHeight();
        double multiplier = 1.2*scale;
        if (mode.equals("UP")) {
            newWidth = (int) (multiplier * newWidth);
            newHeight = (int) (multiplier * newHeight);
        } else if (mode.equals("DOWN")) {
            newWidth = (int) (newWidth / multiplier);
            newHeight = (int) (newHeight / multiplier);
        }
        img.second = rescaleImg(origImage, newWidth, newHeight);
        ImageIcon icon = new ImageIcon(img.second);
        img.first.setIcon(icon);
        img.first.setSize(newWidth, newHeight);
        return img;
    }
}
