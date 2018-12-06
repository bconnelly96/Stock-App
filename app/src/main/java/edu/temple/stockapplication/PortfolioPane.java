package edu.temple.stockapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;

public class PortfolioPane extends Fragment {
    final String fileName = "portfolio_file.json";

    ListView stockList;

    PortfolioPaneAdapter portfolioPaneAdapter;
    StockSelected listener;

    public interface StockSelected {
        void stockSelected(int position) throws JSONException;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (StockSelected) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_portfolio_pane, container, false);

        stockList = v.findViewById(R.id.stock_listview);
        TextView textView = v.findViewById(R.id.add_textview);


        //Read json portfolio file into JSONArray
        JSONArray jsonArray = new JSONReaderWriter().getPortfolioFromFile(getActivity().getFilesDir(), fileName);
        portfolioPaneAdapter = new PortfolioPaneAdapter(getContext(), jsonArray);
        stockList.setAdapter(portfolioPaneAdapter);

        if (jsonArray.length() > 0) {
            textView.setVisibility(View.GONE);
        }

        stockList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    listener.stockSelected(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return v;
    }
}