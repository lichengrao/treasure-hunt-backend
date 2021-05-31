package com.treasurehunt.treasurehunt.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = Listing.Builder.class)
public class SearchListingsRequestBody {

    @JsonProperty("keyword")
    private String keyword;
    @JsonProperty("category")
    private String category;
    @JsonProperty("condition")
    private String condition;
    @JsonProperty("center")
    private GeocodeLocation center;
    @JsonProperty("distance")
    private String distance;
    @JsonProperty("maxPrice")
    private String maxPrice;
    @JsonProperty("minPrice")
    private String minPrice;

    public String getKeyword() {
        return keyword;
    }

    public String getCategory() {
        return category;
    }

    public String getCondition() {
        return condition;
    }

    public GeocodeLocation getCenter() {
        return center;
    }

    public String getDistance() {
        return distance;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public SearchListingsRequestBody(Builder builder) {
        this.keyword = builder.keyword;
        this.category = builder.category;
        this.condition = builder.condition;
        this.center = builder.center;
        this.distance = builder.distance;
        this.maxPrice = builder.maxPrice;
        this.minPrice = builder.minPrice;
    }

    public SearchListingsRequestBody() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Builder {

        private String keyword;
        private String category;
        private String condition;
        private GeocodeLocation center;
        private String distance;
        private String maxPrice;
        private String minPrice;

        public Builder setKeyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setCondition(String condition) {
            this.condition = condition;
            return this;
        }

        public Builder setCenter(GeocodeLocation center) {
            this.center = center;
            return this;
        }

        public Builder setDistance(String distance) {
            this.distance = distance;
            return this;
        }

        public Builder setMaxPrice(String maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }

        public Builder setMinPrice(String minPrice) {
            this.minPrice = minPrice;
            return this;
        }

        public SearchListingsRequestBody build() {
            return new SearchListingsRequestBody(this);
        }
    }
}
