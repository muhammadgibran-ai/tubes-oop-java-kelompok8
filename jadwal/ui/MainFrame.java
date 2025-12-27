package jadwal.ui;
import jadwal.model.*;
import jadwal.service.*;
import jadwal.exception.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
public class MainFrame extends JFrame {
    // SERVICES (BACKEND)
    private JadwalService jadwalService;
    private ReminderService reminderService;

    // GUI (DARI ANGGOTA GUI)
    private FormJadwalPanel formPanel;
    private KalenderPanel kalenderPanel;
    private JTabbedPane tabbedPane;

    // FILE HANDLING
    private static final String DATA_FILE = "jadwal_data.txt";

    // KONSTANTA JAM
    private static final LocalTime JAM_MINIMUM = LocalTime.of(8, 0);   // 08:00
    private static final LocalTime JAM_MAKSIMUM = LocalTime.of(21, 0); // 21:00

    // MAIN METHOD
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Tampilkan LoginDialog dulu
            LoginDialog loginDialog = new LoginDialog(null);
            loginDialog.setVisible(true);  // Modal - blokir sampai selesai

            // 2. Cek hasil login
            if (!loginDialog.isLoginSuccessful()) {
                return; // Keluar aplikasi jika gagal
            }

            // 3. Buka MainFrame hanya jika login sukses
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    // CONSTRUCTOR
    public MainFrame() {
        initServices();
        initUI();
        setupEventHandlers();
        loadDataFromFile();
        startReminderMonitoring();
        refreshTable();
    }

    private void initServices() {
        jadwalService = new JadwalService();
        reminderService = new ReminderService();
    }

