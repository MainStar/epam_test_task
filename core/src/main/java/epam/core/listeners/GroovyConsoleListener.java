package epam.core.listeners;

import com.icfolson.aem.groovy.console.notification.NotificationService;
import com.icfolson.aem.groovy.console.response.RunScriptResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.LoginException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Component(immediate = true, service = NotificationService.class)
public class GroovyConsoleListener implements NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(GroovyConsoleListener.class);

    private static final String LOGS_PARENT = "/apps/epam";
    private static final String LOGS_NODE_NAME = "/apps/epam/logs";
    private static final String FOLDER_TYPE = "nt:folder";

    private Map<String, Object> userData;
    private Map<String, Object> logParentProperties;
    private Map<String, Object> logProperties;
    private ResourceResolver resourceResolver;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Activate
    public void activate(){
        LOG.info("Initializing user map");
        userData = new HashMap<>();
        userData.put(ResourceResolverFactory.SUBSERVICE, "datawrite");

        logParentProperties = new HashMap<>();
        logParentProperties.put("jcr:primaryType", FOLDER_TYPE);

        logProperties = new HashMap<>();
        logProperties.put("jcr:primaryType", "cq:scriptRunningTime");
    }

    @Override
    public void notify(RunScriptResponse runScriptResponse) {
        try {
            resourceResolver = resourceResolverFactory.getResourceResolver(userData);
            Resource parent = resourceResolver.getResource(LOGS_PARENT);

            Resource log = createLogResource(parent);
            log.getResourceResolver().commit();

            ModifiableValueMap valueMap = log.adaptTo(ModifiableValueMap.class);
            valueMap.put("cq:scriptData", runScriptResponse.getData());
            log.getResourceResolver().commit();
        } catch (LoginException e) {
            LOG.error("Unable to login with service user " + e);
        } catch (PersistenceException e) {
            LOG.error("Unable to create new resource " + e);
        } finally {
            closeConnection();
        }
    }

    private Resource createLogResource(Resource parent) throws PersistenceException {
        Resource logParent = createLogsRootIfNotExist(logParentProperties, resourceResolver, parent);
        return resourceResolver.create(logParent, getLogNodeName(), logProperties);
    }

    private Resource createLogsRootIfNotExist(Map<String, Object> logNodeProp, ResourceResolver resourceResolver, Resource parent) throws PersistenceException {
        Resource resource;
        if (resourceResolver.getResource(LOGS_NODE_NAME) == null) {
            resource = resourceResolver.create(parent, "logs", logNodeProp);
            resource.getResourceResolver().commit();
        }else {
            resource = resourceResolver.getResource(LOGS_NODE_NAME);
        }
        return resource;
    }

    private String getLogNodeName(){
        return "log-".concat(LocalTime.now().toString().replace(":", "_").replace(".", "_"));
    }

    private void closeConnection(){
        if (resourceResolver.isLive()){
            resourceResolver.close();
        }
    }
}
