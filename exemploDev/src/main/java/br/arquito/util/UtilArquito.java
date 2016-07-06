package br.arquito.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.lang.StringUtils;

import br.arquito.EnumTipoMetodo;
import br.arquito.excecoes.ArquitoException;

@SuppressWarnings("unchecked")
public final class UtilArquito {

	private UtilArquito() {
		super();
	}

	private static String[] listaEntidades;

	/**
	 * <p>
	 * Este metodo e responsavel para pegar os valores dos objetos conforme o(s)
	 * atributo(s) que for(em) passado(s)
	 * </p>
	 * 
	 * <pre>
	 * List<Object> objetos = UtilObjeto.getValorObjeto(objetoPessoa, "pessoa.endereco", "rua", "bairro", "cidade");
	 * </pre>
	 * 
	 * @param objeto
	 *            Objeto que o valor será pego.
	 * @param entidade
	 *            Entidades (dependencias) que o objeto possui
	 * @param atributos
	 *            Atributos que a entidade e ou o objeto possui
	 * @return Lista de objetos conforme atributos passados, <code>null</code>
	 *         se o objeto for nullo
	 * @throws ArquitoException
	 */
	public static String getValorObjeto(final Object classe, final String caminho, final String atributo, String formatacaoData) throws Exception {
		
		String propriedade;
		
		if(StringUtils.isNotBlank(caminho)) {
			
			propriedade = caminho.concat("." + atributo);
			
		} else {
			
			propriedade = atributo;
		}
		
		try {
			
			Object objeto = BeanUtilsBean.getInstance().getPropertyUtils().getProperty(classe, propriedade);
			
			String valor = StringUtils.EMPTY;
			
			if(objeto == null){
				
				return valor;
				
			}else{
				
				valor = objeto.toString();
			}

			if (StringUtils.isNotBlank(formatacaoData)) {
				
				if (Date.class.equals(obterTipo(classe, propriedade))) {

					SimpleDateFormat formatar = new SimpleDateFormat(formatacaoData);

					valor = formatar.format(objeto);
				}
			}
			return valor;

		} catch (final NestedNullException e) {
			
			throw new ArquitoException(mensagemEntidadeNaoInformada(propriedade));
			
		} catch (final NoSuchMethodException e) {
			
			throw new ArquitoException(mensagemAtributoNaoFoiEncontradoNoObjeto(classe, propriedade));
		}
	}

	public static <T> T getValorObjeto(final Object o, final String propriedade) throws Exception {
		try {

			return (T) BeanUtilsBean.getInstance().getPropertyUtils().getProperty(o, propriedade);

		} catch (final NestedNullException e) {
			throw new ArquitoException(mensagemEntidadeNaoInformada(propriedade));
		} catch (final NoSuchMethodException e) {
			throw new ArquitoException(mensagemAtributoNaoFoiEncontradoNoObjeto(o, propriedade));
		}
	}

