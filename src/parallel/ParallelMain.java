package parallel;

import com.csvreader.CsvReader;

import java.util.ArrayList;
import java.util.BitSet;

import java.util.HashMap;

import refutacion.Hipergrafo;
import refutacion.Refutacion;

public class ParallelMain {
    private static final int BERGE = 1;
    private static final int KAVVADIAS = 2;
    private static final int MURAKAMI = 3;
    
    private ArrayList<ArrayList<BitSet>> transversalesTotales;
    String nomRelacion="prueba.data";
    int numAtributos = 6;
    int numTuplas = 7;
    
    public ParallelMain() {
        transversalesTotales= new ArrayList<ArrayList<BitSet>>();
        System.out.println("Trans tam "+transversalesTotales.size());
        go();
    }
    
    public void go() {
        /*  if(args.length!=3){
            System.err.println("Error, debe ejecutar ingresando -algoritmo -numAtributos -numTuplas");
            System.exit(0);
        } */
        int algoritmoTransversal = KAVVADIAS;
        /* if(args[0].equals("BERGE")) algoritmoTransversal = BERGE;
        else if(args[0].equals("KAVVADIAS") ) algoritmoTransversal = KAVVADIAS;
         */
        

        
        
        
        int[] elementos = new int[numAtributos];

        for (int i = 0; i < numAtributos; i++) {
            elementos[i] = i;
        }


        


        int fil = 0;
        String[][] relacion=null;
        
        try {
            relacion = cargaRelacion(nomRelacion,numTuplas, numAtributos);
        } catch (Exception e) {
        }
        System.out.println("Relacion codificada");


        int[][] relacionCodificada = codificaRelacion(relacion);

        
        
        int contadorDF = 0;
        


        System.out.println();
        System.out.println("Analisis: Refutaciones maximales encontradas");


        long tiempoInicio = System.currentTimeMillis();
        long totalTiempo = 0;

        
        for (int consecuente = numAtributos - 1; consecuente >= 0; consecuente--) {
        
           ParallelProcess p=new ParallelProcess(relacionCodificada, numAtributos, elementos, algoritmoTransversal ,consecuente, this);
            Thread thread = new Thread(p);
            thread.run();
        }
        
        for(ArrayList<BitSet> transv: transversalesTotales)
                                          contadorDF+=transv.size();
        
        totalTiempo =
                (algoritmoTransversal == KAVVADIAS || algoritmoTransversal == MURAKAMI) ? System.currentTimeMillis() -
                tiempoInicio - 100 : System.currentTimeMillis() - tiempoInicio;

        System.out.println("UN TOTAL DE " + contadorDF + " DFs encontradas");

        System.out.println("El tiempo de demora es :" + totalTiempo + " miliseg");
}

    public static String[][] cargaRelacion(String nomRelacion, int numTuplas, int numAtributos)throws Exception{
        CsvReader reader = null;
        int fil = 0;
        String[][] relacion = new String[numTuplas][numAtributos];
        
            // instancio el objeto readerCSV
            reader = new CsvReader(nomRelacion);
            // asigno separador de valores punto y coma, si no lo cambian queda por defecto la coma
            reader.setDelimiter(',');
            // recorremos las filas del fichero
            while (reader.readRecord() && fil < numTuplas) {
                for (int i = 0; i < numAtributos; i++) {
                    relacion[fil][i] = reader.get(i);
                }

                fil++;
            } // end while - recorrido del csv
            
            return relacion;
       
    }

    //funcion que codifica la relacion

    private static int[][] codificaRelacion(String[][] relacion) {
       // Vector v = new Vector();
        int[][] codificada = new int[relacion.length][relacion[0].length];
        int j = 0;
        while (j < relacion[0].length) {


            int codigo = 0;
            HashMap<String, Integer> atr1 = new HashMap<String, Integer>();
            for (int i = 0; i < relacion.length; i++) {
                if (!atr1.containsKey(relacion[i][j])) {
                    atr1.put(relacion[i][j], ++codigo);

                }
                codificada[i][j] = atr1.get(relacion[i][j]);

            }
           // v.addElement(atr1);
            j++;
        }
        //liberar memoria hashmp
        //v.removeAllElements();

        //mostrar la relacion codificada
        for (int i = 0; i < codificada.length; i++) {
            for (int k = 0; k < codificada[0].length; k++) {
                System.out.print("  " + codificada[i][k]);
            }
            System.out.println();

        }


        return codificada;
    }

    void setTransversales(int i, ArrayList<BitSet> bitSet) {
       // System.out.println("Agregando transversales de "+i+"  en arreglo de tam "+transversalesTotales.size());
        this.transversalesTotales.add(bitSet);
    }
}
