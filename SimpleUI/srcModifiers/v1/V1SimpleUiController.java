package v1;

import java.util.List;

import v2.simpleUi.ModifierInterface;



@Deprecated
public interface V1SimpleUiController {
	/**
	 * @param modifierList
	 *            Add all the {@link ModifierInterface} objects which should be
	 *            displayed in the UI
	 * @param optionalMessage
	 */
	public void customizeScreen(List<ModifierInterface> modifierList,
			Object optionalMessage);

}