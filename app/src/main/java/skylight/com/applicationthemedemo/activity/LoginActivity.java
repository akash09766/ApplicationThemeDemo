package skylight.com.applicationthemedemo.activity;

/**
 * Created by Akash on 24/01/16.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

import skylight.com.applicationthemedemo.R;
import skylight.com.applicationthemedemo.fragment.ChooseLangFragment;
import skylight.com.applicationthemedemo.fragment.ChooseThemeFragment;
import skylight.com.applicationthemedemo.model.User;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements ChooseThemeFragment.ChooseListener, ChooseLangFragment.ChooseLangListener {

    private String TAG = LoginActivity.class.getSimpleName();

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String DUMMY_USERANEM = "skylight@gmail.com";
    private static final String DUMMY_PASSWORD = "123456";
    private static final String[] mThemeNames = {"Theme 1", "Theme 2", "Theme 3"};
    private static final String[] mLangNames = {"English", "Hindi"};
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private User user;
    private SharedPreferences pref;
    public static final String MyPREFERENCES = "MyPrefs";
    private int mThemeChoise = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setThemeForApplication();

        super.onCreate(savedInstanceState);

        setLangForApplication();
        setTitle(getString(R.string.app_name));
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        // populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        user = new User(DUMMY_USERANEM, DUMMY_PASSWORD);

        removePrevAttachedDailogs();
    }

    private void removePrevAttachedDailogs() {

        DialogFragment dialogThemeFragment = (DialogFragment) getFragmentManager().findFragmentByTag(ChooseThemeFragment.class.getSimpleName());
        if (dialogThemeFragment != null) {

            dialogThemeFragment.dismiss();
            dialogThemeFragment.dismissAllowingStateLoss();
        }
        DialogFragment dialogLangFragment = (DialogFragment) getFragmentManager().findFragmentByTag(ChooseLangFragment.class.getSimpleName());
        if (dialogLangFragment != null) {

            dialogLangFragment.dismiss();
            dialogLangFragment.dismissAllowingStateLoss();
        }
    }

    private void changeLangauge(String lang_code) {

//        String languageToLoad  = "fa"; // your language
        Locale locale = new Locale(lang_code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
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

    private void setLangForApplication() {

        pref = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        int langChoise = pref.getInt("LangCode", 0);
        switch (langChoise) {
            case 0:
                changeLangauge("en");
                break;
            case 1:
                changeLangauge("hi");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_info:

//                showInfo();
                showLangOption();
                return true;

            case R.id.action_theme:

                int code = pref.getInt("ThemeCode", 0);
                showThemeOption(mThemeNames, code);

                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    private void showLangOption() {

        int langcode = pref.getInt("LangCode", 0);

        FragmentManager manager = getFragmentManager();

        ChooseLangFragment alert = new ChooseLangFragment();

        Bundle b = new Bundle();

        b.putInt("position", langcode);
        b.putStringArray("code", mLangNames);

        alert.setArguments(b);

        alert.show(manager, ChooseLangFragment.class.getSimpleName());
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel_email = false;
        boolean cancel_password = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel_password = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel_email = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel_email = true;
        }

        if (cancel_email || cancel_password) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.equalsIgnoreCase(user.getmUserName());
    }

    private boolean isPasswordValid(String password) {

        if (password.length() > 4) {
            return password.equalsIgnoreCase(user.getmPassword());
        } else {
            return false;
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }


            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                moveToMapActivity();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void moveToMapActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        navigateToActivity(intent);
    }

    private void showThemeOption(String[] data, int currentSeleted) {

        /** Getting the fragment manager */
        FragmentManager manager = getFragmentManager();

        /** Instantiating the DialogFragment class */
        ChooseThemeFragment alert = new ChooseThemeFragment();

        /** Creating a bundle object to store the selected item's index */
        Bundle b = new Bundle();

        /** Storing the selected item's index in the bundle object */
        b.putInt("position", currentSeleted);
        b.putStringArray("code", data);

        /** Setting the bundle object to the dialog fragment object */
        alert.setArguments(b);

        /** Creating the dialog fragment object, which will in turn open the alert dialog window */
        alert.show(manager, ChooseThemeFragment.class.getSimpleName());
    }

    @Override
    public void onPositiveClick(int position, String code) {  //click handler for Choosing one of the bizcode
        showProgress(true);
        Log.d(TAG, "  onPositiveClick() :: position = " + position);
//        mBizCodeView.setText(code);
        int currentSelected = pref.getInt("ThemeCode", 0);
        if (currentSelected == position) {
            showProgress(false);
            return;
        }
        SharedPreferences.Editor editor = pref.edit();

        switch (position) {
            case 0:
                editor.putInt("ThemeCode", position);
                editor.commit();
                recreate();
                break;
            case 1:
                editor.putInt("ThemeCode", position);
                editor.commit();
                recreate();
                break;
            case 2:
                editor.putInt("ThemeCode", position);
                editor.commit();
                recreate();
                break;
        }

        showProgress(false);
    }

    @Override
    public void onSetClick(int position, String code) {
        showProgress(true);
        Log.d(TAG, "  onSetClick() :: position = " + position);
        int currentSelected = pref.getInt("LangCode", 0);
        if (currentSelected == position) {
            showProgress(false);
            return;
        }
        SharedPreferences.Editor editor = pref.edit();
        switch (position) {
            case 0:
                editor.putInt("LangCode", position);
                editor.commit();
                recreate();
                break;
            case 1:
                editor.putInt("LangCode", position);
                editor.commit();
                recreate();
                break;
        }
    }

    @Override
    public void onCancelClick() {
        showProgress(false);
    }


    @Override
    public void onNegativeClick() {
        showProgress(false);
    }

    public void navigateToActivity(Intent intent){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        } else {
            startActivity(intent);
        }
    }

}

