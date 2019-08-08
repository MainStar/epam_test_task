package com.aem.myproject.servlets;

import com.aem.myproject.services.impl.SlingQueryServiceImpl;
import com.day.cq.search.QueryBuilder;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Simple myproject Servlet",
                SLING_SERVLET_PATHS + "=" + "=/services/education/titles",
                SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        })
public class QueryBuilderServlet extends SlingSafeMethodsServlet {

    private static final Logger log = LoggerFactory.getLogger(QueryBuilderServlet.class);

    @Reference
    private SlingQueryServiceImpl pageTitleService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        log.info("Executing Query Builder servlet");
        String title = null;

        Resource resource = request.getResource();
        Page page = resource.adaptTo(Page.class);
        title = page.getTitle();
        log.info("Title: " + title);

        response.getWriter().write("Title: " + title);

        super.doGet(request, response);
    }
}
