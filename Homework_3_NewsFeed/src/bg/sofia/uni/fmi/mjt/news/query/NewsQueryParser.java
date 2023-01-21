package bg.sofia.uni.fmi.mjt.news.query;

public class NewsQueryParser {
    private static final String KEY_WORDS_PARAMETER = "q=";
    private static final String COUNTRY_PARAMETER = "&country=";
    private static final String CATEGORY_PARAMETER = "&category=";
    private static final String PAGE_SIZE_PARAMETER = "&pageSize=";
    private static final String PAGE_PARAMETER = "&page=";

    public String parse(NewsQuery query) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append(KEY_WORDS_PARAMETER).append(query.getKeyWords());

        if (query.getCountry() != null) {
            queryBuilder.append(COUNTRY_PARAMETER).append(query.getCountry());
        }

        if (query.getCategory() != null) {
            queryBuilder.append(CATEGORY_PARAMETER).append(query.getCategory());
        }

        if (query.getPageSize() != null) {
            queryBuilder.append(PAGE_SIZE_PARAMETER).append(query.getPageSize());
        }

        if (query.getPage() != null) {
            queryBuilder.append(PAGE_PARAMETER).append(query.getPage());
        }

        return queryBuilder.toString();
    }
}
