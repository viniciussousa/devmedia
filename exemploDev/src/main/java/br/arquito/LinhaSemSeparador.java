package br.arquito;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import br.arquito.util.UtilArquito;

public class LinhaSemSeparador extends Linha {

    public LinhaSemSeparador() {

    }

    public LinhaSemSeparador(String linhaTexto, Set<Posicao> posicoes) {
        for (Posicao posicao : posicoes) {
            String conteudo = posicao.getConteudoNaLinha(linhaTexto);
            campos.add(new Campo().de(posicao.getPosicaoInicial()).ate(posicao.getPosicaoFinal()).comConteudo(conteudo)
                    .comTipo(EnumTipoConteudo.ALFANUMERICO));
        }
    }

    @Override
    public String construir() {
        StringBuilder linha = gerarLinhaParaCampos();
        return linha.toString();
    }

    @Override
    protected void concatenarNaLinha(StringBuilder linha, Campo campo) {
        if (EnumTipoConteudo.NUMERICO.equals(campo.getTipo())) {
        	
        	String campoSomenteNumeros = UtilArquito.somenteNumeros(campo.toString());
        	
            linha.append(StringUtils.leftPad(campoSomenteNumeros.toUpperCase(), campo.tamanho(), '0'));
            
        } else {
        	
            String valorSemCaracteresEspeciais = UtilArquito.retirarCaracteresEspeciais(campo.toString());
            
            String conteudo = StringUtils.rightPad(valorSemCaracteresEspeciais.toUpperCase(), campo.tamanho());
            
            linha.append(conteudo.substring(0, campo.tamanho()));
        }
    }
    
}
