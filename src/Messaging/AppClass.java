package Messaging;

import java.io.IOException;
import App.*;

//import Zipping.*;
public class AppClass {

    //User ksenija = new User();
    //User milos = new User();




    public static void main(String[] args) {

      //  KeyRings.generateNewKeyPair(1024);

        KeyRings kr = new KeyRings();

//        String msg = "";
//        System.out.println("Hello world");

//        try {
//            Zipping.zip("test.txt", "compressed.zip");
//            Zipping.unzip("compressed.zip", "unziped.txt");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    User u1 = new User("Milos", "123456");
    User u2 = new User("Ksenija", "123456");

        View_User.getUser_view();

    }



}
