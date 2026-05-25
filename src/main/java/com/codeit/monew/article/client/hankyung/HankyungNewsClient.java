package com.codeit.monew.article.client.hankyung;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@Component
public class HankyungNewsClient {

    private final RestClient restClient;
    private static final String HANKYUNG_FEED_URL = "https://www.hankyung.com/feed/all-news";

    public HankyungNewsClient() {
        this.restClient = RestClient.builder()
                .baseUrl(HANKYUNG_FEED_URL)
                .build();
    }

    public record HankyungNewsItem(
            String title,
            String link,
            LocalDateTime pubDate
    ) {}

    public List<HankyungNewsItem> fetchNews() {
        List<HankyungNewsItem> items = new ArrayList<>();
        try {
            String xmlContent = restClient.get()
                    .retrieve()
                    .body(String.class);

            if (xmlContent == null || xmlContent.isBlank()) {
                return items;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Secure XML parsing against XXE vulnerabilities
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlContent)));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("item");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String title = getTagValue("title", element);
                    String link = getTagValue("link", element);
                    String pubDateStr = getTagValue("pubDate", element);

                    LocalDateTime pubDate = null;
                    if (pubDateStr != null && !pubDateStr.isBlank()) {
                        try {
                            pubDate = java.time.ZonedDateTime.parse(pubDateStr, DateTimeFormatter.RFC_1123_DATE_TIME).toLocalDateTime();
                        } catch (Exception e) {
                            pubDate = LocalDateTime.now();
                        }
                    } else {
                        pubDate = LocalDateTime.now();
                    }

                    items.add(new HankyungNewsItem(title, link, pubDate));
                }
            }
        } catch (Exception e) {
            // Return empty or partial list on error
        }
        return items;
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node != null) {
                return node.getTextContent();
            }
        }
        return "";
    }
}
