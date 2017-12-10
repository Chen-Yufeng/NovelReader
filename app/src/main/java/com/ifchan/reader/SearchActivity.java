package com.ifchan.reader;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.ifchan.reader.adapter.HistoryGridViewAdapter;
import com.ifchan.reader.adapter.SearchSuggestionAdapter;
import com.ifchan.reader.entity.Book;
import com.ifchan.reader.helper.DataBaseHelper;
import com.ifchan.reader.utils.recyclerview.SimpleDividerItemDecoration;
import com.ifchan.reader.view.NonScrollGridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String API_HOTWORDS = "http://api.zhuishushenqi.com/book/search-hotword";
    private final String TAG = "@vir SearchActivity";
    private SearchView mSearchView;
    private List<Book> results = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private SearchSuggestionAdapter mAdapter;
    private DataBaseHelper mDataBaseHelper;
    private SQLiteDatabase db;
    private List<String> mHistorys;
    private NonScrollGridView mHistoryGridView;
    private HistoryGridViewAdapter mHistoryGridViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initToolBar();
        initDataBase();
        loadHistoryFromDateBase();
        initBasic();
        initRecyclerView();
    }

    private void loadHistoryFromDateBase() {
        Cursor cursor = db.query("History", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String history = cursor.getString(cursor.getColumnIndex("history"));
                mHistorys.add(history);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void initDataBase() {
        mDataBaseHelper = DataBaseHelper.getInstance(SearchActivity.this);
        db = mDataBaseHelper.getWritableDatabase();
        mHistorys = new ArrayList<>();
    }

    private void initBasic() {
//        hotWordsGridView = findViewById(R.id.activity_search_hot_words);
        mHistoryGridView = findViewById(R.id.activity_search_history);
//        HotWordsGridViewAdapter hotWordsGridViewAdapter = new HotWordsGridViewAdapter
//                (SearchActivity.this, getHotWords());
        mHistoryGridViewAdapter = new HistoryGridViewAdapter(SearchActivity
                .this, getHistory());
//        hotWordsGridView.setAdapter(hotWordsGridViewAdapter);
        mHistoryGridView.setAdapter(mHistoryGridViewAdapter);
        mHistoryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSearchView.setQuery(mHistorys.get(position),false);
            }
        });

        ImageButton imageButton = findViewById(R.id.activity_search_delete_history);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataBaseHelper.removeAllColumns(db);
                renewHistoryDisplay();
            }
        });
    }

    private List<String> getHistory() {
        return mHistorys;
    }

    private List<String> getHotWords() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                URL url = null;
                HttpURLConnection connection = null;
                try {
                    url = new URL(API_HOTWORDS);
                    connection = (HttpURLConnection) url.openConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();

        return null;
    }

    private void initRecyclerView() {
        mRecyclerView = findViewById(R.id.activity_search_suggestion_recycler_view);
        mAdapter = new SearchSuggestionAdapter(results, SearchActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(SearchActivity.this));
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.activity_search_toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_activity, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        setSearchViewAbbr();
        setSearchViewListener();

        return super.onCreateOptionsMenu(menu);
    }

    private void setSearchViewListener() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()) {
                    hideSuggestion();
                    return false;
                }
                getSuggestion(query);
                addInDataBase(query);
                mHistorys.add(query);
                renewHistoryDisplay();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    hideSuggestion();
                } else {
                    getSuggestion(newText);
                }
                return false;
            }
        });
    }

    private void setSearchViewAbbr() {
        mSearchView.setSubmitButtonEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getSuggestion(String input) {
        try {
            final String api = "http://api.zhuishushenqi.com/book/fuzzy-search?query=" + URLEncoder
                    .encode(input, "UTF-8");
            new AsyncTask<Void, Void, Void>() {
                int lastSize = 0;

                @Override
                protected void onPostExecute(Void aVoid) {
                    if (results.size() != 0) {
                        displaySuggestion();
                        clearRecyclerView(lastSize);
                        renewSuggestion(results);
                    }
                }

                @Override
                protected Void doInBackground(Void... voids) {
                    URL url = null;
                    InputStream inputStream = null;
                    HttpURLConnection httpURLConnection;
                    try {
                        url = new URL(api);
                        httpURLConnection = (HttpURLConnection) url
                                .openConnection();
                        httpURLConnection.setRequestMethod("GET");
                        inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader
                                (inputStream));
                        String line;
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        bufferedReader.close();
                        JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                        JSONArray keyWordsJSONArray = jsonObject.getJSONArray("books");
                        lastSize = results.size();
                        Log.d(TAG, "lastSize=" + lastSize);
                        results.clear();
                        File externalFolder = Environment.getExternalStorageDirectory();
                        File imageTemp = new File(externalFolder.getPath() + "/Reader/temp/cover");
                        if (!imageTemp.exists()) {
                            imageTemp.mkdirs();
                        }
                        for (int index = 0; index < keyWordsJSONArray.length(); index++) {
                            JSONObject aBook = keyWordsJSONArray.getJSONObject(index);
                            String bookid = aBook.getString("_id");
                            String title =  aBook.getString("title");
                            String author =  aBook.getString("author");
                            String shortIntro =  aBook.getString("shortIntro");
                            String cover =  aBook.getString("cover");
                            cover = URLDecoder.decode(cover,"UTF-8");
                            cover = cover.substring(cover.indexOf('h'),cover.lastIndexOf('/'));
                            String coverPath = imageTemp.getPath() + "/" +
                                    bookid + ".jpg";
                            // TODO: 12/9/17 cover ans coverPath needs format
                            String site =  aBook.getString("site");
                            String latelyFollower =  aBook.getString("latelyFollower");

                            String retentionRatio =  aBook.getString("retentionRatio");
                            String majorCate =  aBook.getString("cat");
                            results.add(new Book(bookid, title, author, shortIntro, cover, coverPath,
                                    site, Integer.parseInt(latelyFollower), retentionRatio, majorCate));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return null;
                }
            }.execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    // TODO: 12/5/17 COMPLETE IT !

    private void displaySuggestion() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideSuggestion() {
        mRecyclerView.setVisibility(View.GONE);
    }

    private void renewSuggestion(List<Book> bookList) {
        mAdapter.setBookList(bookList);
        mAdapter.notifyItemRangeInserted(0, bookList.size() - 1);
    }

    private void clearRecyclerView(int size) {
        mAdapter.notifyItemRangeRemoved(0, size - 1);
    }

    private void addInDataBase(String text) {
        mDataBaseHelper.addHistory(mHistorys.size()+1, text, db);
    }

    private void renewHistoryDisplay() {
//        mHistoryGridView.setAdapter(new HistoryGridViewAdapter(SearchActivity.this, mHistorys));
        mHistoryGridViewAdapter.notifyDataSetChanged();
    }
}
