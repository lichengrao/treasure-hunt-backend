package com.treasurehunt.treasurehunt.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Storage;
import com.treasurehunt.treasurehunt.db.gcs.GCS;
import com.treasurehunt.treasurehunt.db.mysql.MySQL;
import com.treasurehunt.treasurehunt.entity.Listing;
import com.treasurehunt.treasurehunt.entity.User;
import com.treasurehunt.treasurehunt.utils.ServletUtil;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@MultipartConfig
@WebServlet(name = "ListingServlet", urlPatterns = {"/listing"})
public class ListingServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ListingServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        // Verify token
        String authorizedUserId = ServletUtil.getAuthorizedUserIdFromRequest(request);
        // Get sellerId from request body as foreign key
        String sellerId = request.getParameter("seller_user_id");
        // Verify the two id's are equal
        if (!authorizedUserId.equals(sellerId)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("Token invalid");
            return;
        }

        // Get UUID as ListingId
        String listingId = String.valueOf(System.currentTimeMillis());

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

        // Get fullName, address, and geolocation of seller from userDB
        User user = null;
        try (Connection conn = pool.getConnection()) {
            user = MySQL.getUser(conn, sellerId);
        } catch (SQLException e) {
            logger.warn("Error while attempting to add new listing to MySQL db", e);
            response.setStatus(500);
            response.getWriter().write("Unable to successfully create listing! Please check the application logs for " +
                    "more details.");
        }

        // return if cannot find user in user db
        if (user == null) {
            logger.warn("Cannot find seller's info {} in user db", sellerId);
            return;
        }

        // Read info from request body, and add fullName, address, and geolocation of seller
        Listing.Builder builder = new Listing.Builder();
        builder.setListingId(listingId)
               .setTitle(request.getParameter("title"))
               .setPrice(Double.parseDouble(request.getParameter("price")))
               .setCategory(request.getParameter("category"))
               .setSellerId(sellerId)
               .setDescription(request.getParameter("description"))
               .setItemCondition(request.getParameter("item_condition"))
               .setBrand(request.getParameter("brand"))
               .setPictureUrls(pictureArray.toString())
               .setSellerName(String.format("%s %s", user.getFirstName(), user.getLastName()))
               .setAddress(user.getAddress())
               .setGeocodeLocation(user.getGeocodeLocation());

        // Build a java object which contains all listing info
        Listing listing = builder.build();

        boolean isListingAdded = false;
        // Add listing obj to MySQL database
        try (Connection conn = pool.getConnection()) {
            isListingAdded = MySQL.createListing(conn, listing);
        } catch (SQLException e) {
            logger.warn("Error while attempting to add new listing to MySQL db", e);
            response.setStatus(500);
            response.getWriter().write("Unable to successfully create listing! Please check the application logs for " +
                    "more details.");
        }

        if (!isListingAdded) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().print("Unable to create listing");
            return;
        }
        // ListingId is return as the respondBody
        // so no need to serialize Java objects into JSON string
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(listingId);

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
            logger.warn("Error while attempting to get listing from MySQL db", e);
            response.setStatus(500);
            response.getWriter().write("Unable to successfully get listing! Please check the application logs for " +
                    "more details.");
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(listing));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);

        // TODO
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
            // TODO

            // delete from GCS
            // TODO

        } catch (SQLException e) {
            logger.warn("Error while attempting to delete listing from MySQL db", e);
            response.setStatus(500);
            response.getWriter()
                    .write("Unable to successfully delete listing! Please check the application logs for more details");
            return;
        }
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print("Successfully deleted a listing!");
    }
}
