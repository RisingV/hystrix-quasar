package net.vickymedia.hystrix.quasar;

import co.paralleluniverse.fibers.FiberUtil;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.SuspendableRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by vee on 10/26/16.
 */
public class FiberThreadPoolExecutorAdaptor extends ThreadPoolExecutor {

	private static final Logger log = LoggerFactory.getLogger(FiberThreadPoolExecutorAdaptor.class);

	public FiberThreadPoolExecutorAdaptor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public FiberThreadPoolExecutorAdaptor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
	}

	public FiberThreadPoolExecutorAdaptor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
	}

	public FiberThreadPoolExecutorAdaptor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}

	@Override
	@Suspendable
	public void execute(Runnable command) {
		try {
			FiberUtil.runInFiber((SuspendableRunnable) command::run);
		} catch (ExecutionException | InterruptedException ex) {
			throw new AssertionError(ex);
		}
	}

}
