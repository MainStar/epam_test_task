package com.aem.myproject.util;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator implements BundleActivator {

    private static final Logger log = LoggerFactory.getLogger(Activator.class);

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        log.info("Start test");
        log.info("Keyword: " + bundleContext.getProperty("keyword"));
        log.info("Bundle `My Project` was started");
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        log.info("Bundle `My Project` was stoped");
    }
}
