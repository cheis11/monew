package com.codeit.monew.article.client.hankyung;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HankyungNewsClientTest {

    @Autowired
    private HankyungNewsClient hankyungNewsClient;

    @Test
    void testFetchNews() {
        List<HankyungNewsClient.HankyungNewsItem> items = hankyungNewsClient.fetchNews();
        assertNotNull(items);
        assertFalse(items.isEmpty(), "RSS feed items should not be empty");
        for (HankyungNewsClient.HankyungNewsItem item : items) {
            assertNotNull(item.title());
            assertNotNull(item.link());
            assertNotNull(item.pubDate());
        }
    }
}
