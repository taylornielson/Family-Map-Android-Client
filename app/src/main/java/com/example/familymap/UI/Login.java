package com.example.familymap.UI;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.familymap.Client.Cache;
import com.example.familymap.Client.Proxy;
import com.example.familymap.MainActivity;
import com.example.familymap.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;



public class Login extends Fragment {
    private static final String LOG_TAG = "LoginFragment";

    public static final String ARG_TITLE = "title";

    private String title;

    private EditText password;
    private EditText username;
    private EditText host;
    private EditText port;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private Boolean hostFilled = false;
    private Boolean passwordFilled = false;
    private Boolean userFilled = false;
    private Boolean firstNameFilled = false;
    private Boolean lastNameFilled = false;
    private Boolean emailFilled = false;
    private Boolean portFilled = false;
    private Button signIn;
    private Button register;
    private String passText;
    private String userText;
    private String hostText;
    private String portText;
    private String firstNameText;
    private String lastNameText;
    private String emailText;
    private String genderText;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Boolean doingAsyncTask = false;

    public static final String tag = "familyLoginFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "in onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        password = (EditText) v.findViewById(R.id.passwordField);
        username = (EditText) v.findViewById(R.id.userNameField);
        host = (EditText) v.findViewById(R.id.hostField);
        port = (EditText) v.findViewById(R.id.serverPort);
        firstName = (EditText) v.findViewById(R.id.firstNameField);
        lastName = (EditText) v.findViewById(R.id.lastNameField);
        email = (EditText) v.findViewById(R.id.emailField);
        signIn = (Button) v.findViewById(R.id.signInButton);
        radioGroup = (RadioGroup) v.findViewById(R.id.radio);
        register = (Button) v.findViewById(R.id.registerButton);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(tag, Login.this.toString());
                Login.this.doSignIn();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(tag, Login.this.toString());
                Login.this.doRegister();
            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    userFilled = true;

                } else {
                    userFilled = false;
                }
                checkRegisteredButton();
            }
        });
        host.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    hostFilled = true;

                } else {
                    hostFilled = false;
                }
                checkRegisteredButton();
            }
        });
        port.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    portFilled = true;

                } else {
                    portFilled = false;
                }
                checkRegisteredButton();
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    passwordFilled = true;

                } else {
                    passwordFilled = false;
                }
                checkRegisteredButton();
            }
        });
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    firstNameFilled = true;

                } else {
                    firstNameFilled = false;
                }
                checkRegisteredButton();
            }
        });
        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    lastNameFilled = true;

                } else {
                    lastNameFilled = false;
                }
                checkRegisteredButton();
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    emailFilled = true;

                } else {
                    emailFilled = false;
                }
                checkRegisteredButton();
            }
        });

        return v;
    }

    public void doSignIn() {

        if (doingAsyncTask) return;
        Log.e(tag, "doing login");
        userText = username.getText().toString();
        passText = password.getText().toString();
        portText = port.getText().toString();
        hostText = host.getText().toString();
        doingAsyncTask = true;
        LoginTask loginTask = new LoginTask(userText, passText, portText, hostText);
        loginTask.execute();
    }

    public void checkRegisteredButton() {
        if (userFilled && passwordFilled && emailFilled && firstNameFilled && lastNameFilled && portFilled && hostFilled) {
            Button btn = getView().findViewById(R.id.registerButton);
            btn.setEnabled(true);
            Button btn1 = getView().findViewById(R.id.signInButton);
            btn1.setEnabled(true);
        } else if (portFilled && hostFilled && userFilled && passwordFilled) {
            Button btn = getView().findViewById(R.id.signInButton);
            btn.setEnabled(true);
            if (!emailFilled || !firstNameFilled || !lastNameFilled) {
                Button btn1 = getView().findViewById(R.id.registerButton);
                btn1.setEnabled(false);
            }
        } else {
            Button btn1 = getView().findViewById(R.id.signInButton);
            btn1.setEnabled(false);
            Button btn = getView().findViewById(R.id.registerButton);
            btn.setEnabled(false);
        }
    }

    public void doRegister() {
        if (doingAsyncTask) return;
        userText = username.getText().toString();
        passText = password.getText().toString();
        portText = port.getText().toString();
        hostText = host.getText().toString();
        firstNameText = firstName.getText().toString();
        lastNameText = lastName.getText().toString();
        emailText = email.getText().toString();
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) getView().findViewById(selectedId);
        genderText = radioButton.getText().toString();
        if (genderText.equals("Male")) {
            genderText = "m";
        } else if (genderText.equals("Female")) {
            genderText = "f";
        }
        doingAsyncTask = true;
        Log.e(tag,"made it to register task");
        RegisterTask registerTask = new RegisterTask(userText, passText, portText, hostText, firstNameText, lastNameText, emailText, genderText);
        registerTask.execute();
    }


    public void loginFailed() {
        doingAsyncTask = false;
        Log.e(tag, "failing login");
        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Login Failed", Toast.LENGTH_LONG);
        toast.show();
    }

    public void loginSuccessfull() {
        Log.e(tag, "successful login");
        UserDataLoadTask userDataLoadTask = new UserDataLoadTask();
        userDataLoadTask.execute();
    }

    public class LoginTask extends AsyncTask<URL, Integer, JSONObject> {
        private String userText;
        private String passText;
        private String portText;
        private String hostText;
        private String tag = "Login";

        public LoginTask(String userText, String passText, String portText, String hostText) {
            this.userText = userText;
            this.passText = passText;
            this.portText = portText;
            this.hostText = hostText;
        }

        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject body = new JSONObject();
            try {
                body.put("userName", userText);
                body.put("password", passText);
            } catch (JSONException e) {
                Log.e(tag, "Json Error:" + e.getMessage());
            }
            Proxy.setIP(hostText);
            Proxy.setPort(portText);
            JSONObject returnObject = Proxy.doPostAction("user/login", body);
            if (returnObject != null)
                Log.e(tag, "did post action, result: " + returnObject.toString());
            else {
                Log.e(tag, " res is null");
                return new JSONObject();
            }
            return returnObject;
        }

        public void onPostExecute(JSONObject result) {
            if (result.has("message")) {
                loginFailed();
            } else {
                try {
                    Cache cache = Cache.getInstance();
                    cache.userName = result.getString("personID");
                    cache.authToken = result.getString("authToken");
                    cache.personID = result.getString("personID");
                } catch (JSONException e) {
                    Log.e(tag, "json exception " + e.getMessage() + " str " + e.toString());
                    loginFailed();
                    return;
                } catch (Exception e) {
                    Log.e(tag, "auth exception " + e.getMessage() + " str " + e.toString());
                    loginFailed();
                    return;
                }
                loginSuccessfull();
            }
        }
    }


    public class RegisterTask extends AsyncTask<URL, Integer, JSONObject> {
        private String userText;
        private String passText;
        private String portText;
        private String hostText;
        private String firstNameText;
        private String lastNameText;
        private String emailText;
        private String genderText;
        private String tag = "Register";

        public RegisterTask(String userText, String passText, String portText, String hostText, String firstNameText, String lastNameText, String emailText, String genderText) {
            this.userText = userText;
            this.passText = passText;
            this.portText = portText;
            this.hostText = hostText;
            this.firstNameText = firstNameText;
            this.lastNameText = lastNameText;
            this.emailText = emailText;
            this.genderText = genderText;
            this.tag = tag;
        }


        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject body = new JSONObject();
            try {
                body.put("userName", userText);
                body.put("password", passText);
                body.put("email", emailText);
                body.put("firstName", firstNameText);
                body.put("lastName", lastNameText);
                body.put("gender", genderText);
            } catch (JSONException e) {
                Log.e(tag, "Json Error:" + e.getMessage());
            }
            Proxy.setIP(hostText);
            Proxy.setPort(portText);
            Log.e(tag,"Made it to before postAction");
            JSONObject returnObject = Proxy.doPostAction("user/register", body);
            if (returnObject != null)
                Log.e(tag, "did post action, res: " + returnObject.toString());
            else {
                Log.e(tag, " res is null");
                return new JSONObject();
            }
            return returnObject;
        }

        public void onPostExecute(JSONObject result) {
            if (result.has("message")) {
                loginFailed();
            } else {
                try {
                    Log.e(tag, "before starting cache");
                    Cache cache = Cache.getInstance();
                    Log.e(tag, "after starting cache");
                    cache.userName = result.getString("personID");
                    cache.authToken = result.getString("authToken");
                    cache.personID = result.getString("personID");
                    Log.e(tag, "After trying to assign values");
                } catch (JSONException e) {
                    Log.e(tag, "json exception " + e.getMessage() + " str " + e.toString());
                    loginFailed();
                    return;
                } catch (Exception e) {
                    Log.e(tag, "auth exception " + e.getMessage() + " str " + e.toString());
                    loginFailed();
                    return;
                }
                loginSuccessfull();
            }
        }
    }

    public class UserDataLoadTask extends AsyncTask<URL, Integer, Boolean>
    {
        public static final String tag = "familyudatatask";

        @Override
        public Boolean doInBackground(URL... urls)
        {
            return Cache.loadCache();
        }

        public void onProgressUpdate(Integer... progress)
        {

        }

        public void onPostExecute(Boolean success)
        {
            doingAsyncTask = false;
            if (!success) {
                Log.e(tag,"userDatafail");
                loginFailed();
            }
            else {
                Cache cache = Cache.getInstance();
                String firstName, lastName;
                firstName = cache.getFirstName();
                lastName = cache.getLastName();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), firstName + " " + lastName, Toast.LENGTH_LONG);
                toast.show();
                showMap();
            }
        }
    }



       // The following callbacks are not needed for the example. They are included to show the
    // order of fragment callbacks in the log.
    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.i(LOG_TAG, "in onAttachFragment(Fragment)");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(LOG_TAG, "in onActivityCreated(Bundle)");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "in onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "in onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "in onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "in onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(LOG_TAG, "in onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "in onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(LOG_TAG, "in onDetach()");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(LOG_TAG, "in onSaveInstanceState(Bundle)");
    }
    public void showMap()
    {
        ((MainActivity)getActivity()).showMap();
    }


}
