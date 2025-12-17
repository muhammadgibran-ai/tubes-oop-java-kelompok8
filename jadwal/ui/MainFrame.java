package jadwal.ui;

import jadwal.service.*;

import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        JadwalService js = new JadwalService();
        ReminderService rs = new ReminderService();

        setTitle("Aplikasi Jadwal Kuliah");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new FormJadwalPanel(js, rs));
        rs.mulaiMonitoring();
    }
}