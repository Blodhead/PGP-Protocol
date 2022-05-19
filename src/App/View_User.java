package App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileSystemView;

public class View_User extends JFrame {

    private final JPanel p1 = new JPanel();
    private final JPanel p2 = new JPanel();
    private final JPanel p3 = new JPanel();
    private View_User() {
        super("Pretty Good Privacy protocol");

        //Setting up look and feel, in other words the theme of our App
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        ////////////////////////////////////////////////////////////////
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

    tabbedPane.add("Send message", p1);
    tabbedPane.add("Receive message", p2);
    tabbedPane.add("Edit KeyRing", p3);

    this.add(tabbedPane, BorderLayout.CENTER);

    p1.setLayout(new BorderLayout());
    p2.setLayout(new BorderLayout());
    p3.setLayout(new BorderLayout());

}

private void fill_tab1(){
        //Message Sending
    JPanel form = new JPanel(new BorderLayout(2,2)); //new BorderLayout(2,2)
    form.setBorder(new TitledBorder(""));

    //////////////////////////////////PLAINTEXT////////////////////////////////////////////
    JPanel text_panel = new JPanel(new FlowLayout());
    text_panel.setBorder(new TitledBorder(""));

    JLabel plaintext = new JLabel("Enter plaintext:");
    plaintext.setFont(new Font("Texas", Font.BOLD,20));
    text_panel.add(plaintext);

    JTextField plaintext_field = new JTextField();
    plaintext_field.setPreferredSize(new Dimension(750,40));
    plaintext_field.setFont(new Font("Texas", Font.PLAIN,12));
    text_panel.add(plaintext_field);

    form.add(text_panel, BorderLayout.NORTH);

    //////////////////////////////////OPTIONS///////////////////////////////////////////////

    JPanel options = new JPanel(new GridLayout(1,3));
    options.setBorder(new TitledBorder("Options: "));

    JLabel opt_encryption = new JLabel("Encryption:");
    opt_encryption.setBounds(48, 84, 46, 14);
    options.add(opt_encryption);

    form.add(options,BorderLayout.CENTER);

    //////////////////////////////////SEND//////////////////////////////////////////////////

    JButton send = new JButton("Send");
    form.add(send,BorderLayout.SOUTH);

    p1.add(form);

}
private void fill_tab2(){
        //Message Receiving

    JPanel form = new JPanel(new BorderLayout(2,2));

    JPanel labelFields = new JPanel(new GridLayout(0,2,1,1));
    labelFields.setBorder(new TitledBorder(""));
    JPanel fields = new JPanel(new GridLayout(0,2,1,1));
    fields.setBorder(new TitledBorder(""));

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

    p2.add(form, BorderLayout.CENTER);

}
private void fill_tab3(){}

    public static void getUser_view() {//bice pozvana samo jednom
        new View_User();
    }
}