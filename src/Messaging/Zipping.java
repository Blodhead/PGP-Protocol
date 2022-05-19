package Messaging;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.*;
public class Zipping {

    public static void zip(String txtInput, String zipOutput) throws IOException {

        File fileToZip = new File(txtInput);
        FileInputStream fis = new FileInputStream(fileToZip);

        FileOutputStream fos = new FileOutputStream(zipOutput);
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();
        }

    public static void unzip(String zipInput, String txtOutput) throws IOException {

        // zip next entry trazi sledeci fajl, ali ako mi imamo samo jedan fajl, ne moramo da trazimo dalje

        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipInput));
        ZipEntry zipEntry = zis.getNextEntry();

        File destFile = new File(txtOutput);

        FileOutputStream fos = new FileOutputStream(destFile);
        int len;
        while ((len = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
        }
        fos.close();

        zis.closeEntry();
        zis.close();
    }


}
