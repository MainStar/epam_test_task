package epam.core.servlets;

import epam.core.services.PageSearchService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.servlet.Servlet;
import java.io.IOException;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Simple myproject Servlet",
                SLING_SERVLET_PATHS + "=" + "/services/education/titles",
                SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        })
public class QueryBuilderServlet extends SlingSafeMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(QueryBuilderServlet.class);

    @Reference
    private PageSearchService jcrQueryServiceImpl;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        LOG.info("Executing Query Builder servlet");
        LOG.info("Test test: " + System.getProperty("sling.run.modes"));
        ResourceResolver resourceResolver = request.getResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);
        StringBuilder builder = jcrQueryServiceImpl.listOfNodesFromRootPathByKeyword(session, "/content");
        response.getWriter().write("" + builder);
    }
}
