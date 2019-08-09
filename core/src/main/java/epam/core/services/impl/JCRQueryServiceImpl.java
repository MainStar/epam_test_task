package epam.core.services.impl;

import epam.core.configs.JCRQueryServiceConfig;
import epam.core.services.PageSearchService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.jcr.NodeIterator;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

@Component(service = PageSearchService.class, immediate = true)
@Designate(ocd = JCRQueryServiceConfig.class)
public class JCRQueryServiceImpl implements PageSearchService {

    private String keyword;
    private static final Logger LOG = LoggerFactory.getLogger(JCRQueryServiceImpl.class);
    private static final String QUERY_TITLE_FORMAT = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([%s]) AND CONTAINS([jcr:title], \"%s\")";

    @Activate
    @Modified
    public void activate(JCRQueryServiceConfig jcrQueryServiceConfig){
        this.keyword = jcrQueryServiceConfig.keyword();
    }

    @Override
    public StringBuilder listOfNodesFromRootPathByKeyword(Session session, String rootPath) {

        NodeIterator nodeIterator = null;
        String expression = String.format(QUERY_TITLE_FORMAT, rootPath, keyword);
        StringBuilder builder = new StringBuilder();

        try {
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(expression, Query.JCR_SQL2);
            QueryResult queryResult = query.execute();
            nodeIterator = queryResult.getNodes();

            while (nodeIterator.hasNext()){
                Node node = nodeIterator.nextNode();
                builder.append("Path: " + node.getPath() + "\n");
                if (!String.valueOf(node.getProperty("jcr:title")).isEmpty()){
                    LOG.info("url with title: " + node.getPath());
                }
            }

        } catch (RepositoryException e) {
            return builder.append("Failed while getting value from query");
        }
        return builder;
    }
}
