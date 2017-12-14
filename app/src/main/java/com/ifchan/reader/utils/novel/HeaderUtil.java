package com.ifchan.reader.utils.novel;

import com.ifchan.reader.utils.AppUtils;
import com.ifchan.reader.utils.DeviceUtils;

import java.net.HttpURLConnection;

/**
 * Created by daily on 12/14/17.
 */

public class HeaderUtil {
    public static void setConnectionHeader(HttpURLConnection connection) {
        connection.setRequestProperty("User-Agent",
                "ZhuiShuShenQi/3.40[preload=false;locale=zh_CN;" +
                        "clientidbase=android-nvidia]");
        connection.setRequestProperty("X-User-Agent",
                "ZhuiShuShenQi/3.40[preload=false;locale=zh_CN;" +
                        "clientidbase=android-nvidia]");
        // may need this header.
        connection.setRequestProperty("X-Device-Id", DeviceUtils.getIMEI(AppUtils
                .getAppContext()));
        connection.setRequestProperty("Host", "api.zhuishushenqi.com");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("If-None-Match",
                "W/\"2a04-4nguJ+XAaA1yAeFHyxVImg\"");
        connection.setRequestProperty("If-Modified-Since", "Tue, 02 Aug 2016 03:20:06" +
                " UTC");
    }
}
