package com.treasurehunt.treasurehunt.servlet;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.treasurehunt.treasurehunt.db.GCS;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

@MultipartConfig
@WebServlet(name = "ListingServlet", urlPatterns = {"/listing"})
public class ListingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

            //feel free to test GCS functions here.
            // You can use postman to play with it.
            String seller_id=request.getParameter("seller_user_id");
            String date =request.getParameter("date");
            Part filePart = request.getPart("picture_1");
            String fileName = filePart.getSubmittedFileName();;
            InputStream fileInputStream = filePart.getInputStream();

            String url = GCS.uploadToGCS(fileName, fileInputStream);
            response.getWriter().print(url);
            System.out.print("Success");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.getWriter().print("Hello Listing");
    }
}
