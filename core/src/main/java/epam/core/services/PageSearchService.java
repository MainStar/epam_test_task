package epam.core.services;

import javax.jcr.Session;

public interface PageSearchService {

    /**
     * This function return list of values from root path
     * @param session - JCR Session
     * @param rootPath - root path for search
     * */
    StringBuilder listOfNodesFromRootPathByKeyword(Session session, String rootPath);

}
