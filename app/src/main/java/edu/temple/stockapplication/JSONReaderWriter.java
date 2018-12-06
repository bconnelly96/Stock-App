package edu.temple.stockapplication;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class JSONReaderWriter {

    //Returns a new JSONArray of data read from .json file
    public JSONArray getPortfolioFromFile(File filePath, String fileName) {
        File file = new File (filePath, fileName);
        JSONArray jsonArray = null;
        if (file.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                StringBuilder stringBuilder = new StringBuilder();
                String currLine;
                while ((currLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(currLine);
                    stringBuilder.append('\n');
                }
                bufferedReader.close();

                jsonArray = new JSONArray(stringBuilder.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            jsonArray = new JSONArray();
        }
        return jsonArray;
    }

    public void writePortfolioToFile(File filePath, String fileName, JSONArray jsonArray) {
        File file = new File(filePath, fileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(jsonArray.toString().getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}