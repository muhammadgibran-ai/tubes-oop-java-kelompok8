
package jadwal.exception;

public class JadwalBentrokException extends Exception {

    public JadwalBentrokException(String message) {
        super(message);
    }

    // Constructor baru dengan parameter detail
    public JadwalBentrokException(String hari, String jamMulai, String jamSelesai,
            String kodeMatkul, String namaMatkul) {
        super(createMessage(hari, jamMulai, jamSelesai, kodeMatkul, namaMatkul));
    }

    private static String createMessage(String hari, String jamMulai, String jamSelesai,
            String kodeMatkul, String namaMatkul) {
        return "⛔ JADWAL BENTROK!\n"
                + "Jadwal baru bentrok dengan:\n"
                + " Hari: " + hari + "\n"
                + " Jam: " + jamMulai + " - " + jamSelesai + "\n"
                + " Mata Kuliah: " + namaMatkul + " (" + kodeMatkul + ")";
    }
}



