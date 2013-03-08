package client;

import javax.swing.*;

public class GUI_ChatAdmin extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JButton button1;
    private JList list1;
    private JCheckBox checkBox3;
    private JComboBox comboBox1;

    public GUI_ChatAdmin() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
    }
}
