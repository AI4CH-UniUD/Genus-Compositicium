package it.gt.tesi.compostinominali;

import org.apache.commons.lang3.StringUtils;

/**
 * La classe che contiene le proprietà di un composto nominale, ovvero lemma,
 * tipologia, sottotipologia, l'originale greco e occorrenze del composto nominale nell'opera data.
 * Contiene getter/setter delle proprietà su elencate.
 */
public class Composto {
	
	private String lemma;
	private String categoriaMorfologica;
	private String tipologia;
	private String sottotipologia;
	private String originaleGreco;
	private int occorrenze;
	
	/**
	 * Restituisce true se mancano il lemma, la categoria morfologica, la tipologia e la sottotipologia;
	 * false se ci sono tutte e quattro.
	 * 
	 * @return true se manca il lemma, la catergoria morfologica, la tipologia e la sottotipologia; false altrimenti
	 */
	public boolean isEmpty() {
		return StringUtils.isEmpty(lemma) && StringUtils.isEmpty(categoriaMorfologica) && 
				StringUtils.isEmpty(tipologia) && StringUtils.isEmpty(sottotipologia);
	}
	
	public String getOriginaleGreco() {
		return originaleGreco;
	}

	public void setOriginaleGreco(String originaleGreco) {
		this.originaleGreco = originaleGreco;
	}

	@Override
	public String toString() {
		return "Composto [lemma=" + lemma + ", tipologia=" + tipologia + ", sottotipologia=" + sottotipologia
				+ ", occorrenze=" + occorrenze + "]";
	}

	public int getOccorrenze() {
		return occorrenze;
	}

	public void setOccorrenze(int occorrenze) {
		this.occorrenze = occorrenze;
	}

	public String getLemma() {
		return lemma;
	}
	
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	
	public String getTipologia() {
		return tipologia;
	}
	
	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}
	
	public String getSottotipologia() {
		return sottotipologia;
	}
	
	public void setSottotipologia(String sottotipologia) {
		this.sottotipologia = sottotipologia;
	}

	/**
	 * Restituisce true se il composto nominale ha tipologica "Grecism" e 
	 * sottotipologia "Gr", false altrimenti. I controlli sono case insensitive.
	 *  
	 * @return true se il Composto è un grecismo, false altrimenti
	 */
	public boolean isGrecismo() {
		return "Grecism".equalsIgnoreCase(tipologia) 
				&& "Gr".equalsIgnoreCase(sottotipologia);
	}

	public String getCategoriaMorfologica() {
		return categoriaMorfologica;
	}

	public void setCategoriaMorfologica(String categoriaMorfologica) {
		this.categoriaMorfologica = categoriaMorfologica;
	}

}
