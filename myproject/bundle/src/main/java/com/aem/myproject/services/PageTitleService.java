package com.aem.myproject.services;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;

import java.util.Iterator;

public interface PageTitleService {

    Iterator<Page> getContentPagesTitlesWithSlingQuery(Resource resource);

}
