package com.treasurehunt.treasurehunt.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonDeserialize(builder = Listing.Builder.class))
public class Listing {

    private String listing_id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("price")
    private double price;
    @JsonProperty("category")
    private String category;
    @JsonProperty("seller_id")
    private String seller_id;
    @JsonProperty("seller_name")
    private String seller_name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("item_condition")
    private String item_condition;

    private String address;

    private String picture_urls;

    public String getListing_id() {
        return listing_id;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public String getDescription() {
        return description;
    }

    public String getItem_condition() {
        return item_condition;
    }

    public String getAddress() {
        return address;
    }

    public String getPicture_urls() {
        return picture_urls;
    }

    public Listing(Builder builder) {
        this.listing_id = builder.listing_id;
        this.title = builder.title;
        this.price = builder.price;
        this.category = builder.category;
        this.seller_id = builder.seller_id;
        this.seller_name = builder.seller_name;
        this.description = builder.description;
        this.item_condition = builder.item_condition;
        this.address = builder.address;
        this.picture_urls = builder.picture_urls;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Builder {
        private String listing_id;
        private String title;
        private double price;
        private String category;
        private String seller_id;
        private String seller_name;
        private String description;
        private String item_condition;
        private String address;
        private String picture_urls;

        public Builder setListing_id(String listing_id) {
            this.listing_id = listing_id;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setPrice(double price) {
            this.price = price;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setSeller_id(String seller_id) {
            this.seller_id = seller_id;
            return this;
        }

        public Builder setSeller_name(String seller_name) {
            this.seller_name = seller_name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setItem_condition(String item_condition) {
            this.item_condition = item_condition;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setPicture_urls(String picture_urls) {
            this.picture_urls = picture_urls;
            return this;
        }

        public Listing build() {
            return new Listing(this);
        }
    }
}