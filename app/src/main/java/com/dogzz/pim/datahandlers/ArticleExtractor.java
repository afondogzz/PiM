/*
* @Author: dogzz
* @Created: 7/15/2016
*/

package com.dogzz.pim.datahandlers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArticleExtractor {

    public static String extractArticle(String result) {
        String resultHtml;
        Document doc = Jsoup.parse(result);
        Elements heading = doc.select("div[class=heading]");
        heading.select("div[class*=Breadcrumb]").first().text(""); //remove breadcrumbs
        heading.select("img").attr("width", "99%"); //resize images
        Elements mainContent = doc.select("div[class=mainContent]");
        mainContent.select("img").attr("width", "99%"); //resize images
//        mainContent.select("iframe[src*=youtube]").attr("width", "99%");
        mainContent.select("iframe").attr("width", "99%").removeAttr("height");
        mainContent.select("iframe").attr("height", "99%");
        resultHtml = heading.html().concat(mainContent.html());
        return resultHtml;
    }
}