	/**
	 * <p>
	 * Este metodo e responsavel para atribuir valores para o objeto conforme o
	 * atributos que forem passados
	 * </p>
	 * 
	 * <pre>
	 * List<Object> objetos = UtilObjeto.setValorObjeto(objetoPessoa, "pessoa", "endereco", endereco);
	 * </pre>
	 * 
	 * @param objeto
	 *            Objeto que sera atribuido o valor passado
	 * @param entidade
	 *            Entidades (depedencias) que serao atribuidos valores
	 * @param atributo
	 *            Atributo que sera atribuido o valor
	 * @param valor
	 *            Valor a ser atribuido ao objeto
	 * @throws ArquitoException
	 */
	public static void setValorObjeto(Object objeto, String entidade, String atributo, Object valor) throws ArquitoException {
		validaObjetoInformado(objeto);
		if (entidade == null) {
			setAtributoInformado(objeto, valor, atributo);
		} else {
			preencherListaEntidade(entidade);
			Object objetoInformado = objeto;
			for (Integer i = 0; i < listaEntidades.length; i++) {
				try {
					Method getMetodo = encontrarMetodo(EnumTipoMetodo.GET, objetoInformado, listaEntidades[i], null);

					if (getMetodo.invoke(objetoInformado) == null) {
						Object newObject = Class.forName(getMetodo.getReturnType().getName()).newInstance();

						Method metodo = encontrarMetodo(EnumTipoMetodo.SET, objetoInformado, listaEntidades[i], newObject);
						metodo.invoke(objetoInformado, newObject);
						objetoInformado = newObject;
					} else {
						objetoInformado = getMetodo.invoke(objetoInformado);
					}

					if (i == listaEntidades.length - 1) {
						setAtributoInformado(objetoInformado, valor, atributo);
					}
				} catch (NoSuchMethodException e) {
					mensagemEntidadeNaoEncontradaNoObjeto(objetoInformado, i);
				} catch (IllegalAccessException e) {
					mensagemErroNaoTratado(e);
				} catch (InvocationTargetException e) {
					mensagemErroNaoTratado(e);
				} catch (InstantiationException e) {
					mensagemErroNaoTratado(e);
				} catch (ClassNotFoundException e) {
					mensagemErroNaoTratado(e);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private static synchronized Method encontrarMetodo(EnumTipoMetodo tipo, Object obj, String property, Object valor) throws NoSuchMethodException {
		Method metodo = null;
		Class<?> classe = obj.getClass();
		String tipoMetodo = StringUtils.EMPTY;

		if (tipo.equals(EnumTipoMetodo.SET)) {
			tipoMetodo = String.format("set%s", StringUtils.capitalize(property));
			Class parametroValor = valor.getClass();
			metodo = classe.getMethod(tipoMetodo, parametroValor);
		} else {
			tipoMetodo = String.format("get%s", StringUtils.capitalize(property));
			metodo = obj.getClass().getMethod(tipoMetodo);
		}
		return metodo;
	}

	private static void setAtributoInformado(Object objeto, Object valor, String atributo) throws ArquitoException {
		validaAtributoInformado(atributo);
		try {
			Class<?> clazz = objeto.getClass();
			Field field = clazz.getDeclaredField(atributo);
			if (valor == null || field.getType().isInstance(valor)) {
				field.setAccessible(true);
				field.set(objeto, valor);
			} else {
				throw new IllegalArgumentException(String.format("Impossível atribuir valor. O atributo informado é do tipo : %s e valor passado é do tipo : %s", field.getType(), valor.getClass().getName()));
			}
		} catch (NoSuchFieldException e) {
			throw new ArquitoException(mensagemAtributoNaoFoiEncontradoNoObjeto(objeto, atributo));
		} catch (Exception e) {
			throw new ArquitoException(mensagemErroNaoTratado(e));
		}
	}

	public static boolean isNull(final Object obj) {
		return obj == null;
	}

	public static boolean isNotNull(final Object obj) {
		return !UtilArquito.isNull(obj);
	}

	@SuppressWarnings("rawtypes")
	public static Class obterTipo(final Object o, final String propriedade) throws Exception {
		try {
			return BeanUtilsBean.getInstance().getPropertyUtils().getPropertyType(o, propriedade);
		} catch (final NoSuchMethodException e) {
			throw new ArquitoException(mensagemAtributoNaoFoiEncontradoNoObjeto(o, propriedade));
		}
	}

	public static BigDecimal obterToBigDecimal(final Object o, final String propriedade) throws ArquitoException {
		try {
			Object valor = BeanUtilsBean.getInstance().getPropertyUtils().getProperty(o, propriedade);
			if (valor instanceof BigDecimal) {
				return (BigDecimal) valor;
			} else if (valor instanceof String) {
				return BigDecimal.valueOf(Double.valueOf((String) valor));
			} else if (valor instanceof Double) {
				return BigDecimal.valueOf((Double) valor);
			} else if (valor instanceof Number) {
				return BigDecimal.valueOf(((Number) valor).longValue());
			} else {
				throw new IllegalArgumentException("Tipo não suportado: " + valor.getClass().getName());
			}
		} catch (final Exception e) {
			throw new ArquitoException(e.getMessage());
		}
	}

	public static boolean isNotNull(final Object o, final String propriedade) {
		try {
			return UtilArquito.isNotNull(UtilArquito.getValorObjeto(o, propriedade));
		} catch (final Exception e) {
			return false;
		}
	}

	public static void definir(final Object o, final String propriedade, final Object valor) throws ArquitoException {
		try {
			BeanUtils.setProperty(o, propriedade, valor);
		} catch (final Exception e) {
			throw new ArquitoException(e.getMessage());
		}
	}

	public static void definir(final Object o, final String propriedade, final String valor) throws ArquitoException {
		try {
			Class<?> tipo = BeanUtilsBean.getInstance().getPropertyUtils().getPropertyType(o, propriedade);
			if (Date.class.equals(tipo)) {

				definir(o, propriedade, new Date(Long.parseLong(valor)));

			} else if (Long.class.equals(tipo)) {

				UtilArquito.definir(o, propriedade, Long.parseLong(valor));

			} else if (BigDecimal.class.equals(tipo)) {

				definir(o, propriedade, BigDecimal.valueOf(Double.valueOf(valor)));

			} else if (Integer.class.equals(tipo)) {

				definir(o, propriedade, Integer.parseInt(valor));

			} else if (String.class.equals(tipo)) {

				definir(o, propriedade, (Object) valor);

			} else {

				throw new IllegalArgumentException("Tipo não suportado:" + tipo.getName());
			}
		} catch (Exception e) {

			throw new ArquitoException(e.getMessage());
		}
	}

	public static boolean isTrue(Boolean umBoolean) {
		return UtilArquito.isNotNull(umBoolean) && umBoolean;
	}

	public static boolean equals(Object a, Object b) {
		return UtilArquito.isNotNull(a) && UtilArquito.isNotNull(b) && a.equals(b);
	}

	private static String mensagemAtributoNaoFoiEncontradoNoObjeto(Object objeto, String atributo) {
		return String.format("O atributo : %s, não foi encontrado no objeto : %s", atributo, objeto.getClass().getName());
	}

	private static String mensagemEntidadeNaoInformada(String propriedade) {
		return String.format("O entidade informada : %s encontra-se nullo", propriedade);
	}

	private static String mensagemEntidadeNaoEncontradaNoObjeto(Object objetoInformado, Integer i) {
		return String.format("A entidade : %s não foi encontrada no objeto : %s", listaEntidades[i], objetoInformado.getClass().getName());
	}

	private static String mensagemErroNaoTratado(Exception e) {
		return e.getMessage();
	}

	private static void validaAtributoInformado(String... atributos) {
		if (atributos == null || atributos.length == 0) {
			throw new IllegalArgumentException("É obrigatório informar o atributo do objeto");
		}
	}

	private static void validaObjetoInformado(Object objeto) {
		if (objeto == null) {
			throw new IllegalArgumentException("O parametro objeto está nullo");
		}
	}

	private static void preencherListaEntidade(String entidade) {
		if (entidade.contains(".")) {
			listaEntidades = entidade.split("\\.");
		} else {
			listaEntidades = new String[] { entidade };
		}
	}

	public static void criarDiretorio(final String caminho) throws ArquitoException {
		try {
			if (caminho != null) {

				File file;
				file = new File(caminho);
				if (!file.isDirectory()) {
					file.mkdirs();
				}
			}
		} catch (Exception e) {
			throw new ArquitoException(e.getMessage());
		}
	}
	
	public static String retirarCaracteresEspeciais(String texto){
		String retorno = null;
		
		if(texto != null){
			retorno = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		}
		return retorno;
	}

	public static String somenteNumeros(String texto) {
		String retorno = StringUtils.EMPTY;

		if (texto != null) {

			retorno = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("[^0-9]", "");
		}

		return retorno;
	}
}
