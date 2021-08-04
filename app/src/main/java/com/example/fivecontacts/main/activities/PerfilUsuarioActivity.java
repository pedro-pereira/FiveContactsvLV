package com.example.fivecontacts.main.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.fivecontacts.R;
import com.example.fivecontacts.main.model.User;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private EditText edtLogin;
    private EditText edtSenha;
    private EditText edtNome;
    private EditText edtEmail;
    private Switch   swtLogado;
    private Switch   swtTema;

    private Button btModificar;

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        btModificar = findViewById(R.id.btCriar);
        edtLogin    = findViewById(R.id.edtNovoLogin);
        edtSenha    = findViewById(R.id.edtNovoSenha);
        edtNome     = findViewById(R.id.edtNovoNome);
        edtEmail    = findViewById(R.id.edtNovoEmail);
        swtLogado   = findViewById(R.id.swtEdicaoLogado);
        swtTema     = findViewById(R.id.swtEdicaoTema);

        edtLogin.setEnabled(false);

        Intent quemChamou = this.getIntent();
        if (quemChamou != null) {
            Bundle params = quemChamou.getExtras();
            if (params != null) {
                //Recuperando o Usuario
                user = (User) params.getSerializable("usuario");
                setTitle("Edição - Perfil " + user.getNome());
            }
        }
        if (user != null) {
            edtLogin.setText(user.getLogin());
            edtSenha.setText(user.getSenha());
            edtNome.setText(user.getNome());
            edtEmail.setText(user.getEmail());
            swtLogado.setChecked(user.isManterLogado());
            swtTema.setChecked(user.isTema_escuro());
        }

        btModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setNome(edtNome.getText().toString());
                user.setLogin(edtLogin.getText().toString());
                user.setSenha(edtSenha.getText().toString());
                user.setEmail(edtEmail.getText().toString());
                user.setManterLogado(swtLogado.isChecked());
                user.setManterLogado(swtTema.isChecked());
                salvarModificacoes(user);
            }
        });
    }

    public void salvarModificacoes(User user){
        SharedPreferences salvaUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        SharedPreferences.Editor escritor = salvaUser.edit();

        escritor.putString("nome", user.getNome());
        escritor.putString("senha", user.getSenha());
        escritor.putString("login", user.getLogin());
        escritor.putString("email", user.getEmail());
        escritor.putBoolean("manterLogado", user.isManterLogado());
        escritor.putBoolean("temaEscuro", user.isTema_escuro());

        escritor.commit(); //Salva em Disco

        Toast.makeText(PerfilUsuarioActivity.this,"Modificações Salvas", Toast.LENGTH_LONG).show() ;
        finish();
    }
}