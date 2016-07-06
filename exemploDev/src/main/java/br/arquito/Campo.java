package br.arquito;

public class Campo {

    private Posicao posicao;
    private String conteudo;
    private EnumTipoConteudo tipoConteudo;

    public Campo() {
        posicao = new Posicao();
    }

    public Campo comConteudo(String conteudo) {
        this.conteudo = conteudo;
        return this;
    }

    public Campo comTipo(EnumTipoConteudo tipo) {
        tipoConteudo = tipo;
        return this;
    }

    public String toString() {
        return conteudo;
    }

    public EnumTipoConteudo getTipo() {
        return tipoConteudo;
    }

    public Integer tamanho() {
        return (posicaoFinal() - posicaoInicial()) + 1;
    }

    public Integer posicaoInicial() {
        return posicao.getPosicaoInicial();
    }

    public Integer posicaoFinal() {
        return posicao.getPosicaoFinal();
    }

    public Campo de(Integer posicaoInicial) {
        posicao.setPosicaoInicial(posicaoInicial);
        return this;
    }

    public Campo ate(Integer posicaoFinal) {
        posicao.setPosicaoFinal(posicaoFinal);
        return this;
    }

    public String getConteudo() {
        return conteudo;
    }

    public Campo posicao(Integer posicao) {
        de(posicao);
        return this;
    }
}
