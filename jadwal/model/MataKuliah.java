package jadwal.model;

public class MataKuliah {
private String kode;
private String nama;
private String dosen;
private int sks;

public MataKuliah(String kode, String nama, String dosen, int sks) {
this.kode = kode;
this.nama = nama;
this.dosen = dosen;
this.sks = sks;
}

public String getKode() { return kode; }
public String getNama() { return nama; }
public String getDosen() { return dosen; }
public int getSks() { return sks; }
}