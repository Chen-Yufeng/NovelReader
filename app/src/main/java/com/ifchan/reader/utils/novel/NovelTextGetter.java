package com.ifchan.reader.utils.novel;

import android.app.job.JobInfo;
import android.os.AsyncTask;
import android.os.Environment;

import com.ifchan.reader.entity.Index;
import com.ifchan.reader.utils.AppUtils;
import com.ifchan.reader.utils.DeviceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daily on 12/10/17.
 */

public class NovelTextGetter {
    private List<Index> mIndexList;
    private boolean isLoaded = false;
    public NovelTextGetter() {
        mIndexList = new ArrayList<>();
    }

    public List<Index> getIndexList() {
        return mIndexList;
    }

    public void setIndexList(List<Index> indexList) {
        mIndexList = indexList;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public void download(final String folderName, final int indexNum) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }

            @Override
            protected Void doInBackground(Void... voids) {
                URL url = null;
                try {
                    url = new URL(mIndexList.get(indexNum).getLink());
                    HttpURLConnection httpURLConnection = null;
                    InputStream inputStream = null;
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestProperty("User-Agent",
                            "ZhuiShuShenQi/3.40[preload=false;locale=zh_CN;" +
                                    "clientidbase=android-nvidia]");
                    httpURLConnection.setRequestProperty("X-User-Agent",
                            "ZhuiShuShenQi/3.40[preload=false;locale=zh_CN;" +
                                    "clientidbase=android-nvidia]");
                    // may need this header.
                    httpURLConnection.setRequestProperty("X-Device-Id", DeviceUtils.getIMEI(AppUtils
                            .getAppContext()));
                    httpURLConnection.setRequestProperty("Host", "chapterup.zhuishushenqi.com");
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("If-None-Match",
                            "W/\"2a04-4nguJ+XAaA1yAeFHyxVImg\"");
                    httpURLConnection.setRequestProperty("If-Modified-Since", "Tue, 02 Aug 2016 03:20:06" +
                            " UTC");
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(5000);
                    inputStream = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    JSONObject jsonObject1 = new JSONObject(builder.toString());
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("chapter");
                    File externalFolder = Environment.getExternalStorageDirectory();
                    File temp = new File(externalFolder.getPath() + "/Reader/temp/novel/"+
                            folderName+ "/" +
                            mIndexList.get(indexNum).getTitle());
                    if (!temp.getParentFile().exists()) {
                        temp.getParentFile().mkdirs();
                    }
                    PrintStream printStream = new PrintStream(new FileOutputStream(temp));
                    printStream.print(jsonObject2.getString("cpContent"));
                    printStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void downloadIndexList(final String bookId) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPostExecute(Void aVoid) {
                isLoaded = true;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String u = "http://api.zhuishushenqi.com/atoc?view=summary&book=" + bookId;
                    URL url = new URL(u);
                    HttpURLConnection httpURLConnection = null;
                    InputStream inputStream = null;
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    HeaderUtil.setConnectionHeader(httpURLConnection);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(5000);
                    inputStream = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    JSONArray jsonArray1 = new JSONArray(builder.toString());
                    JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                    String link = jsonObject1.getString("link");
                    String indexLink = link + "?view=chapters";
                    inputStream.close();
                    url = new URL(indexLink);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    HeaderUtil.setConnectionHeader(httpURLConnection);
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(5000);
                    inputStream = httpURLConnection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    builder = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    JSONObject jsonObject2 = new JSONObject(builder.toString());
                    JSONArray jsonArray2 = jsonObject2.getJSONArray("chapters");
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject jsonObject3 = jsonArray2.getJSONObject(i);
                        Index index = new Index(jsonObject3.getString("title"),
                                "http://chapterup.zhuishushenqi.com/chapter/" +
                                        jsonObject3.getString("link"));
                        mIndexList.add(index);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
