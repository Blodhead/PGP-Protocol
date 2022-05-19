package App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileSystemView;

public class View_User extends JFrame {

    private final JPanel p1 = new JPanel();
    private final JPanel p2 = new JPanel();
    private final JPanel p3 = new JPanel();
    private View_User() {
        super("Pretty Good Privacy protocol");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent dog) {
                dispose();
            }
        });

        fill_space();
        setBounds(300, 150, 1300, 800);

        fill_tab1();
        fill_tab2();
        fill_tab3();

        this.setVisible(true);
        this.setResizable(false);
    }

private void fill_space() {

    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JTabbedPane tabbedPane = new JTabbedPane();

    tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

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

    p1.setLayout(new BorderLayout());
    p2.setLayout(new BorderLayout());
    p3.setLayout(new BorderLayout());

    p1.add(j1,BorderLayout.NORTH);
    p2.add(j2,BorderLayout.NORTH);
    p3.add(j3,BorderLayout.NORTH);

}

private void fill_tab1(){
        //ENKRIPCIJA

    JPanel form = new JPanel(new BorderLayout(2,2));

    JPanel labelFields = new JPanel(new GridLayout(0,2,1,1));
    labelFields.setBorder(new TitledBorder("GridLayout"));
    JPanel fields = new JPanel(new GridLayout(0,2,1,1));
    fields.setBorder(new TitledBorder("GridLayout"));

    for (int ii=1; ii<8; ii++) {
        labelFields.add(new JLabel("Label " + ii));
        // if these were of different size, it would be necessary to
        // constrain them using another panel
        labelFields.add(new JTextField(10));
    }

    JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

    //labelFields.add(fields, BorderLayout.EAST);

    form.add(labelFields, BorderLayout.NORTH);
    form.add(j);
    //sgui.add(guiCenter, BorderLayout.CENTER);

    p1.add(form, BorderLayout.CENTER);

}
private void fill_tab2(){}
private void fill_tab3(){}

    public static void getUser_view() {//bice pozvana samo jednom
        new View_User();
    }
}