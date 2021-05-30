package com.treasurehunt.treasurehunt.servlet;

import com.treasurehunt.treasurehunt.auth.PasswordUtils;
import com.treasurehunt.treasurehunt.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "SignupServlet", urlPatterns = {"/signup"})
public class SignupServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(SignupServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.getWriter().print("Hello Signup");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        // Reader user data from the request body
        User user = ServletUtil.readRequestBody(User.class, request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.warn("Cannot parse signup request: {}", request);
            return;
        }

        // Generate and set secure password and salt for user
        PasswordUtils.generateSecurePasswordForUser(user);

        // Query for clean address and geoLocation from Google Maps Geocode API


        boolean isUserAdded;

        // Save user in MySQL users db
    }
}
