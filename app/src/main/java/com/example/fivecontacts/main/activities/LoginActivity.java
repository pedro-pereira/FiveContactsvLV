package com.example.fivecontacts.main.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fivecontacts.R;
import com.example.fivecontacts.main.model.Contato;
import com.example.fivecontacts.main.model.User;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private boolean primeiraVezUser = true;
    private boolean primeiraVezSenha = true;
    private EditText edUser;
    private EditText edPass;
    private Button btLogar;
    private Button btNovo;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Existe um usuário padrão logado?
        if(montarObjetoUserSemLogar()){
            User user = montarObjetoUser();

            preencherListaDeContatos(user);

            //Abrir a atividade de Lista de Contatos
            Intent intent = new Intent(LoginActivity.this, ListaContatosEmergenciaActivity.class);
            intent.putExtra("usuario", user);
            startActivity(intent);
            finish();
        } else { //Checar Usuário e Senha ou clicar em criar novo
            btLogar = findViewById(R.id.btnLogin);
            btNovo  = findViewById(R.id.btnNovoUsuario);
            edUser  = findViewById(R.id.edtLogin);
            edPass  = findViewById(R.id.edtSenha);

            //Colocando Underline (Vamos usar esse campo mais na frente com o FireBase)
            mTextView = findViewById(R.id.txvEsqueceuSenha);
            mTextView.setPaintFlags(mTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            //Evento de limpar Componente
            edUser.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (primeiraVezUser) {
                        edUser.setText("");
                        primeiraVezUser = false;
                    }
                    return false;
                }
            });

            //Evento de limpar Componente
            edPass.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (primeiraVezSenha) {
                        primeiraVezSenha = false;
                        edPass.setText("");
                        edPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                        );
                    }
                    return false;
                }
            });

            btLogar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Ao clicar deve-se:
                    //1- Checar se existe um SharedPreferences
                    //2- Comparar login e senha salvos
                    //3- Se tudo der certo, resgatar lista de contatos
                    //4- Abrir a Atividade lista de Contatos passando como parametro o objeto User e seus 5 Contatos

                    SharedPreferences temUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
                    String loginSalvo = temUser.getString("login", "");
                    String senhaSalva = temUser.getString("senha", "");

                    if ((loginSalvo != null) && (senhaSalva != null)) {
                        //Recuperando da tela
                        String senha = edPass.getText().toString();
                        String login = edUser.getText().toString();

                        //Comparando
                        if ((loginSalvo.compareTo(login) == 0) && (senhaSalva.compareTo(senha) == 0)) {
                            User user = montarObjetoUser();
                            preencherListaDeContatos(user);
                            //Abrindo a Lista de Contatos
                            Intent intent = new Intent(LoginActivity.this, ListaContatosEmergenciaActivity.class);
                            intent.putExtra("usuario", user);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Login e Senha Incorretos", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Login e Senha nulos", Toast.LENGTH_LONG).show();
                    }
                }
            });

            //Novo Usuário
            btNovo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, NovoUsuarioActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private User montarObjetoUser() {
        User user = null;
        SharedPreferences temUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        String loginSalvo         = temUser.getString("login","");
        String senhaSalva         = temUser.getString("senha","");
        String nomeSalvo          = temUser.getString("nome","");
        String emailSalvo         = temUser.getString("email","");
        boolean manterLogado      = temUser.getBoolean("manterLogado",false);
        boolean temaEscuro        = temUser.getBoolean("tema",false);

        user = new User(nomeSalvo, loginSalvo, senhaSalva, emailSalvo, manterLogado);
        user.setTema_escuro(temaEscuro);
        return user;
    }

    private boolean montarObjetoUserSemLogar() {
        SharedPreferences temUser = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        boolean manterLogado = temUser.getBoolean("manterLogado",false);
        return manterLogado;
    }

    protected void preencherListaDeContatos(User user) {
        SharedPreferences recuperarContatos = getSharedPreferences("contatos", Activity.MODE_PRIVATE);

        int num = recuperarContatos.getInt("numContatos", 0);
        ArrayList<Contato> contatos = new ArrayList<Contato>();
        Contato contato;

        for (int i = 1; i <= num; i++) {
            String objSel = recuperarContatos.getString("contato" + i, "");
            if (objSel.compareTo("") != 0) {
                try {
                    ByteArrayInputStream bis = new ByteArrayInputStream(objSel.getBytes(StandardCharsets.ISO_8859_1.name()));
                    ObjectInputStream oos = new ObjectInputStream(bis);
                    contato = (Contato) oos.readObject();

                    if (contato != null) {
                        contatos.add(contato);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        user.setContatos(contatos);
    }
}