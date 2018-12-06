package edu.temple.stockapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class StockDetailsPane extends Fragment {
    TextView companyName;
    WebView stockChart;
    TextView currentPrice;
    TextView openingPrice;

    public StockDetailsPane() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_stock_details_pane, container, false);
        companyName = v.findViewById(R.id.company_name);
        stockChart = v.findViewById(R.id.webView);
        currentPrice = v.findViewById(R.id.current_price);
        openingPrice = v.findViewById(R.id.opening_price);

        Bundle args = getArguments();
        if (args != null) {
            String stockSymbol = args.getString("STOCK_SYMBOL");
            companyName.setText(args.getString("COMPANY_NAME"));
            currentPrice.setText(args.getString("CURRENT_PRICE"));
            openingPrice.setText(args.getString("OPENING_PRICE"));

            stockChart.getSettings().setJavaScriptEnabled(true);
            stockChart.loadUrl("https://macc.io/lab/cis3515/?symbol=" + stockSymbol);
        }
        return v;
    }
}