package edu.temple.stockapplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class JSONReaderWriter {
    final String FILENAME = "portfolio_file.json";
    
    
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

    public JSONObject fetchStock(String stockSymbol) throws JSONException {
        URL url;
        JSONArray jsonArray = new JSONArray();

        try {
            url = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + stockSymbol.toUpperCase());
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
            inputStream.close();
            bufferedReader.close();
            jsonArray.put(new JSONObject(stringBuilder.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray.getJSONObject(0);
    }

    //returns updated JSONArray with new stock
    public JSONArray FetchAndUpdate(File filePath, String stockSymbol) {
        URL url;
        JSONArray jsonArray = null;
        try {
            url = new URL("http://dev.markitondemand.com/MODApis/Api/v2/Quote/json/?symbol=" + stockSymbol.toUpperCase());
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
            inputStream.close();
            bufferedReader.close();

            //Handle invalid input symbol
            String readString = stringBuilder.toString();
            if (!(readString.startsWith("{Message"))) {
                jsonArray = new JSONReaderWriter().getPortfolioFromFile(filePath, FILENAME);
                jsonArray.put(new JSONObject(readString));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}