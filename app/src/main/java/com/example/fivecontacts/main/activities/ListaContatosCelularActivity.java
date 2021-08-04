package com.example.fivecontacts.main.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.fivecontacts.R;
import com.example.fivecontacts.main.model.Contato;
import com.example.fivecontacts.main.model.User;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

public class ListaContatosCelularActivity extends AppCompatActivity {

    private ListView listaContatosCelular;
    private User usuario;
    private EditText edtNome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contatos_celular);

        listaContatosCelular = (ListView) findViewById(R.id.listContatosCelular);
        edtNome = (EditText) findViewById(R.id.edtBuscar);

        //Dados da Intent Anterior
        Intent quemChamou = this.getIntent();
        if (quemChamou != null) {
            Bundle params = quemChamou.getExtras();
            if (params != null) {
                //Recuperando o Usuario
                usuario = (User) params.getSerializable("usuario");
                setTitle("Contatos da agenda de " + usuario.getNome());
            }
        }
    }

    public void onClickBuscar(View v){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
            Log.v("PDM", "Pedir permissão");
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 3333);
            return;
        }
        Log.v("PDM", "Tenho permissão");

        ContentResolver cr = getContentResolver();
        String consulta = ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
        String [] argumentosConsulta= {"%" + edtNome.getText() + "%"};
        Cursor cursor= cr.query(ContactsContract.Contacts.CONTENT_URI, null, consulta, argumentosConsulta, null);
        final String[] nomesContatos = new String[cursor.getCount()];
        final String[] telefonesContatos = new String[cursor.getCount()];
        Log.v("PDM","Tamanho do cursor:"+cursor.getCount());

        int i = 0;
        while (cursor.moveToNext()) {
            int indiceNome = cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME);
            String contatoNome = cursor.getString(indiceNome);
            Log.v("PDM", "Contato " + i + ", Nome:" + contatoNome);
            nomesContatos[i] = contatoNome;
            int indiceContatoID = cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID);
            String contactID = cursor.getString(indiceContatoID);
            String consultaPhone = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactID;
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, consultaPhone, null, null);

            while (phones.moveToNext()) {
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                telefonesContatos[i] = number; //Salvando só último telefone
            }
            i++;
        }

        if (nomesContatos != null) {
            for(int j = 0; j <= nomesContatos.length; j++) {
                ArrayAdapter<String> adaptador;
                // adaptador = new ArrayAdapter<String>(this, R.layout.list_view_layout, nomesContatos);
                adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nomesContatos);
                listaContatosCelular.setAdapter(adaptador);
                listaContatosCelular.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Contato c = new Contato();
                        c.setNome(nomesContatos[i]);
                        c.setNumero(telefonesContatos[i]);
                        Log.i("PEDRO", "Entrei no onItemClick da lista. Valores... " + c.getNome() + " / " + c.getNumero());
                        salvarContato(c);
                        Intent intent = new Intent(getApplicationContext(), ListaContatosEmergenciaActivity.class);
                        Log.i("PEDRO", "Usuário. Valores... " + usuario.getNome());
                        intent.putExtra("usuario", usuario);
                        startActivity(intent);
                        finish();
                    }
                });
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
}