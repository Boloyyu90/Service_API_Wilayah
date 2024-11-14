package com.dimata.service.dewas.wilayah.model;

public class Village {
    private String id;
    private String districtId;
    private String name;

    public Village() {}

    public Village(String id, String districtId, String name) {
        this.id = id;
        this.districtId = districtId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
