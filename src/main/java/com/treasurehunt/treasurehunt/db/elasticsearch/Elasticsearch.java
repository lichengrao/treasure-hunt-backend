package com.treasurehunt.treasurehunt.db.elasticsearch;

import com.alibaba.fastjson.support.geo.Point;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.treasurehunt.treasurehunt.entity.Listing;
import com.treasurehunt.treasurehunt.entity.SearchListingsRequestBody;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.PointInTimeBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.json.JSONArray;
import org.json.JSONObject;


import java.io.DataInput;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Elasticsearch {

    // Deployment - double check here
    private static final String LISTINGS_INDEX = "listings";

    // Build request object for Elasticsearch query
    private static SearchRequest buildListingsSearchRequest(SearchListingsRequestBody requestBody) throws IOException {
        SearchRequest searchRequest = new SearchRequest(LISTINGS_INDEX);

        // Configure searchRequest with data in the requestBody
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(30);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        sourceBuilder.sort(new FieldSortBuilder("price").order(SortOrder.ASC));

        // Configure searchQuery and filters
        if (requestBody.getCategory() != null) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category", "keywords");
            sourceBuilder.query(termQueryBuilder);
        } else if (requestBody.getKeyword() != null && requestBody.getMaxPrice() == null) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("description", "text");
            sourceBuilder.query(matchQueryBuilder);
        } else if (requestBody.getKeyword() != null && requestBody.getMaxPrice() != null) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("description", "text");
            sourceBuilder.query(matchQueryBuilder);
            // TODO filter


        }
        searchRequest.source(sourceBuilder);

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
            JSONArray rawSearchResults = new JSONArray();

            // The SearchHits provides global information about all hits, like total number of hits or the maximum score
            SearchHits hits = searchResponse.getHits();
            // SearchHit provides access to basic information (Note: SearchHit and SearchHits are two different classes)
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                // do something with the SearchHit

                // To retrieve the whole SearchHit as a JSON string
                String sourceAsString = hit.getSourceAsString();
                JSONObject result = new JSONObject(sourceAsString);

                rawSearchResults.put(result);
            }

            return rawSearchResults.toString();
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
            ObjectMapper mapper = new ObjectMapper();

            // Create list of search results
            List<Listing> listingsSearchResults = Arrays.asList(mapper.readValue(rawSearchResults, Listing[].class));

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
            // create jsonMap for the listing object
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                // listing_id
                // TODO we already have listingId
                builder.field("listing_id", listing.getListingId());
                // title
                builder.field("title", listing.getTitle());
                // price
                builder.field("price", listing.getPrice());
                // category
                builder.field("category", listing.getCategory());
                // seller_name
                builder.field("seller_name", listing.getSellerName());
                // brand
                builder.field("brand", listing.getBrand());
                // item_condition
                builder.field("item_condition", listing.getItemCondition());
                // description
                builder.field("description", listing.getDescription());
                // address
                builder.field("address", listing.getAddress());
                // city and state
                builder.field("city_and_state", listing.getCityAndState());
                // location
                builder.startObject("location");
                {
                    builder.field("lat", listing.getGeocodeLocation().getLatitude());
                    builder.field("lon", listing.getGeocodeLocation().getLongitude());
                }
                builder.endObject();

                // picture_urls
                // Elasticsearch arrays do not require a dedicated field data type.
                // Any field can contain zero or more values by default
                builder.field("picture_urls", listing.getPictureUrls().toString());
                // date_created
                builder.field("date_created", listing.getDate());
            }
            builder.endObject();

            // Create index request
            IndexRequest request = new IndexRequest(LISTINGS_INDEX).id(listing.getListingId()).source(builder);
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

            // create jsonMap for the updated listing object
            XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                // listing_id
                // TODO we already have listingId
                builder.field("listing_id", listing.getListingId());
                // title
                builder.field("title", listing.getTitle());
                // category
                builder.field("category", listing.getCategory());
                // brand
                builder.field("brand", listing.getBrand());
                // item_condition
                builder.field("item_condition", listing.getItemCondition());
                // description
                builder.field("description", listing.getDescription());

                // picture_urls
                // Elasticsearch arrays do not require a dedicated field data type.
                // Any field can contain zero or more values by default
                builder.field("picture_urls", listing.getPictureUrls().toString());
                // date_created
                builder.field("date_created", listing.getDate());
            }
            builder.endObject();

            // Create update request
            UpdateRequest request = new UpdateRequest(LISTINGS_INDEX, listing.getListingId()).doc(builder);
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
            DeleteRequest request = new DeleteRequest(LISTINGS_INDEX, listing.getListingId());

            // Execute the request
            client.delete(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ElasticsearchException("Failed to delete Listing");
        }
    }
}
