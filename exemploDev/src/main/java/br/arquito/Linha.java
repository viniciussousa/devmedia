package br.arquito;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public abstract class Linha {

    public Set<Campo> campos;

    public Linha() {
        campos = new HashSet<Campo>();
    }

    public Set<Campo> getCampos() {
        return campos;
    }

    public Linha adicionarCampo(Campo campo) {
        campos.add(campo);
        return this;
    }

    public abstract String construir();
    
    protected StringBuilder gerarLinhaParaCampos() {
        Set<Campo> camposOrdenados = new TreeSet<Campo>(new CampoComparator());
        StringBuilder linha = new StringBuilder();
        camposOrdenados.addAll(getCampos());
        for (Campo campo : camposOrdenados) {
            concatenarNaLinha(linha, campo);
        }
        return linha;
    }
    
    protected abstract void concatenarNaLinha(StringBuilder linha, Campo campo);

}
