package org.yxm.demo.easyswiperefreshlayout.repo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.yxm.demo.easyswiperefreshlayout.pojo.Article;

public class ArticleRepo {

  public List<Article> getArticles() {
    List<Article> datas = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      Article article = new Article();
      article.title = "title:" + i;
      article.content = "content:" + i;
      article.time = new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date());
      datas.add(article);
    }
    return datas;
  }

  public Article getRadomArticle() {
    Random random = new Random();
    int index = random.nextInt(100);
    Article article = new Article();
    article.title = "title:" + index;
    article.content = "content:" + index;
    article.time = new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date());
    return article;
  }
}
