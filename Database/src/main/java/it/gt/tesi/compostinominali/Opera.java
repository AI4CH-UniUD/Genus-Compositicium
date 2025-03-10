package it.gt.tesi.compostinominali;

/**
 * La classe che contiene le informazioni di un'opera, ovvero titolo,
 * genere, sottogenere e abbreviazione. 
 * Contiene getter/setter per le proprità di un'opera.
 */
public class Opera {
	
	private String titolo;
	private String genere;
	private String sottogenere;
	private String abbreviazione;
	
	public String getAbbreviazione() {
		return abbreviazione;
	}

	public void setAbbreviazione(String abbreviazione) {
		this.abbreviazione = abbreviazione;
	}

	public String getTitolo() {
		return titolo;
	}
	
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	
	public String getGenere() {
		return genere;
	}
	
	public void setGenere(String genere) {
		this.genere = genere;
	}
	
	public String getSottogenere() {
		return sottogenere;
	}
	
	public void setSottogenere(String sottogenere) {
		this.sottogenere = sottogenere;
	}

}
