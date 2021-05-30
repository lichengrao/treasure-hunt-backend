package com.treasurehunt.treasurehunt.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodeResult {

    @JsonProperty("results")
    List<GeocodeObject> results;

    @JsonProperty("status")
    String status;

    public GeocodeResult() {
    }

    public List<GeocodeObject> getResults() {
        return results;
    }

    public void setResults(List<GeocodeObject> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public GeocodeLocation getGeocodeLocation() {
        for (GeocodeObject geocodeObject : results) {
            if (geocodeObject.getGeometry() != null) {
                return geocodeObject.getGeometry().getGeocodeLocation();
            }
        }
        return null;
    }

    public String getFormattedAddress() {
        for (GeocodeObject geocodeObject : results) {
            if (geocodeObject.getFormattedAddress() != null) {
                return geocodeObject.getFormattedAddress();
            }
        }
        return null;
    }
}
