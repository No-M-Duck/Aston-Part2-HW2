package org.example.dto;

import java.util.UUID;

public class DirectorDTO {
    private UUID id;
    private String name;
    private String lastName;
    private String country;

    // Конструктор
    public DirectorDTO(UUID id, String name, String lastName, String country) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.country = country;
    }

    // Геттеры и сеттеры
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "DirectorDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
