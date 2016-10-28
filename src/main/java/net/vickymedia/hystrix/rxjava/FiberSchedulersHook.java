package net.vickymedia.hystrix.rxjava;

import net.vickymedia.hystrix.quasar.NewFiberScheduler;
import rx.Scheduler;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;

/**
 * Created by vee on 10/28/16.
 */
public class FiberSchedulersHook extends RxJavaSchedulersHook {

	private static final RxJavaSchedulersHook shared = new FiberSchedulersHook();

	public static RxJavaSchedulersHook getSharedInstance() {
		return shared;
	}

	public static void registerSelf() {
		RxJavaPlugins.getInstance().registerSchedulersHook(getSharedInstance());
	}

	@Override
	public Scheduler getNewThreadScheduler() {
		return NewFiberScheduler.getDefaultInstance();
	}

	@Override
	public Scheduler getComputationScheduler() {
		return NewFiberScheduler.getDefaultInstance();
	}

	@Override
	public Scheduler getIOScheduler() {
		return NewFiberScheduler.getDefaultInstance();
	}

}
