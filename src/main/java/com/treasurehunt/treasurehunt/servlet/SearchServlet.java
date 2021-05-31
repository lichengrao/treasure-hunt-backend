package com.treasurehunt.treasurehunt.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treasurehunt.treasurehunt.db.elasticsearch.Elasticsearch;
import com.treasurehunt.treasurehunt.db.elasticsearch.ElasticsearchClient;
import com.treasurehunt.treasurehunt.entity.Listing;
import com.treasurehunt.treasurehunt.entity.SearchListingsRequestBody;
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
        Map<String, String> filters = new HashMap<>();

        // get all parameterNames and values from url
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String)parameterNames.nextElement();
            if (name.equals("keyword")) {
                builder.setKeyword(request.getParameter(name));
            } else if (name.equals("category")) {
                builder.setCategory(request.getParameter(name));
            } else {
                filters.put(name,request.getParameter(name));
            }
        }
        builder.setFilters(filters);

        SearchListingsRequestBody searchrequest = builder.build();

//        // Test
//        response.getWriter().println(searchrequest.getCategory());
//        response.getWriter().println(searchrequest.getKeyword());
//        response.getWriter().println(searchrequest.getFilters());

        // Get search results
        List<Listing> results = new ArrayList<>();
        try {
            // Start a client in try-with-resources so client will auto-close after execution
            try (RestHighLevelClient client = ElasticsearchClient.createElasticsearchClient()) {

                results = Elasticsearch.getSearchResults(client, searchrequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Write search results into response body
        response.getWriter().print(new ObjectMapper().writeValueAsString(results));
    }
}
