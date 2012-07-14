package com.ipalandroid.questionview;

import java.io.IOException;

import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class MutiplyChoiceQuesionView extends QuestionView {
	
	
	
	public MutiplyChoiceQuesionView(Document questionPage, String url,
			String username, int passcode) {
		super(questionPage, url, username, passcode);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getQuestionView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean validateInput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean sendResult() {
		// TODO Auto-generated method stub
		//Jsoup example, using post to log into depauw moodle
		try {
			Document doc = Jsoup.connect("https://moodle.depauw.edu/login/index.php")
					.data("username", "Your username here")
					.data("password","Your password here").post();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
