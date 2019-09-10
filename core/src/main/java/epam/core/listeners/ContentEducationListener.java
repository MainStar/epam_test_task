package epam.core.listeners;

import epam.core.util.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.EventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Repository;
import javax.jcr.SimpleCredentials;
import javax.jcr.observation.Event;
import javax.jcr.RepositoryException;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

@Component(immediate = true, service = EventListener.class,
        property = {
                EventConstants.EVENT_TOPIC + "=/content/education"
        })
public class ContentEducationListener implements EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(ContentEducationListener.class);
    private static final String CONTENT_EDUCATION_PATH = "/content/education";
    private static final String[] TAGS = {"default:Education"};
    private static final String[] TYPES = {"cq:Page", "nt:unstructured"};
    private Session session;

    @Reference
    private Repository repository;

    @Activate
    public void activate(ComponentContext componentContext) throws Exception {
        LOG.info("Activating `Content Education Listener`");
        try {
            getSession();
            addNewListener();
            LOG.info("Session was registered successfully");
        } catch (RepositoryException e) {
            LOG.error("Unable to register session", e);
        }
    }

    @Deactivate
    public void deactivate() {
        if (session != null)
            session.logout();
    }

    @Override
    public void onEvent(EventIterator eventIterator) {
        LOG.info("Event was catch");
        while (eventIterator.hasNext()) {
            try {
                Node node = session.getNode(eventIterator.nextEvent().getPath());
                if (isNodeHasPageType(node)) {
                    node.addMixin(Constants.MIXING_TAGGABLE_TYPE);
                    session.save();
                    node.setProperty(Constants.TAGS_TYPE, TAGS);
                    session.save();
                }
            } catch (RepositoryException e) {
                LOG.error("Error while treating events", e);
            }
        }
    }

    private void getSession() throws RepositoryException {
        session = repository.login(new SimpleCredentials(Constants.USER_NAME_ADMIN, Constants.USER_PASSWORD_ADMIN.toCharArray()));
    }

    private void addNewListener() throws RepositoryException {
//        session.getWorkspace().getObservationManager().addEventListener(
//                this,
//                Event.NODE_ADDED,
//                CONTENT_EDUCATION_PATH,
//                true,
//                null,
//                null,
//                false
//        );
    }

    private boolean isNodeHasPageType(Node node) throws RepositoryException {
        return node.getProperty(Constants.PRIMARY_TYPE_PROPERTY).getValue().toString().equals(Constants.PAGE_TYPE);
    }
}
