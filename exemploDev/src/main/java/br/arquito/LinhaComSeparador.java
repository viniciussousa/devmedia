package br.arquito;

public class LinhaComSeparador extends Linha {

    private String separador;

    public LinhaComSeparador(String separador) {
        this.separador = separador;
    }

    public LinhaComSeparador(String linhaTexto, String separador) {
        this.separador = separador;

        String[] linhaQuebrada = linhaTexto.split(separador);
        for (int posicao = 0; posicao < linhaQuebrada.length; posicao++) {
            String conteudo = linhaQuebrada[posicao];
            campos.add(new Campo().de(posicao).comConteudo(conteudo));
        }
    }

    @Override
    public String construir() {
        StringBuilder linha = gerarLinhaParaCampos();        
        String linhaTexto = linha.toString();
        linhaTexto = linhaTexto.substring(0, linhaTexto.lastIndexOf(separador));
        return linhaTexto;
    }

    @Override
    protected void concatenarNaLinha(StringBuilder linha, Campo campo) {
        linha.append(campo.getConteudo());
        linha.append(separador);
    }
}
