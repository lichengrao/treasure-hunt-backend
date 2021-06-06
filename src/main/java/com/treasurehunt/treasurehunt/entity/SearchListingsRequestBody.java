package com.treasurehunt.treasurehunt.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
    @JsonProperty("latitude")
    private double latitude;
    @JsonProperty("longitude")
    private double longitude;
    @JsonProperty("distance")
    private String distance;
    @JsonProperty("maxPrice")
    private double maxPrice;
    @JsonProperty("minPrice")
    private double minPrice;
    @JsonProperty("timeInterval")
    private long timeInterval;

    public SearchListingsRequestBody(Builder builder) {
        this.keyword = builder.keyword;
        this.category = builder.category;
        this.condition = builder.condition;
        this.longitude = builder.longitude;
        this.latitude = builder.latitude;
        this.distance = builder.distance;
        this.maxPrice = builder.maxPrice;
        this.minPrice = builder.minPrice;
        this.timeInterval = builder.timeInterval;
    }

    public SearchListingsRequestBody() {
    }

    public String getKeyword() {
        return keyword;
    }

    public String getCategory() {
        return category;
    }

    public String getCondition() {
        return condition;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDistance() {
        return distance;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public long getTmeInterval() {
        return timeInterval;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Builder {

        private String keyword;
        private String category;
        private String condition;
        private double latitude;
        private double longitude;
        private String distance;
        private double maxPrice;
        private double minPrice;
        private long timeInterval;

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

        public Builder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setTimeInterval(long timeInterval) {
            this.timeInterval = timeInterval;
            return this;
        }

        public Builder setDistance(String distance) {
            this.distance = distance;
            return this;
        }

        public Builder setMaxPrice(double maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }

        public Builder setMinPrice(double minPrice) {
            this.minPrice = minPrice;
            return this;
        }

        public SearchListingsRequestBody build() {
            return new SearchListingsRequestBody(this);
        }
    }
}
