package com.treasurehunt.treasurehunt.db.mysql;

import com.treasurehunt.treasurehunt.entity.Listing;
import com.treasurehunt.treasurehunt.entity.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
    public static String createListing(DataSource pool, Listing listing) throws MySQLException {
        // Create a connection from the given pool
        try (Connection conn = pool.getConnection()) {
            // TODO
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            throw new MySQLException("Failed to create Listing");
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
    public void saveListing(DataSource pool, String userId, String listingId) throws MySQLException {
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
    public void unsaveListing(DataSource pool, String userId, String listingId) throws MySQLException {
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
}
