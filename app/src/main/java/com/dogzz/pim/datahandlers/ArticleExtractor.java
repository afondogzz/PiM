/*
* @Author: dogzz
* @Created: 7/15/2016
*/

package com.dogzz.pim.datahandlers;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArticleExtractor {

    public static String extractArticle(String result, boolean showVideo, int videoWidth) {
        String resultHtml;
        videoWidth = videoWidth - 40;
        Document doc = Jsoup.parse(result);
        Elements heading = doc.select("div[class=heading]");
        heading.select("div[class*=Breadcrumb]").first().text(""); //remove breadcrumbs
        heading.select("img").attr("width", "99%"); //resize images
        Elements mainContent = doc.select("div[class=mainContent]");
        mainContent.select("img").attr("width", "99%").removeAttr("height"); //resize images
//        mainContent.select("iframe[src*=youtube]").attr("width", "99%");
//        mainContent.select("iframe").attr("width", "99%").removeAttr("height");
//        mainContent.select("iframe").attr("height", "99%");
        if (!showVideo) {
            Elements iframes = mainContent.select("iframe").tagName("a");
            for (Element iframe : iframes) {
                String url = iframe.attr("src");
                iframe.attr("href", url).text("YouTube Video").removeAttr("src");
            }
        } else {
            Elements iframes = mainContent.select("iframe");
            for (Element iframe : iframes) {
                String width = iframe.attr("width");
                String height = iframe.attr("height");
                int ratio = Integer.valueOf(width) / videoWidth;
                int newHeight = Integer.valueOf(height) * ratio;

            }
        }
        resultHtml = heading.html().concat(mainContent.html());
        return resultHtml;
    }
}
