package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class GUI_Chat extends JFrame {
    private JPanel contentPane;
    private JButton sendButton;
    private JButton inviteButton;
    private JTextField textField1;
    private JList list1;
    private JTextArea textArea1;
    private JButton exitButton;
    private JButton removeButton;
    private Client client;
    private int roomId;

    public GUI_Chat(Client client, int roomId) {

        setContentPane(contentPane);
        getRootPane().setDefaultButton(sendButton);
        this.client = client;
        this.roomId = roomId;
        this.setTitle("Room#" + roomId);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    onSend();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        inviteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    onInvite();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
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
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    onRemove();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }

    private void onRemove() throws IOException {
        this.client.remove(this.roomId, String.valueOf(this.list1.getSelectedValue()));
    }

    private void onInvite() throws IOException {

        String username = this.textField1.getText();
        this.client.inviteUser(this.roomId, username);
        textField1.setText("");
    }

    private void onSend() throws IOException {

        this.client.sendMessage(this.roomId, textField1.getText());
        textField1.setText("");
    }

    public void updateHistory(String message) {
        this.textArea1.append(message);
    }

    public void onExit() throws IOException {
        this.client.exitRoom(this.roomId);
    }

    public void updateUserList(String[] params) {
        this.list1.setListData(params);
    }
}
