package it.gt.tesi.compostinominali;

/**
 * La classe che contiene le informazioni su un dato autore, ovvero nome,
 * secolo di nascita e secolo di morte. 
 * Contiene getter/setter per le proprietà dell'autore.
 */
public class Autore {
	
	private String nome;
	private int secoloNascita;
	private int secoloMorte;
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public int getSecoloNascita() {
		return secoloNascita;
	}
	
	public void setSecoloNascita(int secoloNascita) {
		this.secoloNascita = secoloNascita;
	}
	
	public int getSecoloMorte() {
		return secoloMorte;
	}
	
	public void setSecoloMorte(int secoloMorte) {
		this.secoloMorte = secoloMorte;
	}

}
