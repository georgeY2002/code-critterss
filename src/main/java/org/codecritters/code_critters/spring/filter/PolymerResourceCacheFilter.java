package org.codecritters.code_critters.spring.filter;

/*-
 * #%L
 * Code Critters
 * %%
 * Copyright (C) 2019 Michael Gruber
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.codecritters.code_critters.spring.HtmlResponseWrapper;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

@Component
public class PolymerResourceCacheFilter implements Filter {

    private static String TEMPDIR = "./temp";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // only /lib files needs to be modified
        if (httpRequest.getRequestURI().startsWith("/lib/")) {
            File file = new File(TEMPDIR + httpRequest.getRequestURI());
            if (file.exists() && !file.isDirectory()) {
                //check age of the file
                RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
                if (file.lastModified() >= bean.getStartTime()) {
                    //If younger then runtime use existing one
                    InputStream is = new FileInputStream(file);
                    response.setContentLength((int) file.length());
                    response.setContentType("application/javascript;charset=ISO-8859-1");
                    IOUtils.copy(is, response.getOutputStream());
                    is.close();
                } else {
                    //Create new cached file
                    String responseContent = cacheFile(request, response, chain, file);
                    response.setContentLength(responseContent.length());
                    response.getWriter().write(responseContent);
                }
            } else {
                //Create new cached file
                String responseContent = cacheFile(request, response, chain, file);
                response.setContentLength(responseContent.length());
                response.getWriter().write(responseContent);
            }
        } else {
            //Do nothing and handle as normal request
            chain.doFilter(request, response);
        }
    }

    /**
     * Caches the files addressed by /lib/* and changes their content so that the
     * browser can resolve the paths
     *
     * @param request  Request coming from the browser
     * @param response Response to send back to the browser
     * @param chain    Filterchain doing some more filters and executes the reques
     * @param f        The file to write the data in
     * @return the responses string content
     * @throws IOException      if an error occurs during writing the file
     * @throws ServletException comes from the filter chain
     */
    private String cacheFile(ServletRequest request, ServletResponse response, FilterChain chain, File f) throws IOException, ServletException {
        HtmlResponseWrapper capturingResponseWrapper = new HtmlResponseWrapper(
                (HttpServletResponse) response);
        chain.doFilter(request, capturingResponseWrapper);
        //add "/lib/" in import paths in the file where "@polymer" or "@webcombonent" is
        String pattern = "(?<=(import\\s.{0,100}))(?=(@.{3,20}/))";
        String[] componentsArray = capturingResponseWrapper.getCaptureAsString().split(pattern);
        String responseContent = componentsArray[0];
        for (int i = 1; i < componentsArray.length; ++i) {
            responseContent += "/lib/" + componentsArray[i];
        }
        //create directory and file
        f.getParentFile().mkdirs();
        f.createNewFile();
        FileWriter writer = new FileWriter(f);
        writer.write(responseContent);
        writer.close();
        return responseContent;
    }

    @Override
    public void destroy() {

    }
}
