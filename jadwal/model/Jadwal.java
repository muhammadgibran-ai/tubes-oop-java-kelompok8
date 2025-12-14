package jadwal.model;

import java.time.LocalTime;
import java.util.UUID;

public class Jadwal {
    private String id;
    private String hari;
    private LocalTime jamMulai;
    private LocalTime jamSelesai;
    private MataKuliah mataKuliah;

    public Jadwal(String hari, LocalTime jamMulai,
                  LocalTime jamSelesai, MataKuliah mataKuliah) {
        this.id = UUID.randomUUID().toString();
        this.hari = hari;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.mataKuliah = mataKuliah;
    }

    public String getId() {
        return id;
    }

    public String getHari() {
        return hari;
    }

    public LocalTime getJamMulai() {
        return jamMulai;
    }

    public LocalTime getJamSelesai() {
        return jamSelesai;
    }

    public MataKuliah getMataKuliah() {
        return mataKuliah;
    }
}
