package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI_MessageBox extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel message_text;

    public GUI_MessageBox(String message, final boolean isDeathWindow) {
        this.message_text.setText(message);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK(isDeathWindow);
            }
        });
    }

    private void onOK(boolean isdeathWindow) {
        if (isdeathWindow)
            System.exit(0);
        dispose();
    }
}
