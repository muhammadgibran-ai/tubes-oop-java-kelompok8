package jadwal.ui;

feature/meliarosi
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import javax.swing.event.ListSelectionListener;

public class FormJadwalPanel extends JPanel {

    // =========== KOMPONEN GUI ===========
    private JTextField txtKode, txtNama, txtDosen;
    private JTextField txtJamMulai, txtJamSelesai;
    private JComboBox<String> cmbHari;
    private JSpinner spinnerSKS, spinnerMenitReminder;
    private JButton btnSimpan, btnEdit, btnHapus;

import jadwal.model.*;
import jadwal.service.*;
import jadwal.exception.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;

public class FormJadwalPanel extends JPanel {

    private JadwalService jadwalService;
    private ReminderService reminderService;

    private JTextField txtKode, txtNama, txtDosen;
    private JTextField txtJamMulai, txtJamSelesai;
    private JComboBox<String> cbHari;
main

    private JTable table;
    private DefaultTableModel tableModel;

  feature/meliarosi
    // =========== KONSTRUKTOR ===========
    public FormJadwalPanel() {
        initUI();
    }

    // =========== INITIALIZE UI ===========
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ================= FORM PANEL =================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Jadwal Kuliah"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Row 0: Kode Matkul
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Kode Mata Kuliah:"), gbc);
        gbc.gridx = 1;
        txtKode = new JTextField(15);
        formPanel.add(txtKode, gbc);

        // Row 1: Nama Matkul
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Nama Mata Kuliah:"), gbc);
        gbc.gridx = 1;
        txtNama = new JTextField(15);
        formPanel.add(txtNama, gbc);

        // Row 2: Dosen
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Dosen:"), gbc);
        gbc.gridx = 1;
        txtDosen = new JTextField(15);
        formPanel.add(txtDosen, gbc);

        // Row 3: SKS
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("SKS:"), gbc);
        gbc.gridx = 1;
        spinnerSKS = new JSpinner(new SpinnerNumberModel(2, 1, 6, 1));
        formPanel.add(spinnerSKS, gbc);

        // Row 4: Hari
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Hari:"), gbc);
        gbc.gridx = 1;
        cmbHari = new JComboBox<>(new String[]{"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"});
        formPanel.add(cmbHari, gbc);

        // Row 5: Jam Mulai
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Jam Mulai :"), gbc);
        gbc.gridx = 1;
        txtJamMulai = new JTextField("08:00", 8);
        formPanel.add(txtJamMulai, gbc);

        // Row 6: Jam Selesai
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Jam Selesai :"), gbc);
        gbc.gridx = 1;
        txtJamSelesai = new JTextField("10:00", 8);
        formPanel.add(txtJamSelesai, gbc);

        // Row 7: Reminder (menit sebelum)
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Reminder (menit sebelum):"), gbc);
        gbc.gridx = 1;
        spinnerMenitReminder = new JSpinner(new SpinnerNumberModel(15, 5, 120, 5));
        formPanel.add(spinnerMenitReminder, gbc);

        // Row 8: Button Simpan (KECIL)
        gbc.gridx = 0; gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        btnSimpan = createPastelGlossyButton("Simpan Jadwal",
                new Color(173, 216, 230)); // Light blue pastel
        formPanel.add(btnSimpan, gbc);

        add(formPanel, BorderLayout.NORTH);

        // ================= TABLE PANEL =================
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Daftar Jadwal Kuliah"));

        tableModel = new DefaultTableModel(
                new String[]{"Hari", "Jam", "Kode", "Mata Kuliah", "Dosen", "SKS"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Panel untuk tombol edit/hapus (KECIL)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        // Button Edit - Pastel Green Glossy
        btnEdit = createPastelGlossyButton("Edit",
                new Color(152, 251, 152)); // Light green pastel

        // Button Hapus - Pastel Pink Glossy
        btnHapus = createPastelGlossyButton("Hapus",
                new Color(255, 182, 193)); // Light pink pastel

        buttonPanel.add(btnEdit);
        buttonPanel.add(btnHapus);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.CENTER);
    }

    // =========== METHOD UNTUK BUAT PASTEL GLOSSY BUTTON ===========
    private JButton createPastelGlossyButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Warna berdasarkan state (SAMA PERSIS seperti sebelumnya)
                Color color = baseColor;
                if (getModel().isPressed()) {
                    color = color.darker();
                } else if (getModel().isRollover()) {
                    color = color.brighter();
                }

