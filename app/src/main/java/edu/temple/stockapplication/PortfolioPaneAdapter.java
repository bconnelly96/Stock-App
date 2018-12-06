package edu.temple.stockapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PortfolioPaneAdapter extends BaseAdapter {
    Context context;
    JSONArray jsonArray;

    public PortfolioPaneAdapter(Context context, JSONArray jsonArray) {
        this.context = context;
        this.jsonArray = jsonArray;
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public JSONObject getItem(int position) {
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /*Returns a TextView with
    * Stock Symbol, Current Stock Price,
    * Green/Red background depending on stock's performance*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);

        String symbol = "";
        double currentPrice = 0;
        double openingPrice = 0;
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(position);
            symbol = jsonObject.getString("Symbol");
            currentPrice = jsonObject.getDouble("LastPrice");
            openingPrice = jsonObject.getDouble("Open");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        textView.setText(symbol + " " + String.valueOf(currentPrice));

        //set background color based on stock performance
        if (currentPrice >= openingPrice) {
            textView.setBackgroundColor(Color.GREEN);
        } else {
            textView.setBackgroundColor(Color.RED);
        }
        return textView;
    }

    //called when new stocks are added to portfolio
    public void updateDataset(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }
}
