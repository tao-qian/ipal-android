package com.ipalandroid.questionview;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ipalandroid.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EssayQuestionView extends QuestionView {

	private EditText answerField;


	public EssayQuestionView(Document questionPage, String url, String username, int passcode) {
		super(questionPage, url, username, passcode);
		qText = this.questionPage.select("legend").text();
	}
	@Override
	public View getQuestionView(Context c) {
		
		LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.essay,null);
		answerField = (EditText) layout.findViewById(R.id.answerField);
		TextView qTextView = (TextView) layout.findViewById(R.id.questionText);
		qTextView.setText(qText);
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
				Jsoup.connect(url+"/mod/ipal/tempview.php?user="+username+"&p="+passcode)
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
