package etf.openpgp.cm170333dbk190730d.Messaging;

import etf.openpgp.cm170333dbk190730d.App.View_User;

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

    User u1 = new User("Milos <milos>", "123456");
    User u2 = new User("Ksenija <ksenija>", "123456");

        View_User.getUser_view();

    }



}