                // Background rounded (radius sama 10)
                g2.setColor(color);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                // Efek glossy shine (tambahan untuk efek glossy)
                GradientPaint shine = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 100),
                        0, getHeight()/2, new Color(255, 255, 255, 30)
                );
                g2.setPaint(shine);
                g2.fill(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()/2, 8, 8));

                // Border rounded tipis (sama 1.5f)
                g2.setColor(color.darker());
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0.75f, 0.75f, getWidth()-1.5f, getHeight()-1.5f, 10, 10));

                g2.dispose();

                // Paint text
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // No default border painting
            }
        };

        // Styling properties (SAMA PERSIS seperti sebelumnya)
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        // Text styling (font SAMA PERSIS 11)
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 12));

        // Padding kecil (SAMA PERSIS)
        button.setMargin(new Insets(5, 12, 5, 12));

        // Size KECIL (SAMA PERSIS)
        if (text.equals("Simpan Jadwal")) {
            button.setPreferredSize(new Dimension(120, 32));
        } else {
            button.setPreferredSize(new Dimension(80, 28));
        }

        return button;
    }

    // =========== GETTERS untuk FORM DATA ===========
    public String getKode() { return txtKode.getText().trim(); }
    public String getNama() { return txtNama.getText().trim(); }
    public String getDosen() { return txtDosen.getText().trim(); }
    public int getSks() { return (Integer) spinnerSKS.getValue(); }
    public String getHari() { return (String) cmbHari.getSelectedItem(); }
    public String getJamMulai() { return txtJamMulai.getText().trim(); }
    public String getJamSelesai() { return txtJamSelesai.getText().trim(); }
    public int getMenitReminder() { return (Integer) spinnerMenitReminder.getValue(); }

    // =========== SETTERS untuk FORM DATA ===========
    public void setKode(String kode) { txtKode.setText(kode); }
    public void setNama(String nama) { txtNama.setText(nama); }
    public void setDosen(String dosen) { txtDosen.setText(dosen); }
    public void setSks(int sks) { spinnerSKS.setValue(sks); }
    public void setHari(String hari) { cmbHari.setSelectedItem(hari); }
    public void setJamMulai(String jam) { txtJamMulai.setText(jam); }
    public void setJamSelesai(String jam) { txtJamSelesai.setText(jam); }
    public void setMenitReminder(int menit) { spinnerMenitReminder.setValue(menit); }

    // =========== METHODS untuk TABLE ===========
    public void clearTable() {
        tableModel.setRowCount(0);
    }

    public void addRowToTable(Object[] rowData) {
        tableModel.addRow(rowData);
    }

    public int getSelectedRowIndex() {
        return table.getSelectedRow();
    }

    public void clearTableSelection() {
        table.clearSelection();
    }

    public void setTableColumnIdentifiers(String[] columns) {
        tableModel.setColumnIdentifiers(columns);
    }

    public Object getTableValue(int row, int column) {
        return tableModel.getValueAt(row, column);
    }

    public void setTableColumnWidths(int[] widths) {
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    // =========== METHODS untuk BUTTON LISTENERS ===========
    public void setSimpanListener(ActionListener listener) {
        btnSimpan.addActionListener(listener);
    }

    public void setEditListener(ActionListener listener) {
        btnEdit.addActionListener(listener);
    }

    public void setHapusListener(ActionListener listener) {
        btnHapus.addActionListener(listener);
    }

    // =========== METHODS untuk FORM CONTROL ===========
    public void clearForm() {
        txtKode.setText("");
        txtNama.setText("");
        txtDosen.setText("");
        spinnerSKS.setValue(2);
        cmbHari.setSelectedIndex(0);
        txtJamMulai.setText("08:00");
        txtJamSelesai.setText("10:00");
        spinnerMenitReminder.setValue(15);
        table.clearSelection();
        btnSimpan.setText("Simpan Jadwal");
    }

    public void setSimpanButtonText(String text) {
        btnSimpan.setText(text);
    }

    public void showMessageDialog(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public int showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title,
                JOptionPane.YES_NO_OPTION);
    }

    // =========== VALIDATION METHODS (GUI-only validation) ===========
    public boolean isFormEmpty() {
        return getKode().isEmpty() ||
                getNama().isEmpty() ||
                getDosen().isEmpty();
    }

    public boolean isTimeFormatValid() {
        String timePattern = "\\d{2}:\\d{2}";
        return getJamMulai().matches(timePattern) &&
                getJamSelesai().matches(timePattern);
    }

    public void setTableSelectionListener(ListSelectionListener listener) {
        table.getSelectionModel().addListSelectionListener(listener);
    }

    public void setSelectedRow(int row) {
        if (row >= 0 && row < table.getRowCount()) {
            table.setRowSelectionInterval(row, row);
            table.scrollRectToVisible(table.getCellRect(row, 0, true));
        }
    }

    public void setFormDataFromRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= table.getRowCount()) {
            return;
        }

        try {
            String hari = table.getValueAt(rowIndex, 0).toString();
            String jamRange = table.getValueAt(rowIndex, 1).toString();
            String kode = table.getValueAt(rowIndex, 2).toString();
            String nama = table.getValueAt(rowIndex, 3).toString();
            String dosen = table.getValueAt(rowIndex, 4).toString();
            String sks = table.getValueAt(rowIndex, 5).toString();

            // Parse jam range
            if (jamRange.contains("-")) {
                String[] jamParts = jamRange.split("-");
                if (jamParts.length == 2) {
                    txtJamMulai.setText(jamParts[0].trim());
                    txtJamSelesai.setText(jamParts[1].trim());
                }
            }

            txtKode.setText(kode);
            txtNama.setText(nama);
            txtDosen.setText(dosen);
            spinnerSKS.setValue(Integer.parseInt(sks));
            cmbHari.setSelectedItem(hari);
            spinnerMenitReminder.setValue(15);

        } catch (Exception e) {
            System.err.println("Error setting form data: " + e.getMessage());
        }
    }

    public int getTableRowCount() {
        return table.getRowCount();
      
    private String selectedId = null; // ID jadwal yg sedang diedit

    public FormJadwalPanel(JadwalService js, ReminderService rs) {
        this.jadwalService = js;
        this.reminderService = rs;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // ================= FORM =================
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));

        txtKode = new JTextField();
        txtNama = new JTextField();
        txtDosen = new JTextField();
        txtJamMulai = new JTextField("08:00");
        txtJamSelesai = new JTextField("10:00");

        cbHari = new JComboBox<>(new String[]{
                "Senin", "Selasa", "Rabu", "Kamis", "Jumat"
        });

        JButton btnTambah = new JButton("Tambah Jadwal");
        JButton btnUpdate = new JButton("Update Jadwal");
        JButton btnHapus = new JButton("Hapus Jadwal");

        form.add(new JLabel("Kode MK"));
        form.add(txtKode);
        form.add(new JLabel("Nama MK"));
        form.add(txtNama);
        form.add(new JLabel("Dosen"));
        form.add(txtDosen);
        form.add(new JLabel("Hari"));
        form.add(cbHari);
        form.add(new JLabel("Jam Mulai"));
        form.add(txtJamMulai);
        form.add(new JLabel("Jam Selesai"));
        form.add(txtJamSelesai);

        form.add(btnTambah);
        form.add(btnUpdate);

        add(form, BorderLayout.NORTH);

        // ================= TABLE =================
        tableModel = new DefaultTableModel(
    new String[]{"ID", "Hari", "Jam", "Mata Kuliah", "Dosen"}, 0
);

        table = new JTable(tableModel);
        table.removeColumn(table.getColumnModel().getColumn(0)); // sembunyi ID

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnHapus, BorderLayout.SOUTH);

        // ================= EVENT =================
        btnTambah.addActionListener(e -> tambahJadwal());
        btnUpdate.addActionListener(e -> updateJadwal());
        btnHapus.addActionListener(e -> hapusJadwal());

        table.getSelectionModel().addListSelectionListener(e -> isiFormDariTabel());
    }

    // ================= TAMBAH =================
    private void tambahJadwal() {
        try {
            Jadwal j = buatJadwalDariForm();

            jadwalService.tambahJadwal(j);
            reminderService.tambahReminder(new Reminder(j, 15));

            tableModel.addRow(new Object[]{
    j.getId(),
    j.getHari(),
    j.getJamMulai() + " - " + j.getJamSelesai(),
    j.getMataKuliah().getNama(),
    j.getMataKuliah().getDosen()
});


            clearForm();
            JOptionPane.showMessageDialog(this, "Jadwal ditambahkan");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ================= UPDATE =================
    private void updateJadwal() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Pilih jadwal di tabel dulu");
            return;
        }

        try {
            Jadwal baru = buatJadwalDariForm();
            jadwalService.editJadwal(selectedId, baru);

            int row = table.getSelectedRow();
            tableModel.setValueAt(baru.getHari(), row, 1);
            tableModel.setValueAt(
            baru.getJamMulai() + " - " + baru.getJamSelesai(), row, 2);
            tableModel.setValueAt(
            baru.getMataKuliah().getNama(), row, 3);
            tableModel.setValueAt(
            baru.getMataKuliah().getDosen(), row, 4);


            clearForm();
            selectedId = null;

            JOptionPane.showMessageDialog(this, "Jadwal berhasil diupdate");

        } catch (JadwalBentrokException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // ================= HAPUS =================
    private void hapusJadwal() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String id = (String) tableModel.getValueAt(row, 0);
        jadwalService.hapusJadwal(id);
        tableModel.removeRow(row);

        clearForm();
        selectedId = null;
    }

    // ================= BANTUAN =================
    private Jadwal buatJadwalDariForm() {
        MataKuliah mk = new MataKuliah(
                txtKode.getText(),
                txtNama.getText(),
                txtDosen.getText(),
                3
        );

        return new Jadwal(
                cbHari.getSelectedItem().toString(),
                LocalTime.parse(txtJamMulai.getText()),
                LocalTime.parse(txtJamSelesai.getText()),
                mk
        );
    }

private void isiFormDariTabel() {
    int row = table.getSelectedRow();
    if (row == -1) return;

    selectedId = (String) tableModel.getValueAt(row, 0);

    cbHari.setSelectedItem((String) tableModel.getValueAt(row, 1));

    String jam = (String) tableModel.getValueAt(row, 2);
    String[] splitJam = jam.split(" - ");
    txtJamMulai.setText(splitJam[0]);
    txtJamSelesai.setText(splitJam[1]);

    txtNama.setText((String) tableModel.getValueAt(row, 3));
    txtDosen.setText((String) tableModel.getValueAt(row, 4));
}


    private void clearForm() {
        txtKode.setText("");
        txtNama.setText("");
        txtDosen.setText("");
        txtJamMulai.setText("08:00");
        txtJamSelesai.setText("10:00");
main
    }
}