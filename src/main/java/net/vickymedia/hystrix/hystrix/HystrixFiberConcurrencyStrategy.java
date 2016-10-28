package net.vickymedia.hystrix.hystrix;

import co.paralleluniverse.fibers.Fiber;
import com.netflix.hystrix.HystrixInvokable;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import com.netflix.hystrix.util.PlatformSpecific;
import net.vickymedia.hystrix.quasar.FiberThreadPoolExecutorAdaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vee on 10/27/16.
 */
public class HystrixFiberConcurrencyStrategy extends HystrixConcurrencyStrategy {

	private final static Logger log = LoggerFactory.getLogger(HystrixConcurrencyStrategy.class);

	private final static HystrixConcurrencyStrategy shared = new HystrixFiberConcurrencyStrategy();

	public static HystrixConcurrencyStrategy getSharedInstance() {
		return shared;
	}

	public static void registerSelf() {
		HystrixPlugins plugins = HystrixPlugins.getInstance();
		plugins.registerConcurrencyStrategy(getSharedInstance());
		plugins.registerCommandExecutionHook(new LogHook());
	}

	private static class LogHook extends HystrixCommandExecutionHook {

		@Override
		public <T> void onExecutionStart(HystrixInvokable<T> commandInstance) {
			if (log.isDebugEnabled()) {
				log.debug("Execution started! Current thread named {}, is in fiber? {}", Thread.currentThread().getName(),
						Fiber.isCurrentFiber());
			}
		}

		@Override
		public <T> void onFallbackStart(HystrixInvokable<T> commandInstance) {
			if (log.isDebugEnabled()) {
				log.debug("Fallback started! Current thread named {}, is in fiber? {}", Thread.currentThread().getName(),
						Fiber.isCurrentFiber());
			}
		}

	}

	@Override
	public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixProperty<Integer> corePoolSize,
			HystrixProperty<Integer> maximumPoolSize, HystrixProperty<Integer> keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		ThreadFactory threadFactory;
		if (!PlatformSpecific.isAppEngineStandardEnvironment()) {
			threadFactory = new ThreadFactory() {
				final AtomicInteger threadNumber = new AtomicInteger(0);

				@Override
				public Thread newThread(Runnable r) {
					Thread thread = new Thread(r, "mus-hystrix-" + threadPoolKey.name() + "-" + threadNumber.incrementAndGet());
					thread.setDaemon(true);
					return thread;
				}

			};
		} else {
			threadFactory = PlatformSpecific.getAppEngineThreadFactory();
		}

		final int dynamicCoreSize = corePoolSize.get();
		final int dynamicMaximumSize = maximumPoolSize.get();

		if (dynamicCoreSize > dynamicMaximumSize) {
			log.error("Mus-Hystrix ThreadPool configuration at startup for : " + threadPoolKey.name() + " is trying to set coreSize = " +
					dynamicCoreSize + " and maximumSize = " + dynamicMaximumSize + ".  Maximum size will be set to " +
					dynamicCoreSize + ", the coreSize value, since it must be equal to or greater than the coreSize value");
			return new FiberThreadPoolExecutorAdaptor(dynamicCoreSize, dynamicCoreSize, keepAliveTime.get(), unit, workQueue, threadFactory);
		} else {
			return new FiberThreadPoolExecutorAdaptor(dynamicCoreSize, dynamicMaximumSize, keepAliveTime.get(), unit, workQueue, threadFactory);
		}

	}

}
