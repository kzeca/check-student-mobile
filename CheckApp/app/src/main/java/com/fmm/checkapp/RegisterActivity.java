package com.fmm.checkapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spnTurma;
    LoginActivity login;
    FirebaseAuth firebaseAuth;
    Button btRegister;
    EditText edtEmail, edtNome, edtSenha, edtConfirmar;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setObjects(); //Set all objects from the RegisterActivity Class
        spnTurma = findViewById(R.id.activity_register_spn_turma);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.salas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTurma.setAdapter(adapter);
        spnTurma.setOnItemSelectedListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtEmail.getText().toString().isEmpty()
                        || edtSenha.getText().toString().isEmpty()
                        || edtNome.getText().toString().isEmpty()
                        || edtConfirmar.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Preencha todos os campos!",
                            Toast.LENGTH_SHORT).show();
                    }else{
                        if(!edtSenha.getText().toString().equals(edtConfirmar.getText().toString())){
                            Toast.makeText(getApplicationContext(), "Os campos de senha não coincidem!",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                                progressBar.setVisibility(View.VISIBLE);
                                registerUser();
                            }
                        }
                    }

            });
    }

    private void setObjects() {
        progressBar = findViewById(R.id.register_progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        btRegister = findViewById(R.id.activity_register_bt_cadastrar);
        edtEmail = findViewById(R.id.activity_edt_register_email);
        edtNome = findViewById(R.id.activity_register_edt_nome);
        edtSenha = findViewById(R.id.activity_edt_register_senha);
        edtConfirmar = findViewById(R.id.activity_register_edt_senha_confirmada);
        login = new LoginActivity();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String turma = parent.getItemAtPosition(position).toString();
        login.user.setTurma(turma);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    void registerUser(){
        final String nome = edtNome.getText().toString();
        final String email = edtEmail.getText().toString();
        final String senha = edtSenha.getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            login.user.setSenha(senha);
                            login.user.setEmail(email);
                            login.user.setNome(nome);

                            DatabaseReference database = FirebaseDatabase.getInstance().
                                    getReference("Salas")
                                    .child(login.user.getTurma())
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            database.child("name").setValue(login.user.getNome());
                            Toast.makeText(getApplicationContext(), "Seus dados foram salvos!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                            finish();
                        }else {
                            Toast.makeText(getApplicationContext(), "Sua senha não possui 6 dígitos"
                                    ,Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

}
