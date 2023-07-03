import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class LoginPage extends JFrame {
    private JTextField fileLocalTxt;
    private JButton selectButton;
    private JButton createNewButton;
    private JPasswordField passField;
    private JButton loginButton;
    private JLabel invFileTag;
    private JLabel invPassTag;
    private JPanel panelMain;

    public LoginPage(Dashboard dashboard, FileManager fileMan) {
        LoginPage gui = this;

        gui.setContentPane(gui.panelMain);
        gui.invFileTag.setVisible(false);
        gui.invPassTag.setVisible(false);
        gui.setTitle("Login");
        gui.setSize(400, 350);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int choice = fileChooser.showSaveDialog(gui);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    fileMan.setFile(fileChooser.getSelectedFile());
                    fileLocalTxt.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    gui.invFileTag.setVisible(false);
                    return;
                } else if (fileMan.hasFile()){
                    gui.invFileTag.setVisible(false);
                    return;
                }

                gui.invFileTag.setVisible(true);
            }
        });

        createNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File("database.dat"));
                int choice = fileChooser.showSaveDialog(gui);
                if (choice != JFileChooser.APPROVE_OPTION) {
                    gui.invFileTag.setVisible(true);
                    return;
                }

                fileLocalTxt.setText(fileChooser.getSelectedFile().getAbsolutePath());
                gui.invFileTag.setVisible(false);

                JPanel panel = new JPanel();
                JLabel label = new JLabel("Set Password");
                JPasswordField pass = new JPasswordField(32);
                panel.add(label);
                panel.add(pass);
                String[] options = new String[]{"OK", "Cancel"};
                int option = JOptionPane.showOptionDialog(
                        gui,
                        panel,
                        "Set Password",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        options,
                        options[1]
                );

                if (option == 1 || pass.getPassword().length == 0) {
                    gui.invFileTag.setVisible(true);
                    return;
                }

                boolean success = fileMan.setFile(fileChooser.getSelectedFile());
                if (!success) {
                    gui.invFileTag.setVisible(true);
                    JOptionPane.showMessageDialog(gui, "Something went wrong creating the file, try running as administrator.");
                    return;
                }
                fileMan.genKey(new String(pass.getPassword()), gui);
                gui.setVisible(false);
                dashboard.open(fileMan);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fileLocalTxt.getText().length() == 0) {
                    gui.invFileTag.setVisible(true);
                    return;
                }
                String password = new String(passField.getPassword());

                fileMan.genKey(password, gui);
                boolean success = fileMan.decrypt();
                if (!success) {
                    gui.invPassTag.setVisible(true);
                } else {
                    gui.invPassTag.setVisible(false);
                    gui.setVisible(false);
                    dashboard.open(fileMan);
                }
            }
        });

    }
}
