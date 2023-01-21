package bg.sofia.uni.fmi.mjt.news.dto;

import java.util.Arrays;
import java.util.Objects;

public class News {
    private final String status;
    private final int totalResults;
    private final Article[] articles;


    public News(String status, int totalResults, Article[] articles) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = articles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        News news = (News) o;
        return totalResults == news.totalResults
                && Objects.equals(status, news.status)
                && Arrays.equals(articles, news.articles);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(status, totalResults);
        result = 31 * result + Arrays.hashCode(articles);
        return result;
    }
}
