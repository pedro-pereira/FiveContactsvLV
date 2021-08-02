package com.example.fivecontacts.main.activities;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fivecontacts.R;
import com.example.fivecontacts.main.model.Contato;
import com.example.fivecontacts.main.model.User;
import com.example.fivecontacts.main.utils.UIEducacionalPermissao;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ListaDeContatos_Activity extends AppCompatActivity implements UIEducacionalPermissao.NoticeDialogListener, BottomNavigationView.OnNavigationItemSelectedListener {

    ListView lv;
    BottomNavigationView bnv;
    User user;

    String numeroCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_de_contatos);

        bnv = findViewById(R.id.bnv);
        bnv.setOnNavigationItemSelectedListener(this);
        bnv.setSelectedItemId(R.id.anvLigar);

        lv = findViewById(R.id.listView1);

        //Dados da Intent Anterior
        Intent quemChamou = this.getIntent();
        if (quemChamou != null) {
            Bundle params = quemChamou.getExtras();
            if (params != null) {
                //Recuperando o Usuario
                user = (User) params.getSerializable("usuario");
                if (user != null) {
                    setTitle("Contatos de Emergência de "+user.getNome());
                  //  preencherListView(user); //Montagem do ListView
                    preencherListViewImagens(user);
                  //  if (user.isTema_escuro()){
                    //    ((ConstraintLayout) (lv.getParent())).setBackgroundColor(Color.BLACK);
                    //}
                }
            }
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
                    ByteArrayInputStream bis =
                            new ByteArrayInputStream(objSel.getBytes(StandardCharsets.ISO_8859_1.name()));
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
        Log.v("PDM3","contatos:"+contatos.size());
        user.setContatos(contatos);
    }
    protected  void preencherListViewImagens(User user){

        final ArrayList<Contato> contatos = user.getContatos();
        Collections.sort(contatos);
        if (contatos != null) {
            String[] contatosNomes, contatosAbrevs;
            contatosNomes = new String[contatos.size()];
            contatosAbrevs= new String[contatos.size()];
            Contato c;
            for (int j = 0; j < contatos.size(); j++) {
                contatosAbrevs[j] =contatos.get(j).getNome().substring(0, 1);
                contatosNomes[j] =contatos.get(j).getNome();
            }
            ArrayList<Map<String,Object>> itemDataList = new ArrayList<Map<String,Object>>();;

            for(int i =0; i < contatos.size(); i++) {
                Map<String,Object> listItemMap = new HashMap<String,Object>();
                listItemMap.put("imageId", R.drawable.ic_action_ligar_list);
                listItemMap.put("contato", contatosNomes[i]);
                listItemMap.put("abrevs",contatosAbrevs[i]);
                itemDataList.add(listItemMap);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this,itemDataList,R.layout.list_view_layout_imagem,
                    new String[]{"imageId","contato","abrevs"},new int[]{R.id.userImage, R.id.userTitle,R.id.userAbrev});

            lv.setAdapter(simpleAdapter);


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                    if (checarPermissaoPhone_SMD(contatos.get(i).getNumero())) {

                        Uri uri = Uri.parse(contatos.get(i).getNumero());
                         //  Intent itLigar = new Intent(Intent.ACTION_DIAL, uri);
                            Intent itLigar = new Intent(Intent.ACTION_CALL, uri);
                        startActivity(itLigar);
                    }


                }
            });

        }


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

            adaptador = new ArrayAdapter<String>(this, R.layout.list_view_layout, nomesSP);

            lv.setAdapter(adaptador);


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    if (checarPermissaoPhone_SMD(contatos.get(i).getNumero())) {

                        Uri uri = Uri.parse(contatos.get(i).getNumero());
                      //   Intent itLigar = new Intent(Intent.ACTION_DIAL, uri);
                        Intent itLigar = new Intent(Intent.ACTION_CALL, uri);
                        startActivity(itLigar);
                    }


                }
            });
        }//fim do IF do tamanho de contatos
    }

    protected boolean checarPermissaoPhone_SMD(String numero){

        numeroCall=numero;
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
      == PackageManager.PERMISSION_GRANTED){

          Log.v ("SMD","Tenho permissão");

          return true;

      } else {

            if ( shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)){

                Log.v ("SMD","Primeira Vez");


                String mensagem = "Nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão será solicitada";
                String titulo = "Permissão de acesso a chamadas";
                int codigo =1;
                UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem,titulo, codigo);

                mensagemPermissao.onAttach ((Context)this);
                mensagemPermissao.show(getSupportFragmentManager(), "primeiravez2");

            }else{
                String mensagem = "Nossa aplicação precisa acessar o telefone para discagem automática. Uma janela de permissão será solicitada";
                String titulo = "Permissão de acesso a chamadas II";
                int codigo =1;

                UIEducacionalPermissao mensagemPermissao = new UIEducacionalPermissao(mensagem,titulo, codigo);
                mensagemPermissao.onAttach ((Context)this);
                mensagemPermissao.show(getSupportFragmentManager(), "segundavez2");
                Log.v ("SMD","Outra Vez");

            }
      }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                            int[] grantResults) {
        switch (requestCode) {
            case 2222:
               if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                   Toast.makeText(this, "VALEU", Toast.LENGTH_LONG).show();
                   Uri uri = Uri.parse(numeroCall);
                   //   Intent itLigar = new Intent(Intent.ACTION_DIAL, uri);
                   Intent itLigar = new Intent(Intent.ACTION_CALL, uri);
                   startActivity(itLigar);

               }else{
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
            @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Checagem de o Item selecionado é o do perfil
        if (item.getItemId() == R.id.anvPerfil) {
            //Abertura da Tela MudarDadosUsario
            Intent intent = new Intent(this, PerfilUsuario_Activity.class);
            intent.putExtra("usuario", user);
            startActivityForResult(intent, 1111);

        }
        // Checagem de o Item selecionado é o do perfil
        if (item.getItemId() == R.id.anvMudar) {
            //Abertura da Tela Mudar COntatos
            Intent intent = new Intent(this, AlterarContatos_Activity.class);
            intent.putExtra("usuario", user);
            startActivityForResult(intent, 1112);

        }
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Caso seja um Voltar ou Sucesso selecionar o item Ligar

        if (requestCode == 1111) {//Retorno de Mudar Perfil
            bnv.setSelectedItemId(R.id.anvLigar);
            user=atualizarUser();
            setTitle("Contatos de Emergência de "+user.getNome());
            atualizarListaDeContatos(user);
           // preencherListViewImagens(user);
            preencherListView(user); //Montagem do ListView
        }

        if (requestCode == 1112) {//Retorno de Mudar Contatos
            bnv.setSelectedItemId(R.id.anvLigar);
            atualizarListaDeContatos(user);
            //preencherListViewImagens(user);
            preencherListView(user); //Montagem do ListView
        }



    }

    private User atualizarUser() {
        User user = null;
        SharedPreferences temUser= getSharedPreferences("usuarioPadrao", Activity.MODE_PRIVATE);
        String loginSalvo = temUser.getString("login","");
        String senhaSalva = temUser.getString("senha","");
        String nomeSalvo = temUser.getString("nome","");
        String emailSalvo = temUser.getString("email","");
        boolean manterLogado=temUser.getBoolean("manterLogado",false);

        user=new User(nomeSalvo,loginSalvo,senhaSalva,emailSalvo,manterLogado);
        return user;
    }

    @Override
    public void onDialogPositiveClick(int codigo) {

        if (codigo==1){
          String[] permissions ={Manifest.permission.CALL_PHONE};
          requestPermissions(permissions, 2222);

        }


    }


}


