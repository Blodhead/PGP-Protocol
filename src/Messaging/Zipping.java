package Messaging;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.*;
public class Zipping {

public static void zip(String file_name) throws IOException {
    String sourceFile = file_name;
    FileOutputStream fos = new FileOutputStream("compressed.zip");
    ZipOutputStream zipOut = new ZipOutputStream(fos);
    File fileToZip = new File(sourceFile);
    FileInputStream fis = new FileInputStream(fileToZip);
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

}
