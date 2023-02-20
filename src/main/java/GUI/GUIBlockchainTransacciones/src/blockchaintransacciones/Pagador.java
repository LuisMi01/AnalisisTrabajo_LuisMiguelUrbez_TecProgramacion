package GUI.GUIBlockchainTransacciones.src.blockchaintransacciones;

import java.util.ArrayList;
import java.util.HashMap;

public class Pagador extends Thread {
	
	private float saldo;
	private float importe;
	private float cantidad;
	private Cartera carteraPagador;
	private Cartera carteraRecaudador;
	private Cartera euro;
	private static Transaccion transaccionOrigen;
	private static ArrayList<Bloque> cadenaBloques;
	private HashMap<String,SalidaTransaccion> UTXOs;
	private HashMap<String,SalidaTransaccion> tempUTXOs;
	private static int dificultad;
	
	public Pagador(float saldo, float importe, float cantidad, Cartera carteraPagador, Cartera carteraRecaudador, Cartera euro, ArrayList<Bloque> cadenaBloques, HashMap<String,SalidaTransaccion> UTXOs, int dificultad) {
		this.saldo = saldo;
		this.importe = importe;
		this.cantidad = cantidad;
		this.carteraPagador = carteraPagador;
		this.carteraRecaudador = carteraRecaudador;
		this.euro = euro;
		this.cadenaBloques = cadenaBloques;
		this.UTXOs = UTXOs;
		this.dificultad = dificultad;
	}
	
	public void run() {
		
		anadirDinero(saldo);
		pagarImpuesto(cantidad);
		
	}
	
	public void anadirDinero(float saldo) {
		transaccionOrigen = new Transaccion(euro.clavePublica, carteraPagador.clavePublica, saldo, null);
		transaccionOrigen.generarFirma(euro.clavePrivada);	 //Se firma la transaccion origen manualmente	
		transaccionOrigen.idTransaccion = "0"; //Se asigna el id de la transacci�n manualmente
		transaccionOrigen.salidas.add(new SalidaTransaccion(transaccionOrigen.destinatario, transaccionOrigen.valor, transaccionOrigen.idTransaccion)); //Se a�ade manualmente la salida de las transacciones
		UTXOs.put(transaccionOrigen.salidas.get(0).id, transaccionOrigen.salidas.get(0)); //Es importante almacenar la primera transacci�n en la lista de UTXOs.*/

	}
	
	public void pagarImpuesto(float cantidad) {
		
		System.out.println("Creando y minando el bloque origen... ");
		Bloque origen = new Bloque("0");
		origen.anadirTransaccion(transaccionOrigen);
		anadirBloque(origen);
		Bloque bloque1 = new Bloque(origen.hash);

		if (cantidad<saldo) {
			
			if (cantidad==importe) {
				
				System.out.println("\nEl saldo de la cartera del pagador es de: " + carteraPagador.obtenerSaldo() + " �");
				System.out.println("\nEl pagador entrega la cantidad de " + cantidad + " � al recaudador...");
				bloque1.anadirTransaccion(carteraPagador.enviarDinero(carteraRecaudador.clavePublica, cantidad));
				anadirBloque(bloque1);
				System.out.println("\nEl saldo de la cartera de pagador es de: " + carteraPagador.obtenerSaldo() + " �");
				System.out.println("El saldo de la cartera del recaudador es de: " + carteraRecaudador.obtenerSaldo() + " �");
				
			} else if (cantidad>importe) {
				
				System.out.println("\nEl saldo de la cartera del pagador es de: " + carteraPagador.obtenerSaldo() + " �");
				System.out.println("\nEl pagador entrega la cantidad de " + cantidad + " � al recaudador...");
				bloque1.anadirTransaccion(carteraPagador.enviarDinero(carteraRecaudador.clavePublica, cantidad));
				anadirBloque(bloque1);
				System.out.println("\nEl saldo de la cartera de pagador es de: " + carteraPagador.obtenerSaldo() + " �");
				System.out.println("El saldo de la cartera del recaudador es de: " + carteraRecaudador.obtenerSaldo() + " �");
				
				float cambio = cantidad-importe;
				
				Bloque bloque2 = new Bloque(bloque1.hash);
				System.out.println("\nEl recaudador devuelve " + cambio + " � al pagador...");
				bloque2.anadirTransaccion(carteraRecaudador.enviarDinero(carteraPagador.clavePublica, cambio));
				System.out.println("\nEl saldo de la cartera del pagador es de: " + carteraPagador.obtenerSaldo() + " �");
				System.out.println("El saldo de la cartera del recaudador es de: " + carteraRecaudador.obtenerSaldo() + " �");
				
			}
			
		} else {
			
			Bloque bloque2 = new Bloque(bloque1.hash);
			System.out.println("\nEl pagador quiere entregar m�s dinero del que tiene...");
			bloque2.anadirTransaccion(carteraPagador.enviarDinero(carteraRecaudador.clavePublica, cantidad));
			anadirBloque(bloque2);
			System.out.println("\nEl saldo de la cartera del pagador es de: " + carteraPagador.obtenerSaldo() + " �");
			System.out.println("El saldo de la cartera del recauadador es de: " + carteraRecaudador.obtenerSaldo() + " �");
			
		}
			
	}

