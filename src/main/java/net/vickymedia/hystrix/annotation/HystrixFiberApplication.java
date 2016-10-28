package net.vickymedia.hystrix.annotation;

import co.paralleluniverse.springframework.boot.autoconfigure.web.FiberSpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by vee on 10/27/16.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableHystrixFiberExecution
@EnableCircuitBreaker
@FiberSpringBootApplication
public @interface HystrixFiberApplication {

}
