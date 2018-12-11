package edu.temple.stockapplication;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;

import java.io.File;

public class StockRetThread extends Thread {
    File filePath;
    String stockSymbol;
    String fileName;
    Handler handler;

    public StockRetThread(File filePath, String stockSymbol, String fileName, Handler handler) {
        this.filePath = filePath;
        this.stockSymbol = stockSymbol;
        this.fileName = fileName;
        this.handler = handler;
    }

    @Override
    public void run() {
        JSONReaderWriter jsonReaderWriter = new JSONReaderWriter();
        JSONArray jsonArray = jsonReaderWriter.FetchAndUpdate(filePath, stockSymbol);

        if (jsonArray != null) {
            jsonReaderWriter.writePortfolioToFile(filePath, fileName, jsonArray);
            System.out.println("NEW STOCK ADDED TO PORTFOLIO");

            Message msg = Message.obtain();
            msg.arg1 = 1;
            handler.sendMessage(msg);
        }
    }
}