package com.fmm.checkapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    Button btLogin;
    TextView tvIrTelaCadastro;
    TextView tvEsqueceuSenha;
    EditText edtEmail;
    EditText edtSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btLogin = findViewById(R.id.activity_login_bt_login);
        tvEsqueceuSenha = findViewById(R.id.activity_login_tv_esqueceu_senha);
        tvIrTelaCadastro = findViewById(R.id.activity_login_tv_cadastrar);
        edtEmail = findViewById(R.id.activity_login_edt_email);
        edtSenha = findViewById(R.id.activity_login_edt_senha);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvIrTelaCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvEsqueceuSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }
}
