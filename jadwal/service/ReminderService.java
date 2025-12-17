package jadwal.service;

import jadwal.model.Reminder;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Timer;


public class ReminderService {

    private List<Reminder> daftarReminder = new ArrayList<>();
    private Timer timer; // 🔒 SIMPAN TIMER

    public void tambahReminder(Reminder r) {
        daftarReminder.add(r);
    }

    public void mulaiMonitoring() {

        if (timer != null) return; // 🔒 CEGAH TIMER DOBEL

        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                cekReminder();
            }
        }, 60000, 60000); // ⏱ delay 1 menit
    }


private void cekReminder() {
    LocalDateTime sekarang = LocalDateTime.now();

    for (Reminder r : daftarReminder) {

        if (r.isSudahDitampilkan()) continue;

        if (sekarang.isAfter(r.getWaktuReminder())
            && sekarang.toLocalDate().equals(
                   r.getWaktuReminder().toLocalDate()
               )) {

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                    null,
                    "⏰ Jadwal Kuliah\n"
                    + r.getJadwal().getMataKuliah().getNama()
                    + "\nDosen: "
                    + r.getJadwal().getMataKuliah().getDosen()
                );
            });

            r.setSudahDitampilkan(true);
        }
    }
}
public void hapusReminderByJadwalId(String jadwalId) {
        if (jadwalId == null || jadwalId.isEmpty()) return;
        
        // Hapus semua reminder yang terkait dengan jadwal ini
        daftarReminder.removeIf(reminder -> 
            reminder.getJadwal().getId().equals(jadwalId)
        );
        
        System.out.println("✅ Reminder untuk jadwal " + jadwalId + " dihapus");
    }
}
