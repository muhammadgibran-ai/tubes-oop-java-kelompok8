package jadwal.service;

import jadwal.model.Jadwal;
import jadwal.exception.JadwalBentrokException;
import java.util.*;

public class JadwalService {

    private List<Jadwal> daftarJadwal = new ArrayList<>();

    private Jadwal cariById(String id) {
        for (Jadwal j : daftarJadwal) {
            if (j.getId().equals(id)) {
                return j;
            }
        }
        return null;
    }

    /**
     * Cari jadwal yang bentrok dengan jadwal baru
     *
     * @param jadwalBaru Jadwal baru yang akan dicek
     * @param ignoreId ID jadwal yang diabaikan (untuk edit)
     * @return Jadwal yang bentrok, atau null jika tidak ada bentrok
     */
    private Jadwal cariJadwalBentrok(Jadwal jadwalBaru, String ignoreId) {
        for (Jadwal j : daftarJadwal) {
            if (ignoreId != null && j.getId().equals(ignoreId)) {
                continue;
            }

            if (j.getHari().equalsIgnoreCase(jadwalBaru.getHari())) {
                boolean bentrok = !(jadwalBaru.getJamSelesai().isBefore(j.getJamMulai())
                        || jadwalBaru.getJamMulai().isAfter(j.getJamSelesai()));

                if (bentrok) {
                    return j; // Return jadwal yang bentrok
                }
            }
        }
        return null; // Tidak ada bentrok
    }

    private boolean cekKonflik(Jadwal baru, String ignoreId) {
        return cariJadwalBentrok(baru, ignoreId) != null;
    }

    public void tambahJadwal(Jadwal jadwal) throws JadwalBentrokException {
        // Cari jadwal yang bentrok
        Jadwal jadwalBentrok = cariJadwalBentrok(jadwal, null);
        if (jadwalBentrok != null) {
            throw new JadwalBentrokException(
                    jadwalBentrok.getHari(),
                    jadwalBentrok.getJamMulai().toString(),
                    jadwalBentrok.getJamSelesai().toString(),
                    jadwalBentrok.getMataKuliah().getKode(),
                    jadwalBentrok.getMataKuliah().getNama()
            );
        }
        daftarJadwal.add(jadwal);
    }

    public void editJadwal(String id, Jadwal jadwalBaru) throws JadwalBentrokException {
        Jadwal lama = cariById(id);
        if (lama == null) {
            return;
        }

        // 1. Cari jadwal yang bentrok (abaikan jadwal lama)
        Jadwal jadwalBentrok = cariJadwalBentrok(jadwalBaru, lama.getId());
        if (jadwalBentrok != null) {
            throw new JadwalBentrokException(
                    jadwalBentrok.getHari(),
                    jadwalBentrok.getJamMulai().toString(),
                    jadwalBentrok.getJamSelesai().toString(),
                    jadwalBentrok.getMataKuliah().getKode(),
                    jadwalBentrok.getMataKuliah().getNama()
            );
        }

        // 2. Jika tidak ada konflik, hapus yang lama
        daftarJadwal.remove(lama);

        // 3. Tambahkan yang baru
        daftarJadwal.add(jadwalBaru);
    }
// Di JadwalService.java yang dikirim:

    public void hapusJadwal(String id) {
        Jadwal j = cariById(id);
        if (j != null) {
            daftarJadwal.remove(j);
        }
    }

    public List<Jadwal> getDaftarJadwal() {
        return Collections.unmodifiableList(daftarJadwal);
    }
}