package jadwal.model;

import java.time.*;

public class Reminder {

    private Jadwal jadwal;
    private LocalDateTime waktuReminder;
    private boolean sudahDitampilkan;

    public Reminder(Jadwal jadwal, int menitSebelum) {
        this.jadwal = jadwal;

        LocalDate tanggalKuliah = hitungTanggalBerikutnya(
                jadwal.getHari()
        );

        this.waktuReminder = LocalDateTime.of(
                tanggalKuliah,
                jadwal.getJamMulai()
        ).minusMinutes(menitSebelum);

        if (waktuReminder.isBefore(LocalDateTime.now())) {
    waktuReminder = waktuReminder.plusWeeks(1);
}

    }

    private LocalDate hitungTanggalBerikutnya(String hari) {
    DayOfWeek target;

    switch (hari.toLowerCase()) {
        case "senin":
            target = DayOfWeek.MONDAY;
            break;
        case "selasa":
            target = DayOfWeek.TUESDAY;
            break;
        case "rabu":
            target = DayOfWeek.WEDNESDAY;
            break;
        case "kamis":
            target = DayOfWeek.THURSDAY;
            break;
        case "jumat":
            target = DayOfWeek.FRIDAY;
            break;
        case "sabtu":
            target = DayOfWeek.SATURDAY;
            break;
        case "minggu":
            target = DayOfWeek.SUNDAY;
            break;
        default:
            throw new IllegalArgumentException("Hari tidak valid: " + hari);
    }

    LocalDate today = LocalDate.now();
    while (today.getDayOfWeek() != target) {
        today = today.plusDays(1);
    }
    return today;
}


    public Jadwal getJadwal() { return jadwal; }
    public LocalDateTime getWaktuReminder() { return waktuReminder; }
    public boolean isSudahDitampilkan() { return sudahDitampilkan; }
    public void setSudahDitampilkan(boolean status) {
        this.sudahDitampilkan = status;
    }

    
}
