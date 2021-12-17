package com.example.health_test_update_version;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Results extends AppCompatActivity {

    TextView ResultDescription;
    TextView MetricsDescription;
    ImageView Metrics;
    ImageView WarnImage;
    String[] credentials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        ResultDescription = findViewById(R.id.result_description);
        MetricsDescription = findViewById(R.id.metrics_description);

        Metrics = findViewById(R.id.metrics_tv);
        WarnImage = findViewById(R.id.metrics_tv_smile);

        Intent intent = getIntent();

        credentials = intent.getStringArrayExtra("credentials");
        String first_value = intent.getStringExtra("first_value");
        String second_value = intent.getStringExtra("second_value");

        // Начинаем формирование строковой даты для POST запроса
        String data = String.format("day=15&month=12&year=1990&sex=1&m1=%s&m2=%s",
                first_value, second_value);

        try {
            datasender(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void datasender(String data) throws IOException {

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, data);

        Request request = new Request.Builder()
                .url("http://abashin.ru/cgi-bin/ru/tests/burnout")
                .method("POST", body)
                .addHeader("Host", "abashin.ru")
                .addHeader("Connection", "close")
                .addHeader("Cache-Control", "max-age=0")
                .addHeader("DNT", "1")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;" +
                        "q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .addHeader("Accept-Encoding", "deflate")
                .addHeader("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Content-Length", "43")
                .build();

        // Поток отправки запроса и обработки ответа (обновляет tv при получении ответа 200)
        // Далее создаём callback (метод обратного вызова) каждый раз, когда отправляем форму через ВВОД

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {

                    String message = response.body().string();

                    // Парсим html body из ответа

                    Document html_response = Jsoup.parse(message);
                    String format_message = html_response.text();

                    // Обновляем интерфейс при получении успешного ответа

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            MetricsDescription.setText(format_message);

                            if (format_message.contains("отсутствию")) {
                                resultAwesome();

                            } else if (format_message.contains("небольшому")) {
                                resultGood();

                            } else if (format_message.contains("высокому")) {
                                resultBad();

                            } else if (format_message.contains("Error")) {
                                resultWorst();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void resultAwesome() {
        ResultDescription.setText("Обязательно не забывайте о регулярном профессиональном медосмотре!");
        WarnImage.setVisibility(View.VISIBLE);
        Metrics.setVisibility(View.VISIBLE);
        WarnImage.setBackgroundColor(Color.argb(100, 0, 255, 0));
        Metrics.setImageResource(R.drawable.pers_100);

    }

    public void resultGood() {
        ResultDescription.setText("Сейчас Вам необходимо отдохнуть и проконсультироваться со специалистом, если состояние не станет лучше!");
        WarnImage.setVisibility(View.VISIBLE);
        Metrics.setVisibility(View.VISIBLE);
        WarnImage.setBackgroundColor(Color.argb(100, 255, 131, 0));
        Metrics.setImageResource(R.drawable.pers_60);
    }

    public void resultBad() {
        ResultDescription.setText("Вам просто небходимо сейчас срочно отдохнуть и обратиться в медицинское учреждение для осмотра!");
        WarnImage.setVisibility(View.VISIBLE);
        Metrics.setVisibility(View.VISIBLE);
        WarnImage.setBackgroundColor(Color.argb(100, 255, 255, 0));
        Metrics.setImageResource(R.drawable.pers_40);
    }

    public void resultWorst() {
        ResultDescription.setText("Сейчас срочно Вам необходима госпитализация и больничный режим! У вас критическое переутомление!");
        WarnImage.setVisibility(View.VISIBLE);
        Metrics.setVisibility(View.VISIBLE);
        WarnImage.setBackgroundColor(Color.argb(100, 255, 0, 0));
        Metrics.setImageResource(R.drawable.pers_0);
    }
}