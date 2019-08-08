package epam.core.configs;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface JCRQueryServiceConfig {

    @AttributeDefinition(name = "keyword", description = "Keyword")
    String keyword();

}
