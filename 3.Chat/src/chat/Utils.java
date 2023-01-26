/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.awt.Image;
import java.io.File;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 * @author angel
 */
public class Utils {
    public static void createFolder(String folder) {
        File carpeta = new File("./" + folder);
        if(!carpeta.exists()) {
            try {
                if(carpeta.mkdir()) 
                    System.out.println("Se creo la carpeta");
                else 
                    System.out.println("No se creo la carpeta");
            }catch(SecurityException se) { 
                se.printStackTrace();
            }
        }
    }
    
    public static Icon resizeIcon(ImageIcon icon) {
        Image img = icon.getImage();  
        Image resizedImage = img.getScaledInstance(40, 40,  java.awt.Image.SCALE_SMOOTH);  
        return new ImageIcon(resizedImage);
    }

}
