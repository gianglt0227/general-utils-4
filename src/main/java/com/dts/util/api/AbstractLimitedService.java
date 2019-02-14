/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dts.util.api;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author giang
 */
public abstract class AbstractLimitedService extends HttpServlet {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected String affiliateApiUrl = "";
    protected final String OUTPUT_DATETIME_PATTERN = "yyyy/MM/dd HH:mm:ss";
    protected final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat(OUTPUT_DATETIME_PATTERN)
            .serializeNulls()
            .create();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        logger.info("==> {}?{} ", request.getRequestURI(), request.getQueryString() == null ? "" : request.getQueryString());
        Stopwatch stopwatch = Stopwatch.createStarted();
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Max-Age", "86400");
        String jsonResult = "";

        try {
//            if (this.getClass().getSimpleName().compareTo(checkInfoPlayerServices.class.getSimpleName()) != 0) {
//                if (!RequestCache.getInstance().addRequest(this.getClass().getSimpleName())) {
//                    try (PrintWriter out = response.getWriter()) {
//                        out.write("Reach ConcurrentRequestLimit for " + this.getClass().getSimpleName());
//                        out.flush();
//                        return;
//                    }
//                }
            if (!checkIp(request)) {
                logger.info("This ip is not permitted to access this server");
                response.setStatus(405);
                ApiError error = new ApiError(405, "Invalid IP");
                jsonResult = gson.toJson(error);
            } else {
                JsonElement jsonElement = processRequestDetail(request);
                jsonResult = gson.toJson(jsonElement);
            }
        } catch (InvalidApiParamException ex) {
            response.setStatus(400);
            ApiError error = new ApiError(400, "Invalid parameter: " + ex.getParamName());
            jsonResult = gson.toJson(error);
        } catch (UserFriendlyException ex) {
            response.setStatus(500);
            ApiError error = new ApiError(500, ex.getMessage());
            jsonResult = gson.toJson(error);
        } catch (Exception ex) {
            logger.error("", ex);
            response.setStatus(500);
            ApiError error = new ApiError(500, "System busy");
            jsonResult = gson.toJson(error);
        } finally {
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResult);
                out.flush();
                logger.debug("<== Returned: {}", jsonResult);
            }
//            RequestCache.getInstance().removeRequest(this.getClass().getSimpleName());
            logger.info("<== {}?{} - finished in {} ms", request.getRequestURI(), request.getQueryString() == null ? "" : request.getQueryString(), stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
        }

    }

    protected boolean checkIp(HttpServletRequest request) {
        String curIP = request.getHeader("X-FORWARDED-FOR");
        if (curIP == null) {
            curIP = request.getRemoteAddr();
        }
        logger.info("ClientIp: " + curIP);
        String acceptedIp = getAcceptedIps();
        if (acceptedIp == null || acceptedIp.isEmpty()) {
            return true;
        }
        if (acceptedIp.contains(",")) {
            for (String ip : acceptedIp.split(",")) {
                if (ip.equalsIgnoreCase(curIP)) {
                    return true;
                }
            }
        }
        return acceptedIp.equalsIgnoreCase(curIP);
    }

    protected abstract String getAcceptedIps();

    protected abstract JsonElement processRequestDetail(HttpServletRequest request)
            throws Exception;

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
