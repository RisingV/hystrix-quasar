package net.vickymedia.hystrix;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import net.vickymedia.hystrix.quasar.FiberThreadPoolExecutorAdaptor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by vee on 10/27/16.
 */
public class FiberThreadPoolExecutorAdaptorTest {

//	@Test
	@Suspendable
	public void test() throws InterruptedException, ExecutionException {
		FiberThreadPoolExecutorAdaptor executor = new FiberThreadPoolExecutorAdaptor(2, 4, 1, TimeUnit.MINUTES,
				new ArrayBlockingQueue<>(10));

//		FiberUtil.runInFiber( () -> {
//			testMain();
//		});

		executor.execute(this::testMain);

	}

	@Suspendable
	private void testMain() {
		try {
			System.out.println( "[" + Thread.currentThread().getName() + "]: " );
			Fiber.sleep(10);
		} catch (InterruptedException | SuspendExecution e) {
			throw new AssertionError(e);
		}
	}

}
