package com.example.gittins_mark_s2429709.model;

import android.util.Log;

/**
 * A data model class representing a single currency exchange rate item
 */
public class CurrencyItem {

    // The full title of the currency item
    public String title;

    // The description
    public String description;

    // The publication date of the exchange rate.
    public String pubDate;

    // The three letter currency code
    public String code;

    // The calculated exchange rate as a numeric type for calculations.
    public double rate;

    // The resource ID for the corresponding country flag drawable
    public int flagId;



    /**
     * Provides a simple string representation of the currency item, useful for logging and debugging
     */
    @Override
    public String toString() {
        return title + " " + description + " " + rate + " " + pubDate + " " + code + " " + flagId;
    }
}

