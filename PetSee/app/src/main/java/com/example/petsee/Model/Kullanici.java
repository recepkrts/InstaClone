package com.example.petsee.Model;

public class Kullanici {

    private String ad;
    private String bio;
    private String cins;
    private String id;
    private String kullaniciadi;
    private String macadresi;
    private String resimurl;
    private String soyad;
    private String durum;

    public Kullanici() {
    }

    public Kullanici(String ad, String bio,String cins, String id, String kullaniciadi, String macadresi
            , String resimurl, String soyad,String durum) {
        this.ad = ad;
        this.bio = bio;
        this.cins = cins;
        this.id = id;
        this.kullaniciadi = kullaniciadi;
        this.macadresi = macadresi;
        this.resimurl = resimurl;
        this.soyad = soyad;
        this.durum = durum;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
    public String getCins() {
        return cins;
    }

    public void setCins(String cins) {
        this.cins = cins;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKullaniciadi() {
        return kullaniciadi;
    }

    public void setKullaniciadi(String kullaniciadi) {
        this.kullaniciadi = kullaniciadi;
    }

    public String getMacadresi() {
        return macadresi;
    }

    public void setMacadresi(String macadresi) {
        this.macadresi = macadresi;
    }

    public String getResimurl() {
        return resimurl;
    }

    public void setResimurl(String resimurl) {
        this.resimurl = resimurl;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }
}
