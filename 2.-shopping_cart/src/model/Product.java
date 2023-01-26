package model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.Serializable;

public class Product implements Comparable<Product>, Serializable {
    private int id;
    private String name;
    private String album;
    private String duration;
    private int year;
    private float price;
    private String artist;
    private int amountOfDownloads;
    private ImageIcon image;

    public Product(int id, String name, String album, int year, float price, String duration, String artist, int amountOfDownloads, ImageIcon image) {
        this.id = id;
        this.name = name;
        this.album = album;
        this.year = year;
        this.price = price;
        this.duration = duration;
        this.artist = artist;
        this.amountOfDownloads = amountOfDownloads;
        this.image = image;
    }

    public Product(String name, String album, int year, float price, String duration, String artist, int amountOfDownloads, ImageIcon image) {
        this(-1, name, album, year, price, duration, artist, amountOfDownloads, image);
    }

    /**
     * Used when the server receives the products of a transaction
     * @param id
     * @param price
     */
    public Product(int id, float price) {
        this.id = id;
        this.price = price;
        this.name = "";
        this.album = "";
        this.year = 0;
        this.duration = "0";
        this.artist = "";
        this.amountOfDownloads = 0;
        this.image = null;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
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

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getYear() {
        return year;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getAmountOfDownloads() {
        return amountOfDownloads;
    }

    public void setAmountOfDownloads(int amountOfDownloads) {
        this.amountOfDownloads = amountOfDownloads;
    }

    public ImageIcon getImage() {
        return image;
    }

    public void setImage(ImageIcon image) {
        this.image = image;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public int compareTo(Product otherProduct) {
        if(amountOfDownloads > otherProduct.amountOfDownloads)
            return 1;
        if(amountOfDownloads < otherProduct.amountOfDownloads)
            return -1;
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (getClass() != obj.getClass())
            return false;

        final Product other = (Product) obj;
        if (this.id != other.id)
            return false;

        return true;
    }
}
