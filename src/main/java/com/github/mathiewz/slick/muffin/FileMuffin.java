package com.github.mathiewz.slick.muffin;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.github.mathiewz.slick.util.Log;

/**
 * An implementation of the muffin load/save mechanism based around using the
 * local file system.
 *
 * @author kappaOne
 */
public class FileMuffin implements Muffin {

    /**
     * @see com.github.mathiewz.slick.muffin.Muffin#saveFile(java.util.HashMap,
     *      java.lang.String)
     */
    @Override
    public void saveFile(HashMap<String, ? extends Object> scoreMap, String fileName) throws IOException {
        String userHome = System.getProperty("user.home");
        File file = new File(userHome);
        file = new File(file, ".java");
        if (!file.exists()) {
            file.mkdir();
        }

        file = new File(file, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        // save hashMap
        oos.writeObject(scoreMap);

        oos.close();
    }

    /**
     * @see com.github.mathiewz.slick.muffin.Muffin#loadFile(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public HashMap<String, ? extends Object> loadFile(String fileName) throws IOException {
        HashMap<String, ? extends Object> hashMap = new HashMap<>();
        String userHome = System.getProperty("user.home");

        File file = new File(userHome);
        file = new File(file, ".java");
        file = new File(file, fileName);

        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);

                hashMap = (HashMap<String, ? extends Object>) ois.readObject();

                ois.close();

            } catch (EOFException e) {
                // End of the file reached, do nothing
            } catch (ClassNotFoundException e) {
                Log.error(e);
                throw new IOException("Failed to pull state from store - class not found");
            }
        }

        return hashMap;
    }
}