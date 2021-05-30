package com.treasurehunt.treasurehunt.servlet;

import com.treasurehunt.treasurehunt.db.mysql.MySQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "MyListingsServlet", urlPatterns = {"/my-listings"})
public class MyListingsServlet extends HttpServlet {
    // TODO: Last edited by Ruichen

    private static final Logger logger = LoggerFactory.getLogger(MyListingsServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.getWriter().print("Hello My Listings");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.getWriter().print("Successfully posted a listing");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String listingId = request.getParameter("listing_id");
        DataSource pool = (DataSource) request.getServletContext().getAttribute("mysql-pool");
        try (Connection conn = pool.getConnection()) {
            // delete from MySQL
            MySQL.deleteListing(conn, listingId);

            // delete from ES

            // delete from GCS

        } catch (SQLException e) {
            logger.warn("Error while attempting to delete listing from MySQL db", e);
            response.setStatus(500);
            response.getWriter()
                    .write("Unable to successfully delete listing! Please check the application logs for more details");
        }
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print("Successfully deleted a listing!");
    }
}
