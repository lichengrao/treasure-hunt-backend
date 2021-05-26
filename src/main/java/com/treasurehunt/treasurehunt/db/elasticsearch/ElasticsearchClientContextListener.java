package com.treasurehunt.treasurehunt.db.elasticsearch;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@WebListener("Creates a ES high level client that is stored in the Servlet's context for later use via attribute " +
        "es-client")
public class ElasticsearchClientContextListener implements ServletContextListener {

    private void testESConnection(RestHighLevelClient client) throws ElasticsearchException {
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
            throw new ElasticsearchException("Error while connecting to Elasticsearch");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        RestHighLevelClient client = (RestHighLevelClient) event.getServletContext().getAttribute("es-client");
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new ElasticsearchException("Error while trying to close Elasticsearch Connection");
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        RestHighLevelClient client = (RestHighLevelClient) servletContext.getAttribute("es-client");
        if (client == null) {
            try {
                ElasticsearchClient elasticsearchClient = new ElasticsearchClient();
                client = elasticsearchClient.client;
                servletContext.setAttribute("es-client", client);
            } catch (IOException e) {
                throw new RuntimeException("Unable to connect to Elasticsearch");
            }
        }

        // Test Elasticsearch connection
        try {
            testESConnection(client);
            System.out.println("Successfully created connection to Elasticsearch");
        } catch (ElasticsearchException ex) {
            throw new RuntimeException(
                    "Unable to verify Elasticsearch index, please double check and try again.",
                    ex);
        }
    }
}
