package jadwal.service;

import jadwal.model.Jadwal;
import jadwal.exception.JadwalBentrokException;
import java.util.*;

public class JadwalService {

    private List<Jadwal> daftarJadwal = new ArrayList<>();

    public void tambahJadwal(Jadwal jadwal) throws JadwalBentrokException {
    // ID yang diabaikan adalah null karena ini jadwal baru
    if (cekKonflik(jadwal, null)) { 
        throw new JadwalBentrokException("Jadwal bentrok dengan jadwal lain");
    }
    daftarJadwal.add(jadwal);
}

public void editJadwal(String id, Jadwal jadwalBaru)
        throws JadwalBentrokException {

    Jadwal lama = cariById(id);
    if (lama == null) return;

    // 1. Cek Konflik, abaikan jadwal yang sedang diedit (lama.getId())
    if (cekKonflik(jadwalBaru, lama.getId())) { 
        throw new JadwalBentrokException("Jadwal bentrok setelah diedit");
    }

    // 2. Jika tidak ada konflik, hapus yang lama
    daftarJadwal.remove(lama);

    // 3. Tambahkan yang baru
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
    return Collections.unmodifiableList(daftarJadwal);
}
}
