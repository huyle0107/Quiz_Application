package bku.iot.quizz_application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Introduction extends AppCompatActivity {

    Button btnSelfStudy, btnLearning, btnQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();   //This line hides the action bar

        setContentView(R.layout.activity_introduction);

        btnSelfStudy = findViewById(R.id.SelStudyBtn);
        btnLearning = findViewById(R.id.LearningBtn);
        btnQuiz = findViewById(R.id.QUizBinBtn);

        btnSelfStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                deleteCache(Introduction.this);
                getCacheDir().delete();
//                Intent intent_2 = new Intent(Introduction.this, Login.class);
//                startActivity(intent_2);
//                finish();
            }
        });

        btnLearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                deleteCache(Introduction.this);
                getCacheDir().delete();
//                Intent intent_2 = new Intent(Introduction.this, Login_name_class.class);
//                startActivity(intent_2);
//                finish();
            }
        });

        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                deleteCache(Introduction.this);
                getCacheDir().delete();
                Intent intent_2 = new Intent(Introduction.this,  Login.class);
                startActivity(intent_2);
                finish();
            }
        });
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
            for (String aChildren : children)
            {
                boolean success = deleteDir(new File(dir, aChildren));

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