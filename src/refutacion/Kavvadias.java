package refutacion;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Kavvadias {
    public Kavvadias() {
        super();
        
        StringBuffer args = new StringBuffer();
        args.append("./transversales/kavv ");
        args.append(" -i "+" transversales/ok");
        
        
        System.out.println("ejecutar "+args);

        try
        {
        // Se lanza el ejecutable.
        Process p=Runtime.getRuntime().exec (args.toString());

        // Se obtiene el stream de salida del programa
        InputStream is = p.getInputStream();

        /* Se prepara un bufferedReader para poder leer la salida más comodamente. */
        BufferedReader br = new BufferedReader (new InputStreamReader (is));

        // Se lee la primera linea
        String aux = br.readLine();

        // Mientras se haya leido alguna linea
        while (aux!=null)
        {
        // Se escribe la linea en pantalla
        System.out.println (aux);

        // y se lee la siguiente.
        aux = br.readLine();
        }
        }
        catch (Exception e)
        {
        // Excepciones si hay algún problema al arrancar el ejecutable o al leer su salida.*/
        e.printStackTrace();
        } 
                                                                                        // program
   
        

       
    }
    
    public static void main(String [] a){
        new Kavvadias();
    }
}

