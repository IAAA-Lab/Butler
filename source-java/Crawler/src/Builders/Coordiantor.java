/**
 * Autor: Iñigo Alonso Ruiz
 * Quality supervised by: F.J. Lopez Pellicer
 */

package Builders;

import YalmReader.*;

public class Coordiantor {

	public static void main(String[] args) {
		Configuration config = YamlConfigRunner.read(args[0]);
		System.out.println(config.toString());
		/*
		 * Creates a dockerfile and configuration files for 
		 * the crawl system 
		 */
		
		/*
		 * Si no se he insertado valor en alguna configuracion saldrá el valor null
		 * comprbarlo al hacer los ficheros de salida
		 * 
		 * 
		 * comprobar que todos los valores son del tipo y rango esperado
		 * 
		 * un adaptador intermedio
		 */
	}
}
