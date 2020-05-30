package com.example.petsee.Model;

public class Yorum {

    private String yorum;
    private String yorumid;
    private String gonderen;

    public Yorum() {
    }

    public Yorum(String yorum, String gonderen,String yorumid) {
        this.yorum = yorum;
        this.yorumid = yorumid;
        this.gonderen = gonderen;
    }

    public String getYorum() {
        return yorum;
    }

    public void setYorum(String yorum) {
        this.yorum = yorum;
    }

    public String getGonderen() {
        return gonderen;
    }

    public void setGonderen(String gonderen) {
        this.gonderen = gonderen;
    }

    public String getYorumid() {
        return yorumid;
    }

    public void setYorumid(String yorumid) {
        this.yorumid = yorumid;
    }
}
