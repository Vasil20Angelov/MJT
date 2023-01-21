package bg.sofia.uni.fmi.mjt.news.query;

import bg.sofia.uni.fmi.mjt.news.exceptions.InvalidQueryParameterException;

public class NewsQuery {

    private final String country;
    private final String category;
    private final String keyWords;
    private final String pageSize;
    private final String page;

    public static NewsQueryBuilder builder(String keyWords) throws InvalidQueryParameterException {
        return new NewsQueryBuilder(keyWords);
    }

    private NewsQuery(NewsQueryBuilder builder) {
        keyWords = builder.keyWords;
        category = builder.category;
        country = builder.country;
        pageSize = builder.pageSize;
        page = builder.page;
    }

    public String getCountry() {
        return country;
    }

    public String getCategory() {
        return category;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public String getPageSize() {
        return pageSize;
    }

    public String getPage() {
        return page;
    }

    public static class NewsQueryBuilder {
        private static final int MAX_PAGE = 3;
        private static final int MAX_PAGE_SIZE = 50;

        private final String keyWords;
        private String country;
        private String category;
        private String pageSize;
        private String page;

        public NewsQueryBuilder(String keyWords) throws InvalidQueryParameterException {
            if (keyWords == null || keyWords.isBlank()) {
                throw new InvalidQueryParameterException("The key words cannot be null or empty!");
            }

            this.keyWords = keyWords;
        }

        public NewsQueryBuilder setCountry(String country) throws InvalidQueryParameterException {
            if (country == null || country.isBlank()) {
                throw new InvalidQueryParameterException("The country cannot be null or empty!");
            }

            this.country = country;
            return this;
        }

        public NewsQueryBuilder setCategory(String category) throws InvalidQueryParameterException {
            if (category == null || category.isBlank()) {
                throw new InvalidQueryParameterException("The category cannot be null or empty!");
            }

            this.category = category;
            return this;
        }

        public NewsQueryBuilder setPageSize(int pageSize) throws InvalidQueryParameterException {
            if (pageSize < 1 || pageSize > MAX_PAGE_SIZE) {
                throw new InvalidQueryParameterException("The page number must be in range [1; " + MAX_PAGE_SIZE);
            }

            this.pageSize = Integer.toString(pageSize);
            return this;
        }

        public NewsQueryBuilder setPage(int page) throws InvalidQueryParameterException {
            if (page < 1 || page > MAX_PAGE) {
                throw new InvalidQueryParameterException("The page number must be in range [1; " + MAX_PAGE);
            }

            this.page = Integer.toString(page);
            return this;
        }

        public NewsQuery build() {
            return new NewsQuery(this);
        }
    }
}
