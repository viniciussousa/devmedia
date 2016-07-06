package br.arquito;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.arquito.excecoes.ArquitoException;
import br.arquito.util.UtilArquito;

public class ArquivoTexto {

	private File file;
    private String caminho;
    private String diretorio;
    private List<Linha> linhas;
    private Posicao posicaoDoIdentificador;
    private List<Layout> layouts;

    public ArquivoTexto() {
        linhas = new ArrayList<Linha>();
        layouts = new ArrayList<Layout>();
    }
    
    public ArquivoTexto(File file, Posicao posicaoDoIdentificador) {
    	this();
    	this.file = file;
        this.posicaoDoIdentificador = posicaoDoIdentificador;
    }

    public ArquivoTexto(File file, Posicao posicaoDoIdentificador, Layout... layouts) {
        this();
        this.posicaoDoIdentificador = posicaoDoIdentificador;
        validarQueAlgumLayoutTenhaSidoInformado(layouts);
        validarPosicaoDoIdentificadorInformadaQuandoExistiremMuitosLayoutsProArquivo(layouts);
        preencherLinhas(file, layouts);
    }
    
    public void preencher() {
    	Layout[] layouts = this.layouts.toArray(new Layout[this.layouts.size()]);
    	validarQueAlgumLayoutTenhaSidoInformado(layouts);
        validarPosicaoDoIdentificadorInformadaQuandoExistiremMuitosLayoutsProArquivo(layouts);
        preencherLinhas(this.file, layouts);
    }
    
    public void addLayout(Layout layout) {
    	this.layouts.add(layout);
    }

    private void validarPosicaoDoIdentificadorInformadaQuandoExistiremMuitosLayoutsProArquivo(Layout[] layouts) {
        if (layouts.length > 1 && posicaoDoIdentificador == null) {
            throw new IllegalArgumentException(
                    "Quando houver mais de um possivel layout para o arquivo, a posicao do identificador deve ser informada.");
        }
    }

    private void validarQueAlgumLayoutTenhaSidoInformado(Layout[] layouts) {
        if (layouts.length == 0){
            throw new IllegalArgumentException("Pelo menos um layout deve ser informado para conversao do arquivo.");
        }
    }

    private void preencherLinhas(File file, Layout... layouts) {
        FileReader fileReader = null;
        try {        	
        	fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            String linhaTexto;
            while ((linhaTexto = br.readLine()) != null) {
                adicionarLinhaAPartirDeLayoutApropriado(linhaTexto, layouts);
            }
            br.close();
        } catch (IOException e) {
            new IllegalArgumentException(file.getAbsolutePath());
        }
    }

    private void adicionarLinhaAPartirDeLayoutApropriado(String linhaTexto, Layout... layouts) {
        Layout layout;
        if (existeMaisDeUmLayoutQuePodeSerAplicadoNasLinhas(layouts)) {
            layout = recuperarLayoutDestaLinha(linhaTexto, layouts);
        } else {
            layout = layouts[0];
        }
        Linha linha;
        if (layout.getSeparador() == null) {
            linha = new LinhaSemSeparador(linhaTexto, layout.getPosicoes());
        } else {
            linha = new LinhaComSeparador(linhaTexto, layout.getSeparador());
        }
        linhas.add(linha);
    }

    private Layout recuperarLayoutDestaLinha(String linhaTexto, Layout[] layouts) {
        for (Layout layout : layouts) {
            if (layout.getIdentificador().equals(posicaoDoIdentificador.getConteudoNaLinha(linhaTexto))) {
                return layout;
            }
        }
        return null;
    }

    private boolean existeMaisDeUmLayoutQuePodeSerAplicadoNasLinhas(Layout... layouts) {
        return layouts.length > 1;
    }

    public List<Linha> linhas() {
        return linhas;
    }

    public void adicionarLinha(Linha linha) {
        linhas.add(linha);
    }

    public ArquivoTexto setCaminho(String caminho) {
        this.caminho = caminho;
        return this;
    }
    
    public ArquivoTexto setDiretorio(String diretorio) {
    	this.diretorio = diretorio;
    	return this;
    }

    public Linha lerNaPosicao(Integer posicao) {
        return linhas.get(posicao - 1);
    }

    public File gerar() throws ArquitoException {
    	UtilArquito.criarDiretorio(diretorio);
    	File file = new File(caminho);        
        try {
        	FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            for (Linha linha : linhas()) {
                bw.write(linha.construir());
                bw.write("\r\n");
            }
            bw.close();
            fileWriter.close();
        } catch (IOException e) {
            throw new ArquitoException(e);
        }
        return file;
    }
    
    

}
