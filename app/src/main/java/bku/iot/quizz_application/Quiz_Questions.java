package bku.iot.quizz_application;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Quiz_Questions extends AppCompatActivity {

    private RecyclerView rcvQuestion;
    private QuestionAdapter questAdapter;
    ImageView imageQues;
    TextView quesTitle;
    Button previousBtn, afterBtn;
    String temp, newURL;
    public String[] answer;
    public int a = 0;
    Boolean firstCounter = FALSE;
    Boolean Submit = FALSE;
    public RadioGroup radioGroup;
    RadioButton radioButton;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();   //This line hides the action bar

        setContentView(R.layout.activity_quiz_questions);

        rcvQuestion = findViewById(R.id.recycleView_history_question);
        quesTitle = findViewById(R.id.question_title);
        previousBtn = findViewById(R.id.PreviousBtn);
        afterBtn = findViewById(R.id.AfterBtn);
        imageQues = findViewById(R.id.img_question);
        radioGroup = findViewById(R.id.groupradio);
        radioGroup.clearCheck();

        bundle = getIntent().getExtras();
        if (bundle != null)
        {
            temp = (String)bundle.get("PIN");
        }
        System.out.println(temp);

        int lastIndex = temp.lastIndexOf("/", temp.lastIndexOf("/") - 1);
        if (lastIndex != -1)
        {
            // Extract the substring up to and including the second-to-last '/'
            newURL = temp.substring(0, lastIndex + 1);

            int SecondlastIndex = newURL.lastIndexOf("/", newURL.lastIndexOf("/") - 1);

            newURL = temp.substring(0, SecondlastIndex + 1);

            System.out.println("New URL: " + newURL);
        }
        else
        {
            System.out.println("Invalid URL");
        }

        RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
        String imageUrl = newURL + "CH%20(1).png";

        ImageRequest imageRequest = new ImageRequest(
                imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        // Display the image in the ImageView
                        imageQues.setImageBitmap(bitmap);
                    }
                },
                0, 0,
                ImageView.ScaleType.FIT_CENTER,
                Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });

        // Add the request to the RequestQueue.
        queue1.add(imageRequest);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = newURL + "dapan.txt";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response)
                    {
                        // Use regex to extract key-value pairs
                        String replacedA = response.replaceAll("&#39;", "");

                        // Remove the leading '[' and trailing ']' brackets
                        String B = replacedA.substring(1, replacedA.length() - 1);

                        answer = B.replace("\\n", "").split(", ");

                        a = answer.length;

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Quiz_Questions.this, LinearLayoutManager.HORIZONTAL, false);
                        rcvQuestion.setLayoutManager(linearLayoutManager);

                        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(Quiz_Questions.this, 0);
                        rcvQuestion.addItemDecoration(itemDecoration);

                        for (int i = 0; i < rcvQuestion.getItemDecorationCount(); i++) {
                            if (rcvQuestion.getItemDecorationAt(i) instanceof DividerItemDecoration)
                                rcvQuestion.removeItemDecorationAt(i);
                        }

                        questAdapter = new QuestionAdapter(Quiz_Questions.this, getListQuestion(a), quesTitle);
                        rcvQuestion.setAdapter(questAdapter);

                        questAdapter.MaxQuestion = String.valueOf(a);
                        questAdapter.quesTitle = quesTitle;
                        questAdapter.answer1 = answer;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        });
        queue.add(stringRequest);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton)group.findViewById(checkedId);
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                afterBtn.setText("Câu hỏi sau");

                Submit = FALSE;

                questAdapter.selectedId = radioGroup.getCheckedRadioButtonId();

                TextView questionName = findViewById(R.id.text_questions);

