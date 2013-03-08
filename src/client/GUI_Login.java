package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class GUI_Login extends JDialog {
    private JPanel contentPane;
    private JButton loginButton;
    private JButton exitButton;
    private JButton registerButton;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private Client client;

    public GUI_Login(Client client) {
        this.setTitle("SecureChat2");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(loginButton);
        this.client = client;

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onRegister();
            }
        });
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    onLogin();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    onExit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onRegister() {
        this.client.createRegisterWindow();
    }

    private void onLogin() throws IOException, NoSuchAlgorithmException {
        String username = this.textField1.getText();
        char[] password = this.passwordField1.getPassword();

        this.client.login(username, password);
    }

    private void onExit() throws IOException {
        this.client.logout();
        dispose();
    }
}
