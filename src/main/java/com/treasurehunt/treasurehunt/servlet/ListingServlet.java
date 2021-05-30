package com.treasurehunt.treasurehunt.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Storage;
import com.treasurehunt.treasurehunt.db.gcs.GCS;
import com.treasurehunt.treasurehunt.db.mysql.MySQL;
import com.treasurehunt.treasurehunt.db.mysql.MySQLException;
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

@MultipartConfig
@WebServlet(name = "ListingServlet", urlPatterns = {"/listing"})
public class ListingServlet extends HttpServlet {

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
        String[] queryResult = MySQL.getSellerNameAddress(pool, sellerID);
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
        MySQL.createListing(pool, listing);

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
        Listing listing;

        DataSource pool = (DataSource) request.getServletContext().getAttribute("mysql-pool");
        try {
            listing = MySQL.getListing(pool, listingId);
        } catch (MySQLException e) {
            e.printStackTrace();
            throw new ServletException("Cannot get listings");
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(listing));
    }
}
