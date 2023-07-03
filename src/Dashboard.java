import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.List;

public class Dashboard extends JFrame {
    private JPanel panelMain;
    private JButton addAccountButton;
    private JButton editAccountButton;
    private JButton closeBtn;
    private JButton copyPasswordButton;
    private JTable accountsTable;
    private JButton deleteAccountButton;
    private final LoginPage loginPage;

    public Dashboard() {
        Dashboard gui = this;
        FileManager fileManager = new FileManager();
        this.setEnabled(false);
        this.loginPage = new LoginPage(this, fileManager);

        addAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gui.setEnabled(false);
                Generator gen = new Generator(fileManager, gui, -1);
                gen.setTitle("Create Account");
            }
        });

        copyPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int accountIndex = accountsTable.getSelectedRow();
                if (accountIndex < 0) return;
                String password = fileManager.getAccounts().get(accountIndex)[2];
                StringSelection selection = new StringSelection(password);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, null);
            }
        });

        editAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int accountIndex = accountsTable.getSelectedRow();
                if (accountIndex < 0) return;
                gui.setEnabled(false);
                Generator gen = new Generator(fileManager, gui, accountIndex);
                gen.setTitle("Edit Account");
            }
        });

        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] columnNames = {"Platform", "Account Name"};
                DefaultTableModel model = new DefaultTableModel(columnNames, 0);
                accountsTable.setModel(model);
                gui.setEnabled(false);
                fileManager.closeFile();
                gui.openLogin();
            }
        });

        deleteAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int accountIndex = accountsTable.getSelectedRow();
                if (accountIndex < 0) return;

                JPanel panel = new JPanel();
                JLabel label = new JLabel("Are you sure you want to delete this account?");
                panel.add(label);
                int choice = JOptionPane.showConfirmDialog(
                        gui,
                        panel,
                        "Deletion Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (choice != 0) return;
                boolean success = fileManager.removeAccount(accountIndex);
                if (!success) {
                    JOptionPane.showMessageDialog(gui, "Something went wrong deleting the account, try again or create an issue on github.");
                }
                gui.open(fileManager);
            }
        });
    }

    public void openLogin() {
        this.loginPage.setVisible(true);
        this.loginPage.toFront();
        this.loginPage.requestFocus();
    }

    public void open(FileManager fileMan) {
        List<String[]> accounts = fileMan.getAccounts();
        Object[] columnNames = { "Platform", "Account Name"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        for (Object o : accounts) {
            String[] account = (String[]) o;
            String[] row = new String[]{account[0], account[1]};
            model.addRow(row);
        }
        accountsTable.setModel(model);
        this.setEnabled(true);
        this.toFront();
        this.requestFocus();
    }

    public static void main(String[] args) {
        Dashboard gui = new Dashboard();
        gui.setContentPane(gui.panelMain);
        gui.setTitle("Password Manager");
        gui.setSize(600, 500);
        gui.setVisible(true);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.openLogin();
    }
}
