package com.treasurehunt.treasurehunt.db.gcs;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener("Creates a GCS client that is stored in the Servlet's context for later use via attribute gcs-client")
class GCSClientContextListener implements ServletContextListener {

    private static final String GOOGLE_CLOUD_PROJECT_ID = "treasurehunt-314717";

    static Storage createGCSClient() {
        return StorageOptions.newBuilder().setProjectId(GOOGLE_CLOUD_PROJECT_ID).build().getService();
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // This function is called when the application starts and will safely create a GCS client that can be used
        // to connect to
        ServletContext servletContext = event.getServletContext();
        Storage storage = (Storage) servletContext.getAttribute("gcs-client");
        if (storage == null) {
            storage = createGCSClient();
            servletContext.setAttribute("gcs-client", storage);
            System.out.println("Successfully created connection to GCS");
        }
    }
}
