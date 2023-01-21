package bg.sofia.uni.fmi.mjt.news.query;

import bg.sofia.uni.fmi.mjt.news.exceptions.InvalidQueryParameterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NewsQueryTest {

    @Test
    public void testBuilderThrowsWhenKeyWordsIsNull() {
        assertThrows(InvalidQueryParameterException.class,
                () -> NewsQuery.builder(null).build(),
                "Expected InvalidQueryParameterException to be thrown when keyWords is null");
    }

    @Test
    public void testBuilderThrowsWhenCountryIsNull() {
        assertThrows(InvalidQueryParameterException.class,
                () -> NewsQuery.builder("s").setCountry(null).build(),
                "Expected InvalidQueryParameterException to be thrown when county is null");
    }

    @Test
    public void testBuilderThrowsWhenCategoryIsNull() {
        assertThrows(InvalidQueryParameterException.class,
                () -> NewsQuery.builder("s").setCategory(null).build(),
                "Expected InvalidQueryParameterException to be thrown when category is null");
    }
    @Test
    public void testBuilderThrowsWhenPageSizeIsHigherThanTheLimit() {
        assertThrows(InvalidQueryParameterException.class,
                () -> NewsQuery.builder("s").setPageSize(60).build(),
                "Expected InvalidQueryParameterException to be thrown when pageSize is higher than the limit");
    }

    @Test
    public void testBuilderThrowsWhenPageSizeIsLowerThanTheLimit() {
        assertThrows(InvalidQueryParameterException.class,
                () -> NewsQuery.builder("s").setPageSize(0).build(),
                "Expected InvalidQueryParameterException to be thrown when pageSize is lower than the bound");
    }

    @Test
    public void testBuilderThrowsWhenPageIsHigherThanTheLimit() {
        assertThrows(InvalidQueryParameterException.class,
                () -> NewsQuery.builder("s").setPage(5).build(),
                "Expected InvalidQueryParameterException to be thrown when page is higher than the limit");
    }

    @Test
    public void testBuilderThrowsWhenPageIsLowerThanTheLimit() {
        assertThrows(InvalidQueryParameterException.class,
                () -> NewsQuery.builder("s").setPage(0).build(),
                "Expected InvalidQueryParameterException to be thrown when page is lower than the bound");
    }

    @Test
    public void testBuilderCreatesQuery() throws InvalidQueryParameterException {
        NewsQuery query = NewsQuery.builder("izbori")
                .setPageSize(6)
                .setCountry("bg")
                .setPage(1)
                .setCategory("Politics")
                .build();

        assertEquals("izbori", query.getKeyWords(), "KeyWords are not set properly!");
        assertEquals("bg", query.getCountry(), "Country is not set properly!");
        assertEquals("Politics", query.getCategory(), "Category is not set properly!");
        assertEquals("6", query.getPageSize(), "PageSize is not set properly!");
        assertEquals("1", query.getPage(), "Page is not set properly!");
    }
}
