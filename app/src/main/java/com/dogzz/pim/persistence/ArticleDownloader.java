package com.dogzz.pim.persistence;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;
import com.dogzz.pim.R;
import com.dogzz.pim.asynctask.DownloadTask;
import com.dogzz.pim.datahandlers.ArticleExtractor;
import com.dogzz.pim.datahandlers.HeadersList;
import com.dogzz.pim.dataobject.ArticleHeader;
import it.sephiroth.android.library.picasso.Picasso;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

import static android.content.Context.MODE_PRIVATE;
import static android.provider.Telephony.Mms.Part.FILENAME;
import static com.dogzz.pim.datahandlers.HeadersList.BASE_URL;

/**
 * Class for download and save article offline
 * Created by dogzz on 15.07.2016.
 */
public class ArticleDownloader {
    private static final String LOG_TAG = "ArticleDownloader";
    public static final String FILE_EXT = ".html";
    protected Activity mainActivity;
    private String downloadResult;
    private SQLiteDatabase db;
    private DBHelper mDBHelper;
    private ArticleHeader header;

    public ArticleDownloader(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void saveArticleOffline(ArticleHeader header) {
        this.header = header;
        mDBHelper = new DBHelper(mainActivity, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        db = mDBHelper.getWritableDatabase();
        DownloadTask downloadTask = new DownloadArticleTask();
        downloadTask.execute(header.getArticleUrl());
    }

    public void removeArticle(ArticleHeader selectedArticleHeader) {
        this.header = selectedArticleHeader;
        mDBHelper = new DBHelper(mainActivity, DBHelper.DB_NAME, null, DBHelper.DB_VERSION);
        db = mDBHelper.getWritableDatabase();
        //TODO implement removal of article file and associated pictures
        if (mDBHelper!=null) mDBHelper.close();
    }


    public class DownloadArticleTask extends DownloadTask {

        @Override
        protected Integer doInBackground(String... urls) {
            Integer result;
            // params comes from the execute() call: params[0] is the BASE_URL.
            try {
                result = downloadUrl(urls[0]);
                result = saveArticleContent(result, resultMessage);

            } catch (IOException e) {
                downloadResult = "Error: Unable to retrieve source data. The source is inaccessible.";
                result = 0;
            }
            return result;
        }

        private int saveArticleContent(Integer result, String content) {
            if (result == 1) {
                try {
                    String pureArticle = extractArticle(content);
                    String articleWithoutImg = makeImagesLocal(pureArticle);
                    //save
                    String fileName = saveToFile(articleWithoutImg);
                    saveResultToDB(fileName);
                    if (mDBHelper!=null) mDBHelper.close();
                    return 1;
                } catch (Exception e) {
                    resultMessage = "Something went wrong with loaded data. ".concat(e.getMessage());
                    Log.d(LOG_TAG, resultMessage);
                    return 0;
                }
            } else {
                return 0;
            }
        }

        private String saveToFile(String pureArticle) throws IOException{
            String fileName = header.getArticleUrl().replace(BASE_URL, "");
            fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            if (fileName.contains(".")) {
                fileName = fileName.substring(0,  fileName.lastIndexOf("."));
            }
            writeFile(pureArticle, fileName);
            return fileName;
        }

        private void writeFile(String pureArticle, String fileName) throws IOException{
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                        mainActivity.openFileOutput(fileName.concat(FILE_EXT), MODE_PRIVATE)));
                bw.write(pureArticle);
                bw.close();
                Log.d(LOG_TAG, "File written");
        }

        private void saveResultToDB(String fileName) {
            header.markArticleAsSaved(db, fileName);
        }

        private String extractArticle(String downloadResult) {
            return ArticleExtractor.extractArticle(downloadResult);
        }

        private String makeImagesLocal(String pureArticle) {
            String resultHtml = pureArticle;
            Document doc = Jsoup.parse(pureArticle);
            Elements imgTags = doc.select("img");
            for (Element imgTag: imgTags) {
                String imageUrl = imgTag.attr("src");
                String imageFileName = downloadImage(imageUrl);
                makeImgLinksLocal(imageFileName, imgTag);
            }
            return resultHtml = doc.html();
        }



        private String downloadImage(String imageUrl) {
            String url = imageUrl;
            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.length());
            FileOutputStream out = null;
            if (!imageUrl.contains(BASE_URL)) {
                url = BASE_URL.concat("/").concat(url);
            }
            try {
                Bitmap bmp = Picasso.with(mainActivity).load(url).get();
                out = mainActivity.openFileOutput(filename, MODE_PRIVATE);
                String format = filename.substring(filename.lastIndexOf(".") + 1, filename.length()).toUpperCase();
                format = format.replace("JPG", "JPEG");
                Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.valueOf(format);
                bmp.compress(compressFormat, 90, out);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Image downloading failed. " + e.getMessage());
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return filename;
        }

        private void makeImgLinksLocal(String imageFileName, Element imgTag) {
            imgTag.attr("src", "file://".concat(mainActivity.getFilesDir().getAbsolutePath())
                    .concat("/").concat(imageFileName));
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Integer result) {
            downloadResult = resultMessage;
            if (result == 1) {
                Toast.makeText(mainActivity, "Download finished",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mainActivity, resultMessage,
                        Toast.LENGTH_LONG).show();
            }
            mainActivity.onBackPressed();
//            saveArticleContent(result);
        }
    }


}
