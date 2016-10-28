package net.vickymedia.hystrix;

/**
 * Created by vee on 10/27/16.
 */

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import net.vickymedia.hystrix.annotation.HystrixFiberApplication;
import net.vickymedia.hystrix.hystrix.HystrixFiberConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//@FiberSpringBootApplication // This will enable fiber-blocking
//@EnableCircuitBreaker
@Controller
@HystrixFiberApplication
public class SampleJettyApplication {

	public static void main(String[] args) throws Exception {
		HystrixFiberConcurrencyStrategy.registerSelf();
		SpringApplication.run(SampleJettyApplication.class, args);
	}

	@Value("${name:World}")
	private String name;

	@Suspendable
	public String getHelloMessage() {
		try {
			Fiber.sleep(10);
//			new RawHystrixCommandTest().executeFiber();
		} catch (InterruptedException | SuspendExecution e) {
			throw new AssertionError(e);
		}
		System.out.println("[" +Thread.currentThread().getName()+ "]:hit");
		return "Hello" + this.name;
	}

	@Suspendable
	@HystrixCommand
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public String helloWorld()  {
		return getHelloMessage();
	}

}
