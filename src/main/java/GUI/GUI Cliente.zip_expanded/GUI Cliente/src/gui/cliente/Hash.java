import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import com.google.gson.GsonBuilder;
import java.util.List;

public class Hash {
	
	//Aplicaci�n de Sha256 a la cadena y devoluci�n del resultado. 
	public static String aplicarSha256(String entrada){
		
		try {
			MessageDigest digerir = MessageDigest.getInstance("SHA-256");
	        
			//Aplicaci�n de sha256 a la entrada, 
			byte[] hash = digerir.digest(entrada.getBytes("UTF-8"));
	        
			StringBuffer cadenaHex = new StringBuffer(); // Contendr� el hash en hexadecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) cadenaHex.append('0');
				cadenaHex.append(hex);
			}
			return cadenaHex.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//Aplicaci�on de la firma ECDSA y devoluci�n del resultado en bytes.
	public static byte[] aplicarFirmaECDSA(PrivateKey clavePrivada, String entrada) {
		Signature dsa;
		byte[] salida = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(clavePrivada);
			byte[] cadenaByte = entrada.getBytes();
			dsa.update(cadenaByte);
			byte[] firmaReal = dsa.sign();
			salida = firmaReal;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return salida;
	}
	
	//Verificaci�n de la firmaECDSA 
	public static boolean verificarFirmaECDSA(PublicKey clavePublica, String datos, byte[] firma) {
		try {
			Signature verificarECDSA = Signature.getInstance("ECDSA", "BC");
			verificarECDSA.initVerify(clavePublica);
			verificarECDSA.update(datos.getBytes());
			return verificarECDSA.verify(firma);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//Convierte Object en cadena json
	public static String obtenerJson(Object o) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(o);
	}
	
	//Devoluci�n de dificultad de cadena detino para compararlo con el hash. Ej. la dificultad 5 devolver� "00000"  
	public static String obtenerCadenaDificultad(int dificultad) {
		return new String(new char[dificultad]).replace('\0', '0');
	}
	
	public static String obtenerCadenaDesdeClave(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	public static String obtenerRaizMerkle(ArrayList<Transaccion> transacciones) {
		int cuenta = transacciones.size();
		
		List<String> capaArbolAnterior = new ArrayList<String>();
		for(Transaccion transaccion : transacciones) {
			capaArbolAnterior.add(transaccion.idTransaccion);
		}
		List<String> capaArbol = capaArbolAnterior;
		
		while(cuenta > 1) {capaArbol = new ArrayList<String>();
			for(int i=1; i < capaArbolAnterior.size(); i+=2) {
				capaArbol.add(aplicarSha256(capaArbolAnterior.get(i-1) + capaArbolAnterior.get(i)));
			}
			cuenta = capaArbol.size();
			capaArbolAnterior = capaArbol;
		}
		
		String raizMerkle = (capaArbol.size() == 1) ? capaArbol.get(0) : "";
		return raizMerkle;
	}
}
