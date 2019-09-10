package epam.core.schedulers;

import epam.core.configs.GroovyConsoleSchedulerConfig;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.osgi.service.component.annotations.Component;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.GregorianCalendar;

@Component(immediate = true)
@Designate(ocd=GroovyConsoleSchedulerConfig.class)
public class EducationGroovyConsoleScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(EducationGroovyConsoleScheduler.class);
    private static final int REMAINING_NODE_LIFE_TIME = 5;
    private static final String APP_ROOT_PATH = "/apps/epam";
    private static final String LOGS_DIRECTORY_NAME = "logs";
    private static final String CREATED_TIME_PROPERTY = "jcr:created";

    private Map<String, Object> userProp;
    private ResourceResolver resourceResolver;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Activate
    protected void activate(final GroovyConsoleSchedulerConfig config){
        LOG.info("Activating EducationGroovyConsole Scheduler");
        userProp = new HashMap<>();
        userProp.put(ResourceResolverFactory.SUBSERVICE, "datawrite");
    }

    @Deactivate
    protected void deactivate(ComponentContext componentContext) {
        LOG.info("Deactivating EducationGroovyConsole Scheduler");
        if (resourceResolver.isLive())
            resourceResolver.close();
    }

    @Override
    public void run() {
        LOG.info("Delete Groovy log Scheduler started");
        try {
            resourceResolver = resourceResolverFactory.getResourceResolver(userProp);
            Resource appRoot = resourceResolver.getResource(APP_ROOT_PATH);

            if (appRoot.getChild(LOGS_DIRECTORY_NAME) != null){
                Resource logsRoot = appRoot.getChild(LOGS_DIRECTORY_NAME);
                deleteLogs(logsRoot.listChildren());
                logsRoot.getResourceResolver().commit();
            }
        } catch (LoginException e) {
            LOG.info("Unable to login with service user " + e);
        } catch (PersistenceException e) {
            LOG.info("Unable to delete resource " + e);
        }
    }

    private void deleteLogs(Iterator<Resource> iterator) throws PersistenceException {
        while (iterator.hasNext()){
            Resource resource = resourceResolver.getResource(iterator.next().getPath());
            GregorianCalendar resourceTime = (GregorianCalendar) resource.adaptTo(ValueMap.class).get(CREATED_TIME_PROPERTY);
            if (isNodeExpired(System.currentTimeMillis(), resourceTime.getTimeInMillis()))
                resourceResolver.delete(resource);
        }
    }

    private boolean isNodeExpired(long currentTimeMillis, long nodeTime) {
        return ((currentTimeMillis - nodeTime) / 1000 / 60) >= REMAINING_NODE_LIFE_TIME;
    }
}
