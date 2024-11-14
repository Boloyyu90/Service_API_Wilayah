package com.dimata.service.dewas.wilayah.model;

public class Regency {
    private String id;
    private String provinceId;
    private String name;

    public Regency() {}

    public Regency(String id, String provinceId, String name) {
        this.id = id;
        this.provinceId = provinceId;
        this.name = name;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
