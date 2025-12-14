package jadwal.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reminder {
    private Jadwal jadwal;
    private LocalDateTime waktuReminder;
    private boolean sudahDitampilkan;

    public Reminder(Jadwal jadwal, int menitSebelum) {
        this.jadwal = jadwal;

        // Asumsi reminder untuk hari ini
        LocalDate hariIni = LocalDate.now();

        this.waktuReminder = LocalDateTime.of(
                hariIni,
                jadwal.getJamMulai()
        ).minusMinutes(menitSebelum);

        this.sudahDitampilkan = false;
    }

    public Jadwal getJadwal() {
        return jadwal;
    }

    public LocalDateTime getWaktuReminder() {
        return waktuReminder;
    }

    public boolean isSudahDitampilkan() {
        return sudahDitampilkan;
    }

    public void setSudahDitampilkan(boolean status) {
        this.sudahDitampilkan = status;
    }
}
