package com.ipalandroid.questionview;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EssayQuestionView extends QuestionView {

	private String qText;
	private int question_id;
	private int active_question_id;
	private int course_id;
	private int user_id;
	private int ipal_id;
	private String instructor;
	private EditText answerField;


	public EssayQuestionView(Document questionPage, String url, String username, int passcode) {
		super(questionPage, url, username, passcode);
		qText = this.questionPage.select("legend").text();
		question_id = Integer.parseInt(this.questionPage.select("input[name=question_id]").attr("value"));
		active_question_id = Integer.parseInt(this.questionPage.select("input[name=active_question_id]").attr("value"));
		course_id = Integer.parseInt(this.questionPage.select("input[name=course_id]").attr("value"));
		user_id = Integer.parseInt(this.questionPage.select("input[name=user_id]").attr("value"));
		ipal_id = Integer.parseInt(this.questionPage.select("input[name=ipal_id]").attr("value"));
		instructor = this.questionPage.select("input[name=instructor]").attr("value");
	}
	@Override
	public View getQuestionView(Context c) {
		LinearLayout layout = new LinearLayout(c);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		TextView questionText = new TextView(c);
		LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		questionText.setLayoutParams(lparams);
		questionText.setText(qText);
		questionText.setTextSize(18);
		layout.addView(questionText);
		answerField = new EditText(c);
		layout.addView(answerField);
		return layout;
	}

	@Override
	public Boolean validateInput() {
		if (answerField.getText().toString().trim().length() > 0)
			return true;
		return false;
	}

	@Override
	public Boolean sendResult() {
		if (validateInput()) {
			//SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			try {
				Document doc = Jsoup.connect(this.url+"/mod/ipal/tempview.php?user="+this.username+"&p="+this.passcode)
				.data("answer_id", "-1")
				.data("a_text", answerField.getText().toString())
				.data("question_id", question_id+"")
				.data("active_question_id", active_question_id+"")
				.data("course_id", course_id+"")
				.data("user_id", user_id+"")
				.data("submit", "Submit")
				.data("ipal_id", ipal_id+"")
				.data("instructor", instructor)
				.post();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

}
