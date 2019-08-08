package com.aem.myproject.services.impl;

import com.aem.myproject.services.PageTitleService;
import com.aem.myproject.servlets.QueryBuilderServlet;
import com.day.cq.search.QueryBuilder;
import com.day.cq.wcm.api.Page;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

@Service
public class QueryBuilderServiceImpl implements PageTitleService {

    private static final Logger log = LoggerFactory.getLogger(QueryBuilderServlet.class);

    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public Iterator<Page> getContentPagesTitlesWithSlingQuery(Resource resource) {



        return null;
    }
}
