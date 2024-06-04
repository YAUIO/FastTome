import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;

public class Image {
    private static BufferedImage origImage;

    public static Pair<JLabel, BufferedImage> ParseImageF(JLabel lbl, String image, int x, int y) throws IOException {
        File imageF = new File(image);
        BufferedImage img = ImageIO.read(imageF);
        int newWidth = img.getWidth();
        int newHeight = img.getHeight();
        double multiplier = 1;
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

        img = org.imgscalr.Scalr.resize(img, org.imgscalr.Scalr.Method.BALANCED, newWidth, newHeight); //https://github.com/rkalla/imgscalr
        origImage = img;
        ImageIcon icon = new ImageIcon(img);
        lbl.setIcon(icon);
        lbl.setText(imageF.getName());
        lbl.setHorizontalTextPosition(SwingConstants.CENTER);
        lbl.setVerticalTextPosition(SwingConstants.BOTTOM);
        lbl.setSize(newWidth, newHeight);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setVerticalAlignment(SwingConstants.CENTER);
        return new Pair<>(lbl, img);
    }

    public static Pair<JLabel, BufferedImage> reScale(String mode, Pair<JLabel, BufferedImage> img) throws IOException {
        int newWidth = img.second.getWidth();
        int newHeight = img.second.getHeight();
        double multiplier = 1.2;
        if (mode.equals("UP")) {
            newWidth = (int) (multiplier * newWidth);
            newHeight = (int) (multiplier * newHeight);
        } else if (mode.equals("DOWN")) {
            newWidth = (int) (newWidth / multiplier);
            newHeight = (int) (newHeight / multiplier);
        }
        img.second = org.imgscalr.Scalr.resize(origImage, org.imgscalr.Scalr.Method.BALANCED, newWidth, newHeight); //https://github.com/rkalla/imgscalr
        ImageIcon icon = new ImageIcon(img.second);
        img.first.setIcon(icon);
        img.first.setSize(newWidth, newHeight);
        return img;
    }
}
