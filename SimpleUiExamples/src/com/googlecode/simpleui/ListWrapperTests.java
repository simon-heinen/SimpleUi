package com.googlecode.simpleui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import v2.simpleUi.M_Container;
import v2.simpleUi.M_InfoText;
import v2.simpleUi.M_LeftRight;
import v2.simpleUi.M_Spinner;
import v2.simpleUi.ModifierInterface;
import v3.M_ListWrapperV2;
import v3.M_TextModifier;
import android.content.Context;
import android.util.Log;

public class ListWrapperTests {

	private static String LOG_TAG = "ListWrapperTests";

	public static void generateEditUiForAssociationQuestionQuestion(
			M_Container container, Collection<String> getAnswers,
			ArrayList<String> getQuestions, ArrayList<Long> getCorrectAnswerNrs) {

		container.add(new M_InfoText("Antwortenliste"));
		M_ListWrapperV2<String> m = M_ListWrapperV2
				.newStringCollectionModifier(getAnswers,
						"Neue Antwort hinzuf�gen");
		container.add(m);
		container.add(new M_InfoText("Fragen"));
		container.add(newQuestionCollectionModifier(getQuestions,
				getCorrectAnswerNrs, "Neue Frage hinzuf�gen", m));

	}

	private static M_ListWrapperV2<String> newQuestionCollectionModifier(
			final ArrayList<String> questions,
			final ArrayList<Long> correctAnswerNumbers, String buttonText,
			final M_ListWrapperV2<String> answerListModifier) {

		return new M_ListWrapperV2<String>(questions, buttonText, 5, 1) {
			@Override
			public ModifierInterface getModifierForItem(Context c,
					final String item, final int posInList) {
				M_Spinner possibleAnswersSpinner = new M_Spinner() {

					@Override
					public boolean save(SpinnerItem selectedItem) {

						if (selectedItem.getId() >= answerListModifier
								.getItems().size()) {
							return false;
						}

						int answerNumberPosInList = posInList;
						Log.d(LOG_TAG, "question nr=" + answerNumberPosInList);
						long posOfNewSelectedAnswer = selectedItem.getId();
						if (answerNumberPosInList < correctAnswerNumbers.size()) {
							Log.d(LOG_TAG,
									"   > old value (old answer nr="
											+ correctAnswerNumbers
													.get(answerNumberPosInList));
							Log.d(LOG_TAG, "   > new value (new answer nr)="
									+ posOfNewSelectedAnswer);
							correctAnswerNumbers.set(answerNumberPosInList,
									posOfNewSelectedAnswer);
						} else {
							Log.d(LOG_TAG, "Adding new item");
							correctAnswerNumbers.add(posOfNewSelectedAnswer);
						}
						return true;
					}

					@Override
					public int loadSelectedItemId() {
						int posOfCorrespondingAnswer = questions.indexOf(item);
						if (posOfCorrespondingAnswer == -1) {
							return 0;
						}
						return (int) (long) correctAnswerNumbers
								.get(posOfCorrespondingAnswer);
					}

					@Override
					public List<SpinnerItem> loadListToDisplay() {
						return toSpinnerItemList(answerListModifier.getItems());
					}

					private List<SpinnerItem> toSpinnerItemList(
							ArrayList<v3.M_ListWrapperV2.WrapperItem<String>> items) {
						List<SpinnerItem> r = new ArrayList<M_Spinner.SpinnerItem>();
						for (int i = 0; i < items.size(); i++) {
							v3.M_ListWrapperV2.WrapperItem<String> itemWrapper = items
									.get(i);
							if (!itemWrapper.removeRequest) {
								r.add(new SpinnerItem(i,
										((M_TextModifier) itemWrapper.modifier)
												.getCurrentTextValue()));
							}
						}
						return r;
					}

					@Override
					public CharSequence getTitleForSpinnerBox() {
						return "Antwort ausw�hlen";
					}

					@Override
					public String getVarName() {
						return null;
					}
				};
				M_TextModifier questionTextInput = new M_TextModifier() {

					@Override
					public boolean save(String newValue) {
						questions.remove(item);
						questions.add(newValue);
						return true;
					}

					@Override
					public String load() {
						return item;
					}

					@Override
					public String getVarName() {
						return null;
					}
				};
				return new M_LeftRight(questionTextInput, 3,
						possibleAnswersSpinner, 2);
			}

			@Override
			public String getNewItemInstance(Context c, int posOfNewItemInList) {
				return "";
			}

			@Override
			public boolean onRemoveRequest(String item) {
				if (!questions.remove(item)) {
					Log.w(LOG_TAG, "Item " + item
							+ " to remove was not in the target list!");
				}
				return true;
			}

			@Override
			public boolean onAddRequest(String item) {
				return questions.add(item);
			}

		};
	}

}
