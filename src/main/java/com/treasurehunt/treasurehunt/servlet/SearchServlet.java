package com.treasurehunt.treasurehunt.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treasurehunt.treasurehunt.db.elasticsearch.Elasticsearch;
import com.treasurehunt.treasurehunt.db.elasticsearch.ElasticsearchClient;
import com.treasurehunt.treasurehunt.entity.GeocodeLocation;
import com.treasurehunt.treasurehunt.entity.Listing;
import com.treasurehunt.treasurehunt.entity.SearchListingsRequestBody;
import com.treasurehunt.treasurehunt.utils.ServletUtil;
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

            double lat = Double.valueOf(request.getParameter("latitude"));
            double lon = Double.valueOf(request.getParameter("longitude"));
            double maxPrice = Double.valueOf(request.getParameter("max_price"));
            double minPrice = Double.valueOf(request.getParameter("min_price"));

            // Retrieve all filters from request url
            builder.setKeyword(keyword)
                    .setLatitude(lat)
                    .setLongitude(lon)
                    .setDistance(request.getParameter("radius"))
                    .setCondition(request.getParameter("min_condition"))
                    .setMaxPrice(maxPrice)
                    .setMinPrice(minPrice);
//                    .setDateListed();
        } else {
            builder.setCategory(request.getParameter("category"));
        }

        SearchListingsRequestBody SearchRequest = builder.build();

        // Get search results
        RestHighLevelClient client = (RestHighLevelClient) request.getServletContext().getAttribute("es-client");
        List<Listing> results = new ArrayList<>();
        try {
            results = Elasticsearch.getSearchResults(client, SearchRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Write search results into response body
        response.getWriter().print(new ObjectMapper().writeValueAsString(results));

    }
}
