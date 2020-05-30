package com.example.petsee.Model;

import android.app.Notification;

public class Bildirim {
    private String kullaniciId;
    private String text;
    private String gonderiId;
    private boolean ispost;

    public Bildirim(String kullaniciId, String text, String gonderiId, boolean ispost) {
        this.kullaniciId = kullaniciId;
        this.text = text;
        this.gonderiId = gonderiId;
        this.ispost = ispost;
    }

    public Bildirim() {
    }

    public String getKullaniciId() {
        return kullaniciId;
    }

    public void setKullaniciId(String kullaniciId) {
        this.kullaniciId = kullaniciId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getGonderiId() {
        return gonderiId;
    }

    public void setGonderiId(String gonderiId) {
        this.gonderiId = gonderiId;
    }

    public boolean isIspost() {
        return ispost;
    }

    public void setIspost(boolean ispost) {
        this.ispost = ispost;
    }
}
