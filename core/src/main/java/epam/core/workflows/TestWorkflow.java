package epam.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import epam.core.listeners.ContentEducationListener;
import epam.core.util.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

@Component(immediate = true, service = WorkflowProcess.class, enabled = true)
public class TestWorkflow implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(ContentEducationListener.class);
    private static final String[] TAGS = {"default:Education"};

    @Activate
    public void activate() {
        LOG.info("TestWorkflow activating");
    }

    @Override
    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap) throws WorkflowException {
        Session session = workflowSession.adaptTo(Session.class);
        try {
            Node newNode = session.getNode(workItem.getWorkflowData().getPayload().toString());
            newNode.addMixin(Constants.MIXING_TAGGABLE_TYPE);
            session.save();
            newNode.setProperty(Constants.TAGS_TYPE, TAGS);
            session.save();
        } catch (RepositoryException e) {
            e.printStackTrace();
        } finally {
            session.logout();
            workflowSession.logout();
        }
    }
}

