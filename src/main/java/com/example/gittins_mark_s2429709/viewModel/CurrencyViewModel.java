package com.example.gittins_mark_s2429709.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.gittins_mark_s2429709.model.CurrencyItem;
import com.example.gittins_mark_s2429709.model.CurrencyRepository;
import java.util.List;
import java.util.stream.Collectors;

public class CurrencyViewModel extends AndroidViewModel {
    // LiveData holding the list of currencies to be displayed.
    // MutableLiveData is used here value can be changed
    private final MutableLiveData<List<CurrencyItem>> currencyLiveData = new MutableLiveData<>();

    // A reference to the single repository that acts as the single source of truth for data.
    private final CurrencyRepository repository;

    public CurrencyViewModel(@NonNull Application application) {
        super(application);
        // Get the single instance of the repository
        repository = CurrencyRepository.getInstance();
        // Initialize the LiveData with the current list of rates from the repository.
        currencyLiveData.setValue(repository.getRates());
    }

    public LiveData<List<CurrencyItem>> getCurrencyLiveData() {
        return currencyLiveData;
    }

    public void filter(String query) {
        // Retrieve the original, unfiltered list from the repository
        List<CurrencyItem> filtered = repository.getRates().stream()
                .filter(c -> c.title.toLowerCase().contains(query.toLowerCase())
                        || c.code.toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
        // Update the LiveData with the new filtered list, which will notify any active observers
        currencyLiveData.setValue(filtered);
    }
}
