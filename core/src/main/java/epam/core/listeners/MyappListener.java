package epam.core.listeners;

import epam.core.services.impl.JCRQueryServiceImpl;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

@Component(immediate = true,
            service = EventListener.class)
public class MyappListener implements EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(JCRQueryServiceImpl.class);
    private Session session;

    @Reference
    SlingRepository repository;

    @Activate
    public void activate(ComponentContext componentContext) throws Exception {
        LOG.info("Activating `Myapp listener`");
        String[] nodeTypes = {"cq:Page"};

        try {
            session = repository.loginService("datawrite", null);
            session.getWorkspace().getObservationManager().addEventListener(
                    this,
                    Event.PROPERTY_ADDED|Event.NODE_ADDED|Event.NODE_REMOVED,
                    " /apps/epam",
                    true,
                    null,
                    null,
                    false
            );
            LOG.info("Session was registered successfully");
        } catch (RepositoryException e) {
            LOG.error("Unable to register session", e);
        }
    }

    @Deactivate
    public void deactivate(){
        if (session != null)
            session.logout();
    }

    @Override
    public void onEvent(EventIterator eventIterator) {
        LOG.info("Registered some activity in MyappListener class");
        while (eventIterator.hasNext()){
            try {
                LOG.info("something has been added: " + eventIterator.nextEvent().getPath());
            } catch (RepositoryException e) {
                LOG.error("Error while treating events", e);
            }
        }
    }
}
