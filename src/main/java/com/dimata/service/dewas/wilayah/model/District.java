package com.dimata.service.dewas.wilayah.model;

public class District {
    private String id;
    private String regencyId;
    private String name;

    public District() {}

    public District(String id, String regencyId, String name) {
        this.id = id;
        this.regencyId = regencyId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegencyId() {
        return regencyId;
    }

    public void setRegencyId(String regencyId) {
        this.regencyId = regencyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
