package com.example.gittins_mark_s2429709.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gittins_mark_s2429709.R;

public class ConverterActivity extends AppCompatActivity {

    //elements for the first conversion direction (e.g., GBP to Foreign)
    private EditText amountInput;
    private Button convertButton;
    private TextView resultText;

    // elements for the second conversion direction (e.g., Foreign to GBP)
    private EditText amountInput2;
    private Button convertButton2;
    private TextView resultText2;

    // The exchange rate for the selected currency against the base currency.
    private double rate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        // Initialize the back button to close this activity and return to the previous one.
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        // Retrieve the exchange rate
        // Defaults to 0.0 if the "rate" extra is not found.
        rate = getIntent().getDoubleExtra("rate", 0.0);

        // Initialize components for the first converter (GBP -> Foreign)
        amountInput = findViewById(R.id.amountInput);
        convertButton = findViewById(R.id.convertButton);
        resultText = findViewById(R.id.resultText);

        // Initialize components for the second converter (Foreign -> GBP)
        amountInput2 = findViewById(R.id.amountInput2);
        convertButton2 = findViewById(R.id.convertButton2);
        resultText2 = findViewById(R.id.resultText2);

        // Set up the listener for the first conversion button.
        convertButton.setOnClickListener(v -> {
            String text = amountInput.getText().toString();
            if (text.isEmpty()) {
                resultText.setText("Enter an amount");
                return;
            }

            double amount = Double.parseDouble(text);
            // Calculate the result by multiplying the amount by the rate (GBP → Selected Currency)
            double result = amount * rate;

            // Display the formatted result.
            resultText.setText(String.format("%.2f", result));
        });

        // Set up the listener for the second conversion button.
        convertButton2.setOnClickListener(v -> {
            String text2 = amountInput2.getText().toString();
            if (text2.isEmpty()) {
                resultText2.setText("Enter an amount");
                return;
            }

            double amount2 = Double.parseDouble(text2);
            double result2;

            // Calculate the result by dividing the amount by the rate (Selected Currency → GBP)
            result2 = amount2 / rate;

            // Display the formatted result
            resultText2.setText(String.format("%.2f", result2));
        });
    }
}
