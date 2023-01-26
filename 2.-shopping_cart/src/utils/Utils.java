package utils;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class Utils {
    public static String generateRandomName() {
        return UUID.randomUUID().toString();
    }

    public static String saveImage(ImageIcon image, String imageName) throws IOException {
        Image img = image.getImage();

        BufferedImage bi = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_BGR);

        Graphics2D g2 = bi.createGraphics();
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
        ImageIO.write(bi, "jpg", new File("images/"+imageName));

        return imageName;
    }

    public static boolean deleteImage(String imageName) {
        return deleteFile("iamges/"+imageName);
    }

    public static boolean deleteSong(String songName) {
        return deleteFile("media/"+songName);
    }

    private static boolean deleteFile(String filepath) {
        File file = new File(filepath);
        return file.delete();
    }

    public static ImageIcon resizeImage(ImageIcon imageIcon) {
        Image image = imageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        return new ImageIcon(newimg);  // transform it back
    }

    public static String getExtension(String file) {
        String extension = "";
        int i = file.lastIndexOf('.');
        if (i > 0)
            extension = file.substring(i+1);

        return extension;
    }
}
