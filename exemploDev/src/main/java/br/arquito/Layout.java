package br.arquito;

import java.util.HashSet;
import java.util.Set;

public class Layout {

    private Set<Posicao> posicoes;
    private String identificador;
    private String separador;
    private Long sequencial;

    public Layout() {
        posicoes = new HashSet<Posicao>();
    }

	public Set<Posicao> getPosicoes() {
        return posicoes;
    }

    public Layout adicionarPosicao(Posicao posicao) {
        posicoes.add(posicao);
        return this;
    }

    public Layout setIdentificador(String identificador) {
        this.identificador = identificador;
        return this;
    }

    public Layout setSeparador(String separador) {
        this.separador = separador;
        return this;
    }

    public String getSeparador() {
        return separador;
    }

    public String getIdentificador() {
        return identificador;
    }
    
    public Long getSequencial() {
		return sequencial;
	}

	public void setSequencial(Long sequencial) {
		this.sequencial = sequencial;
	}
}
