package com.treasurehunt.treasurehunt.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = Listing.Builder.class)

public class Listing {
    @JsonProperty("listingId")
    private String listingId;
    @JsonProperty("title")
    private String title;
    @JsonProperty("price")
    private double price;
    @JsonProperty("category")
    private String category;
    @JsonProperty("sellerId")
    private String sellerId;
    @JsonProperty("sellerName")
    private String sellerName;
    @JsonProperty("description")
    private String description;
    @JsonProperty("itemCondition")
    private String itemCondition;
    @JsonProperty("brand")
    private String brand;
    @JsonProperty("address")
    @JsonRawValue
    private String address;
    @JsonProperty("pictureUrls")
    @JsonRawValue
    private String pictureUrls;
    @JsonProperty("date")
    private String date;

    public Listing(Builder builder) {
        this.listingId = builder.listingId;
        this.title = builder.title;
        this.price = builder.price;
        this.category = builder.category;
        this.sellerId = builder.sellerId;
        this.sellerName = builder.sellerName;
        this.description = builder.description;
        this.itemCondition = builder.itemCondition;
        this.address = builder.address;
        this.brand = builder.brand;
        this.pictureUrls = builder.pictureUrls;
        this.date = builder.date;
    }

    public Listing() {
    }

    public String getListingId() {
        return listingId;
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

    public String getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getDescription() {
        return description;
    }

    public String getItemCondition() {
        return itemCondition;
    }

    public String getBrand() {
        return brand;
    }

    public String getAddress() {
        return address;
    }

    public String getPictureUrls() {
        return pictureUrls;
    }

    public String getDate() {
        return date;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Builder {
        private String listingId;
        private String title;
        private double price;
        private String category;
        private String sellerId;
        private String sellerName;
        private String description;
        private String itemCondition;
        private String brand;
        private String address;
        private String pictureUrls;
        private String date;

        public Builder setListingId(String listingId) {
            this.listingId = listingId;
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

        public Builder setSellerId(String sellerId) {
            this.sellerId = sellerId;
            return this;
        }

        public Builder setSellerName(String sellerName) {
            this.sellerName = sellerName;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setItemCondition(String itemCondition) {
            this.itemCondition = itemCondition;
            return this;
        }

        public Builder setBrand(String brand) {
            this.brand = brand;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setPictureUrls(String pictureUrls) {
            this.pictureUrls = pictureUrls;
            return this;
        }

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }

        public Listing build() {
            return new Listing(this);
        }
    }
}