package com.ifchan.reader.fragment;

import android.os.Environment;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ifchan.reader.R;
import com.ifchan.reader.entity.Book;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daily on 11/25/17.
 */

public class FragmentHot extends Fragment {
    private List<Book> mBookList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        getJSON();
        initRecyclerView();
        return inflater.inflate(R.layout.fragment_hot, container, false);
    }

    private void getJSON() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                InputStream inputStream = null;
                try {
                    url = new URL("http://api.zhuishushenqi.com/cats/lv2/statistics");
                    inputStream = url.openStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    JSONObject jsonObject = new JSONObject(builder.toString());
                    JSONArray books = jsonObject.getJSONArray("books");
                    File externalFolder = Environment.getExternalStorageDirectory();
                    File imageTemp = new File(externalFolder.getPath() + "/Reader/temp/cover");
                    if (!imageTemp.exists()) {
                        imageTemp.mkdirs();
                    }
                    for (int i = 0; i < books.length(); i++) {
                        JSONObject jsonObjectBook = books.getJSONObject(i);
                        String id = jsonObjectBook.getString("_id");
                        String title = jsonObjectBook.getString("title");
                        String author = jsonObjectBook.getString("author");
                        String shortIntro = jsonObjectBook.getString("shortIntro");
                        String cover = jsonObjectBook.getString("cover");
                        cover = URLDecoder.decode(cover);
                        String coverPath = imageTemp.getPath() + "/" +
                                id + ".jpg";
                        String site = jsonObjectBook.getString("site");
                        int banned = jsonObjectBook.getInt("banned");
                        int latelyFollower = jsonObjectBook.getInt("latelyFollower");
                        String retentionRatio = Integer.toString(jsonObjectBook.getInt
                                ("retentionRatio"));
                        Book book = new Book(id, title, author, shortIntro, cover, site, banned,
                                latelyFollower, retentionRatio);
                        book.setCoverPath(coverPath);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void initRecyclerView() {

    }
}
