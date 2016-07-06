package br.arquito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ArquivoTextoTest {

    private ArquivoTexto arquivo;

    @Before
    public void setUp() {
        arquivo = new ArquivoTexto().setCaminho("//tmp//testGerarArquivo.arquivo");
    }

    @After
    public void tearDown() {
        assertTrue(new File("//tmp//testGerarArquivo.arquivo").delete());
    }

    @Test
    public void testGerarArquivoNoCaminhoInformado() throws Exception {
        File file = arquivo.gerar();

        assertTrue(file.exists());
        assertEquals("/tmp/testGerarArquivo.arquivo", file.getAbsolutePath());
    }

    @Test
    public void testGerarAdicionandoLinhaComUmCampo() throws Exception {
        arquivo.adicionarLinha(new LinhaSemSeparador().adicionarCampo(new Campo().de(1).ate(7).comConteudo("ruither")));

        File file = arquivo.gerar();

        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        String line = br.readLine();
        assertEquals("RUITHER", line);
        br.close();
    }
    
    @Test
    public void testGerarAdicionandoLinhas() throws Exception {
        arquivo.adicionarLinha(new LinhaSemSeparador().adicionarCampo(new Campo().de(1).ate(9).comConteudo("ruither01")));
        arquivo.adicionarLinha(new LinhaSemSeparador().adicionarCampo(new Campo().de(1).ate(9).comConteudo("ruither02")));
        Layout layout = new Layout();
        layout.adicionarPosicao(new Posicao().setPosicaoInicial(1).setPosicaoFinal(9));

        File file = arquivo.gerar();

        ArquivoTexto arquivo = new ArquivoTexto(file, null, layout);
        Linha primeiraLinha = arquivo.lerNaPosicao(1);
        Linha segundaLinha = arquivo.lerNaPosicao(2);
        assertEquals("RUITHER01", primeiraLinha.construir());
        assertEquals("RUITHER02", segundaLinha.construir());
    }

    @Test
    public void testGerarConcatenandoCamposNaOrdemCorreta() throws Exception {
        Campo primeiroCampo = new Campo().de(7).ate(10).comConteudo("CNPJ");
        Campo segundoCampo = new Campo().de(1).ate(6).comConteudo("PESSOA");
        Campo terceiroCampo = new Campo().de(11).ate(18).comConteudo("ENDERECO");
        arquivo.adicionarLinha(new LinhaSemSeparador().adicionarCampo(primeiroCampo).adicionarCampo(segundoCampo).adicionarCampo(terceiroCampo));
        Layout layout = new Layout();
        layout.adicionarPosicao(new Posicao().setPosicaoInicial(1).setPosicaoFinal(6));
        layout.adicionarPosicao(new Posicao().setPosicaoInicial(7).setPosicaoFinal(10));
        layout.adicionarPosicao(new Posicao().setPosicaoInicial(11).setPosicaoFinal(18));

        File file = arquivo.gerar();

        ArquivoTexto arquivo = new ArquivoTexto(file, null, layout);
        Linha linha = arquivo.lerNaPosicao(1);
        assertEquals("PESSOACNPJENDERECO", linha.construir());
    }

    @Test
    public void testGerarConteudoCompletandoComZerosAEsquerdaQuandoTipoNumerico() throws Exception {
        Campo campo = new Campo().de(1).ate(10).comTipo(EnumTipoConteudo.NUMERICO).comConteudo("159");
        arquivo.adicionarLinha(new LinhaSemSeparador().adicionarCampo(campo));
        Layout layout = new Layout().adicionarPosicao(new Posicao().setPosicaoInicial(1).setPosicaoFinal(10));

        File file = arquivo.gerar();

        ArquivoTexto arquivo = new ArquivoTexto(file, null, layout);
        Linha linha = arquivo.lerNaPosicao(1);
        assertEquals("0000000159", linha.construir());
    }

    @Test
    public void testGerarConteudoCompletandoComEspacosADireitaQuandoTipoTexto() throws Exception {
        Campo campo = new Campo().de(1).ate(10).comTipo(EnumTipoConteudo.ALFANUMERICO).comConteudo("JESUS");
        arquivo.adicionarLinha(new LinhaSemSeparador().adicionarCampo(campo));
        Layout layout = new Layout().adicionarPosicao(new Posicao().setPosicaoInicial(1).setPosicaoFinal(10));

        File file = arquivo.gerar();

        ArquivoTexto arquivo = new ArquivoTexto(file, null, layout);
        Linha linha = arquivo.lerNaPosicao(1);
        assertEquals("JESUS     ", linha.construir());
    }

    @Test
    public void testGerarConteudoCortandoTextoEColocandoUpperCaseQuandoTipoTexto() throws Exception {
        Campo campo = new Campo().de(1).ate(10).comTipo(EnumTipoConteudo.ALFANUMERICO).comConteudo("amarildo amarelo");
        arquivo.adicionarLinha(new LinhaSemSeparador().adicionarCampo(campo));
        Layout layout = new Layout().adicionarPosicao(new Posicao().setPosicaoInicial(1).setPosicaoFinal(10));

        File file = arquivo.gerar();

        ArquivoTexto arquivo = new ArquivoTexto(file, null, layout);
        Linha linha = arquivo.lerNaPosicao(1);
        assertEquals("AMARILDO A", linha.construir());
    }

    @Test
    public void testGerarComLinhasComLayoutsDiferentes() throws Exception {
        // given
        Campo identificadorHeader = new Campo().de(1).ate(1).comTipo(EnumTipoConteudo.NUMERICO).comConteudo("0");
        Campo cnpjEmpresa = new Campo().de(2).ate(25).comTipo(EnumTipoConteudo.NUMERICO).comConteudo("945402075831990000109");
        Campo nomeEmpresa = new Campo().de(26).ate(54).comTipo(EnumTipoConteudo.ALFANUMERICO).comConteudo("DU GREGORIO COMERCIO E TRANSPORTES");
        Campo brancoHeader = new Campo().de(55).ate(63).comTipo(EnumTipoConteudo.ALFANUMERICO).comConteudo("");
        Campo sequencialHeader = new Campo().de(64).ate(70).comTipo(EnumTipoConteudo.NUMERICO).comConteudo("1");
        Linha header = new LinhaSemSeparador().adicionarCampo(identificadorHeader).adicionarCampo(nomeEmpresa).adicionarCampo(brancoHeader)
                .adicionarCampo(cnpjEmpresa).adicionarCampo(sequencialHeader);
        arquivo.adicionarLinha(header);

        Layout layoutHeader = gerarLayoutHeader();

        Campo identificadorTransacao = new Campo().de(1).ate(1).comTipo(EnumTipoConteudo.NUMERICO).comConteudo("1");
        Campo cnpjParcela = new Campo().de(2).ate(18).comTipo(EnumTipoConteudo.NUMERICO).comConteudo("200341814200016-11");
        Campo nomeCliente = new Campo().de(19).ate(46).comTipo(EnumTipoConteudo.ALFANUMERICO).comConteudo("ATACADAO DA construcao LTDA");
        Campo vencimento = new Campo().de(47).ate(54).comTipo(EnumTipoConteudo.NUMERICO).comConteudo("20100725");
        Campo brancoTransacao = new Campo().de(55).ate(63).comTipo(EnumTipoConteudo.ALFANUMERICO).comConteudo("");
        Campo sequencialTransacao = new Campo().de(64).ate(70).comTipo(EnumTipoConteudo.NUMERICO).comConteudo("2");
        Linha transacao = new LinhaSemSeparador().adicionarCampo(identificadorTransacao).adicionarCampo(cnpjParcela).adicionarCampo(nomeCliente)
                .adicionarCampo(vencimento).adicionarCampo(brancoTransacao).adicionarCampo(sequencialTransacao);
        arquivo.adicionarLinha(transacao);

        Layout layoutTransacao = gerarLayoutTransacao();

        Campo identificadorTrailler = new Campo().de(1).ate(1).comTipo(EnumTipoConteudo.NUMERICO).comConteudo("9");
        Campo numericoUm = new Campo().de(2).ate(24).comTipo(EnumTipoConteudo.NUMERICO).comConteudo("3200000000007899819");
        Campo brancoTraillerUm = new Campo().de(25).ate(25).comTipo(EnumTipoConteudo.ALFANUMERICO).comConteudo("");
        Campo numericoDois = new Campo().de(26).ate(48).comTipo(EnumTipoConteudo.NUMERICO).comConteudo("3001355520007899819");
        Campo brancoTraillerDois = new Campo().de(49).ate(63).comTipo(EnumTipoConteudo.ALFANUMERICO).comConteudo("");
        Campo sequencialTrailler = new Campo().de(64).ate(70).comTipo(EnumTipoConteudo.NUMERICO).comConteudo("3");
        Linha trailler = new LinhaSemSeparador().adicionarCampo(identificadorTrailler).adicionarCampo(numericoDois).adicionarCampo(numericoUm)
                .adicionarCampo(brancoTraillerUm).adicionarCampo(brancoTraillerDois).adicionarCampo(sequencialTrailler);
        arquivo.adicionarLinha(trailler);

        Layout layoutTrailler = gerarLayoutTrailler();

        // when
        File file = arquivo.gerar();

        // then
        Posicao posicaoDoIdentificador = new Posicao().setPosicaoInicial(1).setPosicaoFinal(1);
        ArquivoTexto arquivoLeitura = new ArquivoTexto(file, posicaoDoIdentificador, layoutHeader, layoutTrailler, layoutTransacao);
        Linha primeiraLinha = arquivoLeitura.lerNaPosicao(1);
        Linha segundaLinha = arquivoLeitura.lerNaPosicao(2);
        Linha terceiraLinha = arquivoLeitura.lerNaPosicao(3);
        assertEquals("0000945402075831990000109DU GREGORIO COMERCIO E TRANSP         0000001", primeiraLinha.construir());
        assertEquals("120034181420001611ATACADAO DA CONSTRUCAO LTDA 20100725         0000002", segundaLinha.construir());
        assertEquals("900003200000000007899819 00003001355520007899819               0000003", terceiraLinha.construir());
    }

    @Test
    public void testLeituraGerandoCamposNoArquivoTextoDeLeitura() throws Exception {
        // given
        File file = new File("//tmp//testGerarArquivo.arquivo");
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("0000945402075831990000109DU GREGORIO COMERCIO E TRANSP         0000001");
        bw.close();

        Layout layout = gerarLayoutHeader();
        Posicao posicaoDoIdentificador = new Posicao().setPosicaoInicial(1).setPosicaoFinal(1);

        // when
        ArquivoTexto arquivoLeitura = new ArquivoTexto(file, posicaoDoIdentificador, layout);

        // then
        assertEquals(1, arquivoLeitura.linhas().size());
        Linha linha = arquivoLeitura.lerNaPosicao(1);

        List<Campo> campos = new ArrayList<Campo>(linha.getCampos());
        assertEquals(5, campos.size());
        Collections.sort(campos, new CampoComparator());

        assertEquals("0", campos.get(0).getConteudo());
        assertEquals(Integer.valueOf(1), campos.get(0).posicaoInicial());
        assertEquals(Integer.valueOf(1), campos.get(0).posicaoFinal());
        assertEquals(EnumTipoConteudo.ALFANUMERICO, campos.get(0).getTipo());

        assertEquals("000945402075831990000109", campos.get(1).getConteudo());
        assertEquals(Integer.valueOf(2), campos.get(1).posicaoInicial());
        assertEquals(Integer.valueOf(25), campos.get(1).posicaoFinal());
        assertEquals(EnumTipoConteudo.ALFANUMERICO, campos.get(1).getTipo());

        assertEquals("DU GREGORIO COMERCIO E TRANSP", campos.get(2).getConteudo());
        assertEquals(Integer.valueOf(26), campos.get(2).posicaoInicial());
        assertEquals(Integer.valueOf(54), campos.get(2).posicaoFinal());
        assertEquals(EnumTipoConteudo.ALFANUMERICO, campos.get(2).getTipo());

        assertEquals("         ", campos.get(3).getConteudo());
        assertEquals(Integer.valueOf(55), campos.get(3).posicaoInicial());
        assertEquals(Integer.valueOf(63), campos.get(3).posicaoFinal());
        assertEquals(EnumTipoConteudo.ALFANUMERICO, campos.get(3).getTipo());

        assertEquals("0000001", campos.get(4).getConteudo());
        assertEquals(Integer.valueOf(64), campos.get(4).posicaoInicial());
        assertEquals(Integer.valueOf(70), campos.get(4).posicaoFinal());
        assertEquals(EnumTipoConteudo.ALFANUMERICO, campos.get(4).getTipo());
    }

    @Test
    public void testGerandoCopiaDeArquivoAPartirDeInstanciaDeArquivoTextoDeLeitura() throws Exception {
        // given
        File file = new File("//tmp//testGerarArquivo.arquivo");
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("0000945402075831990000109DU GREGORIO COMERCIO E TRANSP         0000001");
        bw.newLine();
        bw.write("120034181420001611ATACADAO DA CONSTRUCAO LTDA 20100725         0000002");
        bw.newLine();
        bw.write("900003200000000007899819 00003001355520007899819               0000003");
        bw.close();

        // when
        Posicao posicaoDoIdentificador = new Posicao().setPosicaoInicial(1).setPosicaoFinal(1);
        ArquivoTexto arquivoLeitura = new ArquivoTexto(file, posicaoDoIdentificador, gerarLayoutHeader(), gerarLayoutTransacao(),
                gerarLayoutTrailler()).setCaminho("//tmp//testGerarArquivoCopia.arquivo");
        File copia = arquivoLeitura.gerar();

        // then
        assertEquals("/tmp/testGerarArquivoCopia.arquivo", copia.getAbsolutePath());
        List<String> linhasNoArquivo = recuperarLinhasNoArquivo(copia);
        assertEquals(3, linhasNoArquivo.size());
        assertEquals("0000945402075831990000109DU GREGORIO COMERCIO E TRANSP         0000001", linhasNoArquivo.get(0));
        assertEquals("120034181420001611ATACADAO DA CONSTRUCAO LTDA 20100725         0000002", linhasNoArquivo.get(1));
        assertEquals("900003200000000007899819 00003001355520007899819               0000003", linhasNoArquivo.get(2));

        assertTrue(copia.delete());
    }

    @Test
    public void testValidarQuandoNenhumLayoutEhPassadoNoConstrutorDoArquivoDeLeitura() throws Exception {
        arquivo.adicionarLinha(new LinhaSemSeparador().adicionarCampo(new Campo().de(1).ate(70).comTipo(EnumTipoConteudo.ALFANUMERICO)
                .comConteudo("0000945402075831990000109DU GREGORIO COMERCIO E TRANSP         0000001")));
        File file = arquivo.gerar();

        Posicao posicaoDoIdentificador = new Posicao().setPosicaoInicial(1).setPosicaoFinal(1);
        try {
            new ArquivoTexto(file, posicaoDoIdentificador);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Pelo menos um layout deve ser informado para conversao do arquivo.", e.getMessage());
        }
    }

    @Test
    public void testValidarQuandoExisteMaisDeUmPossivelLayoutEPosicaoDoIdentificadorNaoFoiInformada() throws Exception {
        arquivo.adicionarLinha(new LinhaSemSeparador().adicionarCampo(new Campo().de(1).ate(70).comTipo(EnumTipoConteudo.ALFANUMERICO)
                .comConteudo("0000945402075831990000109DU GREGORIO COMERCIO E TRANSP         0000001")));
        File file = arquivo.gerar();

        try {
            new ArquivoTexto(file, null, gerarLayoutTransacao(), gerarLayoutHeader());
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Quando houver mais de um possivel layout para o arquivo, a posicao do identificador deve ser informada.", e.getMessage());
        }
    }

    @Test
    public void testGerarArquivoComLinhasSeparadasPorCaractereEspecial() throws Exception {
        Linha linha = new LinhaComSeparador("|");
        linha.adicionarCampo(new Campo().posicao(2).comConteudo("ruither"));
        linha.adicionarCampo(new Campo().posicao(1).comConteudo("1"));
        linha.adicionarCampo(new Campo().posicao(3).comConteudo("888"));
        arquivo.adicionarLinha(linha);

        File file = arquivo.gerar();

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String linhaGerada = br.readLine();
        br.close();
        assertEquals("1|ruither|888", linhaGerada);
    }

    @Test
    public void testLerArquivoComLinhasSeparadasPorCaractereEspecial() throws Exception {
        Linha linha = new LinhaComSeparador("|");
        linha.adicionarCampo(new Campo().posicao(2).comConteudo("ruither"));
        linha.adicionarCampo(new Campo().posicao(1).comConteudo("1"));
        linha.adicionarCampo(new Campo().posicao(3).comConteudo("888"));
        arquivo.adicionarLinha(linha);

        File file = arquivo.gerar();

        ArquivoTexto arquivoLeitura = new ArquivoTexto(file, null, new Layout().setSeparador("\\|"));
        assertEquals(1, arquivoLeitura.linhas().size());
        List<Campo> campos = new ArrayList<Campo>(arquivoLeitura.linhas().get(0).getCampos());
        assertEquals(3, campos.size());
        Collections.sort(campos, new CampoComparator());

        assertEquals("1", campos.get(0).getConteudo());
        assertEquals(Integer.valueOf(0), campos.get(0).posicaoInicial());
        assertNull(campos.get(0).posicaoFinal());

        assertEquals("ruither", campos.get(1).getConteudo());
        assertEquals(Integer.valueOf(1), campos.get(1).posicaoInicial());
        assertNull(campos.get(1).posicaoFinal());

        assertEquals("888", campos.get(2).getConteudo());
        assertEquals(Integer.valueOf(2), campos.get(2).posicaoInicial());
        assertNull(campos.get(2).posicaoFinal());
    }

    private Layout gerarLayoutHeader() {
        Layout layoutHeader = new Layout();
        layoutHeader.setIdentificador("0");
        layoutHeader.adicionarPosicao(new Posicao().setPosicaoInicial(2).setPosicaoFinal(25));
        layoutHeader.adicionarPosicao(new Posicao().setPosicaoInicial(1).setPosicaoFinal(1));
        layoutHeader.adicionarPosicao(new Posicao().setPosicaoInicial(26).setPosicaoFinal(54));
        layoutHeader.adicionarPosicao(new Posicao().setPosicaoInicial(64).setPosicaoFinal(70));
        layoutHeader.adicionarPosicao(new Posicao().setPosicaoInicial(55).setPosicaoFinal(63));
        return layoutHeader;
    }

    private Layout gerarLayoutTransacao() {
        Layout layoutTransacao = new Layout();
        layoutTransacao.setIdentificador("1");
        layoutTransacao.adicionarPosicao(new Posicao().setPosicaoInicial(1).setPosicaoFinal(1));
        layoutTransacao.adicionarPosicao(new Posicao().setPosicaoInicial(2).setPosicaoFinal(18));
        layoutTransacao.adicionarPosicao(new Posicao().setPosicaoInicial(19).setPosicaoFinal(46));
        layoutTransacao.adicionarPosicao(new Posicao().setPosicaoInicial(47).setPosicaoFinal(54));
        layoutTransacao.adicionarPosicao(new Posicao().setPosicaoInicial(55).setPosicaoFinal(63));
        layoutTransacao.adicionarPosicao(new Posicao().setPosicaoInicial(64).setPosicaoFinal(70));
        return layoutTransacao;
    }

    private Layout gerarLayoutTrailler() {
        Layout layoutTrailler = new Layout();
        layoutTrailler.setIdentificador("9");
        layoutTrailler.adicionarPosicao(new Posicao().setPosicaoInicial(1).setPosicaoFinal(1));
        layoutTrailler.adicionarPosicao(new Posicao().setPosicaoInicial(2).setPosicaoFinal(24));
        layoutTrailler.adicionarPosicao(new Posicao().setPosicaoInicial(25).setPosicaoFinal(25));
        layoutTrailler.adicionarPosicao(new Posicao().setPosicaoInicial(26).setPosicaoFinal(48));
        layoutTrailler.adicionarPosicao(new Posicao().setPosicaoInicial(49).setPosicaoFinal(63));
        layoutTrailler.adicionarPosicao(new Posicao().setPosicaoInicial(64).setPosicaoFinal(70));
        return layoutTrailler;
    }

    private List<String> recuperarLinhasNoArquivo(File copia) throws IOException {
        FileReader fr = new FileReader(copia);
        BufferedReader br = new BufferedReader(fr);
        List<String> linhasNoArquivo = new ArrayList<String>();
        String linha;
        while ((linha = br.readLine()) != null) {
            linhasNoArquivo.add(linha);
        }
        br.close();
        return linhasNoArquivo;
    }

}
