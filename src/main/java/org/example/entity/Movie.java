package org.example.entity;

import java.time.LocalDate;
import java.util.UUID;

public class Movie {
    private UUID id;
    private UUID directorId;
    private String title;
    private LocalDate releaseDate;
    private int duration;
    private int hall;

    // Конструктор
    public Movie(UUID id, UUID directorId, String title, LocalDate releaseDate, int duration, int hall) {
        this.id = id;
        this.directorId = directorId;
        this.title = title;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.hall = hall;
    }

    // Геттеры и сеттеры
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDirectorId() {
        return directorId;
    }

    public void setDirectorId(UUID directorId) {
        this.directorId = directorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getHall() {
        return hall;
    }

    public void setHall(int hall) {
        this.hall = hall;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", directorId=" + directorId +
                ", title='" + title + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", duration=" + duration +
                ", hall=" + hall +
                '}';
    }
}
