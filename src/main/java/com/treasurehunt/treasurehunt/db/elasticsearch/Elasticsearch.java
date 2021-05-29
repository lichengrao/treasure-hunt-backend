package com.treasurehunt.treasurehunt.db.elasticsearch;

import com.treasurehunt.treasurehunt.entity.Listing;
import com.treasurehunt.treasurehunt.entity.SearchListingsRequestBody;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Elasticsearch {

    // Deployment - double check here
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

    // Search the listings index and return a list of listings
    public static List<Listing> getSearchResults(RestHighLevelClient client,
                                                 SearchListingsRequestBody requestBody) throws ElasticsearchException {

        try {
            // Get raw response from Elasicsearch
            String rawSearchResults = getRawSearchResults(client, buildListingsSearchRequest(requestBody));

            // Create list of search results
            List<Listing> listingsSearchResults = new ArrayList<>();

            // Add results to listingsSearchResults
            // TODO

            // Return the responseBody
            return listingsSearchResults;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ElasticsearchException("Unable to parse search results sent from Elasticsearch");
        }
    }

    // Add listing to the listings index
    public static void addListing(RestHighLevelClient client, Listing listing) throws ElasticsearchException {
        try {
            // Create index request
            IndexRequest request = new IndexRequest(LISTINGS_INDEX);
            // create jsonMap for the listing object
            // TODO
            Map<String, Object> jsonMap = new HashMap<>();
            request.id("INSERT_DOCUMENT_ID_HERE").source(jsonMap);

            // Execute the request
            client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ElasticsearchException("Failed to create Listing");
        }
    }

    // Update existing listing in the listings index
    public static void updateListing(RestHighLevelClient client, Listing listing) throws ElasticsearchException {
        try {
            // Create update request
            UpdateRequest request = new UpdateRequest(LISTINGS_INDEX, "INSERT_DOCUMENT_ID_HERE");

            // create jsonMap for the updated listing object
            // TODO
            Map<String, Object> jsonMap = new HashMap<>();
            request.doc(jsonMap);

            // Execute the request
            client.update(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ElasticsearchException("Failed to update Listing");
        }
    }

    // Delete existing listing from the listings index
    public static void deleteListing(RestHighLevelClient client, Listing listing) throws ElasticsearchException {
        try {
            // Create delete request
            DeleteRequest request = new DeleteRequest(LISTINGS_INDEX, "INSERT_DOCUMENT_ID_HERE");

            // Execute the request
            client.delete(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ElasticsearchException("Failed to delete Listing");
        }
    }
}
