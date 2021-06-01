package com.treasurehunt.treasurehunt.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treasurehunt.treasurehunt.db.elasticsearch.Elasticsearch;
import com.treasurehunt.treasurehunt.db.elasticsearch.ElasticsearchClient;
import com.treasurehunt.treasurehunt.entity.GeocodeLocation;
import com.treasurehunt.treasurehunt.entity.Listing;
import com.treasurehunt.treasurehunt.entity.SearchListingsRequestBody;
import com.treasurehunt.treasurehunt.utils.ServletUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "SearchServlet", urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(SearchServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        SearchListingsRequestBody.Builder builder = new SearchListingsRequestBody.Builder();

        // Two search mode:
        // 1. keyword associated with filters; 2. category without filters
        String keyword = request.getParameter("keyword");
        if (keyword != null) {
            // Retrieve all filters from request url
            builder.setKeyword(keyword)
                    .setLatitude(Double.valueOf(request.getParameter("latitude")))
                    .setLongitude(Double.valueOf(request.getParameter("longitude")));

            if (request.getParameter("radius") != null) {
                builder.setDistance(request.getParameter("radius"));
            }
            if (request.getParameter("condition") != null) {
                builder.setCondition(request.getParameter("condition"));
            }
            if (request.getParameter("max_price") != null) {
                double max = Double.valueOf(request.getParameter("max_price"));
                if (max < 0.0) {
                    response.getWriter().println("price cannot be negative");
                } else {
                    builder.setMaxPrice(max);
                }
            }
            if (request.getParameter("min_price") != null) {
                double min = Double.valueOf(request.getParameter("min_price"));
                if (min < 0.0) {
                    response.getWriter().println("price cannot be negative");
                } else {
                    builder.setMinPrice(min);
                }
            }
            if (request.getParameter("time_interval") != null) {
                builder.setTimeInterval(request.getParameter("time_interval"));
            }

        } else {
            builder.setCategory(request.getParameter("category"));
        }

        SearchListingsRequestBody requestBody = builder.build();

        // Get search results
        RestHighLevelClient client = (RestHighLevelClient) request.getServletContext().getAttribute("es-client");
//        SearchRequest requestBuild = Elasticsearch.buildListingsSearchRequest(requestBody);
//        String rawSearchResults = Elasticsearch.getRawSearchResults(client, requestBuild);

        List<Listing> finalresult = Elasticsearch.getSearchResults(client, requestBody);

        // Write search results into response body
        response.getWriter().print(new ObjectMapper().writeValueAsString(finalresult));
    }
}