//                System.out.println("Vị trí: " + questAdapter.currentQues + " - Đáp án: " + questAdapter.answerDone.get(questAdapter.currentQues));

                // Ensure that currentQues is a valid position within your RecyclerView's range
                if (questAdapter.currentQues >= 0 && questAdapter.currentQues < questAdapter.getItemCount())
                {
                    // Scroll to the desired position
                    rcvQuestion.scrollToPosition(questAdapter.currentQues);

                    // Post a Runnable to perform the click after the RecyclerView has settled
                    rcvQuestion.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (firstCounter)
                            {
                                RecyclerView.ViewHolder viewHolder = rcvQuestion.findViewHolderForAdapterPosition(questAdapter.currentQues);
                                if (viewHolder instanceof QuestionAdapter.questionViewHolder)
                                {
                                    questAdapter.isButton = FALSE;

                                    questAdapter.isCheck = TRUE;

                                    RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
                                    String imageUrl = newURL + "CH%20(" + (questAdapter.currentQues) + ").png";

                                    ImageRequest imageRequest = new ImageRequest(
                                            imageUrl,
                                            new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap bitmap) {
                                                    // Display the image in the ImageView
                                                    imageQues.setImageBitmap(bitmap);
                                                }
                                            },
                                            0, 0,
                                            ImageView.ScaleType.FIT_CENTER,
                                            Bitmap.Config.ARGB_8888,
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                    // Handle error
                                                }
                                            });

                                    // Add the request to the RequestQueue.
                                    queue1.add(imageRequest);

                                    //System.out.println("The op is choose: " + questAdapter.answerDone.get(questAdapter.currentQues));

                                    if (questAdapter.answerDone.containsKey(questAdapter.currentQues - 1))
                                    {
                                        if (questAdapter.answerDone.get(questAdapter.currentQues) != null)
                                        {
                                            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("A")) {
                                                questAdapter.selectedId = 2131362159;
                                                radioGroup.check(questAdapter.selectedId);
                                                questAdapter.answerQues = "A";
                                            }
                                            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("B")) {
                                                questAdapter.selectedId = 2131362160;
                                                radioGroup.check(questAdapter.selectedId);
                                                questAdapter.answerQues = "B";
                                            }
                                            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("C")) {
                                                questAdapter.selectedId = 2131362161;
                                                radioGroup.check(questAdapter.selectedId);
                                                questAdapter.answerQues = "C";
                                            }
                                            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("D")) {
                                                questAdapter.selectedId = 2131362162;
                                                radioGroup.check(questAdapter.selectedId);
                                                questAdapter.answerQues = "D";
                                            }
                                        }
                                    }
                                    else
                                    {
                                        if (questAdapter.answerDone.get(questAdapter.currentQues) == null)
                                        {
                                            radioButton = (RadioButton) radioGroup.findViewById(questAdapter.selectedId);
                                            if (questAdapter.selectedId != -1)
                                            {
                                                questAdapter.answerQues = (String) radioButton.getText();
                                            }
                                        }
                                        else
                                        {
                                            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("A")) {
                                                questAdapter.selectedId = 2131362159;
                                                radioGroup.check(questAdapter.selectedId);
                                                questAdapter.answerQues = "A";
                                            }
                                            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("B")) {
                                                questAdapter.selectedId = 2131362160;
                                                radioGroup.check(questAdapter.selectedId);
                                                questAdapter.answerQues = "B";
                                            }
                                            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("C")) {
                                                questAdapter.selectedId = 2131362161;
                                                radioGroup.check(questAdapter.selectedId);
                                                questAdapter.answerQues = "C";
                                            }
                                            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("D")) {
                                                questAdapter.selectedId = 2131362162;
                                                radioGroup.check(questAdapter.selectedId);
                                                questAdapter.answerQues = "D";
                                            }
                                        }
                                    }

                                    ((QuestionAdapter.questionViewHolder) viewHolder).layitem.performClick();

                                    questAdapter.currentQues = questAdapter.currentQues - 1;

                                    if (questAdapter.currentQues == 0)
                                    {
                                        firstCounter = FALSE;
                                    }

                                    if (questAdapter.selectedId == -1)
                                    {
                                        System.out.println("The no option is choose in previousBTn");
                                    }
                                    else
                                    {
                                        System.out.println("PreviousBtn -- Vị trí: " + questAdapter.currentQues);
                                    }
                                }
                            }
                            else
                            {
                                if (questAdapter.currentQues == 0)
                                {
                                    questAdapter.currentQues = 1;

                                    if (questAdapter.answerDone.get(questAdapter.currentQues) != null)
                                    {
                                        if (questAdapter.answerDone.get(questAdapter.currentQues).equals("A")) {
                                            questAdapter.selectedId = 2131362159;
                                            radioGroup.check(questAdapter.selectedId);
                                        }
                                        if (questAdapter.answerDone.get(questAdapter.currentQues).equals("B")) {
                                            questAdapter.selectedId = 2131362160;
                                            radioGroup.check(questAdapter.selectedId);
                                        }
                                        if (questAdapter.answerDone.get(questAdapter.currentQues).equals("C")) {
                                            questAdapter.selectedId = 2131362161;
                                            radioGroup.check(questAdapter.selectedId);
                                        }
                                        if (questAdapter.answerDone.get(questAdapter.currentQues).equals("D")) {
                                            questAdapter.selectedId = 2131362162;
                                            radioGroup.check(questAdapter.selectedId);
                                        }

                                        questAdapter.currentQues = 0;
                                    }
                                }
                            }
                        }
                    });
                }
                radioGroup.clearCheck();
            }
        });

        afterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Submit)
                {
//                    System.out.println("Helooooo : " + questAdapter.currentQues);
                    if ((questAdapter.currentQues + 1) == questAdapter.answer1.length)
                    {
//                        System.out.println("Checkkkk!!!! ");

                        questAdapter.selectedId = radioGroup.getCheckedRadioButtonId();

                        if (questAdapter.selectedId == 2131362159)
                        {
                            questAdapter.answerQues = "A";
                        }
                        if (questAdapter.selectedId == 2131362160)
                        {
                            questAdapter.answerQues = "B";
                        }
                        if (questAdapter.selectedId == 2131362161)
                        {
                            questAdapter.answerQues = "C";
                        }
                        if (questAdapter.selectedId == 2131362162)
                        {
                            questAdapter.answerQues = "D";
                        }

                        questAdapter.answerDone.put(questAdapter.currentQues + 1, questAdapter.answerQues);
                    }

                    runProgram();
                }

                if (firstCounter == FALSE)
                {
                    questAdapter.currentQues = 0;
                }

                firstCounter = TRUE;

                questAdapter.selectedId = radioGroup.getCheckedRadioButtonId();

                radioButton = (RadioButton) radioGroup.findViewById(questAdapter.selectedId);

