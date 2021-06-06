package com.treasurehunt.treasurehunt.db.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Elasticsearch {

    // Deployment - double check here
    private static final String LISTINGS_INDEX = "listings";
    private static final int MAX_NUMBER_OF_SEARCH_RESULTS = 30;
    private static final Logger logger = LoggerFactory.getLogger(Elasticsearch.class);

    // Build request object for Elasticsearch query
    private static SearchRequest buildListingsSearchRequest(SearchListingsRequestBody requestBody) throws ElasticsearchException {
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

            // Fuzzy searches keyword in title and description fields
            boolBuilder.must(QueryBuilders.multiMatchQuery(requestBody.getKeyword(), "title", "description")
                                          .fuzziness("AUTO"));
            if (requestBody.getCondition() != null) {
                boolBuilder.filter(QueryBuilders.termQuery("item_condition", requestBody.getCondition()));
            }
            if (requestBody.getDistance() != null) {
                boolBuilder.filter(QueryBuilders.geoDistanceQuery("geo_location")
                                                .point(requestBody.getLatitude(), requestBody.getLongitude())
                                                .distance(requestBody.getDistance(), DistanceUnit.MILES));
            }
            if (requestBody.getMaxPrice() != 0.0) {
                boolBuilder.filter(QueryBuilders.rangeQuery("price").lte(requestBody.getMaxPrice()));
            }
            if (requestBody.getMinPrice() != 0.0) {
                boolBuilder.filter(QueryBuilders.rangeQuery("price").gte(requestBody.getMinPrice()));
            }
            if (requestBody.getTmeInterval() != 0) {
                Instant now = Instant.now();
                boolBuilder.filter(QueryBuilders.rangeQuery("date")
                                                .from(now.minus(requestBody.getTmeInterval(), ChronoUnit.HOURS)));
            }
            sourceBuilder.query(boolBuilder);

        } else if (requestBody.getCategory() != null) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("category", requestBody.getCategory());
            sourceBuilder.query(termQueryBuilder);
        } else {
            logger.warn("Elasticsearch query does not contain keyword or category");
            throw new ElasticsearchException("Search query does not contain keyword or category");
        }

        searchRequest.source(sourceBuilder);

        // return searchRequest object
        return searchRequest;
    }

    // Search the listings index and return a list of listings
    public static List<Listing> getSearchResults(RestHighLevelClient client,
                                                 SearchListingsRequestBody requestBody) throws ElasticsearchException {

        try {
            // Get raw search results from Elasticsearch
            SearchRequest searchRequest = buildListingsSearchRequest(requestBody);
            SearchResponse rawSearchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // Create list of search results
            List<Listing> searchResults = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();

            // Stream results into listingSearchResults
            Arrays.stream(rawSearchResponse.getHits().getHits()).forEach(hit -> {
                try {
                    searchResults
                            .add(objectMapper.readValue(hit.getSourceAsString(), Listing.class));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });

            // Return the responseBody
            return searchResults;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ElasticsearchException("Unable to parse search results sent from Elasticsearch");
        }
    }

    // Add listing to the listings index
    public static void addListing(RestHighLevelClient client, Listing listing) throws ElasticsearchException {
        try {
            // Create index request
            IndexRequest request = new IndexRequest(LISTINGS_INDEX).id(listing.getListingId());
            request.source(new ObjectMapper().writeValueAsString(listing), XContentType.JSON);
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
            UpdateRequest request = new UpdateRequest(LISTINGS_INDEX, listing.getListingId());
            request.doc(new ObjectMapper().writeValueAsString(listing), XContentType.JSON);
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
