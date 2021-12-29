package com.alisoftclub.frameworks.modular.license;

import java.io.Serializable;
import java.util.Objects;

public class License
        implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private String computerId;
    private LicenseType type;
    private long min;
    private long max;

    public License(String id, String title, String computerId, LicenseType type, long min, long max) {
        this.id = id;
        this.title = title;
        this.computerId = computerId;
        this.type = type;
        this.min = min;
        this.max = max;
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

    public String getComputerId() {
        return this.computerId;
    }

    public void setComputerId(String computerId) {
        this.computerId = computerId;
    }

    public LicenseType getType() {
        return this.type;
    }

    public void setType(LicenseType type) {
        this.type = type;
    }

    public long getMin() {
        return this.min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getMax() {
        return this.max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    @Override
    public String toString() {
        return this.title;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.id);
        hash = 31 * hash + Objects.hashCode(this.type);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        License other = (License) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return (this.type == other.type);
    }
}
