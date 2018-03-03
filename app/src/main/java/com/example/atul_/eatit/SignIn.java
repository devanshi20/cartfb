package com.example.atul_.eatit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atul_.eatit.Common.Common;
import com.example.atul_.eatit.model.User;
import com.facebook.FacebookSdk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {
    EditText edtPhone,edtPassword;
    Button btnSignIn;
    TextView btnSignUp;
    ProgressDialog progressDialog;



    public final boolean validate() {
        String phone = edtPhone.getText().toString();
        String password = edtPassword.getText().toString();
        boolean valid = true;
        if (phone.isEmpty() ) {
            edtPhone.setError("enter a valid User Id");
            valid = false;
        } else {
            edtPhone.setError(null);
        }

        if (password.isEmpty()) {
            edtPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            edtPassword.setError(null);
        }

        return valid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_in);


        edtPassword = (EditText) findViewById(R.id.edtpassword);
        edtPhone = (EditText) findViewById(R.id.edtphone);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp=(TextView)findViewById(R.id.btnSignUp);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        printKeyHash();



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validate();
                final ProgressDialog mDialog=new ProgressDialog(SignIn.this);
                mDialog.setMessage("Please wait");
                mDialog.show();
                table_user.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                            mDialog.dismiss();
                            User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                           user.setPhone(edtPhone.getText().toString());


                            if (user.getPassword().equals(edtPassword.getText().toString()))
                            {
                                Intent homeIntent=new Intent(SignIn.this,Home.class);
                                Common.currentUser=user;
                                startActivity(homeIntent);
                                finish();
                            }

                        else
                            {
                                Toast.makeText(SignIn.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {

                            Toast.makeText(SignIn.this,"User does not exist.",Toast.LENGTH_SHORT).show();
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void printKeyHash() {
        try{
            PackageInfo info=getPackageManager().getPackageInfo("com.example.atul_.eatit", PackageManager.GET_SIGNATURES);

            for(Signature signature:info.signatures)
            {
                MessageDigest md=MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


}
