package epam.core.services.impl;

import epam.core.services.NodeService;
import epam.core.servlets.NodeTaskServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component(immediate = true, service = NodeService.class)
public class NodeServiceImpl implements NodeService {

    private static final Logger LOG = LoggerFactory.getLogger(NodeServiceImpl.class);

    @Override
    public Node createNode(Session session) throws RepositoryException {
        return session.getRootNode();
    }

    @Override
    public Node setNodeProperty(String property) {
        return null;
    }

    @Override
    public Node setNodeProperties(Map<String, String> properties, Node node) {
        Set<String> propertyNames = properties.keySet();
        try {
            for (String el : propertyNames){
                node.setProperty(el, properties.get(el));
            }
        } catch (RepositoryException e) {
            LOG.info("Failed while adding node property");
        }
        return node;
    }
}
