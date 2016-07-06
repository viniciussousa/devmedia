package br.arquito.excecoes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArquitoException extends Exception{

    private static final long serialVersionUID = 1L;

    private List<String> listaMensagens = new ArrayList<String>(); 

    public ArquitoException(String message) {
        super(message);
    }
    
    public ArquitoException() {
        super();
    }

    public ArquitoException(List<String> listaMensagens) {
        this.listaMensagens = listaMensagens;
    }

    public ArquitoException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArquitoException(Throwable cause) {
        super(cause);
    }

    public List<String> getListaMensagens() {
        return Collections.unmodifiableList(listaMensagens);
    }
    
    public String getMessage() {
       String demaisMensagens = "";
       if(listaMensagens != null && !listaMensagens.isEmpty()){
          demaisMensagens = listaMensagens.toString();
       }
        return String.format("%s %s", super.getMessage(), demaisMensagens);
    }
}