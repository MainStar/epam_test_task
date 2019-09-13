package epam.core.beans;

import com.adobe.granite.jmx.annotation.Description;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;

@Description("Create user bean")
public interface UserBean {

    @Description("Create a new `content-author` user")
    void createUser() throws RepositoryException;

}
