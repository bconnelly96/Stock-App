package edu.temple.stockapplication;

import org.json.JSONArray;

import java.io.File;

public class StockRetThread extends Thread {
    File filePath;
    String stockSymbol;
    String fileName;

    public StockRetThread(File filePath, String stockSymbol, String fileName) {
        this.filePath = filePath;
        this.stockSymbol = stockSymbol;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        JSONReaderWriter jsonReaderWriter = new JSONReaderWriter();
        JSONArray jsonArray = jsonReaderWriter.FetchAndUpdate(filePath, stockSymbol);

        if (jsonArray != null) {
            jsonReaderWriter.writePortfolioToFile(filePath, fileName, jsonArray);
            System.out.println("NEW STOCK ADDED TO PORTFOLIO");
        }
    }
}