package com.example.ShelfScrap.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "User")
public class User {

    @Id
    @Column(unique = true, nullable = false)
    private String username; // Primary key instead of id

    private String password;

    @ManyToMany
    @JoinTable(
            name = "user_currently_reading",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> currentlyReading;

    @ManyToMany
    @JoinTable(
            name = "user_want_to_read",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> wantToRead;

    @ManyToMany
    @JoinTable(
            name = "user_already_read",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> alreadyRead;

    @ManyToMany
    @JoinTable(
            name = "user_liked",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> liked;

    @ManyToMany
    @JoinTable(
            name = "user_suggested",
            joinColumns = @JoinColumn(name = "username"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> suggested;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Book> getCurrentlyReading() {
        return currentlyReading;
    }

    public void setCurrentlyReading(List<Book> currentlyReading) {
        this.currentlyReading = currentlyReading;
    }

    public List<Book> getWantToRead() {
        return wantToRead;
    }

    public void setWantToRead(List<Book> wantToRead) {
        this.wantToRead = wantToRead;
    }

    public List<Book> getAlreadyRead() {
        return alreadyRead;
    }

    public void setAlreadyRead(List<Book> alreadyRead) {
        this.alreadyRead = alreadyRead;
    }

    public List<Book> getLiked() {
        return liked;
    }

    public void setLiked(List<Book> liked) {
        this.liked = liked;
    }

    public List<Book> getSuggested() {
        return suggested;
    }

    public void setSuggested(List<Book> suggested) {
        this.suggested = suggested;
    }
}

