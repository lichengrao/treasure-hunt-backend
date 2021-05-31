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
    @JsonProperty("filters")
    private Map<String, String> filters;

    public String getKeyword() {
        return keyword;
    }

    public String getCategory() {
        return category;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public SearchListingsRequestBody(Builder builder) {
        this.keyword = builder.keyword;
        this.category = builder.category;
        this.filters = builder.filters;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Builder {
        private String keyword;
        private String category;
        private Map<String, String> filters;

        public Builder setKeyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setFilters(Map<String, String> filters) {
            this.filters = filters;
            return this;
        }

        public SearchListingsRequestBody build() {
            return new SearchListingsRequestBody(this);
        }
    }
}
