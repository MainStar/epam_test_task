package epam.core.consumers;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Component(immediate = true, service = JobConsumer.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "= Listen changes in QueryBuilderServlet",
                JobConsumer.PROPERTY_TOPICS + "=education/querybuilder/job"
        })
public class JobConsumerQueryBuilder implements JobConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(JobConsumerQueryBuilder.class);
    private static final String SERVLET_LOGS_PATH = "/apps/epam/servletlogs";
    private static final String FOLDER_TYPE = "nt:folder";
    private static final String APP_ROOT_DIRECTORY = "/apps/epam";

    private String userID;
    private String requestParameter;

    private ResourceResolver resourceResolver;

    private Map<String, Object> userData;
    private Map<String, Object> logParentProperties;
    private Map<String, Object> logProperties;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Activate
    public void activate() {
        LOG.info("Initializing user map");
        userData = new HashMap<>();
        userData.put(ResourceResolverFactory.SUBSERVICE, "datawrite");

        logParentProperties = new HashMap<>();
        logParentProperties.put("jcr:primaryType", FOLDER_TYPE);

        logProperties = new HashMap<>();
        logProperties.put("jcr:primaryType", "cq:servletLog");
    }

    @Override
    public JobResult process(Job job) {
        LOG.info("Job execution process started");
        getJobData(job);

        if (isRequestDataEmpty(job))
            return JobResult.FAILED;

        try {
            resourceResolver = resourceResolverFactory.getResourceResolver(userData);
            Resource logRoot = getLogRootResource(resourceResolver.getResource(APP_ROOT_DIRECTORY));
            logRoot.getResourceResolver().commit();

            Resource log = resourceResolver.create(logRoot, getLogNodeName(), logProperties);
            log.getResourceResolver().commit();
            ValueMap logPropertiesMap = log.adaptTo(ModifiableValueMap.class);
            logPropertiesMap.put("cq:requestParameter", requestParameter);
            log.getResourceResolver().commit();
        } catch (LoginException e) {
            LOG.info("Unable to get service user " + e);
        } catch (PersistenceException e) {
            LOG.info("Unable to create log root resource " + e);
        }
        return JobResult.OK;
    }

    private void getJobData(Job job){
        userID = job.getProperty("userID").toString();
        requestParameter = job.getProperty("parameter").toString();
    }

    private boolean isRequestDataEmpty(Job job) {
        return job.getProperty("userID").toString().isEmpty() | job.getProperty("parameter").toString().isEmpty();
    }

    private Resource getLogRootResource(Resource appRoot) throws PersistenceException {
        Resource logRoot = null;
        if (resourceResolver.getResource(SERVLET_LOGS_PATH) == null)
            logRoot = resourceResolver.create(appRoot, "servletlogs", logParentProperties);
        else
            logRoot = resourceResolver.getResource(SERVLET_LOGS_PATH);
        return logRoot;
    }

    private String getLogNodeName(){
        return "servlet-log-".concat(LocalTime.now().toString().replace(":", "_").replace(".", "_"));
    }
}
