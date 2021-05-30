package com.treasurehunt.treasurehunt.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.treasurehunt.treasurehunt.entity.Listing;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ServletUtil {
    private static final Logger logger = LoggerFactory.getLogger(ServletUtil.class);

    // Helper method to write listings to servlet response body
    public static void writeListings(HttpServletResponse response, List<Listing> listings) {
        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().print(new ObjectMapper().writeValueAsString(listings));
        } catch (IOException e) {
            logger.warn("Failed to write listings {} into response", listings);
        }
    }

    // Help encrypt the user password before save to the database
    public static String encryptPassword(String userId, String password) {
        return DigestUtils.md5Hex(userId + DigestUtils.md5Hex(password)).toLowerCase();
    }

    // Convert request body to object of class T
    public static <T> T readRequestBody(Class<T> cl, HttpServletRequest request) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(request.getReader(), cl);
        } catch (JsonParseException | JsonMappingException e) {
            logger.warn("Cannot parse/map the following request: {}", request.getReader());
            return null;
        }
    }
}
