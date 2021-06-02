package com.treasurehunt.treasurehunt.db.elasticsearch;

import com.alibaba.fastjson.support.geo.Point;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.treasurehunt.treasurehunt.entity.GeocodeLocation;
import com.treasurehunt.treasurehunt.entity.Listing;
import com.treasurehunt.treasurehunt.entity.SearchListingsRequestBody;
import com.treasurehunt.treasurehunt.utils.DbUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Elasticsearch {

    // Deployment - double check here
    private static final String LISTINGS_INDEX = "listings";
    private static final int MAX_NUMBER_OF_SEARCH_RESULTS = 30;

    // Build request object for Elasticsearch query
    private static SearchRequest buildListingsSearchRequest(SearchListingsRequestBody requestBody) throws IOException {
        SearchRequest searchRequest = new SearchRequest(LISTINGS_INDEX);

        // Configure searchRequest with data in the requestBody
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.size(MAX_NUMBER_OF_SEARCH_RESULTS);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        sourceBuilder.sort(new FieldSortBuilder("price").order(SortOrder.ASC));

        // Configure searchQuery and filters
        if (requestBody.getKeyword() != null) {

            // BoolQueryBuilder is used to assemble query conditions.
            BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
            boolBuilder.must(QueryBuilders.multiMatchQuery(requestBody.getKeyword(), "title", "description"));
            if (requestBody.getCondition() != null) {
                boolBuilder.filter(QueryBuilders.termQuery("item_condition", requestBody.getCondition()));
            }
            if (requestBody.getDistance() != null) {
                boolBuilder.filter(QueryBuilders.geoDistanceQuery("location").point(requestBody.getLatitude(), requestBody.getLongitude()).distance(requestBody.getDistance(), DistanceUnit.MILES));
            }
            if (requestBody.getMaxPrice() != 0.0) {
                boolBuilder.filter(QueryBuilders.rangeQuery("price").lte(requestBody.getMaxPrice()));
            }
            if (requestBody.getMinPrice() != 0.0) {
                boolBuilder.filter(QueryBuilders.rangeQuery("price").gte(requestBody.getMinPrice()));
            }
            if (requestBody.getTmeInterval() != 0) {
                Instant now = Instant.now();
                boolBuilder.filter(QueryBuilders.rangeQuery("date_created").from(now.minus(requestBody.getTmeInterval(), ChronoUnit.HOURS)));
            }
            sourceBuilder.query(boolBuilder);

        } else if (requestBody.getCategory() != null) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category", requestBody.getCategory());
            sourceBuilder.query(termQueryBuilder);
        }

        searchRequest.source(sourceBuilder);

        // return searchRequest object
        return searchRequest;
    }

    // Send the request to Elasticsearch and receive the raw response
    public static SearchHit[] getRawSearchResults(RestHighLevelClient client, SearchRequest searchRequest) throws ElasticsearchException {
        try {
            // Send the request to Elasticsearch, and receive the results in searchResponse
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // The SearchHits provides global information about all hits, like total number of hits or the maximum score
            SearchHits hits = searchResponse.getHits();

            // SearchHit provides access to basic information (Note: SearchHit and SearchHits are two different classes)
            SearchHit[] searchHits = hits.getHits();

            return searchHits;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ElasticsearchException("Unable to receive search results from Elasticsearch");
        }
    }

    // Search the listings index and return a list of listings
    public static List<Listing> getSearchResults(RestHighLevelClient client,
                                                 SearchListingsRequestBody requestBody) throws ElasticsearchException {

        try {
            // Get raw search results from Elasicsearch
            SearchHit[] searchHits = getRawSearchResults(client, buildListingsSearchRequest(requestBody));

            // Create list of search results
            List<Listing> listingsSearchResults = new ArrayList<>();

            for (SearchHit hit : searchHits) {

                // Retrieve each SearchHit as a Map
                Map<String, Object> resultMap = hit.getSourceAsMap();

                // Get GeocodeLocation from the map
                Map<String, Double> geoPointParameters = (Map<String, Double>) resultMap.get("location");
                GeocodeLocation.Builder geoBuilder = new GeocodeLocation.Builder();
                geoBuilder.latitude(geoPointParameters.get("lat")).longitude(geoPointParameters.get("lon"));
                GeocodeLocation geoPoint = geoBuilder.build();

                // Build the Listing java object
                Listing.Builder builder = new Listing.Builder();
                builder.setGeocodeLocation(geoPoint)
                        .setPictureUrls(DbUtils.readPictureUrls(resultMap.get("picture_urls").toString()))
                        .setListingId(resultMap.get("listing_id").toString())
                        .setTitle(resultMap.get("title").toString())
                        .setPrice((Double) resultMap.get("price"))
                        .setCategory(resultMap.get("category").toString())
                        .setSellerName(resultMap.get("seller_name").toString())
                        .setBrand(resultMap.get("brand").toString())
                        .setItemCondition(resultMap.get("item_condition").toString())
                        .setDescription(resultMap.get("description").toString())
                        .setAddress(resultMap.get("address").toString())
                        .setCityAndState(resultMap.get("city_and_state").toString())
                        .setDate(resultMap.get("date_created").toString());

                Listing item = builder.build();

                listingsSearchResults.add(item);

                // Extra Reading : you may also retrieve each SearchHit as a JSON string
//                String sourceAsString = hit.getSourceAsString();
//                JSONObject result = new JSONObject(sourceAsString);
//                JSONArray rawSearchResults = new JSONArray();
//                rawSearchResults.put(result);
            }

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
                builder.field("picture_urls", new ObjectMapper().writeValueAsString(listing.getPictureUrls()));
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
                builder.field("listing_id", listing.getListingId());
                // title
                builder.field("title", listing.getTitle());
                // price
                builder.field("price", listing.getPrice());
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
