package GUI.GUIBlockchainTransacciones.src.blockchaintransacciones;
import java.security.*;
import java.security.Principal;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Cartera {
	
	public PrivateKey clavePrivada;
	public PublicKey clavePublica;
	
	public static HashMap<String,SalidaTransaccion> UTXOs = new HashMap<String,SalidaTransaccion>();
	
	public Cartera() {
		generarParClaves();
	}
		
	public void generarParClaves() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
			SecureRandom aleatorio = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Inicializaci�n del generador de claves y generaci�n de par de claves
			keyGen.initialize(ecSpec, aleatorio); //256 
	        KeyPair parClaves = keyGen.generateKeyPair();
	        // Se asignan las claves p�blica y privada a partir del par de claves
	        clavePrivada = parClaves.getPrivate();
	        clavePublica = parClaves.getPublic();
	        
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public float obtenerSaldo() {
		float total = 0;
		for (Map.Entry<String, SalidaTransaccion> item: Cartera.UTXOs.entrySet()) {
        	SalidaTransaccion UTXO = item.getValue();
            if(UTXO.esMia(clavePublica)) { //Si la salida (monedas) me pertenece
            	UTXOs.put(UTXO.id,UTXO); //Se a�de a la lista de transacciones sin gasto.
            	total += UTXO.valor ; 
            }
        }  
		return total;
	}
	
	public Transaccion enviarDinero(PublicKey _destinatario,float valor ) {
		if(obtenerSaldo() < valor) {
			System.out.println("#No hay suficiente dinero para enviarr la transacci�n. Transacci�n descartada.");
			return null;
		}
		ArrayList<EntradaTransaccion> entradas = new ArrayList<EntradaTransaccion>();
		
		float total = 0;
		for (Map.Entry<String, SalidaTransaccion> item: UTXOs.entrySet()){
			SalidaTransaccion UTXO = item.getValue();
			total += UTXO.valor;
			entradas.add(new EntradaTransaccion(UTXO.id));
			if(total > valor) break;
		}
		
		Transaccion nuevaTransaccion = new Transaccion(clavePublica, _destinatario , valor, entradas);
		nuevaTransaccion.generarFirma(clavePrivada);
		
		for(EntradaTransaccion entrada: entradas){
			UTXOs.remove(entrada.idSalidaTransaccion);
		}
		
		return nuevaTransaccion;
	}

	private class UTXOs {
	}
}


