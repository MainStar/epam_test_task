package epam.core.services.impl;

import epam.core.configs.JCRQueryServiceConfig;
import epam.core.services.PageSearchService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

@Component(service = PageSearchService.class, immediate = true)
@Designate(ocd = JCRQueryServiceConfig.class)
public class JCRQueryServiceImpl implements PageSearchService {

    private String keyword;

    @Activate
    @Modified
    public void activate(JCRQueryServiceConfig jcrQueryServiceConfig){
        this.keyword = jcrQueryServiceConfig.keyword();
    }

    private static final Logger log = LoggerFactory.getLogger(JCRQueryServiceImpl.class);

    @Override
    public StringBuilder listOfNodesFromRootPathByKeyword(Session session, String rootPath) {

        NodeIterator nodeIterator = null;
        String expression = String.format("SELECT * FROM [nt:base] AS s WHERE ISDESCENDANTNODE([%s]) AND CONTAINS([jcr:title], \"%s\")", rootPath, keyword);
        StringBuilder builder = new StringBuilder();

        try {
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            Query query = queryManager.createQuery(expression, Query.JCR_SQL2);
            QueryResult queryResult = query.execute();
            nodeIterator = queryResult.getNodes();

            while (nodeIterator.hasNext()){
                Node node = nodeIterator.nextNode();
                builder.append("Path: " + node.getPath() + "\n");
                if (!String.valueOf(node.getProperty("jcr:title")).equals("")){
                    log.info("url with title: " + node.getPath());
                }
            }

        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return builder;
    }
}
