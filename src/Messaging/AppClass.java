package Messaging;

import java.io.IOException;

//import Zipping.*;
public class AppClass {

    Admin admin = Admin.getAdmin();
    public static void main(String[] args) {
        String msg = "";
        System.out.println("Hello world");

        try {
            Zipping.zip("test.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }



}
