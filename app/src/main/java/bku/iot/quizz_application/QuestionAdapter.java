package bku.iot.quizz_application;

import static java.lang.Boolean.FALSE;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.questionViewHolder>{

    public TextView quesTitle;
    private List<Question> mListQuestion;
    private Context mContext;
    public String MaxQuestion, answerQues;
    public int currentQues = 0;
    public boolean isCheck = FALSE;
    public boolean isButton =  FALSE;
    public boolean notClick =  FALSE;
    public String[] answer1;
    public int selectedId;
    public int answerCorrect;
    public Map<Integer, String> answerDone = new HashMap<>();


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public QuestionAdapter(Context context, List<Question> mListQuestion, TextView quesTitle)
    {
        this.mListQuestion = mListQuestion;
        this.mContext = context;
        this.quesTitle = quesTitle;
    }

    @NonNull
    @Override
    public questionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_each_questions, parent, false);
        return new questionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull questionViewHolder holder, int position)
    {
        Question quest = mListQuestion.get(position);
        int adapterPosition = holder.getAdapterPosition();
        if (quest == null)
        {
            return;
        }

        holder.QuestionName.setText(quest.getQuestion());


        if (quest.getColor() == "#452CDC")
        {
            holder.QuestionName.setBackground(new ColorDrawable(Color.parseColor("#452CDC")));
        }
        else
        {
            // Set the default background color here, e.g., transparent or the original color
            holder.QuestionName.setBackground(null);
        }

        holder.layitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (isCheck)        // To check client click button or the question
                {
                    if (isButton)  // To check AfterBtn or PreviousBtn is clicked
                    {
                        if (selectedId == -1)
                        {
                            Question quest = mListQuestion.get(adapterPosition);

                            currentQues = adapterPosition + 1;

                            quesTitle.setText("Câu hỏi " + Integer.valueOf(currentQues + 1));

                            isCheck = FALSE;

                        }
                        else
                        {
                            Question quest = mListQuestion.get(adapterPosition);

                            if (currentQues == answer1.length)
                            {
                                quesTitle.setText("Câu hỏi " + Integer.valueOf(50));
                            }
                            else
                            {
                                quesTitle.setText("Câu hỏi " + Integer.valueOf(adapterPosition + 2));

                                if (answerDone.get(currentQues) == null)
                                {
                                    if (answerDone.get(currentQues + 1) == null)
                                    {
                                        String result = answerQues.substring(answerQues.indexOf(" ") + 1);
                                        answerDone.put(currentQues, result);

                                        if (quest.getColor() != "#452CDC") {
                                            ColorDrawable backgroundColor = new ColorDrawable(Color.parseColor("#452CDC"));
                                            holder.QuestionName.setBackground(backgroundColor);
                                            quest.setColor("#452CDC"); // Update the color in the Question object
                                        }
                                    }
                                }
                                else
                                {
                                    if (answerDone.get(currentQues + 1) != null)
                                    {
                                        String result = answerQues.substring(answerQues.indexOf(" ") + 1);
                                        answerDone.put(currentQues, result);
                                    }
                                }
                            }

                            isCheck = FALSE;
                        }
                    }
                    else
                    {
                        if (selectedId == -1)
                        {
                            Question quest = mListQuestion.get(adapterPosition);

                            currentQues = adapterPosition;

                            quesTitle.setText("Câu hỏi " + Integer.valueOf(currentQues));

                            isCheck = FALSE;
                        }
                        else {
                            Question quest = mListQuestion.get(adapterPosition);

                            currentQues = adapterPosition;

                            quesTitle.setText("Câu hỏi " + Integer.valueOf(currentQues));

                            if (answerDone.get(currentQues) == null)
                            {
                                String result = answerQues.substring(answerQues.indexOf(" ") + 1);
                                answerDone.put(currentQues + 1, result);
                            }

                            if (answerDone.get(currentQues) == null)
                            {
                                if (quest.getColor() != "#452CDC")
                                {
                                    ColorDrawable backgroundColor = new ColorDrawable(Color.parseColor("#452CDC"));
                                    holder.QuestionName.setBackground(backgroundColor);
                                    quest.setColor("#452CDC"); // Update the color in the Question object
                                }
                            }

                            isCheck = FALSE;
                        }
                    }
                }
                else
                {
                    Question quest = mListQuestion.get(adapterPosition);

                    currentQues = adapterPosition;

                    if (mContext instanceof Quiz_Questions)
                    {
                        ((Quiz_Questions) mContext).LoadImage(currentQues + 1);
                        ((Quiz_Questions) mContext).CheckAnswer(currentQues + 1);
                        ((Quiz_Questions) mContext).CheckEnd(currentQues + 1);
                    }

                    quesTitle.setText("Câu hỏi " + Integer.valueOf(currentQues + 1));
                }

                System.out.println("Đáp án đã chọn câu " + currentQues + " : "+ answerDone.get(currentQues));

//                // Print the elements of the array
//
//                for (String element : answer1) {
//                    System.out.print(element);
//                }

                for (Map.Entry<Integer, String> entry : answerDone.entrySet()) {
                    Integer key = entry.getKey();
                    String value = entry.getValue();
                    System.out.println(key + ": " + value);
                }

                System.out.println(" -- " + quesTitle.getText() + " -- " + quest.getQuestion());

                notifyItemChanged(adapterPosition);
            }
        });

    }


    @Override
    public int getItemCount()
    {
        if (mListQuestion != null)
        {
            return mListQuestion.size();
        }
        return 0;
    }

    public class questionViewHolder extends RecyclerView.ViewHolder {

        public TextView QuestionName;
        public LinearLayout layitem;

        public questionViewHolder(@NonNull View itemView) {
            super(itemView);

            QuestionName = itemView.findViewById(R.id.text_questions);
            layitem = itemView.findViewById(R.id.layout_item);
        }
    }
}
