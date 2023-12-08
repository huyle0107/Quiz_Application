package bku.iot.quizz_application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AnswerShow extends AppCompatActivity
{
    TextView answer_title;
    int TotalQuestion, count;
    String temp, newURL, OriginLink, code, name, className;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();   //This line hides the action bar

        setContentView(R.layout.activity_answer_show);

        bundle = getIntent().getExtras();
        if (bundle != null) {
            temp = (String) bundle.get("Score");
            newURL = (String) bundle.get("Link");
            TotalQuestion = (Integer) bundle.get("Total");
            OriginLink = (String) bundle.get("OriginLink");
        }

        String[] SepPart = OriginLink.split("/");

        // Extract the components
        String code = SepPart[4];
        String name = SepPart[5];
        String className = SepPart[6];

        System.out.println("Code: " + code);
        System.out.println("Name: " + name);
        System.out.println("Class: " + className);

        String[] parts = temp.split("/");
        String lastPart = parts[parts.length - 1];

        answer_title = findViewById(R.id.answer_title);

        answer_title.setText("Số câu đúng: " + lastPart + "/" + TotalQuestion);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://45.119.209.77:5005/sendapan";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("POST success!!!");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Lỗi kết nối");
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("code", code);
                paramV.put("name", name);
                paramV.put("lophoc", className);
                paramV.put("socau", String.valueOf(TotalQuestion));
                return paramV;
            }
        };
        queue.add(stringRequest);

        // Find the parent ViewGroup where you want to add ImageView elements
        LinearLayout imageLayout = findViewById(R.id.image_layout);

        // Define the delay in milliseconds
        long delayMillis = 2000; // 2 seconds

        // Start the recursive method with count = 0
        addImageViewWithDelay(imageLayout, 0, delayMillis);

    }

    // Recursive method to add ImageView elements with a delay
    private void addImageViewWithDelay(LinearLayout imageLayout, int count, long delayMillis)
    {
        if (count <= TotalQuestion)
        {
            Handler handler = new Handler();
            // Create a new ImageView
            ImageView imageView = new ImageView(AnswerShow.this);

            LinearLayout itemLayout = new LinearLayout(AnswerShow.this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);

//            TextView textView = new TextView(AnswerShow.this);
//            textView.setText("Đáp án câu " + Integer.valueOf(count + 1)); // Replace "Text" with your desired text
//            // Set text color to black
//            textView.setTextColor(Color.BLACK);
//            textView.setTypeface(null, Typeface.BOLD);
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Adjust the size as needed
//            textView.setPadding(20, 0, 0, 0); // Adjust left padding as needed

            // Set the image resource for the ImageView (replace with your image resource)
            RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
            String imageUrl = newURL + "LG%20(" + count + ").png";

            // Do some work after the sleep
            // For example, update UI elements using runOnUiThread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageRequest imageRequest = new ImageRequest(
                            imageUrl,
                            new Response.Listener<Bitmap>() {
                                @Override
                                public void onResponse(Bitmap bitmap) {
                                    // Display the image in the ImageView
                                    imageView.setImageBitmap(bitmap);
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

                    imageView.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));

                    // Add the ImageView to the parent layout
//                    itemLayout.addView(textView);
                    imageLayout.addView(imageView);
                    imageLayout.addView(itemLayout);

                    // Call the method recursively with the next count after a delay
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addImageViewWithDelay(imageLayout, count + 1, delayMillis);
                        }
                    }, delayMillis);
                }
            });
        }
    }
}