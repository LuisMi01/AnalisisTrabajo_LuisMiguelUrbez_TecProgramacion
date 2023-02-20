import GUI.GUIBlockchainTransacciones.src.blockchaintransacciones.Hash;
import GUI.GUIBlockchainTransacciones.src.blockchaintransacciones.Transaccion;

import java.util.ArrayList;
import java.util.Date;

public class Bloque {
	
	public String hash;
	public String hashAnterior; 
	public String raizMerkle;
	public ArrayList<Transaccion> transacciones = new ArrayList<Transaccion>(); //Los datos ser�n un mensaje.
	public long timeStamp; //En milisegundos.
	public int nonce;
	
	//Constructor.  
	public Bloque(String hashAnterior ) {
		this.hashAnterior = hashAnterior;
		this.timeStamp = new Date().getTime();
		
		this.hash = calcularHash(); //Nos aseguramos de hacer esto tras asignar el resto de valores.
	}
	
	//C�lculo del nuevo hash basado en el contenido de los bloques
	public String calcularHash() {
		String hashCalculado = Hash.aplicarSha256(
				hashAnterior +
				Long.toString(timeStamp) +
				Integer.toString(nonce) + 
				raizMerkle
				);
		return hashCalculado;
	}
	
	//Incrementa el valor de nonce hasta que se alcanza el hash destino.
	public void minarBloque(int dificultad) {
		raizMerkle = Hash.obtenerRaizMerkle(transacciones);
		String destino = Hash.obtenerCadenaDificultad(dificultad); //Crea una cadena con dificultad * "0" 
		while(!hash.substring( 0, dificultad).equals(destino)) {
			nonce ++;
			hash = calcularHash();
		}
		System.out.println("���Bloque minado!!! : " + hash);
	}
	
	//Se a�aden las transacciones al bloque
	public boolean anadirTransaccion(Transaccion transaccion) {
		//Procesamiento de la transaccion y comprobaci�n de su validez, si no es el bloque origen se ignora.
		if(transaccion == null) return false;		
		if((!"0".equals(hashAnterior))) {
			if((transaccion.procesarTransaccion() != true)) {
				System.out.println("La transacci�n fall� al procesar. Descartada.");
				return false;
			}
		}

		transacciones.add(transaccion);
		System.out.println("La transacci�n de a�adi� satisfactoriamente al bloque");
		return true;
	}
	
}
