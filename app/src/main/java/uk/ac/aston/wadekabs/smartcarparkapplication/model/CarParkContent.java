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
     * A map of sample (car park) items, by ID.
     */
    public static final Map<Integer, CarParkItem> ITEM_MAP = new HashMap<>();

    public static void addItem(CarParkItem item) {
        ITEM_MAP.put(item.id, item);
    }

    public static List<CarParkItem> items() {
        return new ArrayList<>(ITEM_MAP.values());
    }
}
