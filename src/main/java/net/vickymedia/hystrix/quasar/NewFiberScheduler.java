package net.vickymedia.hystrix.quasar;

import co.paralleluniverse.fibers.DefaultFiberScheduler;
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.FiberScheduler;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.strands.SuspendableRunnable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Schedules work on a new fiber.
 */
public class NewFiberScheduler extends Scheduler {
	private final static NewFiberScheduler DEFAULT_INSTANCE = new NewFiberScheduler();

	public static NewFiberScheduler getDefaultInstance() {
		return DEFAULT_INSTANCE;
	}

	private final FiberScheduler fiberScheduler;

	public NewFiberScheduler(FiberScheduler fiberScheduler) {
		if(fiberScheduler == null)
			throw new IllegalArgumentException("Fiber scheduler is null");
		if(fiberScheduler == DefaultFiberScheduler.getInstance() && DEFAULT_INSTANCE != null)
			throw new IllegalArgumentException("Fiber scheduler is the default FiberScheduler; use getDefaultInstance()");
		this.fiberScheduler = fiberScheduler;
	}

	private NewFiberScheduler() {
		this(DefaultFiberScheduler.getInstance());
	}

	@Override
	public Worker createWorker() {
		return new EventLoopScheduler();
	}


	private class EventLoopScheduler extends Scheduler.Worker implements Subscription {
		private final CompositeSubscription innerSubscription = new CompositeSubscription();

		private EventLoopScheduler() {
		}

		@Override
		public Subscription schedule(final Action0 action) {
			if (innerSubscription.isUnsubscribed()) {
				// don't schedule, we are unsubscribed
				return Subscriptions.empty();
			}

			final AtomicReference<Subscription> sf = new AtomicReference<Subscription>();
			final Subscription s = Subscriptions.from(new Fiber<Void>(fiberScheduler, new SuspendableRunnable() {

				@Override
				public void run() throws SuspendExecution {
					try {
						if (innerSubscription.isUnsubscribed()) {
							return;
						}
						action.call();
					} finally {
						// remove the subscription now that we're completed
						Subscription s = sf.get();
						if (s != null) {
							innerSubscription.remove(s);
						}
					}
				}
			}).start());

			sf.set(s);
			innerSubscription.add(s);
			return Subscriptions.create(new Action0() {

				@Override
				public void call() {
					s.unsubscribe();
					innerSubscription.remove(s);
				}

			});
		}

		@Override
		public Subscription schedule(final Action0 action, final long delayTime, final TimeUnit unit) {
			final AtomicReference<Subscription> sf = new AtomicReference<Subscription>();

			final Subscription s = Subscriptions.from(new Fiber<Void>(fiberScheduler, new SuspendableRunnable() {

				@Override
				public void run() throws InterruptedException, SuspendExecution  {
					Fiber.sleep(delayTime, unit);
					try {
						if (innerSubscription.isUnsubscribed()) {
							return;
						}
						// now that the delay is past schedule the work to be done for real on the UI thread
						action.call();
					} finally {
						// remove the subscription now that we're completed
						Subscription s = sf.get();
						if (s != null) {
							innerSubscription.remove(s);
						}
					}
				}
			}).start());

			sf.set(s);
			innerSubscription.add(s);
			return Subscriptions.create(new Action0() {

				@Override
				public void call() {
					s.unsubscribe();
					innerSubscription.remove(s);
				}

			});
		}

		@Override
		public void unsubscribe() {
			innerSubscription.unsubscribe();
		}

		@Override
		public boolean isUnsubscribed() {
			return innerSubscription.isUnsubscribed();
		}
	}
}