//                System.out.println("Id: " + questAdapter.selectedId);

                TextView questionName = findViewById(R.id.text_questions);

                // Ensure that currentQues is a valid position within your RecyclerView's range
                if (questAdapter.currentQues >= 0 && questAdapter.currentQues < questAdapter.getItemCount())
                {
                    // Scroll to the desired position
                    rcvQuestion.scrollToPosition(questAdapter.currentQues);

                    // Post a Runnable to perform the click after the RecyclerView has settled
                    rcvQuestion.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            RecyclerView.ViewHolder viewHolder = rcvQuestion.findViewHolderForAdapterPosition(questAdapter.currentQues);

                            if (viewHolder instanceof QuestionAdapter.questionViewHolder)
                            {
                                questAdapter.isCheck = TRUE;

                                questAdapter.isButton = TRUE;

                                questAdapter.currentQues = questAdapter.currentQues + 1;

//                                System.out.println("Position: " + questAdapter.currentQues);

                                if (questAdapter.answerDone.containsKey(questAdapter.currentQues + 1))
                                {
                                    if (questAdapter.currentQues - 1 == 0)
                                    {
                                        if (questAdapter.answerDone.get(questAdapter.currentQues + 1).equals("A"))
                                        {
                                            questAdapter.selectedId = 2131362159;
                                            radioGroup.check(questAdapter.selectedId);
                                            questAdapter.answerQues = "A";
                                        }
                                        if (questAdapter.answerDone.get(questAdapter.currentQues + 1).equals("B"))
                                        {
                                            questAdapter.selectedId = 2131362160;
                                            radioGroup.check(questAdapter.selectedId);
                                            questAdapter.answerQues = "B";
                                        }
                                        if (questAdapter.answerDone.get(questAdapter.currentQues + 1).equals("C"))
                                        {
                                            questAdapter.selectedId = 2131362161;
                                            radioGroup.check(questAdapter.selectedId);
                                            questAdapter.answerQues = "C";
                                        }
                                        if (questAdapter.answerDone.get(questAdapter.currentQues + 1).equals("D"))
                                        {
                                            questAdapter.selectedId = 2131362162;
                                            radioGroup.check(questAdapter.selectedId);
                                            questAdapter.answerQues = "D";
                                        }

//                                        System.out.println("Hellloooo");
                                    }
                                    else if ((questAdapter.answerDone.get(questAdapter.currentQues) == null) && questAdapter.answerDone.containsKey(questAdapter.currentQues + 1))
                                    {
                                        if (questAdapter.answerDone.get(questAdapter.currentQues + 1).equals("A"))
                                        {
                                            questAdapter.selectedId = 2131362159;
                                            radioGroup.check(questAdapter.selectedId);
                                            questAdapter.answerQues = "A";
                                        }
                                        if (questAdapter.answerDone.get(questAdapter.currentQues + 1).equals("B"))
                                        {
                                            questAdapter.selectedId = 2131362160;
                                            radioGroup.check(questAdapter.selectedId);
                                            questAdapter.answerQues = "B";
                                        }
                                        if (questAdapter.answerDone.get(questAdapter.currentQues + 1).equals("C"))
                                        {
                                            questAdapter.selectedId = 2131362161;
                                            radioGroup.check(questAdapter.selectedId);
                                            questAdapter.answerQues = "C";
                                        }
                                        if (questAdapter.answerDone.get(questAdapter.currentQues + 1).equals("D"))
                                        {
                                            questAdapter.selectedId = 2131362162;
                                            radioGroup.check(questAdapter.selectedId);
                                            questAdapter.answerQues = "D";
                                        }

                                        radioButton = (RadioButton) radioGroup.findViewById(questAdapter.selectedId);

//                                        System.out.println("Hellloooo1");
                                    }
                                    else if (questAdapter.answerDone.get(questAdapter.currentQues + 1) != null)
                                    {
                                        if (radioButton.getText() != questAdapter.answerDone.get(questAdapter.currentQues))
                                        {
                                            questAdapter.answerQues = (String)radioButton.getText();
                                            if (questAdapter.answerDone.get(questAdapter.currentQues + 1).equals("A"))
                                            {
                                                questAdapter.selectedId = 2131362159;
                                                radioGroup.check(questAdapter.selectedId);
                                            }
                                            if (questAdapter.answerDone.get(questAdapter.currentQues + 1).equals("B"))
                                            {
                                                questAdapter.selectedId = 2131362160;
                                                radioGroup.check(questAdapter.selectedId);
                                            }
                                            if (questAdapter.answerDone.get(questAdapter.currentQues + 1).equals("C"))
                                            {
                                                questAdapter.selectedId = 2131362161;
                                                radioGroup.check(questAdapter.selectedId);
                                            }
                                            if (questAdapter.answerDone.get(questAdapter.currentQues + 1).equals("D"))
                                            {
                                                questAdapter.selectedId = 2131362162;
                                                radioGroup.check(questAdapter.selectedId);
                                            }
                                            questAdapter.selectedId = 1;
                                        }
                                        else if (questAdapter.answerDone.get(questAdapter.currentQues) != null)
                                        {
                                            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("A"))
                                            {
                                                questAdapter.selectedId = 2131362159;
                                                radioGroup.check(questAdapter.selectedId);
                                                questAdapter.answerQues = "A";
                                            }
                                            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("B"))
                                            {
                                                questAdapter.selectedId = 2131362160;
                                                radioGroup.check(questAdapter.selectedId);
                                                questAdapter.answerQues = "B";
                                            }
                                            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("C"))
                                            {
                                                questAdapter.selectedId = 2131362161;
                                                radioGroup.check(questAdapter.selectedId);
                                                questAdapter.answerQues = "C";
                                            }
                                            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("D"))
                                            {
                                                questAdapter.selectedId = 2131362162;
                                                radioGroup.check(questAdapter.selectedId);
                                                questAdapter.answerQues = "D";
                                            }
                                        }
                                        else if (questAdapter.answerDone.get(questAdapter.currentQues).equals("A"))
                                        {
                                            questAdapter.selectedId = 2131362159;
                                            radioGroup.check(questAdapter.selectedId);
                                            questAdapter.answerQues = "A";
                                        }
                                        else if (questAdapter.answerDone.get(questAdapter.currentQues).equals("B"))
                                        {
                                            questAdapter.selectedId = 2131362160;
                                            radioGroup.check(questAdapter.selectedId);
                                            questAdapter.answerQues = "B";
                                        }
                                        else if (questAdapter.answerDone.get(questAdapter.currentQues).equals("C"))
                                        {
                                            questAdapter.selectedId = 2131362161;
                                            radioGroup.check(questAdapter.selectedId);
                                            questAdapter.answerQues = "C";
                                        }
                                        else if (questAdapter.answerDone.get(questAdapter.currentQues).equals("D"))
                                        {
                                            questAdapter.selectedId = 2131362162;
                                            radioGroup.check(questAdapter.selectedId);
                                            questAdapter.answerQues = "D";
                                        }
                                        radioButton = (RadioButton) radioGroup.findViewById(questAdapter.selectedId);

