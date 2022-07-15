package com.example.crp;

import java.util.List;

public class Chat {
    private String sender, text, time, identifier;
    private List<String> rel, irel;
    private int rele;

    public Chat(String sender, String text, String time, String identifier, List<String> rel, List<String> irel) {
        this.sender = sender;
        this.text = text;
        this.time = time;
        this.identifier = identifier;
        this.rel = rel;
        this.irel = irel;

        if (rel == null && irel == null) {
            this.rele = 0;
        } else if (rel == null) {
            this.rele = -irel.size();
        } else if (irel == null) {
            this.rele = rel.size();
        } else {
            this.rele = rel.size() - irel.size();
        }
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<String> getRel() {
        return rel;
    }

    public void setRel(List<String> rel) {
        this.rel = rel;
        if (rel == null && irel == null) {
            this.rele = 0;
        } else if (rel == null) {
            this.rele = -irel.size();
        } else if (irel == null) {
            this.rele = rel.size();
        } else {
            this.rele = rel.size() - irel.size();
        }
    }

    public List<String> getIrel() {
        return irel;
    }

    public void setIrel(List<String> irel) {
        this.irel = irel;
        if (rel == null && irel == null) {
            this.rele = 0;
        } else if (rel == null) {
            this.rele = -irel.size();
        } else if (irel == null) {
            this.rele = rel.size();
        } else {
            this.rele = rel.size() - irel.size();
        }
    }

    public void setRele(int rele) {
        this.rele = rele;
    }

    public int getRele() {
        return rele;
    }
}
