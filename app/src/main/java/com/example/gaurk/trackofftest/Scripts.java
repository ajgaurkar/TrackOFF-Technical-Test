package com.example.gaurk.trackofftest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by gaurk on 3/13/2018.
 */
//Activity showing listview populated with script URLs
public class Scripts extends AppCompatActivity {
    private ListView scriptsListView;
    ArrayList<String> scripts_arraylist;
    int src_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scripts);

        //UI elements initializations
        scriptsListView = (ListView) findViewById(R.id.scripts_list_view);
        TextView totalScriptsTextView = (TextView) findViewById(R.id.scripts_count);
        TextView totalScriptsURLTextView = (TextView) findViewById(R.id.scripts_url_count);

        //Get response from Mainactivity in bundle
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String response = extras.getString("url_response");

        //Creating document object from response to perfrom further parsing
        Document document = Jsoup.parse(response);
        Elements elements = document.select("script");

        //looping through response to parse element attribute
        scripts_arraylist = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {

            //Check the element with script tags and fetch the data within "src" attribute
            if (!(elements.get(i).attr("src").isEmpty()) && elements.get(i).attr("src").endsWith(".js")) {
                System.out.println("attr : " + elements.get(i).getElementsByAttribute("src"));
                src_count++;
                scripts_arraylist.add(src_count + ". " + elements.get(i).attr("src"));
            }
        }

        //Set output to textviews
        totalScriptsTextView.setText(elements.size() + " Script tags found");
        totalScriptsURLTextView.setText(src_count + " Script URL found ");
        populateList(scripts_arraylist);

    }

    //Populate listview with the script url
    private void populateList(ArrayList<String> scripts_arraylist) {
        scriptsListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, scripts_arraylist));
    }

}
