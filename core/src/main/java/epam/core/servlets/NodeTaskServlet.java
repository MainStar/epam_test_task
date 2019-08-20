package epam.core.servlets;

import epam.core.services.NodeService;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

@Component(immediate = true,
        service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "= Node task Servlet",
                SLING_SERVLET_PATHS + "=" + "/services/education/node-task",
                SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET
        })
public class NodeTaskServlet extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(NodeTaskServlet.class);
    private Session session;

    private static Map<String, String> nodeProperties = new HashMap<>();
    static {
        nodeProperties.put("jcr:primaryType", "nt:unstructured");
        nodeProperties.put("test key 1", "test value 2");
        nodeProperties.put("test key 2", "test value 2");
    }

    @Reference
    private NodeService nodeService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        LOG.info("Node task Servlet");

        ResourceResolver resourceResolver = request.getResourceResolver();
        session = resourceResolver.adaptTo(Session.class);
        try {
            Node node = session.getRootNode();
            LOG.info("Root node path: " + node.getPath());
            Node newNode = node.addNode("var/removedProperties/test-property" + new Random().nextInt(100));
            nodeService.setNodeProperties(nodeProperties, newNode);
            session.save();
            session.logout();
        } catch (RepositoryException e) {
            LOG.error("Failed while creating new node\n", e);
        }
    }
}
