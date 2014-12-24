package simpleui.util;

import java.util.List;

public interface ActivityLifecycleController {

	void addLifecycleListener(ActivityLifecycleListener l);

	List<? extends ActivityLifecycleListener> getAllLifecycleListeners();

}
