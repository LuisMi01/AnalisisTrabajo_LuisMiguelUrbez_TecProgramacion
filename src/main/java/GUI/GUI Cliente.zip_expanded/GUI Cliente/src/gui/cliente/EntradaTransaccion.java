import GUI.GUIBlockchainTransacciones.src.blockchaintransacciones.SalidaTransaccion;

public class EntradaTransaccion {
	public String idSalidaTransaccion; //Hace referencia a salidasTransacciones -> IdTransaccion
	public SalidaTransaccion UTXO; //Contiene la salida de la transacci�n sin gasto
	
	public EntradaTransaccion(String idSalidaTransaccion) {
		this.idSalidaTransaccion = idSalidaTransaccion;
	}
}
