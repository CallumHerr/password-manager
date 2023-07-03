import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Generator extends JFrame {
    private JTextField nameField;
    private JPasswordField passField;
    private JButton genPassBtn;
    private JButton copyPasswordButton;
    private JButton saveChangesButton;
    private JCheckBox symbolsToggle;
    private JSpinner passSpinner;
    private JPanel panelMain;
    private JLabel errorTag;
    private JTextField platformField;

    public Generator(FileManager fileMan, Dashboard dashboard, int index) {
        Generator gui = this;
        PasswordRandom passGen = new PasswordRandom();

        gui.setContentPane(gui.panelMain);
        gui.setSize(400, 350);
        gui.symbolsToggle.setSelected(true);
        gui.passSpinner.setValue(32);
        gui.errorTag.setVisible(false);
        gui.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        gui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dashboard.open(fileMan);
                gui.dispose();
            }
        });

        if (index >= 0) {
            String[] info = fileMan.getAccounts().get(index);
            gui.platformField.setText(info[0]);
            gui.nameField.setText(info[1]);
            gui.passField.setText(info[2]);
        }
        gui.setVisible(true);

        genPassBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int length = (int) passSpinner.getValue();
                boolean symbolsAllowed = symbolsToggle.isSelected();
                String password = passGen.newPassword(length, symbolsAllowed);

                passField.setText(password);
            }
        });

        copyPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = new String(passField.getPassword());
                if (password.length() == 0) return;
                StringSelection passSelect = new StringSelection(password);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(passSelect, null);
            }
        });

        saveChangesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String platform = platformField.getText();
                String username = nameField.getText();
                String password = new String(passField.getPassword());
                if (username.length() == 0) {
                    errorTag.setText("Invalid Username");
                    errorTag.setVisible(true);
                    return;
                }
                if (password.length() == 0) {
                    errorTag.setText("Enter or Generate a password");
                    errorTag.setVisible(true);
                    return;
                }
                if (platform.length() == 0) {
                    errorTag.setText("Invalid Platform");
                    errorTag.setVisible(true);
                    return;
                }
                Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
                Matcher matcher = pattern.matcher(username);
                if (matcher.find()) {
                    errorTag.setText("Username cannot contain symbols");
                    errorTag.setVisible(true);
                    return;
                }
                Matcher platformMatcher = pattern.matcher(platform);
                if (platformMatcher.find()) {
                    errorTag.setText("Platforms cannot contain symbols");
                    errorTag.setVisible(true);
                    return;
                }

                boolean success = fileMan.addAccount(platform, username, password, index);
                if (!success) {
                    JOptionPane.showMessageDialog(gui, "Something went wrong while saving your account, try again or make an issue on github.");
                    return;
                }
                dashboard.open(fileMan);
                gui.dispose();
            }
        });
    }
}
