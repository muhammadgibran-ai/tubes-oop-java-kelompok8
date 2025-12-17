package jadwal.ui;

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

    private JTable table;
    private DefaultTableModel tableModel;

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
    }
}