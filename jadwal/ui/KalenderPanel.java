package jadwal.ui;

import jadwal.model.Jadwal;
import jadwal.model.MataKuliah;
import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class KalenderPanel extends JPanel {

    private List<Jadwal> daftarJadwal;
    private Map<String, Color> warnaMatkul;

    // Konfigurasi grid
    private static final String[] HARI = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
    private static final int JAM_MULAI = 8;  // 07:00
    private static final int JAM_SELESAI = 21; // 21:00
    private static final int TOTAL_JAM = JAM_SELESAI - JAM_MULAI;

    public KalenderPanel() {
        warnaMatkul = new HashMap<>();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("KALENDER JADWAL KULIAH", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Scroll pane untuk kalender
        JScrollPane scrollPane = new JScrollPane(createCalendarGrid());
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(scrollPane, BorderLayout.CENTER);

        // Legend panel
        add(createLegendPanel(), BorderLayout.SOUTH);
    }

    private JPanel createCalendarGrid() {
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(1, 1, 1, 1);

        // =========== HEADER ROW ===========
        // Cell kosong kiri atas
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridheight = 2;
        JLabel emptyLabel = createCellLabel("", new Color(240, 240, 240), true);
        gridPanel.add(emptyLabel, gbc);

        // Header hari
        for (int i = 0; i < HARI.length; i++) {
            gbc.gridx = i + 1;
            gbc.gridy = 0;
            gbc.gridheight = 2;
            JLabel hariLabel = createCellLabel(HARI[i], new Color(200, 220, 240), true);
            hariLabel.setFont(new Font("Arial", Font.BOLD, 12));
            gridPanel.add(hariLabel, gbc);
        }

        // =========== TIME COLUMN ===========
        gbc.gridheight = 1;
        for (int jam = 0; jam < TOTAL_JAM; jam++) {
            int jamAktual = JAM_MULAI + jam;

            // Label jam (setiap jam)
            gbc.gridx = 0;
            gbc.gridy = jam * 2 + 2;
            String waktu = String.format("%02d:00", jamAktual);
            JLabel timeLabel = createCellLabel(waktu, new Color(245, 245, 245), false);
            timeLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
            gridPanel.add(timeLabel, gbc);

            // Cell separator (garis tipis)
            gbc.gridy = jam * 2 + 3;
            JLabel separator = createCellLabel("", new Color(230, 230, 230), false);
            separator.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
            gridPanel.add(separator, gbc);
        }

        // =========== KOLOM HARI ===========
        for (int hariIdx = 0; hariIdx < HARI.length; hariIdx++) {
            for (int jam = 0; jam < TOTAL_JAM; jam++) {
                // Cell untuk jadwal (atas)
                gbc.gridx = hariIdx + 1;
                gbc.gridy = jam * 2 + 2;
                JLabel cellAtas = createCellLabel("", Color.WHITE, false);
                gridPanel.add(cellAtas, gbc);

                // Cell separator (bawah)
                gbc.gridy = jam * 2 + 3;
                JLabel cellBawah = createCellLabel("", Color.WHITE, false);
                cellBawah.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
                gridPanel.add(cellBawah, gbc);
            }
        }

        // Set preferred size
        gridPanel.setPreferredSize(new Dimension(800, TOTAL_JAM * 60 + 50));

        return gridPanel;
    }

    private JLabel createCellLabel(String text, Color background, boolean isHeader) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(background);

        if (isHeader) {
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            label.setFont(new Font("Arial", Font.BOLD, 12));
        } else {
            label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            label.setFont(new Font("Arial", Font.PLAIN, 10));
        }

        return label;
    }

    public void tampilkanJadwal(List<Jadwal> jadwalList) {
        this.daftarJadwal = jadwalList;
        warnaMatkul.clear();

        // Hapus komponen lama
        removeAll();

        // Re-initialize UI
        initUI();

        // Isi jadwal ke kalender
        isiJadwalKeGrid();

        // Refresh tampilan
        revalidate();
        repaint();
    }

    private void isiJadwalKeGrid() {
        if (daftarJadwal == null || daftarJadwal.isEmpty()) {
            return;
        }

        JPanel gridPanel = (JPanel) ((JScrollPane) getComponent(1)).getViewport().getView();
        GridBagLayout layout = (GridBagLayout) gridPanel.getLayout();

        for (Jadwal jadwal : daftarJadwal) {
            // Cari index hari
            int hariIdx = -1;
            for (int i = 0; i < HARI.length; i++) {
                if (HARI[i].equalsIgnoreCase(jadwal.getHari())) {
                    hariIdx = i;
                    break;
                }
            }

            if (hariIdx == -1) continue;

            // Parse waktu
            LocalTime mulai = jadwal.getJamMulai();
            LocalTime selesai = jadwal.getJamSelesai();

            // Konversi ke grid position
            int startHour = mulai.getHour();
            int startMinute = mulai.getMinute();
            int endHour = selesai.getHour();
            int endMinute = selesai.getMinute();

            // Validasi jam dalam range
            if (startHour < JAM_MULAI || endHour > JAM_SELESAI) continue;

            // Hitung posisi grid
            int startGridY = (startHour - JAM_MULAI) * 2 + 2;
            if (startMinute >= 30) startGridY++; // Setengah jam

            int durationInHalfHours = ((endHour - startHour) * 2) +
                    (endMinute >= 30 ? 1 : 0) -
                    (startMinute >= 30 ? 1 : 0);

            // Dapatkan warna untuk mata kuliah
            Color warna = getColorForMatkul(jadwal.getMataKuliah());

            // Buat label untuk jadwal
            String displayText = "<html><center><b>" +
                    jadwal.getMataKuliah().getNama() +
                    "</b><br>" +
                    mulai.toString() + " - " + selesai.toString() +
                    "</center></html>";

            JLabel jadwalLabel = new JLabel(displayText, SwingConstants.CENTER);
            jadwalLabel.setOpaque(true);
            jadwalLabel.setBackground(warna);
            jadwalLabel.setForeground(Color.WHITE);
            jadwalLabel.setBorder(BorderFactory.createLineBorder(warna.darker(), 1));
            jadwalLabel.setFont(new Font("Arial", Font.BOLD, 10));

            // Set tooltip dengan detail lengkap
            jadwalLabel.setToolTipText(createTooltipText(jadwal));

            // Atur posisi di grid
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = hariIdx + 1;
            gbc.gridy = startGridY;
            gbc.gridheight = Math.max(1, durationInHalfHours);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.insets = new Insets(1, 1, 1, 1);

            // Tambah ke grid
            gridPanel.add(jadwalLabel, gbc, 0);
        }
    }

    private Color getColorForMatkul(MataKuliah mk) {
        String key = mk.getKode();
        if (!warnaMatkul.containsKey(key)) {
            // Generate warna berdasarkan hash kode matkul
            int hash = key.hashCode();
            int r = Math.abs(hash % 180) + 50;   // 50-230
            int g = Math.abs((hash / 100) % 180) + 50;
            int b = Math.abs((hash / 10000) % 180) + 50;
            warnaMatkul.put(key, new Color(r, g, b));
        }
        return warnaMatkul.get(key);
    }

    private String createTooltipText(Jadwal jadwal) {
        MataKuliah mk = jadwal.getMataKuliah();
        return "<html><b>" + mk.getNama() + " (" + mk.getKode() + ")</b><br>" +
                "Dosen: " + mk.getDosen() + "<br>" +
                "SKS: " + mk.getSks() + "<br>" +
                "Hari: " + jadwal.getHari() + "<br>" +
                "Waktu: " + jadwal.getJamMulai() + " - " + jadwal.getJamSelesai() +
                "</html>";
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        legendPanel.setBorder(BorderFactory.createTitledBorder("Keterangan Warna Mata Kuliah"));

        if (daftarJadwal != null) {
            for (Jadwal jadwal : daftarJadwal) {
                MataKuliah mk = jadwal.getMataKuliah();
                String key = mk.getKode();

                // Cegah duplikat legend
                boolean sudahAda = false;
                for (Component comp : legendPanel.getComponents()) {
                    if (comp instanceof JLabel) {
                        JLabel label = (JLabel) comp;
                        if (label.getText().contains(mk.getKode())) {
                            sudahAda = true;
                            break;
                        }
                    }
                }

                if (!sudahAda) {
                    Color warna = getColorForMatkul(mk);

                    JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

                    // Color box
                    JLabel colorBox = new JLabel("   ");
                    colorBox.setOpaque(true);
                    colorBox.setBackground(warna);
                    colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                    colorBox.setPreferredSize(new Dimension(20, 15));

                    // Text label
                    JLabel textLabel = new JLabel(mk.getKode() + " - " + mk.getNama());
                    textLabel.setFont(new Font("Arial", Font.PLAIN, 10));

                    itemPanel.add(colorBox);
                    itemPanel.add(textLabel);
                    legendPanel.add(itemPanel);
                }
            }
        }

        // Jika tidak ada jadwal
        if (legendPanel.getComponentCount() == 0) {
            JLabel emptyLabel = new JLabel("Belum ada jadwal");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            emptyLabel.setForeground(Color.GRAY);
            legendPanel.add(emptyLabel);
        }

        return legendPanel;
    }
}