//                                        System.out.println("Vị trí câu chọn: " + radioGroup.getCheckedRadioButtonId() + " --- Đáp án chọn: " + questAdapter.answerQues);
                                    }
                                }

                                if (questAdapter.selectedId == -1)
                                {
                                    System.out.println("The no option is choose in afterBTn");
                                }
                                else if (questAdapter.selectedId == 1)
                                {
                                    System.out.println("Đáp án mới: " + questAdapter.answerQues);
                                }
                                else
                                {
                                    System.out.println("AfterBtn -- Vị trí: " + questAdapter.currentQues + " - Đáp án: " + radioButton.getText());

                                    questAdapter.answerQues = (String) radioButton.getText();
                                }

                                ((QuestionAdapter.questionViewHolder) viewHolder).layitem.performClick();

                                if ((questAdapter.currentQues + 1) == questAdapter.answer1.length)
                                {
                                    afterBtn.setText("Nộp bài");
                                    Submit = TRUE;
                                }
                                else
                                {
                                    Submit = FALSE;
                                }

                                RequestQueue queue2 = Volley.newRequestQueue(getApplicationContext());
                                String imageUrl1 = newURL + "CH%20("+ (questAdapter.currentQues + 1) +").png";

                                ImageRequest imageRequest1 = new ImageRequest(
                                        imageUrl1,
                                        new Response.Listener<Bitmap>() {
                                            @Override
                                            public void onResponse(Bitmap bitmap) {
                                                // Display the image in the ImageView
                                                imageQues.setImageBitmap(bitmap);
                                            }
                                        },
                                        0, 0,
                                        ImageView.ScaleType.FIT_CENTER,
                                        Bitmap.Config.ARGB_8888,
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // Handle error
                                            }
                                        });

                                // Add the request to the RequestQueue.
                                queue2.add(imageRequest1);
                            }
                        }
                    });
                }
                radioGroup.clearCheck();
            }
        });
    }

    public void CheckEnd(int position)
    {
        if (position == questAdapter.answer1.length)
        {
            afterBtn.setText("Nộp bài");
            Submit = TRUE;
        }
        else
        {
            Submit = FALSE;
        }
    }

    public void CheckAnswer(int position)
    {
        if (questAdapter.answerDone.containsKey(position))
        {
            if (questAdapter.answerDone.get(position).equals("A"))
            {
                questAdapter.selectedId = 2131362159;
                radioGroup.check(questAdapter.selectedId);
            }
            if (questAdapter.answerDone.get(position).equals("B"))
            {
                questAdapter.selectedId = 2131362160;
                radioGroup.check(questAdapter.selectedId);
            }
            if (questAdapter.answerDone.get(position).equals("C"))
            {
                questAdapter.selectedId = 2131362161;
                radioGroup.check(questAdapter.selectedId);
            }
            if (questAdapter.answerDone.get(position).equals("D"))
            {
                questAdapter.selectedId = 2131362162;
                radioGroup.check(questAdapter.selectedId);
            }
        }
    }

    public void LoadImage(int position)
    {
        radioGroup.clearCheck();

        if (questAdapter.currentQues == questAdapter.answer1.length)
        {
            afterBtn.setText("Nộp bài");
            Submit = TRUE;
        }

        if (questAdapter.answerDone.get(questAdapter.currentQues) != null)
        {
            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("A"))
            {
                questAdapter.selectedId = 2131362159;
                radioGroup.check(questAdapter.selectedId);
            }
            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("B"))
            {
                questAdapter.selectedId = 2131362160;
                radioGroup.check(questAdapter.selectedId);
            }
            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("C"))
            {
                questAdapter.selectedId = 2131362161;
                radioGroup.check(questAdapter.selectedId);
            }
            if (questAdapter.answerDone.get(questAdapter.currentQues).equals("D"))
            {
                questAdapter.selectedId = 2131362162;
                radioGroup.check(questAdapter.selectedId);
            }
        }
        RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
        String imageUrl = newURL + "CH%20(" + position + ").png";

        ImageRequest imageRequest = new ImageRequest(
                imageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        // Display the image in the ImageView
                        imageQues.setImageBitmap(bitmap);
                    }
                },
                0, 0,
                ImageView.ScaleType.FIT_CENTER,
                Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });

        // Add the request to the RequestQueue.
        queue1.add(imageRequest);
    }

    private List<Question> getListQuestion(int a)
    {
        List<Question> list = new ArrayList<>();
        for(int i = 1; i <= a; i++)
        {
            list.add(new Question(String.valueOf(i), "#ffffff"));
        }
        return list;
    }

    private void runProgram()
    {
        deleteCache(Quiz_Questions.this);
        getCacheDir().delete();

//        System.out.println("Tổng số câu: " + questAdapter.currentQues + " --- " + questAdapter.answer1.length);

        if (questAdapter.currentQues <= questAdapter.answer1.length)
        {
            questAdapter.answerCorrect = 0;
            for (int j = 1; j <= questAdapter.answer1.length; j++)
            {
                String k = questAdapter.answerDone.get(j);
                if (k == null)
                {
                    k = "$$";
                }

                if (k.equals(questAdapter.answer1[j - 1]))
                {
                    questAdapter.answerCorrect++;
                    System.out.println("Câu " + j + " : " + k + " --- Đáp án đúng: " + questAdapter.answer1[j - 1]);
                }

                System.out.println("Câu " + j + " : " + k + " --- Đáp án: " + questAdapter.answer1[j - 1]);
            }
        }
        Intent intent_2 = new Intent(Quiz_Questions.this, AnswerShow.class);
        intent_2.putExtra("Score", temp + questAdapter.answerCorrect);
        intent_2.putExtra("Link", newURL);
        intent_2.putExtra("Total", answer.length);
        intent_2.putExtra("OriginLink", temp);
        startActivity(intent_2);
        finish();
    }

    public static void deleteCache(Context context)
    {
        try
        {
            File dir = context.getCacheDir();
            deleteDir(dir);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static boolean deleteDir(File dir)
    {
        if (dir != null && dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                boolean success = deleteDir(new File(dir, children[i]));

                if (!success)
                {
                    return false;
                }
            }
            return dir.delete();
        }
        else if (dir != null && dir.isFile())
        {
            return dir.delete();
        }
        else
        {
            return false;
        }
    }
}