package net.vickymedia.hystrix.spring;

import net.vickymedia.hystrix.annotation.EnableHystrixFiberExecution;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.cloud.commons.util.SpringFactoryImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Created by vee on 10/27/16.
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public class EnableHystrixFiberExecutionImportSelector extends
		SpringFactoryImportSelector<EnableHystrixFiberExecution> {

	@Override
	protected boolean isEnabled() {
		return new RelaxedPropertyResolver(getEnvironment()).getProperty(
				"hystrix.fiber.execution.enabled", Boolean.class, Boolean.TRUE);
	}

}
