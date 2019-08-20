package epam.core.services;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.List;
import java.util.Map;

public interface NodeService {

    Node createNode(Session session) throws RepositoryException;
    Node setNodeProperty(String property);
    Node setNodeProperties(Map<String, String> properties, Node node);

}
