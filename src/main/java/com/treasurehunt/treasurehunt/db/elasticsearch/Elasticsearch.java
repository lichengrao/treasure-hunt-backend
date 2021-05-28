package com.treasurehunt.treasurehunt.db.elasticsearch;

import com.treasurehunt.treasurehunt.entity.SearchListingsRequestBody;
import com.treasurehunt.treasurehunt.entity.SearchListingsResponseBody;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

public class Elasticsearch {

    private static final String LISTINGS_INDEX = "listings";

    // Build request object for Elasticsearch query
    private static SearchRequest buildListingsSearchRequest(SearchListingsRequestBody requestBody) {
        SearchRequest searchRequest = new SearchRequest(LISTINGS_INDEX);

        // Configure searchRequest with data in the requestBody
        // TODO

        // return searchRequest object
        return searchRequest;
    }

    // Send the request to Elasticsearch and receive the raw response
    private static String getRawSearchResults(RestHighLevelClient client, SearchRequest searchRequest) throws ElasticsearchException {
        try {
            // Send the request to Elasticsearch, and receive the results in searchResponse
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // Parse the response received from Elasticsearch
            // TODO

            return "";
        } catch (Exception e) {
            e.printStackTrace();
            throw new ElasticsearchException("Unable to receive search results from Elasticsearch");
        }
    }

    public static SearchListingsResponseBody getSearchResults(RestHighLevelClient client,
                                                              SearchListingsRequestBody requestBody) throws ElasticsearchException {

        try {
            // Get raw response from Elasicsearch
            String rawSearchResults = getRawSearchResults(client, buildListingsSearchRequest(requestBody));

            // Create the responseBody object from the raw response
            // TODO
            SearchListingsResponseBody responseBody = new SearchListingsResponseBody();

            // Return the responseBody
            return responseBody;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ElasticsearchException("Unable to parse results sent from Elasticsearch");
        }
    }
}
