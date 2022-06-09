
package App;

import Messaging.Decryption;
import Messaging.Encryption;
import Messaging.KeyRings;
import Messaging.User;
import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPUtil;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Vector;

public class View_User extends JFrame {

    private static View_User u1;
    private User current_user;

    private final JPanel p1 = new JPanel();
    private final JPanel p2 = new JPanel();
    private final JPanel p3 = new JPanel();
    private final JPanel p4 = new JPanel();
    private JCheckBox opt_encryption_check;

    private JCheckBox opt_authentication_check;
    private JComboBox<String> encryption_algorithm;

    private static JScrollPane public_key_pool1;
    private static JScrollPane private_key_pool1;

    public static JScrollPane private_key_pool3;
    public static JScrollPane public_key_pool3;

    public static JList<String> private_Jlist;
    public static Vector<String> private_list;
    public static Vector<String> public_list;
    public static JList<String> public_JList;

    private JButton exp_button;
    private JButton imp_key_button;
    private JPasswordField pass;

    private JTextField username;
    private JTextField mail;
    private JPasswordField pass_field;
    private JComboBox dsa_choice;
    private JComboBox elGamal_choice;
    private JButton generate_dsa;
    private JCheckBox dsa_button;
    private JCheckBox elGamal_button;
    private JButton del_button;
    public JFrame error_msg;
    private JComboBox<String> userChoice;
    public JList<String> selected_list;
    public JButton registerUser;
    private JTextField reg_username;
    private JTextField reg_mail;
    private JPasswordField reg_pass;
    private Vector<String> optionsToChoose;
    private JList<String> lista1;
    private JList<String> lista2;
    private JButton send;
    private JTextField plaintext_field;
    private File plaintext_file;
    private JLabel plaintext_file_label;
    private JButton choose_plaintext_file;

    private JCheckBox plaintext_selected;
    private JCheckBox file_selected;
    private JFileChooser file_deryptor;
    private JButton decrypt;
    private JPasswordField password_decrypt;
    private JCheckBox zip;
    private JCheckBox radix64;

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

        selected_list = null;
        fill_space();
        setBounds(300, 150, 1300, 800);

        fill_tab1();
        fill_tab2();
        fill_tab3();
        fill_tab4();
        add_action_listeners();

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
        tabbedPane.add("User settings", p4);

        this.add(tabbedPane, BorderLayout.CENTER);

        p1.setLayout(new BorderLayout());
        p2.setLayout(new BorderLayout());
        p3.setLayout(new BorderLayout());
        p4.setLayout(new BorderLayout());

