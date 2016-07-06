package br.arquito;

import java.util.Comparator;

public class CampoComparator implements Comparator<Campo> {

    @Override
    public int compare(Campo o1, Campo o2) {
        return o1.posicaoInicial().compareTo(o2.posicaoInicial()) ;
    }

}
