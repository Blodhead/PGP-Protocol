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
    TitledBorder t1 = new TitledBorder("Options: ");
    t1.setTitleFont(new Font("Texas", Font.BOLD,18));
    options.setBorder(t1);

        ///////////////////////////////Encryption///////////////////////////////////////
        JPanel opt_encryption = new JPanel(new BorderLayout());
        opt_encryption.setBorder(new TitledBorder(""));

        JPanel opt_encryption_header = new JPanel(new FlowLayout());

        JCheckBox opt_encryption_check = new JCheckBox("");

        JLabel opt_encryption_label = new JLabel("Encryption:");
        opt_encryption_label.setFont(new Font("Texas", Font.BOLD,18));

        opt_encryption_header.add(opt_encryption_check);
        opt_encryption_header.add(opt_encryption_label);
        opt_encryption.add(opt_encryption_header, BorderLayout.NORTH);

        JPanel opt_encryption_body = new JPanel(new GridBagLayout());
        GridBagConstraints c0 = new GridBagConstraints();
        c0.fill = GridBagConstraints.HORIZONTAL;
        c0.gridx = 0;
        c0.gridy = 0;

        JLabel choose_enc_text = new JLabel("Choose encryption algorithm: ");
        choose_enc_text.setFont(new Font("Texas", Font.ITALIC, 18));
        opt_encryption_body.add(choose_enc_text,c0);
        {
            c0.gridx = 1;
            opt_encryption_body.add(new JPanel(), c0);
        }
        c0.gridx = 2;
        String []encryption_algorithms = {"3DES","IDEA"};
        JComboBox encryption_algorithm = new JComboBox(encryption_algorithms);
        opt_encryption_body.add(encryption_algorithm,c0);
        {
            c0.gridx = 0;
            c0.gridy = 1;
            opt_encryption_body.add(new JPanel(), c0);
            c0.gridy = 2;
            opt_encryption_body.add(new JPanel(), c0);
            c0.gridy = 3;
            opt_encryption_body.add(new JPanel(), c0);
            c0.gridy = 4;
            opt_encryption_body.add(new JPanel(), c0);
            c0.gridy = 5;
            opt_encryption_body.add(new JPanel(), c0);
            c0.gridy = 6;
            opt_encryption_body.add(new JPanel(), c0);
            c0.gridy = 7;
            opt_encryption_body.add(new JPanel(), c0);

        }
        c0.gridy = 8;

        JLabel choose_enc_key = new JLabel("Choose DSA key: ");
        choose_enc_key.setFont(new Font("Texas", Font.ITALIC, 18));
        opt_encryption_body.add(choose_enc_key,c0);

        JList lista = new JList();
        JScrollPane key_pool = new JScrollPane(lista);
        key_pool.setPreferredSize(new Dimension(300,500));

        c0.gridy = 9;

        opt_encryption_body.add(key_pool,c0);


        opt_encryption.add(opt_encryption_body, BorderLayout.CENTER);

        options.add(opt_encryption);


        ///////////////////////////////Authentication////////////////////////////////////
    {
        JPanel opt_authentication = new JPanel(new BorderLayout());
        opt_authentication.setBorder(new TitledBorder(""));

        JPanel opt_authentication_header = new JPanel(new FlowLayout());

        JCheckBox opt_authentication_check = new JCheckBox("");

        JLabel opt_authentication_label = new JLabel("Authentication:");
        opt_authentication_label.setFont(new Font("Texas", Font.BOLD, 18));

        opt_authentication_header.add(opt_authentication_check);
        opt_authentication_header.add(opt_authentication_label);
        opt_authentication.add(opt_authentication_header, BorderLayout.NORTH);

        JPanel opt_authentication_body = new JPanel();
        opt_authentication_body.add(new JLabel("alihdsiwql"));

        opt_authentication.add(opt_authentication_body, BorderLayout.CENTER);


        options.add(opt_authentication);
    }

        ////////////////////////////////Additional////////////////////////////////////////

    {
        JPanel opt_additional = new JPanel(new BorderLayout());
        opt_additional.setBorder(new TitledBorder(""));

        JPanel opt_additional_header = new JPanel(new FlowLayout());

        JLabel opt_additional_label = new JLabel("Additional:");
        opt_additional_label.setFont(new Font("Texas", Font.BOLD, 18));

        opt_additional_header.add(opt_additional_label);
        opt_additional.add(opt_additional_header, BorderLayout.NORTH);

        JPanel opt_additional_body = new JPanel(new GridBagLayout());
        JCheckBox zip = new JCheckBox("Zip compressions");
        zip.setFont(new Font("Texas", Font.ITALIC, 18));
        JCheckBox radix64 = new JCheckBox("Radix64 conversion");
        System.out.println(radix64.getLocation());
        radix64.setFont(new Font("Texas", Font.ITALIC, 18));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        opt_additional_body.add(zip, c);

        {
            c.gridy = 1;
            opt_additional_body.add(new JPanel(), c);
            c.gridy = 2;
            opt_additional_body.add(new JPanel(), c);
            c.gridy = 3;
            opt_additional_body.add(new JPanel(), c);
            c.gridy = 4;
            opt_additional_body.add(new JPanel(), c);
            c.gridy = 5;
            opt_additional_body.add(new JPanel(), c);
            c.gridy = 6;
            opt_additional_body.add(new JPanel(), c);
        }

        c.gridy = 7;
        opt_additional_body.add(radix64,c);

        opt_additional.add(opt_additional_body, BorderLayout.CENTER);
        options.add(opt_additional);
    }

    form.add(options,BorderLayout.CENTER);

    //////////////////////////////////SEND//////////////////////////////////////////////////

    JPanel send_panel = new JPanel(new FlowLayout());
    JButton send = new JButton("Send");
    send.setPreferredSize(new Dimension(100,40));
    send_panel.add(send);
    form.add(send_panel,BorderLayout.SOUTH);

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