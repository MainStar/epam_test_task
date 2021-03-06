package epam.core.beans.impl;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;
import epam.core.beans.UserBean;
import epam.core.listeners.ContentEducationListener;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.management.DynamicMBean;
import javax.management.NotCompliantMBeanException;
import java.util.HashMap;
import java.util.Map;

@Component(immediate = true,
        service = DynamicMBean.class,
        property = {
                "jmx.objectname = org.redquark.demo.core.jmx:type=System Info MBean"
        })
public class UserBeanImpl extends AnnotatedStandardMBean implements UserBean {

    private static final Logger LOG = LoggerFactory.getLogger(ContentEducationListener.class);
    private static final String CONTENT_AUTHOR_USER = "content-author";
    private static final String CONTENT_RESOURCE = "/content";
    private static final String CRX_REPLICATE_PERMISSIONS = "crx:replicate";
    private String GROUP_NAME = "authors";

    private Map<String, Object> userData;
    private ResourceResolver resourceResolver;

    public UserBeanImpl() throws NotCompliantMBeanException {
        super(UserBean.class);
    }

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Activate
    public void activate() {
        LOG.info("Initializing user map");
        userData = new HashMap<>();
        userData.put(ResourceResolverFactory.SUBSERVICE, "test");
    }

    @Override
    public void createUser() throws RepositoryException {
        try {
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(userData);

            UserManager manager = resourceResolver.adaptTo(UserManager.class);
            Group groupAuthors = (Group) manager.getAuthorizable(GROUP_NAME);
            deleteUserIfExist(manager);
            resourceResolver.commit();
            User user = manager.createUser(CONTENT_AUTHOR_USER, CONTENT_AUTHOR_USER);
            resourceResolver.commit();

            denyReplications();
            resourceResolver.commit();
            groupAuthors.addMember(user);
            resourceResolver.commit();

        } catch (LoginException e) {
            LOG.info("Unable to login: " + e);
        } catch (PersistenceException e) {
            LOG.error("Unable to commit changes: " + e);
        } finally {
            if (resourceResolver.isLive()) {
                resourceResolver.close();
            }
        }
    }

    private void deleteUserIfExist(UserManager manager) throws RepositoryException {
        Authorizable authorizable = manager.getAuthorizable(CONTENT_AUTHOR_USER);
        if (authorizable instanceof User) {
            User user = (User) authorizable;
            user.remove();
        }
    }

    private void denyReplications() throws RepositoryException, PersistenceException {
        AccessControlUtils.deny(resourceResolver.getResource(CONTENT_RESOURCE).adaptTo(Node.class),
                CONTENT_AUTHOR_USER, new String[]{CRX_REPLICATE_PERMISSIONS});
    }
}
