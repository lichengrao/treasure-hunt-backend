package com.treasurehunt.treasurehunt.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treasurehunt.treasurehunt.db.elasticsearch.Elasticsearch;
import com.treasurehunt.treasurehunt.db.elasticsearch.ElasticsearchException;
import com.treasurehunt.treasurehunt.entity.Listing;
import com.treasurehunt.treasurehunt.entity.SearchListingsRequestBody;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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
                   .setLatitude(Double.parseDouble(request.getParameter("latitude")))
                   .setLongitude(Double.parseDouble(request.getParameter("longitude")));

            if (request.getParameter("radius") != null) {
                builder.setDistance(request.getParameter("radius"));
            }
            if (request.getParameter("condition") != null) {
                builder.setCondition(request.getParameter("condition"));
            }
            if (request.getParameter("max_price") != null) {
                double max = Double.parseDouble(request.getParameter("max_price"));
                if (max < 0.0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("price cannot be negative");
                    return;
                } else {
                    builder.setMaxPrice(max);
                }
            }

            if (request.getParameter("min_price") != null) {
                double min = Double.parseDouble(request.getParameter("min_price"));
                builder.setMinPrice(min);
            }

            if (request.getParameter("time_interval") != null) {
                long interval = Long.parseLong(request.getParameter("time_interval"));
                if (interval < 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().println("interval cannot be negative");
                    return;
                } else {
                    builder.setTimeInterval(interval);
                }
            }

        } else {
            builder.setCategory(request.getParameter("category"));
        }

        SearchListingsRequestBody requestBody = builder.build();


        // Get search results
        RestHighLevelClient client = (RestHighLevelClient) request.getServletContext().getAttribute("es-client");
        List<Listing> finalResult;

        try {
            finalResult = Elasticsearch.getSearchResults(client, requestBody);
        } catch (ElasticsearchException e) {
            logger.warn("Failed to get search results from Elasicsearch", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("Failed to get search results from Elasticsearch");
            return;
        }
        // Write search results into response body
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(new ObjectMapper().writeValueAsString(finalResult));
    }
}
