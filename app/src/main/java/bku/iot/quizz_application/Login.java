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

public class Login extends AppCompatActivity {

    TextInputEditText textInputEditTextPIN;
    Button buttonLogin;
    String PIN;
    TextView textviewError;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();   //This line hides the action bar

        setContentView(R.layout.activity_login);

        textInputEditTextPIN = findViewById(R.id.Id_Class);
        buttonLogin = findViewById(R.id.EnterBtn);
        textviewError = findViewById(R.id.errorLogin);
        progressBar = findViewById(R.id.loadingLogin);

        // kill keyboard when enter is pressed
        textInputEditTextPIN.setInputType(InputType.TYPE_TEXT_VARIATION_URI ); // optional - sets the keyboard to URL mode
        textInputEditTextPIN.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            /**
             * This listens for the user to press the enter button on
             * the keyboard and then hides the virtual keyboard
             */
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Clear focus from textInputEditText1
                    textInputEditTextPIN.clearFocus();
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textInputEditTextPIN.getWindowToken(), 0);
                    return true;
                }

                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (actionId == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textInputEditTextPIN.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textviewError.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                PIN = String.valueOf(textInputEditTextPIN.getText());
                if (PIN.isEmpty())
                {
                    // Show an error message or handle the case where either name or cla_ss is empty
                    textviewError.setText("PIN code cannot be empty");
                    textviewError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    runProgram();
                }
            }
        });

    }

    private void runProgram()
    {
        deleteCache(Login.this);
        getCacheDir().delete();
        Intent intent_2 = new Intent(Login.this, Login_name_class.class);
        intent_2.putExtra("PIN_code", "http://45.119.209.77:5005/view/" + PIN + "/");
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