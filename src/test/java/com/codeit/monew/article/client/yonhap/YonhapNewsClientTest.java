package com.codeit.monew.article.client.yonhap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YonhapNewsClientTest {

    @Autowired
    private YonhapNewsClient yonhapNewsClient;

    @Test
    void testFetchNews() {
        List<YonhapNewsClient.YonhapNewsItem> items = yonhapNewsClient.fetchNews();
        assertNotNull(items);
        assertFalse(items.isEmpty(), "RSS feed items should not be empty");
        for (YonhapNewsClient.YonhapNewsItem item : items) {
            assertNotNull(item.title());
            assertNotNull(item.link());
            assertNotNull(item.pubDate());
        }
    }
}
