package itis.practice.testing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public String getJsonFromAssets(String fileName) {
        try {
            StringBuilder builder = new StringBuilder();
            InputStream json = getAssets().open(fileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                builder.append(str);
            }
            in.close();
            return builder.toString();
        } catch (IOException ignored) {
            return "";
        }
    }
}
