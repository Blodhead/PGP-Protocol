package App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
public class View_User extends JFrame {

    public static View_User user_view;
    final static String BUTTONPANEL = "Card with JButtons";
    final static String TEXTPANEL = "Card with JTextField";
    JPanel cards;
    private int currentCard = 0;

    private View_User() {
        super("User_view");


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent dog) {
                dispose();
            }
        });

        fill_space();
        setBounds(200, 100, 1550, 850);
        this.setVisible(true);
    }

private void fill_space() {

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JTabbedPane tabbedPane = new JTabbedPane();

    tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

    JPanel p1 = new JPanel();
    JPanel p2 = new JPanel();
    JPanel p3 = new JPanel();

    p1.setFont(new Font("Serif", Font.BOLD,24));
    p2.setFont(new Font("Serif", Font.BOLD,24));
    p3.setFont(new Font("Serif", Font.BOLD,24));

    tabbedPane.setBounds(50, 50, 200, 200);

    tabbedPane.add("Encryption", p1);
    tabbedPane.add("Decryption", p2);
    tabbedPane.add("Edit KeyRing", p3);

    this.add(tabbedPane, BorderLayout.CENTER);

    JLabel j1 = new JLabel("Encryption");
    JLabel j2 = new JLabel("Decryption");
    JLabel j3 = new JLabel("Edit KeyRing");

    j1.setFont(new Font("Serif", Font.BOLD,30));
    j2.setFont(new Font("Serif", Font.BOLD,30));
    j3.setFont(new Font("Serif", Font.BOLD,30));

    p1.add(j1);
    p2.add(j2);
    p3.add(j3);

}


    public static View_User getUser_view() {//bice pozvana samo jednom
        return new View_User();
    }
}