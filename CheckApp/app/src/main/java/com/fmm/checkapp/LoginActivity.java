package com.fmm.checkapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button btLogin;
    TextView tvIrTelaCadastro;
    TextView tvEsqueceuSenha;
    EditText edtEmail;
    EditText edtSenha;
    private FirebaseAuth auth;
    FirebaseUser firebaseUser;
    ProgressBar progressBar;
    static Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        user = new Usuario();
        btLogin = findViewById(R.id.activity_login_bt_login);
        tvEsqueceuSenha = findViewById(R.id.activity_login_tv_esqueceu_senha);
        tvIrTelaCadastro = findViewById(R.id.activity_login_tv_cadastrar);
        edtEmail = findViewById(R.id.activity_login_edt_email);
        edtSenha = findViewById(R.id.activity_login_edt_senha);
        progressBar = findViewById(R.id.activity_login_progressBar);
        auth  = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setEmail(edtEmail.getText().toString());
                user.setSenha(edtSenha.getText().toString());
                
                if(user.getEmail().isEmpty() || user.getSenha().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Os campos de email e senha são obrigatórios",
                            Toast.LENGTH_SHORT).show();
                }else {
                    String email = user.getEmail();
                    String senha = user.getSenha();
                    progressBar.setVisibility(View.VISIBLE);
                    login(email, senha);
                }
            }
        });

        tvIrTelaCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        tvEsqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void login(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                            finish();
                        }
                        else{
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Email e/ou senha incorretos",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            user.setEmail(edtEmail.getText().toString());
            user.setSenha(edtSenha.getText().toString());

            switch (actionId) {
                case EditorInfo.IME_ACTION_SEND:
                    if(user.getEmail().isEmpty() || user.getSenha().isEmpty()){
                        Toast.makeText(getApplicationContext(),
                                "Os campos de email e senha são obrigatórios",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }else {
                        String email = user.getEmail();
                        String password = user.getSenha();
                        progressBar.setVisibility(View.VISIBLE);
                        login(email, password);
                        break;
                    }
            }
            return false;
        }
    };
    /*
    @Override
    protected void onStart() {                  FUNÇÃO PARA PEGAR O USUÁRIO JÁ LOGADO!
        if(firebaseUser != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        super.onStart();
    }*/
}
