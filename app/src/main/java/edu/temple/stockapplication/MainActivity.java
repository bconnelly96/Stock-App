package edu.temple.stockapplication;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity implements PortfolioPane.StockSelected {
    final String FILENAME = "portfolio_file.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //windowed dialog triggered by FAB click
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton2);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View mView = getLayoutInflater().inflate(R.layout.dialog_add, null);
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                final EditText addInput = mView.findViewById(R.id.symbol_add);
                Button cancelButton = mView.findViewById(R.id.cancel_button);
                Button addButton = mView.findViewById(R.id.add_button);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addStock(addInput.getText().toString());
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    /*triggered by click on PortfolioPane fragment's ListView.
     * sets the arguments for a DetailsPane fragment and adds it to container*/
    @Override
    public void stockSelected(int position) throws JSONException {
        JSONObject jsonObject = new JSONReaderWriter().getPortfolioFromFile(getFilesDir(), FILENAME).getJSONObject(position);

        StockDetailsPane stockDetailsPane = new StockDetailsPane();
        Bundle args = new Bundle();
        args.putString("STOCK_SYMBOL", jsonObject.getString("Symbol"));
        args.putString("COMPANY_NAME", jsonObject.getString("Name"));
        args.putString("CURRENT_PRICE", Integer.toString(jsonObject.getInt("LastPrice")));
        args.putString("OPENING_PRICE", Integer.toString(jsonObject.getInt("Open")));
        stockDetailsPane.setArguments(args);
    }

    Handler StockAddedHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.success_string), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.failure_string), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    /*add new stock data to portfolio file.
    * return true if stock data can be added i.e. stock symbol provided is valid*/
    private void addStock(final String stockSymbol) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                URL url;
                Message message = Message.obtain();
                message.arg1 = 0;
                try {
                    url = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + stockSymbol.toUpperCase());
                    URLConnection urlConnection = url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    StringBuilder stringBuilder = new StringBuilder();
                    while((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                        stringBuilder.append('\n');
                    }
                    inputStream.close();
                    bufferedReader.close();
                    JSONArray jsonArray = new JSONReaderWriter().getPortfolioFromFile(getFilesDir(), FILENAME);
                    jsonArray.put(new JSONObject(stringBuilder.toString()));
                    JSONReaderWriter jsonReaderWriter = new JSONReaderWriter();
                    jsonReaderWriter.writePortfolioToFile(getFilesDir(), FILENAME, jsonArray);

                    message.arg1 = 1;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                StockAddedHandler.sendMessage(message);
            }

        };
        thread.start();

    }
}
