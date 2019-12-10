package com.example.finalcurrencyconvertor;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.Toast;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //UI Interface Objects
    EditText currencyOne;
    EditText currencyTwo;
    Button converter;
    Spinner spinnerOne;
    Spinner spinnerTwo;
    String first;
    String second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets up UI interface to work
        currencyOne = findViewById(R.id.currencyOne);
        currencyTwo = findViewById(R.id.currencyTwo);
        converter = findViewById(R.id.convertButton);
        spinnerOne = findViewById(R.id.spinnerOne);
        spinnerTwo = findViewById(R.id.spinnerTwo);

        //helper function
        begin();
    }

    private void begin() {
        spinnerOne.setOnItemSelectedListener(new SpinnerTo());
        spinnerTwo.setOnItemSelectedListener(new SpinnerFrom());
        converter.setOnClickListener(new View.OnClickListener() {
            @Override
            // first checks for various errors, then executes
            public void onClick(View v) {
                if (first.equals("GEOFFCOIN") || second.equals("GEOFFCOIN")) {
                    Toast.makeText(MainActivity.this, "GeoffCoin is the rarest cryptocurrency.  It is priceless and cannot be converted to other currencies", Toast.LENGTH_LONG).show();
                    return;
                } else if (currencyOne.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter a number please", Toast.LENGTH_LONG).show();
                    return;
                } else if (currencyOne.getText().toString().equals("0")) {
                    Toast.makeText(MainActivity.this, "Enter a number please", Toast.LENGTH_LONG).show();
                } else if (first.equals(second)) {
                        Toast.makeText(MainActivity.this, "Can't convert between identical currencies", Toast.LENGTH_LONG).show();
                        return;
                }
                else {
                    getJSON o = new getJSON();
                    o.execute(currencyOne.getText().toString());
                }
            }
        });
    }

    private void displayError(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

    //sets currency "first"
    private class SpinnerFrom implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            first = parent.getItemAtPosition(position).toString();
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    // sets currency "second"
    private class SpinnerTo implements AdapterView.OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            second = parent.getItemAtPosition(position).toString();
        }
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    // gets conversion rates and JSON objects from frankfurter API
    class getJSON extends AsyncTask<String, Void, Void> {
        ProgressDialog announcement = new ProgressDialog(MainActivity.this);
        String error = "", apiResponse = "";

        protected Void doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url("https://frankfurter.app/latest?amount=" + params[0] + "&from=" + second + "&to=" + first).build();
            System.out.println(request);

            try {
                Response response = client.newCall(request).execute();
                apiResponse = response.body().string();
                //Double apiDouble = Double.parseDouble(apiResponse);
                //Double returner = 1 / apiDouble;
                //apiResponse = Double.toString(returner);
            } catch (Exception e) {
                error = e.toString();
            }
            return null;
        }

        //loading page as connects to internet
        protected void onPreExecute() {
            announcement.setMessage("$$ Cha Ching $$");
            announcement.show();
            super.onPreExecute();
        }

        // displays new calculated currency
        protected void onPostExecute(Void v) {
            announcement.dismiss();
            if (!error.isEmpty()) {
                displayError( error);
            }
            try {
                currencyTwo.setText(new JSONObject(apiResponse).getJSONObject("rates").getString(first));
            } catch (Exception e) {
                displayError(e.toString());
            }
            super.onPostExecute(v);
        }
    }
}