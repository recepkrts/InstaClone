package com.example.petsee.Model;

public class Mesaj {

    private String mesajid;
    private String gonderen;
    private String alici;
    private String mesaj;
    private String zaman;
    private String tarih;

    public Mesaj(String mesajid, String gonderen, String alici, String mesaj, String zaman, String tarih) {
        this.mesajid = mesajid;
        this.gonderen = gonderen;
        this.alici = alici;
        this.mesaj = mesaj;
        this.zaman = zaman;
        this.tarih = tarih;
    }

    public Mesaj() {
    }

    public String getMesajid() {
        return mesajid;
    }

    public void setMesajid(String mesajid) {
        this.mesajid = mesajid;
    }

    public String getGonderen() {
        return gonderen;
    }

    public void setGonderen(String gonderen) {
        this.gonderen = gonderen;
    }

    public String getAlici() {
        return alici;
    }

    public void setAlici(String alici) {
        this.alici = alici;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public String getZaman() {
        return zaman;
    }

    public void setZaman(String zaman) {
        this.zaman = zaman;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }
}
