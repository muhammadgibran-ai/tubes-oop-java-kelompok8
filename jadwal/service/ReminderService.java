package jadwal.service;

import jadwal.model.Reminder;
import java.time.LocalDateTime;
import java.util.*;

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
        }, 0, 60000); // tiap 1 menit
    }

    private void cekReminder() {
        LocalDateTime sekarang = LocalDateTime.now();

        for (Reminder r : daftarReminder) {
            if (!r.isSudahDitampilkan()
                    && !sekarang.isBefore(r.getWaktuReminder())) {

                System.out.println(
                        "🔔 Reminder: "
                        + r.getJadwal().getMataKuliah().getNama()
                );
                r.setSudahDitampilkan(true);
            }
        }
    }
}
