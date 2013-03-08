package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class GUI_Main extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton exitButton;
    private JList list1;
    private JButton refreshButton;
    private JButton showAllChatWindowsButton;
    private Client client;

    public GUI_Main(Client client, String username) throws IOException {

        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        this.client = client;
        setTitle("SecureChat2 # Logged in as " + username);

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    onRefresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    onCreateRoom();
                } catch (IOException e) {
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
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });

        onRefresh(); // refresh the user list on initialization
        showAllChatWindowsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                showAllChatWindows();
            }
        });
    }

    private void showAllChatWindows() {
        this.client.showAllChatWindows();
    }

    private void onRefresh() throws IOException {
        this.client.getUserList();
    }

    private void onCreateRoom() throws IOException {
        this.client.createRoom();
    }

    private void onExit() throws IOException {
        this.client.logout();
        dispose();
    }

    public void updateList(String[] params) {
        this.list1.setListData(params);
    }
}
