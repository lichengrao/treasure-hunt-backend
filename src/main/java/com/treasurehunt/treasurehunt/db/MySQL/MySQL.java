package com.treasurehunt.treasurehunt.db.MySQL;

import com.treasurehunt.treasurehunt.db.MySQL.MySQLException;
import com.treasurehunt.treasurehunt.entity.Listing;
import com.treasurehunt.treasurehunt.entity.User;
import org.json.JSONObject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQL {

    private static final String USERS_DB = "users";
    private static final String LISTINGS_DB = "listings";
    private static final String SAVED_RECORDS_DB = "saved_records";

    // Create user in users db
    public static String createUser(DataSource pool, User user) throws MySQLException {
        try (Connection conn = pool.getConnection()) {
            // TODO
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            throw new MySQLException("Failed to create User");
        }
    }

    // Create new listing in listings db
    public static void createListing(Connection conn, Listing listing) throws MySQLException {
        // Create a connection from the given pool
        try {

            // Insert the new data to listingsDB
            String sql = "INSERT INTO listings VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement postListing = conn.prepareStatement(sql)) {

                postListing.setString(1, listing.getListingId());
                postListing.setString(2, listing.getTitle());
                postListing.setDouble(3, listing.getPrice());
                postListing.setString(4, listing.getCategory());
                postListing.setString(5, listing.getDescription());
                postListing.setString(6, listing.getItemCondition());
                postListing.setString(7, listing.getBrand());
                postListing.setObject(8, listing.getPictureUrls());
                postListing.setString(9, listing.getSellerId());
                postListing.setString(10, listing.getSellerName());
                postListing.setString(11, listing.getAddress());
                postListing.setTimestamp(12, new java.sql.Timestamp(System.currentTimeMillis()));

                postListing.executeUpdate();

            } catch (Exception throwables) {
                throwables.printStackTrace();
                throw new MySQLException("Failed to add listing to DB");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Update an existing listing in listings db
    public static String updateListing(DataSource pool, Listing listing) throws MySQLException {
        // Create a connection from the given pool
        try (Connection conn = pool.getConnection()) {
            // TODO
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            throw new MySQLException("Failed to update Listing");
        }
    }

    // Delete an existing listing in listings db
    public static String deleteListing(DataSource pool, Listing listing) throws MySQLException {
        // Create a connection from the given pool
        try (Connection conn = pool.getConnection()) {
            // TODO
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            throw new MySQLException("Failed to delete Listing");
        }
    }

    // Get listings created by user
    public static List<Listing> getMyListings(DataSource pool, String userId) throws MySQLException {
        // Build a return object
        List<Listing> myListings = new ArrayList<>();

        // Create a connection from the given pool
        try (Connection conn = pool.getConnection()) {

            // Query users DB for userId == seller_id
            // TODO

            // Add listings in ResultSet to myListings
            // TODO

            // Return myListings
            return myListings;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MySQLException("Failed to get my listings");
        }
    }

    // Get listings saved by user
    public static List<Listing> getSavedListings(DataSource pool, String userId) throws MySQLException {
        // Build a return object
        List<Listing> savedListings = new ArrayList<>();

        // Create a connection from the given pool
        try (Connection conn = pool.getConnection()) {

            // Query saved_records DB for userId == user_id and query listings DB with listing_id's
            // Use JOIN to optimize
            // TODO

            // Add listings in ResultSet to savedListings
            // TODO

            // Return savedListings
            return savedListings;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MySQLException("Failed to get saved listings");
        }
    }

    // Create a saved record in saved_records
    public static void saveListing(DataSource pool, String userId, String listingId) throws MySQLException {
        // Create a connection from the given pool
        try (Connection conn = pool.getConnection()) {
            // Build and execute SQL statement
            // TODO
            String sql = "";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MySQLException("Failed to save listing");
        }
    }

    // Delete a saved record in saved_records
    public static void unsaveListing(DataSource pool, String userId, String listingId) throws MySQLException {
        // Create a connection from the given pool
        try (Connection conn = pool.getConnection()) {
            // Build and execute SQL statement
            // TODO
            String sql = "";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MySQLException("Failed to unsave listing");
        }
    }

    public static String[] getSellerNameAddress(Connection conn, String SellerID) throws SQLException {

        String sql = "SELECT first_name, last_name, address FROM users WHERE user_id = ?";

        String[] result = new String[3];
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1,SellerID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                result[0] = rs.getString("first_name");
                result[1] = rs.getString("last_name");
                result[2] = rs.getString("address");
            }

        }
        return result;
    }

    public static Listing getListing(Connection conn, String listingID) throws SQLException {

//        JSONObject listing = new JSONObject();
        Listing listing = null;

        String sql = "SELECT * FROM listings WHERE listing_id = ?";

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1,listingID);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
//                listing.put("listingID",rs.getString("listing_id"));
//                listing.put("title",rs.getString("title"));
//                listing.put("price",rs.getString("price"));
//                listing.put("category",rs.getString("category"));
//                listing.put("description",rs.getString("description"));
//                listing.put("itemCondition",rs.getString("item_condition"));
//                listing.put("brand",rs.getString("brand"));
//                listing.put("pictureUrls",rs.getString("picture_urls"));
//                listing.put("sellerId",rs.getString("seller_id"));
//                listing.put("sellerName",rs.getString("seller_name"));
//                listing.put("address",rs.getString("address"));
//                listing.put("date",rs.getString("date"));

                Listing.Builder builder = new Listing.Builder();
                builder.setListingId(listingID)
                        .setTitle(rs.getString("title"))
                        .setPrice(rs.getDouble("price"))
                        .setCategory(rs.getString("category"))
                        .setDescription(rs.getString("description"))
                        .setItemCondition(rs.getString("item_condition"))
                        .setBrand(rs.getString("brand"))
                        .setPictureUrls((JSONObject) rs.getObject("picture_urls"))
                        .setSellerId(rs.getString("seller_id"))
                        .setSellerName(rs.getString("seller_name"))
                        .setAddress(rs.getString("address"))
                        .setDate(rs.getString("date"));

                listing = builder.build();
            }

        }

        return listing;

    }
}
