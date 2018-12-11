package edu.temple.stockapplication;


import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
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



//container, details_container
public class MainActivity extends AppCompatActivity implements PortfolioPane.StockSelected {
    final String FILENAME = "portfolio_file.json";

    FragmentManager fragmentManager;
    PortfolioPane portfolioPaneFragment;
    FileObserver fileObserver;
    StockUpdateThread updateThread;
    final int[] portfolioUpdated = new int[1];
    Bundle savedDetailsBundle;

    /*receives message from UpdateThread after data written to file;
    * sets portfolioUpdated[] so Portfolio Fragment's listview contents update*/
    Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.arg1 == 1) {
                portfolioUpdated[0] = 1;
            }
            return false;
        }
    });

    Handler toastHandler = new Handler(new Handler.Callback() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        portfolioPaneFragment = new PortfolioPane();
        fragmentManager.beginTransaction().add(R.id.container, portfolioPaneFragment).commit();

        //retain worker thread's instance state
        if (savedInstanceState == null) {
            portfolioUpdated[0] = 0;
            updateThread = new StockUpdateThread(getFilesDir(), updateHandler);
            updateThread.start();
        } else {
            StockDetailsPane stockDetailsPane = new StockDetailsPane();
            stockDetailsPane.setArguments(savedInstanceState.getBundle("details_bundle"));

            if (findViewById(R.id.details_container) != null) {
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction().replace(R.id.details_container, stockDetailsPane).commit();
            } else {
                if (savedDetailsBundle != null) {
                    fragmentManager.beginTransaction().replace(R.id.container, stockDetailsPane).addToBackStack(null).commit();
                }
            }
        }

        //update the UI upon update of portfolio_file.json
        fileObserver = new FileObserver(getFilesDir().getAbsolutePath()) {
            @Override
            public void onEvent(int event, @Nullable String path) {
                if (portfolioUpdated[0] == 1) {
                    final PortfolioPaneAdapter portfolioPaneAdapter = (PortfolioPaneAdapter) portfolioPaneFragment.portfolioPaneAdapter;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            portfolioPaneAdapter.updateDataset(new JSONReaderWriter().getPortfolioFromFile(getFilesDir(), FILENAME));
                            portfolioPaneAdapter.notifyDataSetChanged();
                        }
                    });
                    System.out.println("UPDATED IN ON EVENT " + event);
                }
                portfolioUpdated[0] = 0;
            }
        };
        fileObserver.startWatching();

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
                        String stockSymbol = addInput.getText().toString();
                        StockRetThread stockRetThread = new StockRetThread(getFilesDir(), stockSymbol, FILENAME, toastHandler);
                        stockRetThread.start();

                        final PortfolioPaneAdapter portfolioPaneAdapter = (PortfolioPaneAdapter) portfolioPaneFragment.portfolioPaneAdapter;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                portfolioPaneAdapter.updateDataset(new JSONReaderWriter().getPortfolioFromFile(getFilesDir(), FILENAME));
                                portfolioPaneAdapter.notifyDataSetChanged();
                            }
                        });
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBundle("details_bundle", savedDetailsBundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedDetailsBundle = savedInstanceState.getBundle("details_bundle");
    }

    /*triggered by click on PortfolioPane fragment's ListView.
     * sets the arguments for a DetailsPane fragment and adds it to container*/
    @Override
    public void stockSelected(int position) throws JSONException {
        JSONObject jsonObject = new JSONReaderWriter()
                .getPortfolioFromFile(getFilesDir(), FILENAME)
                .getJSONObject(position);

        StockDetailsPane stockDetailsPane = new StockDetailsPane();
        Bundle args = new Bundle();
        args.putString("STOCK_SYMBOL", jsonObject.getString("Symbol"));
        args.putString("COMPANY_NAME", jsonObject.getString("Name"));
        args.putString("CURRENT_PRICE", Integer.toString(jsonObject.getInt("LastPrice")));
        args.putString("OPENING_PRICE", Integer.toString(jsonObject.getInt("Open")));
        stockDetailsPane.setArguments(args);

        if (findViewById(R.id.details_container) != null) {
            fragmentManager.beginTransaction().replace(R.id.details_container, stockDetailsPane).commit();
        } else {
            fragmentManager.beginTransaction().replace(R.id.container, stockDetailsPane).addToBackStack(null).commit();
        }
        savedDetailsBundle = args;
    }
}