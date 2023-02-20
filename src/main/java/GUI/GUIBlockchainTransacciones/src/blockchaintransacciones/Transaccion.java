package GUI.GUIBlockchainTransacciones.src.blockchaintransacciones;
import java.security.*;
import java.util.ArrayList;

public class Transaccion {
	
	public String idTransaccion; //Contiene un hash de la transacci�n
	public PublicKey remitente; //Direcci�n/clave p�blica de los remitentes.
	public PublicKey destinatario; //Direcci�n/clave p�blica de los destinatarios.
	public float valor; //Contiene la cantidad que queremos enviar al destinatario.
	public byte[] firma; //Para prevenir que nadie m�s gaste dinero de nuestra catera.
	
	public ArrayList<EntradaTransaccion> entradas = new ArrayList<EntradaTransaccion>();
	public ArrayList<SalidaTransaccion> salidas = new ArrayList<SalidaTransaccion>();
	
	private static int secuencia = 0; //Una cuenta aproximada de la cantidad de transacciones que han sido generadas 
	
	// Constructor: 
	public Transaccion(PublicKey del, PublicKey al, float valor,  ArrayList<EntradaTransaccion> entradas) {
		this.remitente = del;
		this.destinatario = al;
		this.valor = valor;
		this.entradas = entradas;
	}
	
	public boolean procesarTransaccion() {
		
		if(verificarFirma() == false) {
			System.out.println("#La firma de la transaccion fall� en la verificacion");
			return false;
		}
				
		//Obtenci�n de las entradas de la transaccion (Asegur�ndose de que son sin gasta):
		for(EntradaTransaccion i : entradas) {
			i.UTXO = UTXOs.get(i.idSalidaTransaccion);
		}

		//Comprobaci�n si latransacci�n es v�lida:
		if(obtenerValorEntradas() < GUI.GUIBlockchainTransacciones.src.blockchaintransacciones.Principal.transaccionMinima) {
			System.out.println("Las entradas de la transacci�n son demasiado pequenas: " + obtenerValorEntradas());
			System.out.println("Por favor, introduzca una cantidad mayor a " + GUI.GUIBlockchainTransacciones.src.blockchaintransacciones.Principal.transaccionMinima);
			return false;
		}
		
		//Generaci�n de las salidas de la transacci�n:
		float dejadoSobre = obtenerValorEntradas() - valor; //Obtenci�n del valor de las entradas de lo que queda del cambio:
		idTransaccion = calcularHash();
		salidas.add(new SalidaTransaccion( this.destinatario, valor, idTransaccion)); //Env�o del valor al destinatario
		salidas.add(new SalidaTransaccion( this.remitente, dejadoSobre,idTransaccion)); //Devoluci�n de lo que queda del 'cambio' al remitente		
				
		//A�ade salidas a la listas sin gasto
		for(SalidaTransaccion o : salidas) {
			GUI.GUIBlockchainTransacciones.src.blockchaintransacciones.Principal.UTXOs.put(o.id , o);
		}
		
		//Elimina las enradas de lastransacciones de las listas UTXO com pagadas:
		for(EntradaTransaccion i : entradas) {
			if(i.UTXO == null) continue; //Si la transacci�n no se ha encontrado se omite 
			Principal.UTXOs.remove(i.UTXO.id);
		}
		
		return true;
	}
	
	public float obtenerValorEntradas() {
		float total = 0;
		for(EntradaTransaccion i : entradas) {
			if(i.UTXO == null) continue; //Si no se encuetra la transacci�n se omite. Este comportamiento podr�a no ser �ptimo.
			total += i.UTXO.valor;
		}
		return total;
	}
	
	public void generarFirma(PrivateKey clavePrivada) {
		String datos = Hash.obtenerCadenaDesdeClave(remitente) + Hash.obtenerCadenaDesdeClave(destinatario) + Float.toString(valor)	;
		firma = Hash.aplicarFirmaECDSA(clavePrivada,datos);		
	}
	
	public boolean verificarFirma() {
		String datos = Hash.obtenerCadenaDesdeClave(remitente) + Hash.obtenerCadenaDesdeClave(destinatario) + Float.toString(valor)	;
		return Hash.verificarFirmaECDSA(remitente, datos, firma);
	}
	
	public float obtenerValorSalidas() {
		float total = 0;
		for(SalidaTransaccion o : salidas) {
			total += o.valor;
		}
		return total;
	}
	
	private String calcularHash() {
		secuencia++; //Incremento de la secuencia para evitar que 2 transaciones id�nticas tengan el mismo hash
		return Hash.aplicarSha256(
				Hash.obtenerCadenaDesdeClave(remitente) +
				Hash.obtenerCadenaDesdeClave(destinatario) +
				Float.toString(valor) + secuencia
				);
	}

	private static class UTXOs {
		public static SalidaTransaccion get(String idSalidaTransaccion) {
			return null;
		}
	}
}
