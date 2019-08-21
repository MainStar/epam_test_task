package epam.core.filters;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.EngineConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import java.io.IOException;

@Component(service = Filter.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "= Query Builder filter incoming requests",
                EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                Constants.SERVICE_RANKING + ":Integer=3000",
                EngineConstants.SLING_FILTER_PATTERN + "=" + "/services/education/titles"
        })
public class QueryBuilderFilter implements Filter {

    private final Logger LOG = LoggerFactory.getLogger(QueryBuilderFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOG.info("Query Builder filter started");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        SlingHttpServletRequest request = (SlingHttpServletRequest) servletRequest;
        SlingHttpServletResponse response = (SlingHttpServletResponse) servletResponse;

        String rootPath = request.getParameter("rootPath");

        try {
            Node parentNode = getParentNode(request, rootPath);
            request.setAttribute("rootPath", parentNode.getPath());
            filterChain.doFilter(request, response);
        } catch (RepositoryException e) {
            LOG.error("Failed while getting parent node " + e);
        }
    }

    private Node getParentNode(SlingHttpServletRequest request, String rootPath) throws RepositoryException {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Resource resource = resourceResolver.getResource(rootPath);
        Node node = resource.adaptTo(Node.class);
        return node.getParent();
    }

    @Override
    public void destroy() {
    }
}
