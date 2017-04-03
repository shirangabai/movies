package shiran.movies.mainApp.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import shiran.movies.U;


public class MoviesHttpClient {
    public String getMoviesData(String place){
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        String searchUrl = U.SEARCH_URL_PREFIX + U.searchQuery + U.SEARCH_URL_SUFFIX;

        try {
            connection = (HttpURLConnection) (new URL(searchUrl)).openConnection();
            connection.setRequestMethod("GET");
            //connection.setDoInput(true);
            //connection.setDoOutput(true);
            connection.connect();

            //read the response
            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = null;
            while ((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line + "\r\n");
            }

            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            connection.disconnect();

        }

        return null;
    }



}
