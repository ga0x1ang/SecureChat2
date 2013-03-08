package client;

import common.SCUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class GUI_Register extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPasswordField passwordField1;
    private JTextField textField1;
    private JPasswordField passwordField2;
    private Client client;

    public GUI_Register(Client client) {
        this.setTitle("Registration");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.client = client;
        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    onOK();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onCancel();
            }
        });
    }

    private void onOK() throws IOException, NoSuchAlgorithmException {

        String username = this.textField1.getText();
        char[] password = this.passwordField1.getPassword();
        char[] repassword = this.passwordField2.getPassword();

        boolean usernameChecked = SCUtils.usernameChecker(username);
        boolean passwordStrengthChecked = SCUtils.passwordStrengthChecker(password);
        boolean passwordMatchChecked = String.valueOf(password).equals(String.valueOf(repassword));


        boolean inputChecked = usernameChecked && passwordMatchChecked && passwordMatchChecked;

        if (inputChecked) {
            this.client.register(username, password);
        } else {
            this.client.popupMessageBox(new String[]{"username should not contains space, 3-16 length\n passwords not match or password not strong enough", String.valueOf(false)});
        }
    }

    private void onCancel() {
        dispose();
    }
}
