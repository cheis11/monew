package com.codeit.monew.article.client.chosun;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChosunNewsClientTest {

    @Autowired
    private ChosunNewsClient chosunNewsClient;

    @Test
    void testFetchNews() {
        List<ChosunNewsClient.ChosunNewsItem> items = chosunNewsClient.fetchNews();
        assertNotNull(items);
        assertFalse(items.isEmpty(), "RSS feed items should not be empty");
        for (ChosunNewsClient.ChosunNewsItem item : items) {
            assertNotNull(item.title());
            assertNotNull(item.link());
            assertNotNull(item.pubDate());
        }
    }
}
