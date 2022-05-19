package Messaging;

import java.io.IOException;
import App.*;
//import Zipping.*;
public class AppClass {

    User ksenija = new User();
    User milos = new User();

    Admin admin = Admin.getAdmin();
    public static void main(String[] args) {
        String msg = "";
        System.out.println("Hello world");

        try {
            Zipping.zip("test.txt", "compressed.zip");
            Zipping.unzip("compressed.zip", "unziped.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        View_User.getUser_view();

    }



}
