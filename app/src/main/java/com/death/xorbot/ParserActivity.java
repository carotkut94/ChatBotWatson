package com.death.xorbot;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ParserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parser);
        new ParseWeb().execute();

    }


    static class ParseWeb extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            Document doc = null;
            try {
                doc = Jsoup.connect("https://stackoverflow.com/search?q=python+hashmap").userAgent("Solve problems").get();
                Log.e("DATA", doc.title());
                Element element = doc.select("div.search-results.js-search-results").first();
                Elements elements = element.select("div.summary");
                Elements stats = element.select("div.statscontainer");

                Log.e("Length", "" + elements.size());

                for (int i = 0; i < elements.size(); i++) {
                    Log.e("Question Title", elements.get(i).getElementsByClass("result-link").text());
                    Log.e("Question excerpt", elements.get(i).getElementsByClass("excerpt").text());
                    Log.e("Started on & by", elements.get(i).getElementsByClass("started fr").text());
                    Log.e("Votes", stats.get(i).getElementsByClass("vote-count-post ").text());
                    Log.e("Answers", stats.get(i).getElementsByClass("status answered-accepted").text());
                    Log.e("Solution Link", elements.get(i).getElementsByTag("a").attr("href"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            assert doc != null;
            return  doc.title();
        }
    }
}
