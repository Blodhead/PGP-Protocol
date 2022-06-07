
package App;

import Messaging.KeyRings;
import Messaging.User;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
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
    private JScrollPane private_key_pool;

    public static JScrollPane private_key_pool1;
    public static JScrollPane public_key_pool1;

    public static JList<String> private_Jlist;
    public static Vector<String> private_list;
    public static Vector<String> public_list;
    public static JList<String> public_JList;

    private JScrollPane public_key_pool;
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
    }
    private void fill_tab1(){
        //Message Sending
        JPanel form = new JPanel(new BorderLayout(2,2)); //new BorderLayout(2,2)
        form.setBorder(new TitledBorder(""));

        //////////////////////////////////PLAINTEXT////////////////////////////////////////////
        {
            JPanel text_panel = new JPanel(new FlowLayout());
            text_panel.setBorder(new TitledBorder(""));

            JLabel plaintext = new JLabel("Enter plaintext:");
            plaintext.setFont(new Font("Texas", Font.BOLD, 20));
            text_panel.add(plaintext);

            JTextField plaintext_field = new JTextField();
            plaintext_field.setPreferredSize(new Dimension(750, 40));
            plaintext_field.setFont(new Font("Texas", Font.PLAIN, 12));
            text_panel.add(plaintext_field);

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

                JList<String> lista = new JList<>();
                private_key_pool = new JScrollPane(lista);
                private_key_pool.setPreferredSize(new Dimension(300, 500));
                temp2.add(private_key_pool, BorderLayout.CENTER);
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

                JList<String> lista2 = new JList<>(); ///LISTA JAVNIH KLJUCEVA
                public_key_pool = new JScrollPane(lista2);
                public_key_pool.setPreferredSize(new Dimension(300, 500));
                temp3.add(public_key_pool, BorderLayout.CENTER);
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
                opt_additional_body.add(radix64, c);

                opt_additional.add(opt_additional_body, BorderLayout.CENTER);
                options.add(opt_additional);
            }

            form.add(options, BorderLayout.CENTER);
        }
        //////////////////////////////////SEND//////////////////////////////////////////////////
        {
            JPanel send_panel = new JPanel(new FlowLayout());
            JButton send = new JButton("Send");
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

        JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        form.add(j,BorderLayout.CENTER);

        JPanel labelFields = new JPanel(null);
        labelFields.setBorder(new TitledBorder(""));
        labelFields.setPreferredSize(new Dimension(800,180));

        JLabel ciphertext_label = new JLabel("Ciphertext: ");
        ciphertext_label.setFont(new Font("Texas",Font.ITALIC,18));
        ciphertext_label.setBounds(300,30,100,20);

        JLabel plaintext_label = new JLabel("Plaintext: ");
        plaintext_label.setFont(new Font("Texas",Font.ITALIC,18));
        plaintext_label.setBounds(300,110,100,20);

        JTextField ciphertext = new JTextField();
        ciphertext.setBounds(400,30,500,30);
        JTextField plaintext = new JTextField();
        plaintext.setBounds(400,110,500,30);


        JButton decrypt = new JButton("Receive");
        decrypt.setBounds(560,70,100,30);
        labelFields.add(decrypt);

        ///        showMessageDialog(null, "This is even shorter");

        labelFields.add(ciphertext_label);
        labelFields.add(plaintext_label);
        labelFields.add(ciphertext);
        labelFields.add(plaintext);

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

            private_list = new Vector<>();
            private_Jlist = new JList<>(private_list.toArray(new String[0]));
            private_Jlist.setSelectedIndex(0);
            private_Jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            private_key_pool1 = new JScrollPane(private_Jlist);
            private_key_pool1.setBounds(100, 40, 300, 330);
            show_panel.add(private_key_pool1);

            ///LISTA JAVNIH KLJUCEVA
            JLabel publ_key = new JLabel("Public key ring:");
            publ_key.setFont(myFont);
            publ_key.setBounds(530, 10, 150, 25);
            show_panel.add(publ_key);


            public_list = new Vector<>();
            public_JList = new JList<>(public_list.toArray(new String[0]));
            public_JList.setSelectedIndex(0);
            public_JList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            public_key_pool1 =new JScrollPane(public_JList);
            public_key_pool1.setBounds(530, 40, 300, 330);
            show_panel.add(public_key_pool1);

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

            try {
                if(dsa_button.isSelected())
                    KeyRings.generateNewUserKeyPair("DSA",username.getText(), String.valueOf(pass_field.getPassword()),mail.getText(), Integer.parseInt( dsa_choice.getSelectedItem().toString()));
                else
                    KeyRings.generateNewUserKeyPair("ElGamal",username.getText(),String.valueOf(pass_field.getPassword()),mail.getText(), Integer.parseInt( elGamal_choice.getSelectedItem().toString()));

                View_User.private_list.removeAll(private_list);
                View_User.private_list.addAll(User.getSecretKeys(username.getText()));

                private_Jlist.setListData(private_list);

                View_User.public_list.removeAll(public_list);
                View_User.public_list.addAll(User.getPublicKeys());

                public_JList.setListData(public_list);


            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        });

        opt_encryption_check.addActionListener(e -> {
            if(!opt_encryption_check.isSelected()){
                encryption_algorithm.setEnabled(false);
                private_key_pool.setEnabled(false);
            }else{
                encryption_algorithm.setEnabled(true);
                private_key_pool.setEnabled(true);
            }
        });

        opt_authentication_check.addActionListener(e -> {
            if(!opt_authentication_check.isSelected()){
                pass.setEnabled(false);
                public_key_pool.setEnabled(false);
            }else{
                pass.setEnabled(true);
                public_key_pool.setEnabled(true);
            }
        });

        exp_button.addActionListener(ae -> {

            JFileChooser chooser;

            if(selected_list != null)
            chooser = new JFileChooser();
            else return;

            if((selected_list.getSelectedValue().toCharArray())[0] == '#'){
                if(selected_list == public_JList)
                JOptionPane.showMessageDialog(error_msg,
                        "Choose a valid user to export his public keys!",
                        "Error message",
                        JOptionPane.ERROR_MESSAGE);
                else if(selected_list == private_Jlist)
                    JOptionPane.showMessageDialog(error_msg,
                            "Choose user to export private key!",
                            "Error message",
                            JOptionPane.ERROR_MESSAGE);
                return;
            }


            FileNameExtensionFilter filter = new FileNameExtensionFilter("ASC files (*.asc)", "*.asc");
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
            JFileChooser chooser = new JFileChooser();
            File fileToLoad;
            FileNameExtensionFilter filter = new FileNameExtensionFilter("ASC files (*.asc)", "*.asc");
            chooser.setFileFilter(filter);
            boolean good_choice = false;
            int returnVal;

            while(!good_choice) {
                returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION){
                    fileToLoad = chooser.getSelectedFile();
                    if(fileToLoad.getName().contains("private.asc")){
                        ///
                        good_choice = true;
                    }else if(fileToLoad.getName().contains("public.asc")) {
                        ///
                        good_choice = true;
                    }else{
                        JOptionPane.showMessageDialog(error_msg,
                                "Choose a valid private or public key ring!",
                                "Error message",
                                JOptionPane.ERROR_MESSAGE);
                    }

                }else return;
            }

        });

        del_button.addActionListener(e -> {

            if(selected_list == public_JList){
                User.removePublicKey(selected_list.getSelectedValue());//#keyId
            }else if(selected_list == private_Jlist){
                User.removePrivateKey(selected_list.getSelectedValue());//#keyId
            }

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

        userChoice.addActionListener(e ->{

            current_user = User.getUser(String.valueOf(userChoice.getSelectedItem()));

        });

        registerUser.addActionListener(e -> {
            if(reg_mail.getText() == "" || Arrays.toString(reg_pass.getPassword()) == "" || reg_username.getText() == ""){
                JOptionPane.showMessageDialog(error_msg,
                        "All fields must be filled!",
                        "Error message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            else if(User.getUser(reg_username.getText()) != null){
                JOptionPane.showMessageDialog(error_msg,
                        "User already exists!",
                        "Error message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            optionsToChoose.add(reg_username.getText());
            new User(reg_username.getText(),Arrays.toString(reg_pass.getPassword()));
        });

    }

    public static void getUser_view() {//bice pozvana samo jednom
        if(u1 == null)
            u1 = new View_User();

    }
}
