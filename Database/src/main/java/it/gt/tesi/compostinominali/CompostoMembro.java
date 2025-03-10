package it.gt.tesi.compostinominali;

import org.apache.commons.lang3.StringUtils;

/**
 * Classe che contiene le proprietà lemma e categoria morfologica per un
 * composto nominale o un membro. Inoltre contiene l'originale greco dei 
 * grecismi.
 * Contiene getter/setter per le proprietà elencate.
 */
public class CompostoMembro {
	
	public String getOriginaleGreco() {
		return originaleGreco;
	}

	public void setOriginaleGreco(String originaleGreco) {
		this.originaleGreco = originaleGreco;
	}

	private String lemma;
	private String categoriaMorfologica;
	private String originaleGreco;
	
	public CompostoMembro(String lemma, String categoriaMorfologica) {
		this.lemma = lemma;
		this.categoriaMorfologica = categoriaMorfologica;
	}

	public String getLemma() {
		return lemma;
	}
	
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	
	public String getCategoriaMorfologica() {
		return categoriaMorfologica;
	}
	
	public void setCategoriaMorfologica(String categoriaMorfologica) {
		this.categoriaMorfologica = categoriaMorfologica;
	}
	
	/**
	 * Restituisce true se questo CompostoMembro è vuoto, ovvero se manca il lemma e
	 * manca la catergoria morfologica.
	 * 
	 * @return true se il CompostoMembro è vuoto, false altrimenti
	 */
	public boolean isEmpty() {
		return StringUtils.isEmpty(lemma) && 
				StringUtils.isEmpty(categoriaMorfologica);
	}

}
