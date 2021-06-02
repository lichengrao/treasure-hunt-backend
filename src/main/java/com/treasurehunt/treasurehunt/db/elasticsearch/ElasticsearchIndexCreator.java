package com.treasurehunt.treasurehunt.db.elasticsearch;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticsearchIndexCreator {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ElasticsearchIndexCreator.class);
        try {
            // Connect to Elasticsearch
            logger.debug("Connecting to Elasticsearch");
            try (RestHighLevelClient client = ElasticsearchClient.createElasticsearchClient()) {
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
                        builder.startObject("price");
                        {
                            builder.field("type", "double");
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

                        // city and state
                        builder.startObject("city_and_state");
                        {
                            builder.field("type", "keyword");
                        }
                        builder.endObject();

                        // location
                        builder.startObject("location");
                        {
                            builder.field("type", "geo_point");
                        }
                        builder.endObject();

                        // picture_urls
                        // Elasticsearch arrays do not require a dedicated field data type.
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
                logger.debug("Index \"listings\" creation status: {}", acknowledged);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
