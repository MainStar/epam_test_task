package com.aem.myproject.services.impl;

import com.aem.myproject.services.PageTitleService;
import com.aem.myproject.servlets.QueryBuilderServlet;
import com.day.cq.wcm.api.Page;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import static org.apache.sling.query.SlingQuery.$;

import java.util.Iterator;

@Service
public class SlingQueryServiceImpl implements PageTitleService {

    @Reference
    private static final Logger log = LoggerFactory.getLogger(QueryBuilderServlet.class);

    @Override
    public Iterator<Page> getContentPagesTitlesWithSlingQuery(Resource resource) {

        Iterator<Resource> listChild = resource.listChildren();

        while (listChild.hasNext()){
//            Resource resourceCurrent = listChild.next();
//            log.info(String.valueOf($(resourceCurrent).closest("cq:Page[jcr:content/jcr:title]")));
//            String authorConfig = System.getProperty("sling.run.modes");
//            if (String.valueOf($(resourceCurrent).closest("cq:Page[jcr:content/jcr:title]")).equals(authorConfig)){
//
//            }
        }
        return null;
    }
}