	public static Boolean esValidaCadena() {
		Bloque bloqueActual; 
		Bloque bloqueAnterior;
		String hashDestino = new String(new char[dificultad]).replace('\0', '0');
		HashMap<String,SalidaTransaccion> tempUTXOs = new HashMap<String,SalidaTransaccion>(); //Una lista funcional de transacciones sin gasto en el estado de un bloque dado.
		tempUTXOs.put(transaccionOrigen.salidas.get(0).id, transaccionOrigen.salidas.get(0));
		
		//Recorrido de la cadena de bloques para comprobar los hashes:
		for(int i=1; i < cadenaBloques.size(); i++) {
			
			bloqueActual = cadenaBloques.get(i);
			bloqueAnterior = cadenaBloques.get(i-1);
			//Comparaci�n del hash registrado con el calculado:
			if(!bloqueActual.hash.equals(bloqueActual.calcularHash()) ){
				System.out.println("#Los hashes actuales no son iguales");
				return false;
			}
			//Comparaci�n del hash anterior con el registrado
			if(!bloqueAnterior.hash.equals(bloqueActual.hashAnterior) ) {
				System.out.println("#Los hashes anteriores no son iguales");
				return false;
			}
			//Comprobar si el hash se ha resuelto
			if(!bloqueActual.hash.substring( 0, dificultad).equals(hashDestino)) {
				System.out.println("#Este bloque no ha sido minado");
				return false;
			}
			
			//Recorrido de las transacciones de la cadena de bloques:
			SalidaTransaccion tempSalida;
			for(int t=0; t <bloqueActual.transacciones.size(); t++) {
				Transaccion transaccionActual = bloqueActual.transacciones.get(t);
				
				if(!transaccionActual.verificarFirma()) {
					System.out.println("#La firma de la transacci�n(" + t + ") es inv�lida");
					return false; 
				}
				if(transaccionActual.obtenerValorEntradas() != transaccionActual.obtenerValorSalidas()) {
					System.out.println("#Las entradas de la transaccion (" + t + ") no son iguales a las salidas");
					return false; 
				}
				
				for(EntradaTransaccion entrada: transaccionActual.entradas) {	
					tempSalida = tempUTXOs.get(entrada.idSalidaTransaccion);
					
					if (tempSalida == null) {
						System.out.println("#Falta la entrada referenciada de la transacci�n (" + t + ")");
						return false;
					}
					
					if (entrada.UTXO.valor != tempSalida.valor) {
						System.out.println("#El valor de la enrada referenciada de la transacci�n (" + t + ") es inv�lido");
						return false;
					}
					
					tempUTXOs.remove(entrada.idSalidaTransaccion);
				}
				
				for (SalidaTransaccion salida: transaccionActual.salidas) {
					tempUTXOs.put(salida.id, salida);
				}
				
				if (transaccionActual.salidas.get(0).destinatario != transaccionActual.destinatario) {
					System.out.println("#El destinatario de la salida de la transaccion(" + t + ") no es quien deber�a ser.");
					return false;
				}
			
				if (transaccionActual.salidas.get(1).destinatario != transaccionActual.destinatario) {
					System.out.println("#El 'cambio' de la salida de la transacci�n(" + t + ") no es del remitente.");
					return false;
				}
				
			}
			
		}
		System.out.println("La cadena de bloques no es v�lida");
		return true;
	}
	
	private void anadirBloque(Bloque nuevoBloque) {
		nuevoBloque.minarBloque(dificultad);
		cadenaBloques.add(nuevoBloque);
	}

				
}


