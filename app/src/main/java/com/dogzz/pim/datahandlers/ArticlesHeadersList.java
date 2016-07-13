/*
* @Author: dogzz
* @Created: 7/13/2016
*/

package com.dogzz.pim.datahandlers;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.support.v7.widget.RecyclerView;
import com.dogzz.pim.dataobject.ArticleHeader;
import com.dogzz.pim.exception.SourceConnectException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArticlesHeadersList extends HeadersList {

    public ArticlesHeadersList(RecyclerView recyclerView, Activity activity, ConnectivityManager connectivityManager) {
        super(recyclerView, activity, connectivityManager);
    }

    protected void loadArticlesListFromSource() throws SourceConnectException {
        DownloadArticlesListTask downloadTask = new DownloadArticlesListTask();
//            String url = currentPageNumber == 1 ? BASE_URL.concat("/") : BASE_URL.concat("/?page=").concat(String.valueOf(currentPageNumber));
        String url = BASE_URL.concat(String.format(PATH_URL, (currentPageNumber-1)*12));
        downloadTask.execute(url);
    }

    @Override
    protected List<ArticleHeader> extractArticlesHeaders(String result) {
        List<ArticleHeader> headers = new ArrayList<>();
        Document doc = Jsoup.parse(result);
        Elements rawHeaders = doc.select("div[class~=effect.*article]");
        Collections.reverse(rawHeaders);
        for (Element rawHeader : rawHeaders) {
            ArticleHeader header = new ArticleHeader();
            header.setTitle(rawHeader.select("h4").text());
            header.setSubTitle(rawHeader.select("p").text());
            header.setArticleUrl(BASE_URL.concat("/").concat(rawHeader.select("a").attr("href").replace("/", "")));
            header.setArticleImageUrl(BASE_URL.concat("/").concat(rawHeader.select("img").attr("data-original").trim()));
            header.setLoadDate(System.currentTimeMillis());
            header.setType(0);
            headers.add(header);
        }
        Collections.reverse(headers);
        return headers;
    }
}
