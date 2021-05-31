package com.treasurehunt.treasurehunt.entity;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

public abstract class SearchListingsRequestBody implements HttpServletRequest {

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }
}
