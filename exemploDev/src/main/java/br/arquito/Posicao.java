package br.arquito;

public class Posicao {

    private Integer posicaoInicial;
    private Integer posicaoFinal;

    public Integer getPosicaoInicial() {
        return posicaoInicial;
    }

    public Posicao setPosicaoInicial(Integer posicaoInicial) {
        this.posicaoInicial = posicaoInicial;
        return this;
    }

    public Integer getPosicaoFinal() {
        return posicaoFinal;
    }

    public Posicao setPosicaoFinal(Integer posicaoFinal) {
        this.posicaoFinal = posicaoFinal;
        return this;
    }

    public String getConteudoNaLinha(String linhaTexto) {
        return linhaTexto.substring(posicaoInicial - 1, posicaoFinal);
    }

}
