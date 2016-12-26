package uk.ac.aston.wadekabs.smartcarparkapplication.model;

/**
 * Created by Bhalchandra Wadekar on 22/12/2016.
 */

/**
 * A car park item representing a piece of content.
 */
public class CarParkItem {

    public final int id;
    public final String content;
    public final String details;

    public CarParkItem(int id, String content, String details) {
        this.id = id;
        this.content = content;
        this.details = details;
    }

    @Override
    public String toString() {
        return content;
    }
}

