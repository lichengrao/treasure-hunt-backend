package com.treasurehunt.treasurehunt.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = GeocodeLocation.Builder.class)
public class GeocodeLocation {

    @JsonProperty("lat")
    private final double latitude;

    @JsonProperty("lon")
    private final double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private GeocodeLocation(Builder builder) {
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Builder {

        @JsonProperty("lat")
        private double latitude;

        @JsonProperty("lng")
        private double longitude;

        public Builder latitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public GeocodeLocation build() {
            return new GeocodeLocation(this);
        }
    }
}
