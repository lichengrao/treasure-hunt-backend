package com.treasurehunt.treasurehunt.db.elastic_search;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class ElasticSearchIndexCreator {
    public static void main(String[] args) {
        try {
            // Connect to ElasticSearch
            System.out.println("Connecting to ElasticSearch");
            ElasticSearchClient elasticSearchClient = new ElasticSearchClient();
            try (RestHighLevelClient client = elasticSearchClient.client) {
                // Create Index Request
                CreateIndexRequest request = new CreateIndexRequest("listings");

                // Specify Index Mappings
                XContentBuilder builder = XContentFactory.jsonBuilder();
                builder.startObject();
                {
                    builder.startObject("properties");
                    {
                        // listing_id
                        builder.startObject("listing_id");
                        {
                            builder.field("type", "keyword");
                        }
                        builder.endObject();

                        // title
                        builder.startObject("title");
                        {
                            builder.field("type", "text");
                        }
                        builder.endObject();

                        // category
                        builder.startObject("category");
                        {
                            builder.field("type", "keyword");
                        }
                        builder.endObject();

                        // seller_name
                        builder.startObject("seller_name");
                        {
                            builder.field("type", "keyword");
                            builder.field("index", false);
                        }
                        builder.endObject();

                        // brand
                        builder.startObject("brand");
                        {
                            builder.field("type", "text");
                        }
                        builder.endObject();

                        // item_condition
                        builder.startObject("item_condition");
                        {
                            builder.field("type", "keyword");
                        }
                        builder.endObject();

                        // description
                        builder.startObject("description");
                        {
                            builder.field("type", "text");
                        }
                        builder.endObject();

                        // address
                        builder.startObject("address");
                        {
                            builder.field("type", "keyword");
                            builder.field("index", false);
                        }
                        builder.endObject();

                        // location
                        builder.startObject("location");
                        {
                            builder.field("type", "geo_point");
                        }
                        builder.endObject();

                        // picture_urls
                        // ElasticSearch arrays do not require a dedicated field data type.
                        // Any field can contain zero or more values by default
                        builder.startObject("picture_urls");
                        {
                            builder.field("type", "keyword");
                            builder.field("index", false);
                        }
                        builder.endObject();

                        // date_created
                        builder.startObject("date_created");
                        {
                            builder.field("type", "date");
                        }
                        builder.endObject();
                    }
                    builder.endObject();
                }
                builder.endObject();
                request.mapping(builder);

                // Synchronous Execution
                CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
                boolean acknowledged = createIndexResponse.isAcknowledged();
                System.out.printf("Index \"listings\" creation status: %s%n", acknowledged);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