    private void initUI() {
        setTitle("Aplikasi Pengelolaan Jadwal Kuliah");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Menu Bar
        createMenuBar();
        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Tab 1: Form Input
        formPanel = new FormJadwalPanel();
        int[] widths = {80, 120, 80, 150, 120, 50};
        formPanel.setTableColumnWidths(widths);
        tabbedPane.addTab("Form Input", formPanel);

        // Tab 2: Kalender View (BARU)
        kalenderPanel = new KalenderPanel();
        tabbedPane.addTab("Kalender View", kalenderPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Status Bar
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Simpan ke File");
        JMenuItem loadItem = new JMenuItem("Load dari File");
        JMenuItem exitItem = new JMenuItem("Keluar");

        saveItem.addActionListener(e -> saveDataToFile());
        loadItem.addActionListener(e -> loadDataFromFile());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void setupEventHandlers() {
        formPanel.setSimpanListener(e -> handleSimpan());
        formPanel.setEditListener(e -> handleEdit());
        formPanel.setHapusListener(e -> handleHapus());

        // =========== TAMBAH INI UNTUK FITUR KLIK TABEL ===========
        // Ketika tabel diklik, isi form dengan data yang dipilih
        formPanel.setTableSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = formPanel.getSelectedRowIndex();
                if (selectedRow != -1) {
                    isiFormDenganDataTabel(selectedRow);
                }
            }
        });
    }

    // =========== METHOD UNTUK ISI FORM DARI TABEL ===========
    private void isiFormDenganDataTabel(int rowIndex) {
        try {
            List<Jadwal> daftarJadwal = jadwalService.getDaftarJadwal();
            if (rowIndex >= 0 && rowIndex < daftarJadwal.size()) {
                Jadwal jadwal = daftarJadwal.get(rowIndex);
                MataKuliah mk = jadwal.getMataKuliah();

                // Isi form dengan data
                formPanel.setKode(mk.getKode());
                formPanel.setNama(mk.getNama());
                formPanel.setDosen(mk.getDosen());
                formPanel.setSks(mk.getSks());
                formPanel.setHari(jadwal.getHari());
                formPanel.setJamMulai(jadwal.getJamMulai().toString());
                formPanel.setJamSelesai(jadwal.getJamSelesai().toString());

                // Ganti teks tombol simpan jadi "Simpan Baru"
                formPanel.setSimpanButtonText("Simpan Baru");
            }
        } catch (Exception e) {
            System.err.println("Error mengisi form: " + e.getMessage());
        }
    }

    // =========== METHOD VALIDASI JAM ===========
    private boolean validasiJamKuliah(String jamMulaiStr, String jamSelesaiStr) {
        try {
            LocalTime jamMulai = parseTime(jamMulaiStr);
            LocalTime jamSelesai = parseTime(jamSelesaiStr);

            // Validasi format waktu (basic)
            if (jamMulai == null || jamSelesai == null) {
                formPanel.showMessageDialog(
                        "Format waktu salah! Gunakan HH:mm (contoh: 08:00)",
                        "Validasi Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return false;
            }

            // Validasi jam minimum (08:00)
            if (jamMulai.isBefore(JAM_MINIMUM)) {
                formPanel.showMessageDialog(
                        "Jam mulai tidak boleh sebelum 08:00!\n"
                        + "Jam yang diinput: " + jamMulaiStr + "\n"
                        + "Jam minimum: 08:00",
                        "Validasi Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return false;
            }

            if (jamSelesai.isBefore(JAM_MINIMUM)) {
                formPanel.showMessageDialog(
                        "Jam selesai tidak boleh sebelum 08:00!\n"
                        + "Jam yang diinput: " + jamSelesaiStr + "\n"
                        + "Jam minimum: 08:00",
                        "Validasi Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return false;
            }

            // Validasi jam maksimum (21:00)
            if (jamMulai.isAfter(JAM_MAKSIMUM)) {
                formPanel.showMessageDialog(
                        "Jam mulai tidak boleh lewat dari 21:00!\n"
                        + "Jam yang diinput: " + jamMulaiStr + "\n"
                        + "Jam maksimum: 21:00",
                        "Validasi Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return false;
            }

            if (jamSelesai.isAfter(JAM_MAKSIMUM)) {
                formPanel.showMessageDialog(
                        "Jam selesai tidak boleh lewat dari 21:00!\n"
                        + "Jam yang diinput: " + jamSelesaiStr + "\n"
                        + "Jam maksimum: 21:00",
                        "Validasi Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return false;
            }

            // Validasi jam selesai > jam mulai
            if (!jamSelesai.isAfter(jamMulai)) {
                formPanel.showMessageDialog(
                        "Jam selesai harus setelah jam mulai!\n"
                        + "Jam mulai: " + jamMulaiStr + "\n"
                        + "Jam selesai: " + jamSelesaiStr,
                        "Validasi Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return false;
            }

            // Validasi durasi minimal (misalnya minimal 1 jam)
            long durasiMenit = java.time.Duration.between(jamMulai, jamSelesai).toMinutes();
            if (durasiMenit < 60) {
                formPanel.showMessageDialog(
                        "Durasi kuliah minimal 1 jam (60 menit)!\n"
                        + "Durasi saat ini: " + durasiMenit + " menit",
                        "Validasi Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return false;
            }

            return true;

        } catch (DateTimeParseException e) {
            formPanel.showMessageDialog(
                    "Format waktu salah! Gunakan HH:mm (contoh: 08:00)",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        } catch (Exception e) {
            formPanel.showMessageDialog(
                    "Terjadi kesalahan validasi waktu: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    // =========== HANDLE SIMPAN ===========
    private void handleSimpan() {
        System.out.println("=== START: Handle Simpan ===");

        try {
            // TAHAP 1: USER INPUT DATA
            System.out.println("1. User input data mata kuliah...");

            String kode = formPanel.getKode();
            String nama = formPanel.getNama();
            String dosen = formPanel.getDosen();
            int sks = formPanel.getSks();
            String hari = formPanel.getHari();
            String jamMulaiStr = formPanel.getJamMulai();
            String jamSelesaiStr = formPanel.getJamSelesai();
            int menitReminder = formPanel.getMenitReminder();

            // VALIDASI INPUT DASAR
            if (kode.isEmpty() || nama.isEmpty() || dosen.isEmpty()) {
                formPanel.showMessageDialog(
                        "Kode, Nama, dan Dosen tidak boleh kosong!",
                        "Validasi Error",
                        JOptionPane.ERROR_MESSAGE
                );
                System.out.println("✗ Validasi gagal: Form kosong");
                return;
            }

            if (!formPanel.isTimeFormatValid()) {
                formPanel.showMessageDialog(
                        "Format waktu harus HH:mm (contoh: 08:00)",
                        "Validasi Error",
                        JOptionPane.ERROR_MESSAGE
                );
                System.out.println("✗ Validasi gagal: Format waktu salah");
                return;
            }

            // VALIDASI JAM KULIAH (08:00 - 21:00)
            if (!validasiJamKuliah(jamMulaiStr, jamSelesaiStr)) {
                System.out.println("✗ Validasi gagal: Range jam tidak valid");
                return;
            }

            // Parse waktu (setelah validasi)
            LocalTime jamMulai = parseTime(jamMulaiStr);
            LocalTime jamSelesai = parseTime(jamSelesaiStr);

            // TAHAP 2: BUAT OBJEK MATAKULIAH
            System.out.println("2. Buat objek MataKuliah...");
            MataKuliah mataKuliah = new MataKuliah(kode, nama, dosen, sks);

            // TAHAP 3: BUAT OBJEK JADWAL
            System.out.println("3. Buat objek Jadwal...");
            Jadwal jadwal = new Jadwal(hari, jamMulai, jamSelesai, mataKuliah);

            // TAHAP 4: CEK KONFLIK WAKTU
            System.out.println("4. Cek konflik waktu...");

            try {
                jadwalService.tambahJadwal(jadwal);
                System.out.println("   ✓ Tidak ada konflik waktu");
            } catch (JadwalBentrokException e) {
                // TAHAP 4a: KONFLIK ADA
                System.out.println("   ✗ Konflik ditemukan");
                formPanel.showMessageDialog(
                        "Jadwal Konflik!\n" + e.getMessage(),
                        "Konflik Jadwal",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // TAHAP 6: BUAT REMINDER
            System.out.println("6. Buat reminder...");
            Reminder reminder = new Reminder(jadwal, menitReminder);
            reminderService.tambahReminder(reminder);

            // TAHAP 7: UPDATE TAMPILAN
            System.out.println("7. Update tampilan...");
            refreshTable();
            formPanel.clearForm();
            formPanel.setSimpanButtonText("Simpan Jadwal");

            // TAHAP 8: SELESAI
            System.out.println("8. Selesai - Tampilkan pesan sukses");
            formPanel.showMessageDialog(
                    "Jadwal berhasil disimpan!\n"
                    + "Mata Kuliah: " + nama + "\n"
                    + "Hari: " + hari + "\n"
                    + "Jam: " + jamMulai + " - " + jamSelesai + "\n"
                    + "Reminder: " + menitReminder + " menit sebelum kuliah",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE
            );

            System.out.println("=== END: Handle Simpan (SUCCESS) ===\n");

        } catch (DateTimeParseException e) {
            System.out.println("✗ Error: Format waktu parsing");
            formPanel.showMessageDialog(
                    "Format waktu salah! Gunakan HH:mm (contoh: 08:00)",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
            formPanel.showMessageDialog(
                    "Terjadi kesalahan: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    // =========== HANDLE EDIT ===========
    private void handleEdit() {
        int selectedRow = formPanel.getSelectedRowIndex();
        if (selectedRow == -1) {
            formPanel.showMessageDialog(
                    "Pilih jadwal yang akan diedit di tabel!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            // Validasi dasar form
            if (!validateFormInput()) {
                return;
            }

            // VALIDASI JAM KULIAH (08:00 - 21:00)
            if (!validasiJamKuliah(formPanel.getJamMulai(), formPanel.getJamSelesai())) {
                return;
            }

            // Parse waktu (setelah validasi)
            LocalTime jamMulai = parseTime(formPanel.getJamMulai());
            LocalTime jamSelesai = parseTime(formPanel.getJamSelesai());

            // Ambil jadwal lama
            List<Jadwal> daftarJadwal = jadwalService.getDaftarJadwal();
            if (selectedRow >= daftarJadwal.size()) {
                formPanel.showMessageDialog("Data tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Jadwal jadwalLama = daftarJadwal.get(selectedRow);
            String idJadwalLama = jadwalLama.getId();

            // Buat objek baru
            MataKuliah mk = new MataKuliah(
                    formPanel.getKode(),
                    formPanel.getNama(),
                    formPanel.getDosen(),
                    formPanel.getSks()
            );

            Jadwal jadwalBaru = new Jadwal(
                    formPanel.getHari(),
                    jamMulai,
                    jamSelesai,
                    mk
            );

            // Cek konflik waktu untuk edit
            try {
                jadwalService.editJadwal(idJadwalLama, jadwalBaru);
            } catch (JadwalBentrokException e) {
                formPanel.showMessageDialog(
                        "Jadwal Konflik!\n" + e.getMessage(),
                        "Konflik Jadwal",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Update UI
            refreshTable();
            formPanel.clearForm();
            formPanel.setSimpanButtonText("Simpan Jadwal");

            formPanel.showMessageDialog(
                    "Jadwal berhasil diupdate!\n"
                    + "Note: Reminder mengikuti settingan jadwal terbaru",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (DateTimeParseException e) {
            formPanel.showMessageDialog(
                    "Format waktu salah! Gunakan HH:mm",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            formPanel.showMessageDialog(
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    // =========== HANDLE HAPUS ===========
    private void handleHapus() {
        int selectedRow = formPanel.getSelectedRowIndex();
        if (selectedRow == -1) {
            formPanel.showMessageDialog(
                    "Pilih jadwal yang akan dihapus di tabel!",
                    "Peringatan",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = formPanel.showConfirmDialog(
                "Apakah Anda yakin ingin menghapus jadwal ini?",
                "Konfirmasi Hapus"
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            List<Jadwal> daftarJadwal = jadwalService.getDaftarJadwal();
            if (selectedRow >= daftarJadwal.size()) {
                formPanel.showMessageDialog("Data tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Jadwal jadwal = daftarJadwal.get(selectedRow);
            String idJadwal = jadwal.getId();

            // 1. Hapus jadwal dari jadwalService
            jadwalService.hapusJadwal(idJadwal);

            // 2. ✅ PERBAIKAN INI: Hapus juga reminder-nya
            reminderService.hapusReminderByJadwalId(idJadwal);

            // 3. Update tampilan
            refreshTable();
            formPanel.clearForm();
            formPanel.setSimpanButtonText("Simpan Jadwal");

            // 4. Update pesan sukses
            formPanel.showMessageDialog(
                    "✅ Jadwal berhasil dihapus!\n"
                    + "Reminder terkait juga telah dinonaktifkan", // ✅ Pesan diperbaiki
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            formPanel.showMessageDialog(
                    "Error menghapus: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        }
    }

    // =========== VALIDASI ===========
    private boolean validateFormInput() {
        if (formPanel.isFormEmpty()) {
            formPanel.showMessageDialog(
                    "Kode, Nama, dan Dosen tidak boleh kosong!",
                    "Validasi Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        if (!formPanel.isTimeFormatValid()) {
            formPanel.showMessageDialog(
                    "Format waktu harus HH:mm (contoh: 08:00)",
                    "Validasi Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        return true;
    }

    // =========== HELPER METHODS ===========
    private LocalTime parseTime(String timeStr) throws DateTimeParseException {
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
    }

    private void refreshTable() {
        formPanel.clearTable();

        for (Jadwal jadwal : jadwalService.getDaftarJadwal()) {
            MataKuliah mk = jadwal.getMataKuliah();
            Object[] row = {
                jadwal.getHari(),
                jadwal.getJamMulai() + " - " + jadwal.getJamSelesai(),
                mk.getKode(),
                mk.getNama(),
                mk.getDosen(),
                mk.getSks()
            };
            formPanel.addRowToTable(row);
        }
        // Refresh kalender (BARU)
        kalenderPanel.tampilkanJadwal(jadwalService.getDaftarJadwal());

        updateStatusBar(jadwalService.getDaftarJadwal().size());
    }

    // =========== FILE HANDLING ===========
    private void saveDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            List<Jadwal> daftarJadwal = jadwalService.getDaftarJadwal();

            writer.write(String.valueOf(daftarJadwal.size()));
            writer.newLine();

            for (Jadwal jadwal : daftarJadwal) {
                MataKuliah mk = jadwal.getMataKuliah();

                writer.write(jadwal.getId() + "|"
                        + jadwal.getHari() + "|"
                        + jadwal.getJamMulai() + "|"
                        + jadwal.getJamSelesai() + "|"
                        + mk.getKode() + "|"
                        + mk.getNama() + "|"
                        + mk.getDosen() + "|"
                        + mk.getSks());
                writer.newLine();
            }

            formPanel.showMessageDialog(
                    "Data berhasil disimpan ke file!",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException e) {
            formPanel.showMessageDialog(
                    "Gagal menyimpan file: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadDataFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("File data tidak ditemukan, mulai dengan data kosong");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line = reader.readLine();
            if (line == null) {
                return;
            }

            int count = Integer.parseInt(line.trim());
            int loadedCount = 0;

            jadwalService = new JadwalService();
            reminderService = new ReminderService();

            for (int i = 0; i < count; i++) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }

                String[] parts = line.split("\\|");
                if (parts.length >= 8) {
                    try {
                        String hari = parts[1];
                        LocalTime jamMulai = LocalTime.parse(parts[2]);
                        LocalTime jamSelesai = LocalTime.parse(parts[3]);

                        MataKuliah mk = new MataKuliah(
                                parts[4],
                                parts[5],
                                parts[6],
                                Integer.parseInt(parts[7])
                        );

                        Jadwal jadwal = new Jadwal(hari, jamMulai, jamSelesai, mk);

                        jadwalService.tambahJadwal(jadwal);

                        Reminder reminder = new Reminder(jadwal, 15);
                        reminderService.tambahReminder(reminder);

                        loadedCount++;

                    } catch (Exception e) {
                        System.err.println("Error parsing line: " + e.getMessage());
                    }
                }
            }

            refreshTable();

            formPanel.showMessageDialog(
                    "Data berhasil dimuat: " + loadedCount + " jadwal",
                    "Sukses",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException e) {
            formPanel.showMessageDialog(
                    "Gagal memuat file: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (NumberFormatException e) {
            formPanel.showMessageDialog(
                    "Format file tidak valid!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========== REMINDER MONITORING ===========
    private void startReminderMonitoring() {
        System.out.println("=== START: Reminder Monitoring ===");
        System.out.println("1. Ambil daftar jadwal...");
        System.out.println("2. Mulai monitoring setiap 1 menit...");

        reminderService.mulaiMonitoring();
    }

    // =========== STATUS BAR ===========
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        JLabel statusLabel = new JLabel(" Status: Siap");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        JLabel countLabel = new JLabel("Jadwal: 0");
        countLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        countLabel.setName("countLabel");

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(countLabel, BorderLayout.EAST);

        return statusBar;
    }

    private void updateStatusBar(int count) {
        Component statusBar = getContentPane().getComponent(1);
        if (statusBar instanceof JPanel) {
            for (Component comp : ((JPanel) statusBar).getComponents()) {
                if (comp instanceof JLabel && "countLabel".equals(comp.getName())) {
                    ((JLabel) comp).setText("Jadwal: " + count);
                    break;
                }
            }
        }
    }
}