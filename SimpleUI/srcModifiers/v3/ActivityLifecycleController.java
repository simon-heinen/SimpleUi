package v3;

import java.util.List;

import v2.simpleUi.ActivityLifecycleListener;

public interface ActivityLifecycleController {

	void addLifecycleListener(ActivityLifecycleListener l);

	List<? extends ActivityLifecycleListener> getAllLifecycleListeners();

}
