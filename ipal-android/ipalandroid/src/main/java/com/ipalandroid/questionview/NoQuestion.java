package com.ipalandroid.questionview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ipalandroid.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * This class represents No Question View. It creates the view from HTML,
 * validates input when there is no active question
 *
 * @authors W. F. Junkin and Ngoc Nguyen, DePauw Open Source Development Team
 *
 */

public class NoQuestion extends QuestionView {
    private static String TAG = "NoQuestionView";
    private EditText answerField;
    private LinearLayout layout;

    public NoQuestion(Document questionPage, String url, String username, int passcode) {
        super(questionPage, url, username, passcode);
        Log.d(TAG, "23 in " + TAG);
        qText = this.questionPage.select("legend").text();
    }

    @Override
    public View getQuestionView(Context c) {

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) inflater.inflate(R.layout.activity_no_question,null);
        return layout;

    }

    public LinearLayout getLayout()
    {
        return layout;
    }

    @Override
    public Boolean validateInput() {
            return true;
    }

    @Override
    public Boolean sendResult() {
            try {
                Jsoup.connect(url+"/mod/ipal/tempview.php?user="+username+"&p="+passcode)
                        .data("answer_id", "-1")
                        .data("a_text", "No active question")
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

}
