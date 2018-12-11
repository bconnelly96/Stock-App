package edu.temple.stockapplication;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class StockUpdateThread extends Thread {
    final String FILENAME = "portfolio_file.json";
    //interval of update in miliseconds
    final int INTERVAL = 60000;

    File filePath;
    Handler handler;

    public StockUpdateThread(File filePath, Handler handler) {
        this.filePath = filePath;
        this.handler = handler;
    }

    //
    @Override
    public void run() {
        while (true) {
            JSONReaderWriter jsonReaderWriter = new JSONReaderWriter();
            JSONArray initialPortfolio = jsonReaderWriter.getPortfolioFromFile(filePath, FILENAME);
            JSONArray newPortfolio = new JSONArray();

            //save updated stock data into new JSONArray representing updated portfolio
            for (int i = 0; i < initialPortfolio.length(); i++) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject = jsonReaderWriter.fetchStock(initialPortfolio.getJSONObject(i).getString("Symbol"));
                    newPortfolio.put(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //write new portfolio to file
            jsonReaderWriter.writePortfolioToFile(filePath, FILENAME, newPortfolio);
            Message msg = Message.obtain();
            msg.arg1 = 1;
            handler.sendMessage(msg);

            System.out.println("PORTFOLIO UPDATED");
            try {
                //repeat every minute
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}