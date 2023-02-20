package GUI.GUIBlockchain.G.src.guiblockchain;

import java.util.Date;

public class Bloque {
	
	public String hash;
	public String hashAnterior; 
	private String datos; //Los datos ser�n un mensaje.
	private long timeStamp; //En milisegundos.
	private int nonce;
	
	//Constructor.  
	public Bloque(String datos,String hashAnterior) {
		this.datos = datos;
		this.hashAnterior = hashAnterior;
		this.timeStamp = new Date().getTime();
		
		this.hash = calcularHash(); //Nos aseguramos de que hacemos esto tras establecer el resto de valores.
	}
	
	//C�lculo de nuevo hash basado en el contenido de los bloques
	public String calcularHash() {
		String hashCalculado = Hash.aplicarSha256( 
				hashAnterior +
				Long.toString(timeStamp) +
				Integer.toString(nonce) + 
				datos 
				);
		return hashCalculado;
	}
	
	//Incrementa el valor de nonce hasta que se alcanza el hash destino.
	public void minarBloque(int dificultad) {
		String destino = Hash.obtenerCadenaDificultad(dificultad); //Creaci�n de una cadena con dificultad * "0" 
		while(!hash.substring( 0, dificultad).equals(destino)) {
			nonce ++;
			hash = calcularHash();
		}
		System.out.println("���Bloque minado!!! : " + hash);
	}
	
}
