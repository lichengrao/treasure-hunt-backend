package com.treasurehunt.treasurehunt.db.MySQL;

import com.treasurehunt.treasurehunt.entity.Listing;
import com.zaxxer.hikari.HikariDataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;

@WebListener("Creates a connection pool that is stored in the Servlet's context for later use via attribute mysql-pool")
public class MySQLConnectionPoolContextListener implements ServletContextListener {

    private void createTable(DataSource pool) throws SQLException {
        // Safely attempt to create the table schema.
        try (Connection conn = pool.getConnection()) {
            String stmt = "CREATE TABLE IF NOT EXISTS users ( "
                    + "user_id VARCHAR(255) NOT NULL, "
                    + "password VARCHAR(255) NOT NULL, "
                    + "first_name VARCHAR(255) NOT NULL, "
                    + "last_name VARCHAR(255) NOT NULL, "
                    + "email VARCHAR(255) NOT NULL, "
                    + "address VARCHAR(255), "
                    + "PRIMARY KEY (user_id) "
                    + ")";
            try (PreparedStatement createTableStatement = conn.prepareStatement(stmt);) {
                createTableStatement.execute();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        HikariDataSource pool = (HikariDataSource) event.getServletContext().getAttribute("mysql-pool");
        if (pool != null) {
            pool.close();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext servletContext = event.getServletContext();
        DataSource pool = (DataSource) servletContext.getAttribute("mysql-pool");
        if (pool == null) {
            try {
                MySQLConnectionPool mySQLConnectionPool = new MySQLConnectionPool();
                pool = mySQLConnectionPool.pool;
                servletContext.setAttribute("mysql-pool", pool);
            } catch (IOException ex) {
                throw new RuntimeException("Unable to fetch MySQL login credentials");
            }
        }
        try {
            createTable(pool);
            System.out.println("Successfully created connection to MySQL");
        } catch (SQLException ex) {
            throw new RuntimeException(
                    "Unable to verify table schema, please double check and try again.",
                    ex);

        }
    }

    public static void createNewListing(Connection conn, Listing item) throws SQLException {

        try {

            // Insert the new data to listingsDB
            String sql = "INSERT INTO listings VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement postListing = conn.prepareStatement(sql)) {

                postListing.setString(1, item.getListingId());
                postListing.setString(2, item.getTitle());
                postListing.setDouble(3, item.getPrice());
                postListing.setString(4, item.getCategory());
                postListing.setString(5, item.getDescription());
                postListing.setString(6, item.getItemCondition());
                postListing.setString(7, item.getBrand());
                postListing.setString(8, item.getPictureUrls());
                postListing.setString(9, item.getSellerId());
                postListing.setString(10, item.getSellerName());
                postListing.setString(11, item.getAddress());
                postListing.setTimestamp(12, new java.sql.Timestamp(System.currentTimeMillis()));

                postListing.execute();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
                        .setPictureUrls(rs.getString("picture_urls"))
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
