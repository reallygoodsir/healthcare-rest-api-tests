package com.really.good.sir.dto;

public class SpecializationDTO {
    private int id;
    private String name;

    public SpecializationDTO() {
    }

    public SpecializationDTO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SpecializationDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