        current_user = User.getUser("Milos <milos>");

    }
    private void fill_tab1(){
        //Message Sending
        JPanel form = new JPanel(new BorderLayout(2,2)); //new BorderLayout(2,2)
        form.setBorder(new TitledBorder(""));

        //////////////////////////////////PLAINTEXT////////////////////////////////////////////
        {
            JPanel text_panel = new JPanel(null);
            text_panel.setPreferredSize(new Dimension(500,70));
            text_panel.setBorder(new TitledBorder(""));

            plaintext_selected = new JCheckBox();
            plaintext_selected.setBounds(40, 23,20,20);
            text_panel.add(plaintext_selected);

            JLabel plaintext = new JLabel("Enter plaintext:");
            plaintext.setBounds(60,18,200,30);
            plaintext.setFont(new Font("Texas", Font.BOLD, 20));
            text_panel.add(plaintext);

            plaintext_field = new JTextField();
            plaintext_field.setBounds(220,15,480,40);
            plaintext_field.setFont(new Font("Texas", Font.PLAIN, 12));
            text_panel.add(plaintext_field);

            JLabel or = new JLabel("OR");
            or.setBounds(730,18, 30,30);
            or.setFont(new Font("Texas", Font.BOLD, 20));
            text_panel.add(or);

            file_selected = new JCheckBox();
            file_selected.setBounds(800,23,20,20);
            text_panel.add(file_selected);
            file_selected.setSelected(true);
            plaintext_field.setEnabled(false);

            JLabel choose_a_file = new JLabel("Choose a file: ");
            choose_a_file.setFont(new Font("Texas", Font.BOLD, 20));
            choose_a_file.setBounds(820,18,140,30);
            text_panel.add(choose_a_file);

            choose_plaintext_file = new JButton("Choose...");
            choose_plaintext_file.setBounds(960,18, 90,30);
            text_panel.add(choose_plaintext_file);

            plaintext_file_label = new JLabel("");
            plaintext_file_label.setBounds(1060,18,200,30);
            //plaintext_file_label.setPreferredSize(new Dimension(100,30));
            text_panel.add(plaintext_file_label);
            plaintext_file_label.setVisible(false);

            ButtonGroup btn_group = new ButtonGroup();

            btn_group.add(plaintext_selected);
            btn_group.add(file_selected);

            form.add(text_panel, BorderLayout.NORTH);
        }
        //////////////////////////////////OPTIONS///////////////////////////////////////////////
        {
            JPanel options = new JPanel(new GridLayout(1, 3));
            TitledBorder t1 = new TitledBorder("Options: ");
            t1.setTitleFont(new Font("Texas", Font.BOLD, 18));
            options.setBorder(t1);

            ///////////////////////////////Encryption///////////////////////////////////////
            {
                JPanel opt_encryption = new JPanel(new BorderLayout());
                opt_encryption.setBorder(new TitledBorder(""));

                JPanel opt_encryption_header = new JPanel(new FlowLayout());

                opt_encryption_check = new JCheckBox("");
                opt_encryption_check.setSelected(true);

                JLabel opt_encryption_label = new JLabel("Encryption:");
                opt_encryption_label.setFont(new Font("Texas", Font.BOLD, 18));

                opt_encryption_header.add(opt_encryption_check);
                opt_encryption_header.add(opt_encryption_label);
                opt_encryption.add(opt_encryption_header, BorderLayout.NORTH);

                JPanel opt_encryption_body = new JPanel(new BorderLayout());

                opt_encryption_body.add(new JPanel());

                JPanel wrap = new JPanel(new BorderLayout());
                JPanel temp = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel choose_enc_text = new JLabel("Choose encryption algorithm: ");
                choose_enc_text.setFont(new Font("Texas", Font.ITALIC, 18));
                temp.add(choose_enc_text);

                String[] encryption_algorithms = {"3DES", "IDEA"};
                encryption_algorithm = new JComboBox<>(encryption_algorithms);
                temp.add(encryption_algorithm);

                wrap.add(temp, BorderLayout.NORTH);
                wrap.add(new JPanel(), BorderLayout.SOUTH);

                opt_encryption_body.add(wrap, BorderLayout.NORTH);

                JPanel temp2 = new JPanel(new BorderLayout());
                JLabel choose_enc_key = new JLabel("Choose public ElGamal key: ");
                choose_enc_key.setFont(new Font("Texas", Font.ITALIC, 17));
                temp2.add(choose_enc_key, BorderLayout.NORTH);

                public_list = new Vector<>();

                lista1 = new JList<>(public_list.toArray(new String[0]));
                lista1.setSelectedIndex(0);
                lista1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                public_key_pool1 = new JScrollPane(lista1);
                public_key_pool1.setPreferredSize(new Dimension(300, 500));
                temp2.add(public_key_pool1, BorderLayout.CENTER);

                opt_encryption_body.add(temp2, BorderLayout.CENTER);

                opt_encryption.add(opt_encryption_body, BorderLayout.CENTER);

                options.add(opt_encryption);
            }
            ///////////////////////////////Authentication////////////////////////////////////
            {
                JPanel opt_authentication = new JPanel(new BorderLayout());
                opt_authentication.setBorder(new TitledBorder(""));

                JPanel opt_authentication_header = new JPanel(new FlowLayout());

                opt_authentication_check = new JCheckBox("");
                opt_authentication_check.setSelected(true);

                JLabel opt_authentication_label = new JLabel("Authentication:");
                opt_authentication_label.setFont(new Font("Texas", Font.BOLD, 18));

                opt_authentication_header.add(opt_authentication_check);
                opt_authentication_header.add(opt_authentication_label);
                opt_authentication.add(opt_authentication_header, BorderLayout.NORTH);

                JPanel opt_authentication_body = new JPanel(new BorderLayout());

                JPanel wrapping = new JPanel(new BorderLayout());
                JPanel temp1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
                JLabel choose_enc_text1 = new JLabel("Password: ");
                choose_enc_text1.setFont(new Font("Texas", Font.ITALIC, 18));
                temp1.add(choose_enc_text1);

                pass = new JPasswordField();
                pass.setPreferredSize(new Dimension(250, 25));
                temp1.add(pass);
                wrapping.add(temp1, BorderLayout.NORTH);
                wrapping.add(new JPanel(), BorderLayout.SOUTH);


                JPanel temp3 = new JPanel(new BorderLayout());
                JLabel choose_aut_key = new JLabel("Choose private DSA key: ");
                choose_aut_key.setFont(new Font("Texas", Font.ITALIC, 17));
                temp3.add(choose_aut_key, BorderLayout.NORTH);

                private_list = new Vector<>();
                lista2 = new JList<>(private_list.toArray(new String[0]));
                lista2.setSelectedIndex(0);
                lista2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


                private_key_pool1= new JScrollPane(lista2);
                private_key_pool1.setPreferredSize(new Dimension(300, 500));
                temp3.add(private_key_pool1, BorderLayout.CENTER);
                opt_authentication_body.add(temp3, BorderLayout.CENTER);


                opt_authentication_body.add(wrapping, BorderLayout.NORTH);

                opt_authentication.add(opt_authentication_body, BorderLayout.CENTER);


                options.add(opt_authentication);
            }
            ////////////////////////////////Additional////////////////////////////////////////
            {
                JPanel opt_additional = new JPanel(new BorderLayout());
                opt_additional.setBorder(new TitledBorder(""));

                JPanel opt_additional_header = new JPanel(new FlowLayout());

                JLabel opt_additional_label = new JLabel("Additional options:");
                opt_additional_label.setFont(new Font("Texas", Font.BOLD, 18));

                opt_additional_header.add(opt_additional_label);
                opt_additional.add(opt_additional_header, BorderLayout.NORTH);

                JPanel opt_additional_body = new JPanel(new GridBagLayout());
                zip = new JCheckBox("Zip compressions");
                zip.setFont(new Font("Texas", Font.ITALIC, 18));
                radix64 = new JCheckBox("Radix64 conversion");
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
                opt_additional_body.add(radix64, c);

                opt_additional.add(opt_additional_body, BorderLayout.CENTER);
                options.add(opt_additional);
            }

            form.add(options, BorderLayout.CENTER);
        }
        //////////////////////////////////SEND//////////////////////////////////////////////////
        {
            JPanel send_panel = new JPanel(new FlowLayout());
            send = new JButton("Send");
            send.setPreferredSize(new Dimension(100, 40));
            send_panel.add(send);
            form.add(send_panel, BorderLayout.SOUTH);
        }
        p1.add(form);

    }
    private void fill_tab2(){
        //Message Receiving

        JPanel form = new JPanel(new BorderLayout(2,2));

        JPanel fields = new JPanel(new GridLayout(0,2,1,1));
        fields.setBorder(new TitledBorder(""));

        JLabel insert_text = new JLabel("Choose .asc file you want receive:",SwingConstants.CENTER);
        insert_text.setFont(new Font("Texas",Font.ITALIC,20));
        insert_text.setBorder(new EmptyBorder(10,0,0,0));
        form.add(insert_text,BorderLayout.NORTH);

        file_deryptor = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        form.add(file_deryptor,BorderLayout.CENTER);

        JPanel labelFields = new JPanel(null);
        labelFields.setBorder(new TitledBorder(""));
        labelFields.setPreferredSize(new Dimension(800,120));

        JLabel ciphertext_label = new JLabel("Password: ");
        ciphertext_label.setFont(new Font("Texas",Font.ITALIC,18));
        ciphertext_label.setBounds(440,32,100,20);

        password_decrypt = new JPasswordField();
        password_decrypt.setBounds(545,30,200,30);

        decrypt = new JButton("Receive");
        decrypt.setBounds(560,70,100,30);
        labelFields.add(decrypt);

        labelFields.add(ciphertext_label);

        labelFields.add(password_decrypt);

        form.add(labelFields, BorderLayout.SOUTH);

        p2.add(form, BorderLayout.CENTER);

    }
    private void fill_tab3(){

        JPanel adition = new JPanel(new BorderLayout());
        adition.setBorder(new TitledBorder(""));

        JPanel show_panel = new JPanel(null);
        show_panel.setBorder(new TitledBorder(""));

        JPanel del_panel = new JPanel();
        del_panel.setBorder(new TitledBorder(""));


        adition.add(show_panel, BorderLayout.CENTER);

        Font myFont = new Font("Texas", Font.ITALIC, 18);
        JPanel gen_panel = new JPanel(new BorderLayout());

        error_msg = new JFrame();
        error_msg.setBounds(150, 150, 250, 250);

        //////////////////////GENERATE KEYS////////////////////

        {
            JPanel gen_form = new JPanel(null);
            gen_form.setBorder(new TitledBorder(""));

            JLabel key_gen_label = new JLabel("Key generation:");
            key_gen_label.setFont(new Font("Texas", Font.BOLD, 18));
            key_gen_label.setBounds(80, 30, 250, 25);
            gen_panel.add(key_gen_label);

            JLabel name_txt = new JLabel("Name: ");
            name_txt.setBounds(20, 100, 70, 20);
            name_txt.setFont(myFont);
            gen_form.add(name_txt);

            username = new JTextField();
            username.setBounds(120, 98, 150, 30);
            gen_form.add(username);

            JLabel mail_text = new JLabel("E-mail: ");
            mail_text.setBounds(20, 150, 70, 20);
            mail_text.setFont(myFont);
            gen_form.add(mail_text);

            mail = new JTextField();
            mail.setBounds(120, 148, 150, 30);
            gen_form.add(mail);

            JLabel pass_text = new JLabel("Password: ");
            pass_text.setBounds(20, 200, 100, 20);
            pass_text.setFont(myFont);
            gen_form.add(pass_text);

            pass_field = new JPasswordField();
            pass_field.setBounds(120, 198, 150, 30);
            gen_form.add(pass_field);

            JLabel dsa_choose_txt = new JLabel("DSA: ");
            dsa_choose_txt.setBounds(20, 250, 100, 20);
            dsa_choose_txt.setFont(myFont);
            gen_form.add(dsa_choose_txt);

            dsa_button = new JCheckBox();
            dsa_button.setBounds(130,254,20,20);
            elGamal_button = new JCheckBox();
            elGamal_button.setBounds(130, 290,20,20);

            gen_form.add(dsa_button);
            dsa_button.setSelected(true);
            gen_form.add(elGamal_button);


            ButtonGroup button_group = new ButtonGroup();

            button_group.add(dsa_button);
            button_group.add(elGamal_button);

            JLabel elGamal_choose_txt = new JLabel("ElGamal: ");
            elGamal_choose_txt.setBounds(20, 290, 100, 20);
            elGamal_choose_txt.setFont(myFont);
            gen_form.add(elGamal_choose_txt);

            String[] dsael_Gamal_choice_opt = {"1024", "2048", "4096"};
            elGamal_choice = new JComboBox<>(dsael_Gamal_choice_opt);
            elGamal_choice.setBounds(170, 288, 100, 30);
            elGamal_choice.setEnabled(false);
            gen_form.add(elGamal_choice);

            String[] dsa_choice_opt = {"1024", "2048"};
            dsa_choice = new JComboBox<>(dsa_choice_opt);
            dsa_choice.setBounds(170, 248, 100, 30);
            dsa_choice.setEnabled(true);
            gen_form.add(dsa_choice);

            gen_form.setPreferredSize(new Dimension(300, 500));
            gen_panel.add(gen_form, BorderLayout.CENTER);

            generate_dsa = new JButton("Generate");
            generate_dsa.setPreferredSize(new Dimension(100,40));
            gen_panel.add(generate_dsa, BorderLayout.SOUTH);
            adition.add(gen_panel, BorderLayout.WEST);
        }

        //////////////////////TABLES///////////////////////////

        {
            ///LISTA PRIVATNIH KLJUCEVA
            JLabel priv_key = new JLabel("Private key ring:");
            priv_key.setFont(myFont);
            priv_key.setBounds(100, 10, 150, 25);
            show_panel.add(priv_key);

            private_Jlist = new JList<>(private_list.toArray(new String[0]));
            private_Jlist.setSelectedIndex(0);
            private_Jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            private_key_pool3 = new JScrollPane(private_Jlist);
            private_key_pool3.setBounds(100, 40, 300, 330);
            show_panel.add(private_key_pool3);

            ///LISTA JAVNIH KLJUCEVA
            JLabel publ_key = new JLabel("Public key ring:");
            publ_key.setFont(myFont);
            publ_key.setBounds(530, 10, 150, 25);
            show_panel.add(publ_key);

            public_JList = new JList<>(public_list.toArray(new String[0]));
            public_JList.setSelectedIndex(0);
            public_JList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            public_key_pool3 =new JScrollPane(public_JList);
            public_key_pool3.setBounds(530, 40, 300, 330);
            show_panel.add(public_key_pool3);

        }

        /////////////////////Miscellaneous///////////////////////////
        {
            JPanel selection = new JPanel(new GridLayout(1, 3));
            selection.setPreferredSize(new Dimension(800, 300));
            selection.setBorder(new TitledBorder(""));

            JPanel export_key = new JPanel(null);
            ///////////////////////////////EXPORT//////////////////////////////
            {

                TitledBorder t1 = new TitledBorder("Export:");
                t1.setTitleFont(new Font("Texas", Font.BOLD, 18));
                export_key.setBorder(t1);

                JLabel exp_txt = new JLabel("Export selected key to: ");
                exp_txt.setFont(new Font("Texas", Font.PLAIN, 15));
                exp_txt.setBounds(20, 30, 200, 25);
                export_key.add(exp_txt);

                exp_button = new JButton("Export");
                exp_button.setBounds(170, 230, 100, 30);
                export_key.add(exp_button);

            }
            JPanel import_key = new JPanel(null);
            ///////////////////////////////IMPORT//////////////////////////////
            {
                TitledBorder t2 = new TitledBorder("Import:");
                t2.setTitleFont(new Font("Texas", Font.BOLD, 18));
                import_key.setBorder(t2);

                JLabel imp_txt = new JLabel("Import selected key from: ");
                imp_txt.setFont(new Font("Texas", Font.PLAIN, 15));
                imp_txt.setBounds(20, 30, 200, 25);
                import_key.add(imp_txt);

                imp_key_button = new JButton("Import");
                imp_key_button.setBounds(170, 230, 100, 30);
                import_key.add(imp_key_button);

            }
            JPanel delete_key = new JPanel(null);
            ///////////////////////////////DELETION////////////////////////////
            {
                TitledBorder t3 = new TitledBorder("Delete:");
                t3.setTitleFont(new Font("Texas", Font.BOLD, 18));
                delete_key.setBorder(t3);

                JLabel del_txt = new JLabel("Delete selected key: ");
                del_txt.setFont(new Font("Texas", Font.PLAIN, 15));
                del_txt.setBounds(20, 30, 200, 25);
                delete_key.add(del_txt);

                del_button = new JButton("Delete");
                del_button.setBounds(170, 230, 100, 30);
                delete_key.add(del_button);
            }


            selection.add(import_key);
            selection.add(export_key);
            selection.add(delete_key);


            adition.add(selection, BorderLayout.SOUTH);
        }
        p3.add(adition);

    }
    private void fill_tab4(){

///////////////////////////ChangeUser Panel////////////////////////////

        JPanel show_panel = new JPanel(null);
        show_panel.setBorder(new TitledBorder(""));
        show_panel.setPreferredSize(new Dimension(400,500));

        optionsToChoose = User.getAllUsers();

        JLabel change_text = new JLabel("Current user: ");
        change_text.setBounds(100,60,300,50);
        change_text.setFont(new Font("Texas", Font.ITALIC, 28));
        show_panel.add(change_text);

        userChoice = new JComboBox<>(optionsToChoose);
        userChoice.setMaximumRowCount(12);
        userChoice.setBounds(300,70,200,40);
        show_panel.add(userChoice);

///////////////////////////AddUser Panel////////////////////////////
        JPanel del_panel = new JPanel(null);
        del_panel.setBorder(new TitledBorder(""));

        JLabel register_user = new JLabel("Register new User: ");
        register_user.setBounds(200,60,300,50);
        register_user.setFont(new Font("Texas", Font.ITALIC, 28));
        del_panel.add(register_user);

        Font myFont = new Font("Texas", Font.ITALIC, 18);

        JLabel name_txt = new JLabel("Name: ");
        name_txt.setBounds(140, 170, 70, 20);
        name_txt.setFont(myFont);
        del_panel.add(name_txt);

        reg_username = new JTextField();
        reg_username.setBounds(240, 168, 150, 30);
        del_panel.add(reg_username);

        JLabel mail_text = new JLabel("E-mail: ");
        mail_text.setBounds(140, 244, 70, 20);
        mail_text.setFont(myFont);
        del_panel.add(mail_text);

        reg_mail = new JTextField();
        reg_mail.setBounds(240, 243, 150, 30);
        del_panel.add(reg_mail);

        JLabel pass_text = new JLabel("Password: ");
        pass_text.setBounds(140, 318, 100, 20);
        pass_text.setFont(myFont);
        del_panel.add(pass_text);

        reg_pass = new JPasswordField();
        reg_pass.setBounds(240, 317, 150, 30);
        del_panel.add(reg_pass);

        registerUser = new JButton("Register");
        registerUser.setBounds(260,380,100,30);
        del_panel.add(registerUser);

        show_panel.setMinimumSize(new Dimension(640,800));
        del_panel.setMinimumSize(new Dimension(630,800));
        JSplitPane change_user_page = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,show_panel,del_panel);
        p4.add(change_user_page);

    }
    private void add_action_listeners(){

        generate_dsa.addActionListener( e -> {

            if(!current_user.getUsername().equals(username.getText()+ " <" + mail.getText() + ">")) {
                JOptionPane.showMessageDialog(error_msg,
                        "This user can generate private keys only for him/her self!",
                        "Error message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(username.getText().equals("") || mail.getText().equals("") ||pass_field.getPassword().length == 0){
                JOptionPane.showMessageDialog(error_msg,
                        "Parameters missing!",
                        "Error message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!User.CheckPassword(current_user.getUsername(), String.valueOf(pass_field.getPassword()))) {
                JOptionPane.showMessageDialog(error_msg,
                        "Must enter a valid password!!",
                        "Error message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                if(dsa_button.isSelected())
                    KeyRings.generateNewUserKeyPair("DSA",username.getText() , String.valueOf(pass_field.getPassword()),mail.getText(), Integer.parseInt( dsa_choice.getSelectedItem().toString()));
                else
                    KeyRings.generateNewUserKeyPair("ElGamal",username.getText(),String.valueOf(pass_field.getPassword()),mail.getText(), Integer.parseInt( elGamal_choice.getSelectedItem().toString()));

                View_User.private_list.removeAll(private_list);
                View_User.private_list.addAll(User.getSecretKeys(username.getText()+ " <" + mail.getText() + ">"));

                private_Jlist.setListData(private_list);
                lista2.setListData(private_list);

                View_User.public_list.removeAll(public_list);
                View_User.public_list.addAll(User.getPublicKeys());

                public_JList.setListData(public_list);
                lista1.setListData(public_list);


            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        });

        opt_encryption_check.addActionListener(e -> {
            if(!opt_encryption_check.isSelected()){
                encryption_algorithm.setEnabled(false);
                public_key_pool1.setEnabled(false);
                lista1.clearSelection();
                lista1.setEnabled(false);

            }else{
                encryption_algorithm.setEnabled(true);
                public_key_pool1.setEnabled(true);
                lista1.setEnabled(true);
            }
        });

        opt_authentication_check.addActionListener(e -> {
            if(!opt_authentication_check.isSelected()){
                pass.setEnabled(false);
                private_key_pool1.setEnabled(false);
                lista2.clearSelection();
                lista2.setEnabled(false);
            }else{
                pass.setEnabled(true);
                private_key_pool1.setEnabled(true);
                lista2.setEnabled(true);
            }
        });

        exp_button.addActionListener(ae -> {

            JFileChooser chooser;
            //chooser.setCurrentDirectory(File dir);

            if(selected_list != null)
                chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            else return;

            if((selected_list.getSelectedValue().toCharArray())[0] == '#'){
                if(selected_list == private_Jlist){
                    JOptionPane.showMessageDialog(error_msg,
                            "Choose user to export private key!",
                            "Error message",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }else if(selected_list == public_JList) {
                    JOptionPane.showMessageDialog(error_msg,
                            "Choose a valid user to export his public keys!",
                            "Error message",
                            JOptionPane.ERROR_MESSAGE);
                }
                return;
            }


            FileNameExtensionFilter filter = new FileNameExtensionFilter("ASC files (*.asc)", "asc");
            chooser.setFileFilter(filter);

            int returnVal;

            if(selected_list == public_JList)
                chooser.setDialogTitle("Export public key ring:");
            else if(selected_list == private_Jlist)
                chooser.setDialogTitle("Export private key ring:");

            returnVal = chooser.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION){
                ////////////////////PUBLIC KEY
                if(selected_list == public_JList){
                    PGPPublicKeyRing pkr = User.getPublicKeyRing(selected_list.getSelectedValue());
                    try (ArmoredOutputStream out = new ArmoredOutputStream(Files.newOutputStream(Paths.get(chooser.getSelectedFile() + "-public.asc")))) {
                        pkr.encode(out);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                //////////////////////PRIVATE KEY
                else if(selected_list == private_Jlist){

                    PGPSecretKeyRing pkr = User.getSecretKeyRing(selected_list.getSelectedValue());

                    try (ArmoredOutputStream out = new ArmoredOutputStream(Files.newOutputStream(Paths.get(chooser.getSelectedFile() + "-private.asc")))) {
                        pkr.encode(out);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        });

        imp_key_button.addActionListener(ae -> {


            JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            File fileToLoad = null;
            FileNameExtensionFilter filter = new FileNameExtensionFilter("ASC files (*.asc)", "asc");
            chooser.setFileFilter(filter);
            boolean good_choice = false;
            int returnVal;

            while(!good_choice) {
                returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION){
                    fileToLoad = chooser.getSelectedFile();
                    if(fileToLoad.getName().contains("private.asc") || fileToLoad.getName().contains("public.asc")){

                        // da li je ispravan user

                        //CURRENT_USER ne bi trebao da moze da doda privatne kljuceve vlasnika fjla
                        /*if(fileToLoad.getName().contains("private.asc")){
                            JOptionPane.showMessageDialog(error_msg,
                                    "You can import only your private keys!",
                                    "Error message",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }*/

                        good_choice = true;

                        try {
                            FileInputStream fis = new FileInputStream(fileToLoad);

                            if (!KeyRings.importKeyRing(String.valueOf(userChoice.getSelectedItem()), fis)) {
                                JOptionPane.showMessageDialog(error_msg,
                                        "Choose your own private key ring!",
                                        "Error message",
                                        JOptionPane.ERROR_MESSAGE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }else{
                        JOptionPane.showMessageDialog(error_msg,
                                "Choose a valid private or public key ring!",
                                "Error message",
                                JOptionPane.ERROR_MESSAGE);
                    }

                }else return;
            }

            if(fileToLoad.getName().contains("private.asc")) {
                current_user = User.getUser(String.valueOf(userChoice.getSelectedItem()));

                View_User.private_list.removeAll(private_list);
                View_User.private_list.addAll(User.getSecretKeys(current_user.getUsername()));

                private_Jlist.setListData(private_list);
                lista2.setListData(private_list);
            }
            if(fileToLoad.getName().contains("public.asc")) {
                View_User.public_list.removeAll(public_list);
                View_User.public_list.addAll(User.getPublicKeys());

                public_JList.setListData(public_list);
                lista1.setListData(public_list);
            }
        });

        del_button.addActionListener(e -> {

            if(selected_list == public_JList){
                User.removePublicKey(selected_list.getSelectedValue());//#keyId
            }else if(selected_list == private_Jlist){
                User.removePrivateKey(selected_list.getSelectedValue());//#keyId
            }
            View_User.private_list.removeAll(private_list);
            View_User.private_list.addAll(User.getSecretKeys(username.getText()+ " <" + mail.getText() + ">"));

            private_Jlist.setListData(private_list);
            lista2.setListData(private_list);

            View_User.public_list.removeAll(public_list);
            View_User.public_list.addAll(User.getPublicKeys());

            public_JList.setListData(public_list);
            lista1.setListData(public_list);
        });

        private_Jlist.addListSelectionListener(e -> {
            if(selected_list == public_JList){
                public_JList.clearSelection();
            }
            selected_list = private_Jlist;

        });

        public_JList.addListSelectionListener(e -> {
            if(selected_list == private_Jlist){
                private_Jlist.clearSelection();
            }
            selected_list = public_JList;
        });

        dsa_button.addActionListener(e -> {
            dsa_choice.setEnabled(true);
            elGamal_choice.setEnabled(false);
        });

        elGamal_button.addActionListener(e -> {
            elGamal_choice.setEnabled(true);
            dsa_choice.setEnabled(false);
        });

        plaintext_selected.addActionListener(e -> {
            if(!file_selected.isSelected() && plaintext_selected.isSelected()){
                file_selected.setSelected(false);
                plaintext_field.setEnabled(true);
                choose_plaintext_file.setEnabled(false);
            }

        });

        file_selected.addActionListener(e -> {
            if(file_selected.isSelected() && !plaintext_selected.isSelected()){
                plaintext_selected.setSelected(false);
                plaintext_field.setEnabled(false);
                choose_plaintext_file.setEnabled(true);
            }
        });

        userChoice.addActionListener(e -> {
            current_user = User.getUser(String.valueOf(userChoice.getSelectedItem()));

            View_User.private_list.removeAll(private_list);
            View_User.private_list.addAll(User.getSecretKeys(current_user.getUsername()));

            private_Jlist.setListData(private_list);
            lista2.setListData(private_list);
        });

        registerUser.addActionListener(e -> {
            if(Objects.equals(reg_mail.getText(), "") || String.valueOf(reg_pass.getPassword()).equals("") || Objects.equals(reg_username.getText(), "")){
                JOptionPane.showMessageDialog(error_msg,
                        "All fields must be filled!",
                        "Error message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            else if(User.getUser(reg_username.getText() + " <" + reg_mail.getText() + ">") != null){
                JOptionPane.showMessageDialog(error_msg,
                        "User already exists!",
                        "Error message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            optionsToChoose.removeAll(User.getAllUsers());
            User u = new User(reg_username.getText() + " <" + reg_mail.getText() + ">", String.valueOf(reg_pass.getPassword()));

            Vector<String> list = User.getAllUsers();
            Iterator value = list.iterator();
            while (value.hasNext()) {

                optionsToChoose.add(String.valueOf(value.next()));
            }



        });

        send.addActionListener(e -> {
            File file_for_encryption;
            /////////////////////FILE/PLAINTEXT///////////////////
            {
                if ((plaintext_selected.isSelected() && plaintext_field.getText().equals("")) ||
                        file_selected.isSelected() && plaintext_file == null) {
                    JOptionPane.showMessageDialog(error_msg,
                            "Must choose plaintext or file to encrypt!",
                            "Error message",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                file_for_encryption = new File("encrypted_file.txt");
                if (plaintext_selected.isSelected()) {
                    FileWriter myWriter;
                    try {
                        myWriter = new FileWriter(file_for_encryption);
                        myWriter.write(plaintext_field.getText());
                        myWriter.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                if (file_selected.isSelected()) {
                    file_for_encryption = plaintext_file;
                }

                ///////////////////////////keys////////////////////////
                if(opt_encryption_check.isSelected()){
                    if (((JList<String>) public_key_pool1.getViewport().getView()).getSelectedValue() == null) {
                        JOptionPane.showMessageDialog(error_msg,
                                "Must choose a public key!!",
                                "Error message",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (((((JList<String>) public_key_pool1.getViewport().getView()).getSelectedValue())).charAt(0) != '#') {
                    JOptionPane.showMessageDialog(error_msg,
                            "Choose a public dsa key!!",
                            "Error message",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                    }
                }

                if(opt_authentication_check.isSelected()) {
                    if (((JList<String>) private_key_pool1.getViewport().getView()).getSelectedValue() == null) {
                        JOptionPane.showMessageDialog(error_msg,
                                "Must choose a private key!!",
                                "Error message",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (((((JList<String>) private_key_pool1.getViewport().getView()).getSelectedValue())).charAt(0) == '#') {
                        JOptionPane.showMessageDialog(error_msg,
                                "Choose a private dsa key!!",
                                "Error message",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            ////////////////////////////password///////////////////////
            {
                if(opt_authentication_check.isSelected()){
                    if (pass.getPassword() == null) {
                        JOptionPane.showMessageDialog(error_msg,
                                "Must enter a password!!",
                                "Error message",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    } else if (!User.CheckPassword(current_user.getUsername(), String.valueOf(pass.getPassword()))) {
                        JOptionPane.showMessageDialog(error_msg,
                                "Must enter a valid password!!",
                                "Error message",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            boolean is3DES = false;
            if(encryption_algorithm.getSelectedItem() == "3DES") is3DES = true;

            try {
                Encryption.signEncryptFile(file_for_encryption.getPath(),
                        User.getPublicKey(((JList<String>)public_key_pool1.getViewport().getView()).getSelectedValue()),
                        User.getSecretKey(((JList<String>)private_key_pool1.getViewport().getView()).getSelectedValue()),
                        String.valueOf(pass.getPassword()).toCharArray(),
                        opt_encryption_check.isSelected(),
                        opt_authentication_check.isSelected(),
                        zip.isSelected(),
                        radix64.isSelected(),
                        is3DES);
            } catch (Exception ex) {
                System.out.println("GRESKAAA");
                throw new RuntimeException(ex);
            }

            if (file_selected.isSelected()){
            JOptionPane.showMessageDialog(error_msg,
                    "File "+file_for_encryption.getName()+" was encrypted, and saved to Desktop!",
                    "Info message",
                    JOptionPane.INFORMATION_MESSAGE);
            }else if(plaintext_selected.isSelected()){
                JOptionPane.showMessageDialog(error_msg,
                        "File "+file_for_encryption.getName()+" was created and encrypted, saved in project folder!",
                        "Info message",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            return;
        });

        choose_plaintext_file.addActionListener(e -> {

            JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION){
                plaintext_file = chooser.getSelectedFile(); //treba proveriti tipove
                plaintext_file_label.setText(plaintext_file.getName());
                plaintext_file_label.setFont(new Font("Texas",Font.ITALIC,18));
                plaintext_file_label.setVisible(true);
            }
        });

        decrypt.addActionListener(e -> {

            if (!User.CheckPassword(current_user.getUsername(), String.valueOf(password_decrypt.getPassword()))) {
                JOptionPane.showMessageDialog(error_msg,
                        "Must enter a valid password!!",
                        "Error message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if(!file_deryptor.getSelectedFile().getName().contains(".pgp")){
                JOptionPane.showMessageDialog(error_msg,
                        "File must be .pgp type!",
                        "Error message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Decryption.decryptAndVerify(file_deryptor.getSelectedFile(),String.valueOf(password_decrypt.getPassword()).toCharArray());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (PGPException ex) {
                throw new RuntimeException(ex);
            } catch (SignatureException ex) {
                throw new RuntimeException(ex);
            }


            JOptionPane.showMessageDialog(error_msg,
                    "File has been decrypted!",
                    "Error message",
                    JOptionPane.INFORMATION_MESSAGE);



        });
    }

    public static void getUser_view() {//bice pozvana samo jednom
        if(u1 == null)
            u1 = new View_User();

    }
}
