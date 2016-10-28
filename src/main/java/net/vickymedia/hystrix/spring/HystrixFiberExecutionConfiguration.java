package net.vickymedia.hystrix.spring;

import net.vickymedia.hystrix.hystrix.HystrixFiberConcurrencyStrategy;
import net.vickymedia.hystrix.rxjava.FiberSchedulersHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by vee on 10/27/16.
 */
@Configuration
@ConditionalOnProperty(value = "hystrix.fiber.enabled", matchIfMissing = true)
public class HystrixFiberExecutionConfiguration implements SmartLifecycle {

	private static final Logger log = LoggerFactory.getLogger(HystrixFiberExecutionConfiguration.class);

	private final AtomicBoolean started = new AtomicBoolean(false);

	private void doStop() {
		started.set(false);
		log.info("HystrixFiberExecutionConfiguration stopped.");
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
		doStop();
		callback.run();
	}

	@Override
	public void start() {
		FiberSchedulersHook.registerSelf();
		HystrixFiberConcurrencyStrategy.registerSelf();
		started.set(true);
		log.info("HystrixFiberExecutionConfiguration started.");
	}

	@Override
	public void stop() {
		doStop();
	}

	@Override
	public boolean isRunning() {
		return started.get();
	}

	@Override
	public int getPhase() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
