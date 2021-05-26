package com.treasurehunt.treasurehunt.db.elastic_search;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ElasticsearchIntegrationDemo {
    public static void main(String[] args) {
        try {
            ElasticsearchClient elasticsearchClient = new ElasticsearchClient();
            try (RestHighLevelClient client = elasticsearchClient.client) {

                // Indexing
                Map<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("description", "this is a test");
                jsonMap.put("timestamp", new Date());
                IndexRequest indexRequest = new IndexRequest("testing")
                        .id("1").source(jsonMap);
                client.index(indexRequest, RequestOptions.DEFAULT);

                System.out.printf("Indexed document to index \"%s\" with id %s%n",
                        indexRequest.index(),
                        indexRequest.id());

                // Getting a Document
                GetRequest getRequest = new GetRequest("testing", "1");
                GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

                System.out.printf("Retrieved document: {Id: %s, Description: %s, Test Time: %s}%n",
                        getResponse.getId(),
                        getResponse.getSource().get("description"),
                        getResponse.getSource().get("timestamp"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
