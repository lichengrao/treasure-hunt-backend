package com.treasurehunt.treasurehunt.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treasurehunt.treasurehunt.external.GoogleMapsClient;
import com.treasurehunt.treasurehunt.external.GoogleMapsException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "GeocodeServlet", urlPatterns = {"/geocode"})
public class GeocodeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        // get address parameter from request
        String address = request.getParameter("address");
        GoogleMapsClient googleMapsClient = new GoogleMapsClient();

        // Let the client know the returned data is in JSON format.
        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().print(new ObjectMapper().writeValueAsString(googleMapsClient.getGeocodeResult(address)));
        } catch (GoogleMapsException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

    }
}
