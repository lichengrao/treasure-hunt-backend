package com.treasurehunt.treasurehunt.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = Picture.Builder.class)
public class Picture {

    @JsonProperty("name")
    private String name;
    @JsonProperty("url")
    @JsonRawValue
    private String url;

    public Picture(Picture.Builder builder) {
        this.name = builder.name;
        this.url = builder.url;

    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Builder {
        private String name;
        private String url;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Picture build() {
            return new Picture(this);
        }
    }

}
