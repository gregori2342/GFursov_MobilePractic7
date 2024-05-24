package ru.mirea.fursovgs.httpurlconnection;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.mirea.fursovgs.httpurlconnection.databinding.ActivityMainBinding;

public class DownloadWeather extends AsyncTask<String, Void, String> {
    private ActivityMainBinding binding;
    //https://api.open-meteo.com/v1/forecast?latitude=55.75&longitude=37.62&current_weather=true
    public DownloadWeather(ActivityMainBinding binding) {
        this.binding = binding; // сохраняем ссылку на binding
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        binding.weather.setText("Загружаем...");
    }
    @Override
    protected String doInBackground(String... urls) {
        try {
            return downloadIpInfo(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }
    @Override
    protected void onPostExecute(String result) {
        binding.weather.setText("Результат");
        Log.d(MainActivity.class.getSimpleName(), result);
        try {
            JSONObject responseJson = new JSONObject(result);
            Log.d(MainActivity.class.getSimpleName(), "Response: " + responseJson);
            JSONObject currentWeather = responseJson.getJSONObject("current_weather");
            binding.temperature.setText("temperature: " + currentWeather.getString("temperature"));
            binding.windspeed.setText("windspeed: " + currentWeather.getString("windspeed"));
            binding.time.setText("time: " + currentWeather.getString("time"));

            if(currentWeather.getString("is_day")=="1"){
                binding.day.setText("День");
            }else {
                binding.day.setText("Ночь");
            }
            binding.winddirection.setText("winddirection: "+getWindDirection(Integer.parseInt(currentWeather.getString("winddirection"))));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPostExecute(result);
    }

    private String downloadIpInfo(String address) throws IOException {
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(100000);
            connection.setConnectTimeout(100000);
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200 OK
                inputStream = connection.getInputStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int read = 0;
                while ((read = inputStream.read()) != -1) {
                    bos.write(read);
                }
                bos.close();
                data = bos.toString();
            } else {
                data = connection.getResponseMessage() + ". Error Code: " + responseCode;
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return data;
    }
    public static String getWindDirection(int degrees) {
        if (degrees >= 0 && degrees < 45) {
            return "Северное";
        } else if (degrees >= 45 && degrees < 90) {
            return "Северо-Восточное";
        } else if (degrees >= 90 && degrees < 135) {
            return "Восточное";
        } else if (degrees >= 135 && degrees < 180) {
            return "Юго-Восточное";
        } else if (degrees >= 180 && degrees < 225) {
            return "Южное";
        } else if (degrees >= 225 && degrees < 270) {
            return "Юго-Западное";
        } else if (degrees >= 270 && degrees < 315) {
            return "Западное";
        } else if (degrees >= 315 && degrees < 360) {
            return "Северо-Западное";
        } else {
            return "Недопустимое значение градусов";
        }
    }
}
