package jadwal.ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.event.*;

public class LoginDialog extends JDialog {
    private boolean loginSuccessful = false;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private static final String CREDENTIALS_FILE = "login_data.txt";

    public LoginDialog(Frame parent) {
        super(parent, "Login Aplikasi", true);
        initUI();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setSize(350, 200);
        setResizable(false);

        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("LOGIN APLIKASI JADWAL KULIAH");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtUsername = new JTextField(15);
        formPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        txtPassword = new JPasswordField(15);
        formPanel.add(txtPassword, gbc);

        // Info label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        JLabel infoLabel = new JLabel("Masukkan username dan password");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        infoLabel.setForeground(Color.GRAY);
        formPanel.add(infoLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnLogin = new JButton("Login");
        JButton btnCancel = new JButton("Keluar");

        btnLogin.addActionListener(e -> handleLogin());
        btnCancel.addActionListener(e -> handleCancel());

        // Enter key untuk login
        getRootPane().setDefaultButton(btnLogin);

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);

        // Set focus ke username field
        SwingUtilities.invokeLater(() -> txtUsername.requestFocus());
        // ====================================================
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Validasi input
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username dan Password tidak boleh kosong!",
                    "Validasi Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cek apakah file credentials sudah ada
        File file = new File(CREDENTIALS_FILE);

        if (!file.exists()) {
            // Pertama kali login, simpan credentials
            int choice = JOptionPane.showConfirmDialog(this,
                    "Anda login pertama kali.\n" +
                            "Simpan username dan password ini?\n" +
                            "(Data ini nantinya dapat diganti dengan mengedit file " + CREDENTIALS_FILE + ")",
                    "Konfirmasi Simpan",
                    JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                saveCredentials(username, password);
            }
            loginSuccessful = true;
        } else {
            // Cek credentials dari file
            if (checkCredentials(username, password)) {
                loginSuccessful = true;
            } else {
                JOptionPane.showMessageDialog(this,
                        "Username atau Password salah!",
                        "Login Gagal",
                        JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtPassword.requestFocus();
                return;
            }
        }

        dispose(); // Tutup dialog
    }

    private void handleCancel() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin keluar dari aplikasi?",
                "Konfirmasi Keluar",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void saveCredentials(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE))) {
            writer.write(username + "|" + password);
            JOptionPane.showMessageDialog(this,
                    "Credentials disimpan di file: " + CREDENTIALS_FILE,
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menyimpan credentials: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean checkCredentials(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CREDENTIALS_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    return parts[0].equals(username) && parts[1].equals(password);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading credentials: " + e.getMessage());
        }
        return false;
    }

    // =========== TAMBAHAN METHOD ===========
    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public String getUsername() {
        return txtUsername != null ? txtUsername.getText().trim() : "";
    }

    public String getPassword() {
        return txtPassword != null ? new String(txtPassword.getPassword()) : "";
    }
}
