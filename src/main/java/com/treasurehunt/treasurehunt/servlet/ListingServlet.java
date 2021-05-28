package com.treasurehunt.treasurehunt.servlet;

import com.google.cloud.storage.Storage;
import com.treasurehunt.treasurehunt.db.gcs.GCS;

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        response.getWriter().print("Hello Listing");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        // Extract DB connections from the Servlet Context, reusing the ones that were created in the ContextListener
        // when the application was started
        Storage storage = (Storage) request.getServletContext().getAttribute("gcs-client");

        Part filePart = request.getPart("picture_1");
        String fileName = filePart.getSubmittedFileName();
        InputStream fileInputStream = filePart.getInputStream();

        String url = GCS.uploadPicture(storage, fileName, fileInputStream);
        response.getWriter().print(url);
        System.out.print("Success");
    }
}
