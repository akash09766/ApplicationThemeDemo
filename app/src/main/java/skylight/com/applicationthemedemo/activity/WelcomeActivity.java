package skylight.com.applicationthemedemo.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import skylight.com.applicationthemedemo.R;

public class WelcomeActivity extends AppCompatActivity {

    private SharedPreferences pref;
    public static final String MyPREFERENCES = "MyPrefs";
    private int mThemeChoise = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setThemeForApplication();
        setTitle(getString(R.string.title_activity_welcome));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

    }

    private void setThemeForApplication() {

        pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mThemeChoise = pref.getInt("ThemeCode", 0);
        switch (mThemeChoise) {
            case 0:
                setTheme(R.style.AppTheme);
                break;
            case 1:
                setTheme(R.style.AppTheme_01);
                break;
            case 2:
                setTheme(R.style.AppTheme_02);
                break;
        }
    }

}
