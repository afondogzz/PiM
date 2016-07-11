package com.dogzz.pim.datahandlers;

import com.dogzz.pim.dataobject.ArticleHeader;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;


/**
 * Created by afon on 02.07.2016.
 */
public class ArticlesListTest {
    @Test
    public void getArticlesHeaders() throws Exception {
        ArticlesList articlesList = new ArticlesList();
        List<ArticleHeader> result = articlesList.getArticlesHeaders();
//        assertThat(result, equalToIgnoringCase(""));
    }

}