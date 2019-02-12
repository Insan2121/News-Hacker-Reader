package com.example.insan.newshackerreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Map<Integer, String> articleURLs = new HashMap<Integer, String>();
    Map<Integer, String> articleTitles = new HashMap<Integer, String>();
    ArrayList<Integer> articleIds   = new ArrayList<Integer>();

    SQLiteDatabase articleDB ;

    ArrayList<String> titles = new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    ArrayList<String> urls = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                i.putExtra("articleUrl", urls.get(position));
                startActivity(i);

                Log.i("articleURL", urls.get(position));

            }
        });
        articleDB = this.openOrCreateDatabase("Articles",MODE_PRIVATE, null);


        articleDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY, articleId INTEGER, url  VARCHAR, title VARCHAR, content VARCHAR)");


        updateListView();




        articleDB.execSQL("DELETE FROM articles");

        DownloadTask task = new DownloadTask();
        try {
            String result = task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();

            JSONArray jsonArray = new JSONArray(result);

            for (int i = 0; i < 20 ; i++) {

                String articleId = jsonArray.getString(i);

               DownloadTask getArticle = new DownloadTask();

                String articleInfo = getArticle.execute("https://hacker-news.firebaseio.com/v0/item/"+ articleId+ ".json?print=pretty").get();

                JSONObject jsonObject = new JSONObject(articleInfo);


                String articleTitle = jsonObject.getString("title");
                String artileURL = jsonObject.getString("url");

                articleIds.add(Integer.valueOf(articleId));
                articleTitles.put(Integer.valueOf(articleId), articleTitle);
                articleURLs.put(Integer.valueOf(articleId), artileURL);


               String sql =  "INSERT INTO articles(articleId, url, title) VALUES (?,?,?)";

                SQLiteStatement statement = articleDB.compileStatement(sql);

                statement.bindString(1, articleId);
                statement.bindString(2, artileURL);
                statement.bindString(3, articleTitle);

                statement.execute();

            }

            updateListView();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateListView() {

        try {


            Cursor c = articleDB.rawQuery("SELECT * FROM articles ORDER BY articleId DESC", null);

            int articleIdIndex = c.getColumnIndex("articleId");
            int urlIndex = c.getColumnIndex("url");
            int titleIndex = c.getColumnIndex("title");


            c.moveToFirst();

            titles.clear();
            urls.clear();

            while (c.moveToNext()) {

                titles.add(c.getString(titleIndex));
                urls.add(c.getString(urlIndex));

                Log.i("articleId", Integer.toString(c.getInt(articleIdIndex)));
                Log.i("articleUrl", c.getString(urlIndex));
                Log.i("articleTitle", c.getString(titleIndex));

            }


            arrayAdapter.notifyDataSetChanged();

        }catch (Exception e) {
            e.printStackTrace();
        }




    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();


                while (data != -1) {
                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }



            } catch (Exception e) {
                e.printStackTrace();
            }


            return result;
        }
    }

}
