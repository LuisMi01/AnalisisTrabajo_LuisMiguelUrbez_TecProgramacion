package GUI.GUIBlockchain.G.src.guiblockchain;

import java.security.MessageDigest;
import com.google.gson.GsonBuilder;

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
	
	//Se convierte Object en cadena json
	public static String obtenerJson(Object o) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(o);
	}
	
	//Devuelve la dificultad de la cadena destion para compararlo con el hash. Ej. La dificultad 5 devolver� "00000"  
	public static String obtenerCadenaDificultad(int dificultad) {
		return new String(new char[dificultad]).replace('\0', '0');
	}
	
	
}
