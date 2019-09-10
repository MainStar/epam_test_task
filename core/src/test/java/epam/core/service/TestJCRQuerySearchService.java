package epam.core.service;

import epam.core.services.PageSearchService;
import epam.core.services.impl.JCRQueryServiceImpl;
import org.apache.jackrabbit.commons.JcrUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.QueryResult;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.List;

public class TestJCRQuerySearchService {

    public static final String USER_NAME = "admin";
    public static final String USER_PASSWORD = "12345Vlad";

    private Session session;
    private String rootNode = "/content";
    private List<String> requestPaths = new ArrayList<>();

    @Inject
    private PageSearchService jcrQueryService;

    @Inject
    private Repository repository;

    @Before
    public  void setup() throws RepositoryException {

//        jcrQueryService = new JCRQueryServiceImpl();
        session = repository.login(new SimpleCredentials(USER_NAME, USER_PASSWORD.toCharArray()));

        requestPaths.add("/content/communities/enablement/createresourcessteps/test");
        requestPaths.add("/content/we-retail/us/en/user/smartlist/Test/jcr:content");
        requestPaths.add("/content/we-retail/us/en/content-page-test-2/jcr:content");
        requestPaths.add("/content/we-retail/test-live-copy/jcr:content");
    }

//    @Test
    public void testExecuteQueryWithKeyword() throws RepositoryException {
        QueryResult queryResult = jcrQueryService.executeQueryWithKeyword(session, rootNode);
        System.out.println(queryResult.getNodes().getSize());
        Assert.assertNotEquals(queryResult.getNodes().getSize(), 0);
    }

//    @Test
    public void testExtractPaths() throws RepositoryException {
        NodeIterator nodeIterator = jcrQueryService.executeQueryWithKeyword(session, rootNode).getNodes();
        StringBuilder builder = jcrQueryService.extractPaths(nodeIterator);
        for (String el : requestPaths) {
            Assert.assertTrue(builder.toString().contains(el));
        }
    }
}
