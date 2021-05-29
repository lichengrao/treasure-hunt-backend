package com.treasurehunt.treasurehunt.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treasurehunt.treasurehunt.db.GCS;
import com.treasurehunt.treasurehunt.db.MySQL.MySQLConnectionPoolContextListener;
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

@MultipartConfig
@WebServlet(name = "ListingServlet", urlPatterns = {"/listing"})
public class ListingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        // Get UUID as ListingID
        String ID = String.valueOf(System.currentTimeMillis());

        // Upload pictures and get urls
        JSONObject pictureArray = new JSONObject();

        String[] nameList = {"picture_1", "picture_2", "picture_3"};
        for (int i = 0; i < nameList.length; i += 1) {

            Part filePart = request.getPart(nameList[i]);

            // In case that seller didn't upload all three pictures
            if (filePart.getSubmittedFileName().trim().length() != 0) {

                String fileName = System.currentTimeMillis() + filePart.getSubmittedFileName();
                InputStream fileInputStream = filePart.getInputStream();
                String url = GCS.uploadToGCS(fileName, fileInputStream);

                JSONObject picture = new JSONObject();
                picture.put("name", fileName);
                picture.put("url", url);

                pictureArray.put(nameList[i],picture);
            }
        }

        // Get sellerID from usersDB as foreign key
        String sellerID = request.getParameter("seller_user_id");

        // Read info from request body (SellerName and Address will be added later from userDB)
        Listing.Builder builder = new Listing.Builder();
        builder.setListingId(ID)
                .setTitle(request.getParameter("title"))
                .setPrice(Double.parseDouble(request.getParameter("price")))
                .setCategory(request.getParameter("category"))
                .setSellerId(sellerID)
                .setDescription(request.getParameter("description"))
                .setItemCondition(request.getParameter("condition"))
                .setBrand(request.getParameter("brand"))
                .setPictureUrls(pictureArray.toString());

        // Connect to MySQL
        DataSource pool = (DataSource) request.getServletContext().getAttribute("my-pool");
        try (Connection conn = pool.getConnection()) {

            // Get fullName and address from userDB, and add these info to builder
            String[] queryResult = MySQLConnectionPoolContextListener.getSellerNameAddress(conn, sellerID);
            String fullName = queryResult[0] + " " + queryResult[1];
            String address = queryResult[2];

            // Add SellerName and Address to builder
            builder.setSellerName(fullName).setAddress(address);

            // Build a java object which contains all listing info
            Listing listing = builder.build();

            // Add these info to MySQL database
            MySQLConnectionPoolContextListener.createNewListing(conn, listing);

        } catch (SQLException ex) {
            response.setStatus(500);
            response.getWriter().write("data received, but unable to insert data to MySQL");
        }

        // ListingID is return as the respondBody
        // so no need to serialize Java objects into JSON string
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(ID);

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        String listingID = request.getParameter("listing_id");
//        JSONObject listing = new JSONObject();
        Listing listing = null;

        DataSource pool = (DataSource) request.getServletContext().getAttribute("my-pool");

        try (Connection conn = pool.getConnection()) {
            listing = MySQLConnectionPoolContextListener.getListing(conn, listingID);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Return JSONObject as response
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(listing));
    }
}
