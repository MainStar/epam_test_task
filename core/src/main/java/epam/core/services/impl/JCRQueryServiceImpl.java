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
import javax.jcr.RepositoryException;
import javax.jcr.NodeIterator;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

@Component(service = PageSearchService.class, immediate = true)
@Designate(ocd = JCRQueryServiceConfig.class)
public class JCRQueryServiceImpl implements PageSearchService {

    /**
     * Queries
     */
    private static final String QUERY_TITLE_FORMAT = "SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([%s]) AND CONTAINS([jcr:title], \"%s\")";

    private String keyword;
    private static final Logger LOG = LoggerFactory.getLogger(JCRQueryServiceImpl.class);

    @Activate
    @Modified
    public void activate(JCRQueryServiceConfig jcrQueryServiceConfig) {
        this.keyword = jcrQueryServiceConfig.keyword();
    }

    @Override
    public QueryResult executeQueryWithKeyword(Session session, String rootNode) throws RepositoryException {
        LOG.info("Query String: " + String.format(QUERY_TITLE_FORMAT, rootNode, keyword));
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        Query query = queryManager.createQuery(String.format(QUERY_TITLE_FORMAT, rootNode, keyword), Query.JCR_SQL2);
        return query.execute();
    }

    @Override
    public StringBuilder extractPaths(NodeIterator nodeIterator) throws RepositoryException {
        StringBuilder builder = new StringBuilder();
        while (nodeIterator.hasNext())
            builder.append(nodeIterator.nextNode().getPath() + "\n");
        return builder;
    }
}
