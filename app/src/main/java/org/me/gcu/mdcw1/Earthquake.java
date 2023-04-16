//
// Name                 Andrew McGowan
// Student ID           S2033682
// Programme of Study   Computing
//

package org.me.gcu.mdcw1;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Earthquake implements Serializable {
    private String title;
    private String description;
    private String link;
    private String pubDate;
    private Date date;
    private float magnitude;

    private double latitude;
    private double longitude;

    public Earthquake() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
        SimpleDateFormat format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.US);
        try {
            this.date = format.parse(pubDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getDate() {
        return date;
    }

    public float getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Earthquake{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", link='" + link + '\'' +
                ", pubDate='" + pubDate + '\'' +
                '}';
    }
}
