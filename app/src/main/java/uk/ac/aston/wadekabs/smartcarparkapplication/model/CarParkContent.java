package uk.ac.aston.wadekabs.smartcarparkapplication.model;

/**
 * Created by Bhalchandra Wadekar on 22/12/2016.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 */
public class CarParkContent {

    /**
     * An array of sample (car park) items.
     */
    public static final List<CarParkItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (car park) items, by ID.
     */
    public static final Map<String, CarParkItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createCarParkItem(i));
        }
    }

    private static void addItem(CarParkItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static CarParkItem createCarParkItem(int position) {
        return new CarParkItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }
}
