package com.ipalandroid.questionview;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ipalandroid.R;
import com.ipalandroid.common.ImageDownloader;
import com.ipalandroid.common.TouchImageView;

public class MutipleChoiceQuesionView extends QuestionView {

	private class Choice
	{
		private String text;
		private int value;

		public Choice(String cText, int cValue) {
			text= cText;
			value= cValue;
		}

		public String getText() {
			return text;
		}

		public int getValue() {
			return value;
		}

	}

	private ArrayList<Choice> choices = new ArrayList<Choice>();
	private String currentChoice;

	public MutipleChoiceQuesionView(Document questionPage, String url, String username, int passcode)
	{
		super(questionPage, url, username, passcode);
		questionPage = this.questionPage;
		Elements spans = this.questionPage.getElementsByTag("span");
		for (Element s: spans) {
			String cText = s.select("label").text();
			if (s.select("input").attr("value").length() > 0) {
				int cValue = Integer.parseInt(s.select("input").attr("value"));
				choices.add(new Choice(cText, cValue));
			}
			
		}
		qText = this.questionPage.select("legend").text();
		imageURL = questionPage.select("img").attr("src");
		Log.w("IMAGE URL", imageURL+ "   a a a");
	}

	@Override
	public View getQuestionView(Context c) {
		/*LinearLayout layout = new LinearLayout(c);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		TextView questionText = new TextView(c);
		LayoutParams lparams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		questionText.setLayoutParams(lparams);
		questionText.setText(qText);
		questionText.setTextSize(18);
		layout.addView(questionText);*/
		LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//ScrollView view = (ScrollView) inflater.inflate(R.layout.multichoice, null);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.multichoice,null);
		//LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
		TextView qTextView = (TextView) layout.findViewById(R.id.questionText);
		qTextView.setText(qText);
		
		//Download and display the image
		ImageDownloader downloader = new ImageDownloader(c);
		Bitmap img = downloader.getImage(imageURL);
		TouchImageView tiv = new TouchImageView(c);
		tiv.setImageBitmap(img);
		layout.addView(tiv);
		RadioGroup g = (RadioGroup) layout.findViewById(R.id.answerChoice);
		for (Choice ch: choices) {
			RadioButton b = new RadioButton(c);
			b.setText(ch.text);
			b.setTextColor(c.getResources().getColor(R.color.view_text_color));
			//b.setTextColor(R.color.view_text_color);
			g.addView(b,new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
		g.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup rg, int checkedId) {
				for(int i=0; i<rg.getChildCount(); i++) {
					RadioButton btn = (RadioButton) rg.getChildAt(i);
					if(btn.getId() == checkedId) {
						currentChoice = (String) btn.getText();
						return;
					}
				}
			}
		});
		return layout;
	}

	@Override
	public Boolean validateInput() {
		return true;
	}

	@Override
	public Boolean sendResult() {
		//SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
		int valueToSend = getChoiceValueFromText(currentChoice);
		if (valueToSend != -1) {
			//Jsoup example, using post to log into depauw moodle
			try {
				Jsoup.connect(url+"/mod/ipal/tempview.php?user="+username+"&p="+passcode)
				.data("answer_id", valueToSend+"")
				.data("a_text", "")
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

	private int getChoiceValueFromText(String cText) {
		for (Choice c: choices) {
			if (c.getText().equals(cText)) {
				return c.getValue();
			}
		}
		return -1;
	}

}
