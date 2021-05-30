package com.treasurehunt.treasurehunt.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Storage;
import com.treasurehunt.treasurehunt.db.gcs.GCS;
import com.treasurehunt.treasurehunt.db.mysql.MySQL;
import com.treasurehunt.treasurehunt.entity.Listing;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@MultipartConfig
@WebServlet(name = "ListingServlet", urlPatterns = {"/listing"})
public class ListingServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ListingServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        // Get UUID as ListingID
        String id = String.valueOf(System.currentTimeMillis());

        // Upload pictures and get urls
        JSONObject pictureArray = new JSONObject();
        Storage storage = (Storage) request.getServletContext().getAttribute("gcs-client");

        String[] nameList = {"picture_1", "picture_2", "picture_3"};
        for (int i = 0; i < nameList.length; i += 1) {

            Part filePart = request.getPart(nameList[i]);

            // In case that seller didn't upload all three pictures
            if (filePart.getSubmittedFileName().trim().length() != 0) {

                String fileName = System.currentTimeMillis() + filePart.getSubmittedFileName();
                InputStream fileInputStream = filePart.getInputStream();

                String url = GCS.uploadPicture(storage, fileName, fileInputStream);

                JSONObject picture = new JSONObject();
                picture.put("name", fileName);
                picture.put("url", url);

                pictureArray.put(nameList[i], picture);
            }
        }

        // Connect to MySQL
        DataSource pool = (DataSource) request.getServletContext().getAttribute("mysql-pool");
        // Get sellerID from request body as foreign key
        String sellerID = request.getParameter("seller_user_id");

        // Get fullName and address from userDB
        String[] queryResult = new String[3];
        try (Connection conn = pool.getConnection()) {
            queryResult = MySQL.getSellerNameAddress(conn, sellerID);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error while attempting to add new listing to MySQL db", e);
            response.setStatus(500);
            response.getWriter().write("Unable to successfully create listing! Please check the application logs for " +
                    "more details.");
        }
        String fullName = queryResult[0] + " " + queryResult[1];
        String address = queryResult[2];


        // Read info from request body
        Listing.Builder builder = new Listing.Builder();
        builder.setListingId(id)
                .setTitle(request.getParameter("title"))
                .setPrice(Double.parseDouble(request.getParameter("price")))
                .setCategory(request.getParameter("category"))
                .setSellerId(sellerID)
                .setDescription(request.getParameter("description"))
                .setItemCondition(request.getParameter("condition"))
                .setBrand(request.getParameter("brand"))
                .setPictureUrls(pictureArray.toString())
                .setSellerName(fullName)
                .setAddress(address);

        // Build a java object which contains all listing info
        Listing listing = builder.build();

        // Add these info to MySQL database
        try (Connection conn = pool.getConnection()) {
            MySQL.createListing(conn, listing);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error while attempting to add new listing to MySQL db", e);
            response.setStatus(500);
            response.getWriter().write("Unable to successfully create listing! Please check the application logs for " +
                    "more details.");
        }

        // ListingID is return as the respondBody
        // so no need to serialize Java objects into JSON string
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(id);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        String listingId = request.getParameter("listing_id");
        Listing listing = new Listing();

        DataSource pool = (DataSource) request.getServletContext().getAttribute("mysql-pool");
        try (Connection conn = pool.getConnection()) {
            listing = MySQL.getListing(conn, listingId);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error while attempting to get listing from MySQL db", e);
            response.setStatus(500);
            response.getWriter().write("Unable to successfully get listing! Please check the application logs for " +
                    "more details.");
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(listing));
    }
}
