package com.example.yash.registerlogin;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;



public class Login extends AppCompatActivity implements View.OnClickListener {

    Button button;
    EditText editText,editText2,editText3;
    RadioGroup radiousertype;
    RadioButton radioButton;

    Userlocalstore userlocalstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        radiousertype = (RadioGroup) findViewById(R.id.radiousertype);

        button = (Button) findViewById(R.id.button);




        userlocalstore = new Userlocalstore(this);


        // check if you are connected or not
        if(userlocalstore.getuserloggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
        }
        else {
            if (isConnected()) {
                Toast.makeText(getApplicationContext(), "You are Conncted", Toast.LENGTH_SHORT).show();
                button.setOnClickListener(this);

            } else {
                Toast.makeText(getApplicationContext(), "You are Not Conncted", Toast.LENGTH_SHORT).show();
                button.setOnClickListener(this);
            }
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button:
                if(isConnected()) {
                    if(!validate())
                        Toast.makeText(getApplicationContext(), "Fill All Credentials", Toast.LENGTH_SHORT).show();
                    else {


                        int selectedId = radiousertype.getCheckedRadioButtonId();
                        radioButton = (RadioButton) findViewById(selectedId);

                        String usertype =radioButton.getText().toString();
                        String username = editText.getText().toString();
                        String password = editText2.getText().toString();
                        String date     = editText3.getText().toString();

                        if(usertype == "Teacher")
                           usertype = "E";
                        else
                            usertype = "S";
                        User user = new User(username,password,date,usertype);
                        System.out.println(user.user + user.password + user.usertype + user.date );

                        authenticate(user);

                    }
                }
                else
                    Toast.makeText(getApplicationContext(), "You are Not Conncted", Toast.LENGTH_SHORT).show();
                break;


        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }


    private void authenticate(User user) {

        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.fetchuserdatainbackground(user, new GetUserCallBack() {
            @Override
            public void done(User returneduser) {
                if(returneduser.error == null) {

                    loguserin(returneduser);

                }
                else{
                    showerrormessage(returneduser.error);
                }
                }
            }
        );
    }

    private void showerrormessage(String error){
        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(Login.this);
        dialogbuilder.setMessage(error);
        dialogbuilder.setPositiveButton("OKAY",null);
        dialogbuilder.show();
    }

    private void loguserin(User returneduser) {

        System.out.println(returneduser.user + returneduser.password + returneduser.usertype + returneduser.date + returneduser.success + returneduser.error + returneduser.authkey);
        userlocalstore.userData(returneduser);
        userlocalstore.setUserloggedIn(true);

        startActivity(new Intent(this, MainActivity.class));

    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }


    private boolean validate(){
        if(editText.getText().toString().trim().equals(""))
            return false;
        else if(editText2.getText().toString().trim().equals(""))
            return false;
        else if(editText3.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }

    }
