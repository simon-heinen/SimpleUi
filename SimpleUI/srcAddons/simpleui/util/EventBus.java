package simpleui.util;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;

public class EventBus {

	private static Bus instance;

	public static Bus getBus() {
		if (instance == null) {
			instance = new Bus(ThreadEnforcer.ANY);
		}
		return instance;
	}

	/**
	 * send any PoJo event object (interfaces are not allowed but abstract
	 * objects) via this method and it will be received by all listeners which
	 * previously used {@link EventBus#register(Object)}
	 * 
	 * @param event
	 */
	public static void post(Object event) {
		getBus().post(event);

	}

	/**
	 * a listener needs an public event method with an {@link Subscribe}
	 * annotation on it and as a parameter the event Pojo to listen to. The rest
	 * is handled by the event system
	 * 
	 * @param listener
	 */
	public static void register(Object listener) {
		getBus().register(listener);
	}

	public static void unregister(Object listener) {
		getBus().unregister(listener);
	}

}
