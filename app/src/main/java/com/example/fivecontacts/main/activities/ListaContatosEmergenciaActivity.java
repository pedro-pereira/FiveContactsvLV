package com.example.fivecontacts.main.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.fivecontacts.R;
import com.example.fivecontacts.main.model.Contato;
import com.example.fivecontacts.main.model.User;
import com.example.fivecontacts.main.utils.UIEducacionalPermissao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ListaContatosEmergenciaActivity extends AppCompatActivity implements UIEducacionalPermissao.NoticeDialogListener {

    private ListView listaContatos;
    private FloatingActionButton btnAdicionaContato;
    private User usuario;

    private String nomeContatoSelecionado;
    private String numeroContatoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contatos_emergencia);

        listaContatos = findViewById(R.id.lista_contatos_emergencia);
        listaContatos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                nomeContatoSelecionado = (String) parent.getItemAtPosition(position);
                return false;
            }
        });
        registerForContextMenu(listaContatos);

        // Botão adiciona - ini
        btnAdicionaContato = (FloatingActionButton) findViewById(R.id.btnAdicionaContato);
        btnAdicionaContato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencao = new Intent(ListaContatosEmergenciaActivity.this, ListaContatosCelularActivity.class);
                intencao.putExtra("usuario", usuario);
                startActivity(intencao);
            }
        });
        // Botão adiciona - fim

        //Dados da Intent Anterior
        Intent quemChamou = this.getIntent();
        if (quemChamou != null) {
            Bundle params = quemChamou.getExtras();
            if (params != null) {
                //Recuperando o Usuario
                usuario = (User) params.getSerializable("usuario");
                if (usuario != null) {
                    setTitle("Contatos de Emergência de "+ usuario.getNome());
                    preencherListView(usuario);
                }
            }
        }
    }

    public void salvarContato (Contato w){
        SharedPreferences salvaContatos = getSharedPreferences("contatos", Activity.MODE_PRIVATE);

        int num = salvaContatos.getInt("numContatos", 0); //checando quantos contatos já tem
        SharedPreferences.Editor editor = salvaContatos.edit();
        try {
            ByteArrayOutputStream dt = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(dt);
            dt = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(dt);
            oos.writeObject(w);
            String contatoSerializado = dt.toString(StandardCharsets.ISO_8859_1.name());
            editor.putString("contato" + (num + 1), contatoSerializado);
            editor.putInt("numContatos", num + 1);
        } catch(Exception e) {
            e.printStackTrace();
        }
        editor.commit();
        usuario.getContatos().add(w);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem ligarDial = menu.add("Acionar DIAL do celular...");
        MenuItem ligarDireto = menu.add("Ligar agora");
        MenuItem deletar = menu.add("Deletar");
        super.onCreateContextMenu(menu, v, menuInfo);

        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Contato contato = new Contato();
                contato.setNome(nomeContatoSelecionado);
                usuario.getContatos().remove(contato);
                preencherListView(usuario);
                return false;
            }
        });

        ligarDial.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Contato contato = new Contato();
                contato.setNome(nomeContatoSelecionado);

                numeroContatoSelecionado = usuario.getContatos().get(usuario.getContatos().indexOf(contato)).getNumero();
                Uri uri = Uri.parse("tel:" + numeroContatoSelecionado);
                Intent intent = new Intent(Intent.ACTION_DIAL,uri);
                startActivity(intent);

                return false;
            }
        });

        ligarDireto.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Contato contato = new Contato();
                contato.setNome(nomeContatoSelecionado);
                numeroContatoSelecionado = usuario.getContatos().get(usuario.getContatos().indexOf(contato)).getNumero();

                if (checarPermissaoPhone_SMD(numeroContatoSelecionado)) {
                    Uri uri = Uri.parse("tel:" + numeroContatoSelecionado);
                    Intent intent = new Intent(Intent.ACTION_CALL, uri);
                    startActivity(intent);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemMenuPerfil:
                Intent intencaoPerfil = new Intent(ListaContatosEmergenciaActivity.this, PerfilUsuarioActivity.class);
                intencaoPerfil.putExtra("usuario", usuario);
                startActivity(intencaoPerfil);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);
        return true;
    }

    protected void preencherListView(User user) {
        final ArrayList<Contato> contatos = user.getContatos();

        if (contatos != null) {
            final String[] nomesSP;
            nomesSP = new String[contatos.size()];
            Contato c;
            for (int j = 0; j < contatos.size(); j++) {
                nomesSP[j] = contatos.get(j).getNome();
            }

            ArrayAdapter<String> adaptador;
            adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nomesSP);
            listaContatos.setAdapter(adaptador);
        }
    }

    protected boolean checarPermissaoPhone_SMD(String numero){
        //numeroParaLigar = numero;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
            Log.v ("SMD","Tenho permissão");
            return true;
        } else {
            if ( shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)){
                Log.v ("SMD","Primeira Vez");
                String mensagem = "Nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão será exibida.";
                String titulo = "Permissão de acesso a chamadas";
                int codigo = 1;
                UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem,titulo, codigo);

                mensagemPermissao.onAttach ((Context)this);
                mensagemPermissao.show(getSupportFragmentManager(), "primeiravez2");

            } else {
                String mensagem = "Nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão será exibida.";
                String titulo = "Permissão de acesso a chamadas II";
                int codigo = 1;

                UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem, titulo, codigo);
                mensagemPermissao.onAttach ((Context) this);
                mensagemPermissao.show(getSupportFragmentManager(), "segundavez2");
                Log.v ("SMD","Outra Vez");
            }
        }
        return false;
    }

    @Override
    public void onDialogPositiveClick(int codigo) {
        if (codigo == 1){
            String[] permissions = {Manifest.permission.CALL_PHONE};
            requestPermissions(permissions, 2222);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 2222:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "VALEU", Toast.LENGTH_LONG).show();
                    Uri uri = Uri.parse(numeroContatoSelecionado);
                    Intent itLigar = new Intent(Intent.ACTION_CALL, uri);
                    startActivity(itLigar);
                } else {
                    Toast.makeText(this, "SEU FELA!", Toast.LENGTH_LONG).show();

                    String mensagem= "Seu aplicativo pode ligar diretamente, mas sem permissão não funciona. Se você marcou não perguntar mais, você deve ir na tela de configurações para mudar a instalação ou reinstalar o aplicativo  ";
                    String titulo= "Porque precisamos telefonar?";
                    UIEducacionalPermissao mensagemPermisso = new UIEducacionalPermissao(mensagem,titulo,2);
                    mensagemPermisso.onAttach((Context)this);
                    mensagemPermisso.show(getSupportFragmentManager(), "segundavez");
                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Caso seja um Voltar ou Sucesso selecionar o item Ligar

        if (requestCode == 1111) {//Retorno de Mudar Perfil
            usuario = atualizarUser();
            setTitle("Contatos de Emergência de " + usuario.getNome());
            atualizarListaDeContatos(usuario);
            preencherListView(usuario); //Montagem do ListView
        }

        if (requestCode == 1112) {//Retorno de Mudar Contatos
            atualizarListaDeContatos(usuario);
            preencherListView(usuario); //Montagem do ListView
        }
    }

    protected void atualizarListaDeContatos(User user){
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

    private User atualizarUser() {
        User user = null;
        SharedPreferences temUser      = getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        String            loginSalvo   = temUser.getString("login","");
        String            senhaSalva   = temUser.getString("senha","");
        String            nomeSalvo    = temUser.getString("nome","");
        String            emailSalvo   = temUser.getString("email","");
        boolean           manterLogado = temUser.getBoolean("manterLogado",false);

        user = new User(nomeSalvo, loginSalvo, senhaSalva, emailSalvo, manterLogado);
        return user;
    }

}