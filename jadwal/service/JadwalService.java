package jadwal.service;

import jadwal.model.Jadwal;
import jadwal.exception.JadwalBentrokException;
import java.util.*;

public class JadwalService {

    private List<Jadwal> daftarJadwal = new ArrayList<>();

    public void tambahJadwal(Jadwal jadwal) throws JadwalBentrokException {
        if (cekKonflik(jadwal, null)) {
            throw new JadwalBentrokException("Jadwal bentrok dengan jadwal lain");
        }
        daftarJadwal.add(jadwal);
    }

    public void editJadwal(String id, Jadwal jadwalBaru)
            throws JadwalBentrokException {

        Jadwal lama = cariById(id);
        if (lama == null) return;

        daftarJadwal.remove(lama);

        if (cekKonflik(jadwalBaru, null)) {
            daftarJadwal.add(lama); // rollback manual
            throw new JadwalBentrokException("Jadwal bentrok setelah diedit");
        }

        daftarJadwal.add(jadwalBaru);
    }

    public void hapusJadwal(String id) {
        Jadwal j = cariById(id);
        if (j != null) {
            daftarJadwal.remove(j);
        }
    }

    private Jadwal cariById(String id) {
        for (Jadwal j : daftarJadwal) {
            if (j.getId().equals(id)) {
                return j;
            }
        }
        return null;
    }

    private boolean cekKonflik(Jadwal baru, String ignoreId) {
        for (Jadwal j : daftarJadwal) {
            if (ignoreId != null && j.getId().equals(ignoreId)) continue;

            if (j.getHari().equalsIgnoreCase(baru.getHari())) {
                boolean bentrok =
                        !(baru.getJamSelesai().isBefore(j.getJamMulai()) ||
                          baru.getJamMulai().isAfter(j.getJamSelesai()));
                if (bentrok) return true;
            }
        }
        return false;
    }

    public List<Jadwal> getDaftarJadwal() {
        return daftarJadwal;
    }
}
