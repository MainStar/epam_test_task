package epam.core.services;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.QueryResult;

public interface PageSearchService {

    /**
     * This function executing query to search URLs by root node path, and by keyword
     *
     * @param session  - JCR Session
     * @param rootNode - root node for search
     * @throws RepositoryException - throws executing query
     */
    QueryResult executeQueryWithKeyword(Session session, String rootNode) throws RepositoryException;

    /**
     * This function extracting values (paths) from node iterator
     *
     * @param nodeIterator - NodeIterator with nodes
     * @return StringBuilder - a builder with extracted paths
     * @throws RepositoryException - throws getting Node and Path from NodeIterator
     */
    StringBuilder extractPaths(NodeIterator nodeIterator) throws RepositoryException;

}
