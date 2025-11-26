package com.example.gittins_mark_s2429709.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A singleton repository that acts as a centralized in-memory cache for currency exchange rate data.
 * there is a single source of truth for the currency data throughout the application,
 */
public class CurrencyRepository {

    // The single, static instance of the repository, created at class-loading time.
    private static final CurrencyRepository INSTANCE = new CurrencyRepository();

    // The in-memory list that holds the current currency rate items.
    private final List<CurrencyItem> currencyList = new ArrayList<>();

    /**
     * A private constructor to prevent external instantiation, enforcing the singleton pattern.
     */
    private CurrencyRepository() { }

    /**
     * Provides global access to the single instance of the repository
     */
    public static CurrencyRepository getInstance() {
        return INSTANCE;
    }

    /**
     * Atomically updates the stored list of currency rates
     */
    public synchronized void updateRates(List<CurrencyItem> newList) {
        currencyList.clear();
        currencyList.addAll(newList);
    }

    /**
     * Retrieves a copy of the current list of currency rates
     */
    public synchronized List<CurrencyItem> getRates() {
        return new ArrayList<>(currencyList); // Return a copy
    }
}
