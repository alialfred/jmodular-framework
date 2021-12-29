package com.alisoftclub.frameworks.modular.license;

import java.io.Serializable;

public class LicenseKey
        implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private String serial;
    private String seed;

    public LicenseKey() {
    }

    public LicenseKey(String id, String title, String serial, String seed) {
        this.id = id;
        this.title = title;
        this.serial = serial;
        this.seed = seed;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSerial() {
        return this.serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSeed() {
        return this.seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    @Override
    public String toString() {
        return this.title;
    }
}
