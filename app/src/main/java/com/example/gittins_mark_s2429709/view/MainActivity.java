package com.example.gittins_mark_s2429709.view;

//
// Name                 Mark Gittins
// Student IDs2429709
// Programme of Study   Software developlment
//

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.SearchView;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.example.gittins_mark_s2429709.R;
import com.example.gittins_mark_s2429709.model.CurrencyRepository;
import com.example.gittins_mark_s2429709.viewModel.CurrencyViewModel;
import com.example.gittins_mark_s2429709.worker.CurrencyWorker;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private RecyclerView ratesList;
    private TextView dateDisplay;
    private SearchView searchView;
    private RateAdapter adapter;

    // ViewModel for managing UI-related data in a lifecycle-conscious way.
    CurrencyViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the user interface layout for this Activity.
        setContentView(R.layout.activity_main);

        // Initialize UI components
        ratesList = findViewById(R.id.ratesList);
        ratesList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RateAdapter(this, new ArrayList<>()); // Initialize the adapter with an empty list.
        ratesList.setAdapter(adapter); // Attach the adapter to the RecyclerView.

        dateDisplay = findViewById(R.id.dateDisplay);
        searchView = findViewById(R.id.searchView);

        // Initialize the ViewModel.
        viewModel = new ViewModelProvider(this).get(CurrencyViewModel.class);

        // Observe the LiveData from the ViewModel for changes to the currency list.
        viewModel.getCurrencyLiveData().observe(this, list -> {
            // When data changes, update the adapter's list.
            adapter.setCurrencyList(list);
            adapter.notifyDataSetChanged();
            // Update the date display with the publication date from the first item.
            if (!list.isEmpty()) {
                dateDisplay.setText("Last Updated: " + list.get(0).pubDate);
            }
        });

        // Set up a listener for the search view to handle user input.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.filter(query); // Trigger the filter operation in the ViewModel.
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.filter(newText); // Trigger the filter operation in the ViewModel on every character change.
                return false;
            }
        });

        // Perform initial UI update and schedule background tasks.
        updateUi();
        scheduleAutoUpdate();
        observeData();
    }

    private void scheduleAutoUpdate() {
        // Define constraints for the background work
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Create a periodic request to run the CurrencyWorker every hour.
        PeriodicWorkRequest request =
                new PeriodicWorkRequest.Builder(CurrencyWorker.class, 1, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build();

        // queue the periodic work only one instance with this name is active.
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "currency_auto_update",
                ExistingPeriodicWorkPolicy.UPDATE, // If work exists, update it with the new request.
                request
        );

        // Create and enqueue a one-time request for an immediate initial data load.
        OneTimeWorkRequest initial =
                new OneTimeWorkRequest.Builder(CurrencyWorker.class)
                        .addTag("initial_load")
                        .build();

        WorkManager.getInstance(this).enqueue(initial);
    }

    private void observeData() {
        // Observe the unique periodic work for updates.
        WorkManager.getInstance(this)
                .getWorkInfosForUniqueWorkLiveData("currency_auto_update")
                .observe(this, workInfos -> updateUi());

        // Observe the initial one-time work by its tag.
        WorkManager.getInstance(this)
                .getWorkInfosByTagLiveData("initial_load")
                .observe(this, workInfos -> updateUi());
    }

    private void updateUi() {
        // Get the latest rates from the singleton repository and set them on the adapter.
        adapter.setCurrencyList(CurrencyRepository.getInstance().getRates());
        adapter.notifyDataSetChanged(); // Refresh the RecyclerView.

        // Update the date display if the repository contains data.
        if (!CurrencyRepository.getInstance().getRates().isEmpty()) {
            dateDisplay.setText("Last Updated: " + CurrencyRepository.getInstance().getRates().get(0).pubDate);
        }
    }
}
