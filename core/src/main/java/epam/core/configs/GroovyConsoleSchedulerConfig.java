package epam.core.configs;

import epam.core.util.Constants;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name= Constants.DELETE_JOB_NAME)
public @interface GroovyConsoleSchedulerConfig {

    @AttributeDefinition(name = "Cron-job expression")
    String scheduler_expression() default "0 0/10 * 1/1 * ? *";

    @AttributeDefinition(name = "Concurrent task",
            description = "Whether or not to schedule this task concurrently")
    boolean scheduler_concurrent() default true;
}
