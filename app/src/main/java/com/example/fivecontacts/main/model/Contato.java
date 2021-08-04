package com.example.fivecontacts.main.model;

import java.io.Serializable;
import java.util.Objects;

public class Contato implements Serializable, Comparable {

    private String nome;
    private String numero;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Override
    public int compareTo(Object o) {
        Contato c2 = (Contato) o;
        return this.getNome().compareTo(c2.getNome());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contato contato = (Contato) o;
        return nome.equals(contato.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}
