package com.example.fivecontacts.main.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.fivecontacts.R;
import com.example.fivecontacts.main.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PerfilUsuario_Activity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    EditText edUser;
    EditText edPass;
    EditText edNome;
    EditText edEmail;
    Switch swLogado;

    Button btModificar;
    BottomNavigationView bnv;

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        btModificar=findViewById(R.id.btCriar);
        bnv=findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(this);
        bnv.setSelectedItemId(R.id.anvPerfil);

        edUser=findViewById(R.id.edT_Login2);
        edPass=findViewById(R.id.edt_Pass2);
        edNome=findViewById(R.id.edtNome);
        edEmail=findViewById(R.id.edEmail);
        swLogado=findViewById(R.id.swLogado);

        Intent quemChamou = this.getIntent();
        if (quemChamou != null) {
            Bundle params = quemChamou.getExtras();
            if (params != null) {
                //Recuperando o Usuario
                user = (User) params.getSerializable("usuario");
                setTitle("Alterar dados de "+user.getNome());

            }
        }
        if (user != null) {
                   edUser.setText(user.getLogin());
                    edPass.setText(user.getSenha());
                    edNome.setText(user.getNome());
                    edEmail.setText(user.getEmail());
                    swLogado.setChecked(user.isManterLogado());
        }

        btModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setNome(edNome.getText().toString());
                user.setLogin(edUser.getText().toString());
                user.setSenha(edPass.getText().toString());
                user.setEmail(edEmail.getText().toString());
                user.setManterLogado(swLogado.isChecked());
                salvarModificacoes(user);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Checagem de o Item selecionado é a de mudanças de contatos
        if (item.getItemId() == R.id.anvMudar) {
            //Abertura da Tela de Perfil
            Intent intent = new Intent(this, AlterarContatos_Activity.class);
            intent.putExtra("usuario", user);
            startActivity(intent);

        }
        // Checagem de o Item selecionado é Ligar
        if (item.getItemId() == R.id.anvLigar) {
            //Abertura da Tela Mudar COntatos
            Intent intent = new Intent(this, ListaDeContatos_Activity.class);
            intent.putExtra("usuario", user);
            startActivity(intent);

        }
        return true;
    }

    public void salvarModificacoes(User user){
        SharedPreferences salvaUser= getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        SharedPreferences.Editor escritor= salvaUser.edit();

        escritor.putString("nome",user.getNome());
        escritor.putString("senha",user.getSenha());
        escritor.putString("login",user.getLogin());

        //Escrever no SharedPreferences
        escritor.putString("email",user.getEmail());
        escritor.putBoolean("manterLogado",user.isManterLogado());


        //Falta Salvar o E-mail

        escritor.commit(); //Salva em Disco

        Toast.makeText(PerfilUsuario_Activity.this,"Modificações Salvas",Toast.LENGTH_LONG).show() ;

        finish();
    }
}