package jadwal.service;

import jadwal.model.Reminder;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Timer;


public class ReminderService {

    private List<Reminder> daftarReminder = new ArrayList<>();

    public void tambahReminder(Reminder r) {
        daftarReminder.add(r);
    }

    public void mulaiMonitoring() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                cekReminder();
            }
        }, 0, 60000);
    }

    private void cekReminder() {
        LocalDateTime sekarang = LocalDateTime.now();

        for (Reminder r : daftarReminder) {
            if (!r.isSudahDitampilkan()
                    && !sekarang.isBefore(r.getWaktuReminder())) {

                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(
                                null,
                                "Reminder:\n"
                                + r.getJadwal().getMataKuliah().getNama()
                                + "\nHari: " + r.getJadwal().getHari()
                        )
                );

                r.setSudahDitampilkan(true);
            }
        }
    }
}
