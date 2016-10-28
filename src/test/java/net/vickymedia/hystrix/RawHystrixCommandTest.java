package net.vickymedia.hystrix;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.futures.AsyncListenableFuture;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import net.vickymedia.hystrix.util.HystrixUtil;

import java.util.concurrent.ExecutionException;

/**
 * Created by vee on 10/28/16.
 */
public class RawHystrixCommandTest extends HystrixCommand {

	public RawHystrixCommandTest() {
		super(HystrixCommandGroupKey.Factory.asKey("rawTest"));
	}

	public Object executeFiber() throws InterruptedException, ExecutionException, SuspendExecution {
		return AsyncListenableFuture.get(HystrixUtil.toListenableFuture(toObservable()));
	}

	@Override
	protected Object run() throws Exception {
		Fiber.sleep(10);
		System.out.println("done!");
		return "";
	}

}
