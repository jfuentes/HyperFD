package parallel;

import refutacion.*;
    import java.util.ArrayList;
    import java.util.Arrays;
    import java.util.HashMap;

    import com.csvreader.CsvReader;

    import java.io.BufferedReader;
    import java.io.BufferedWriter;

    import java.io.DataInputStream;
    import java.io.FileInputStream;
    import java.io.FileWriter;

    import java.io.IOException;

    import java.io.InputStream;
    import java.io.InputStreamReader;

    import java.util.BitSet;
    import java.util.Iterator;
    import java.util.List;

    import java.util.StringTokenizer;

    import utils.IteradorCombinacion;


public class ParallelProcess extends Thread {

    


    


    
        private static final int BERGE = 1;
        private static final int KAVVADIAS = 2;
        private static final int MURAKAMI = 3;
        
        private int [][]relacionCodificada;
        private int numAtributos;
            private int []elementos;
                        private int algoritmo;
                        private int consecuente;
                        private ParallelMain miMain;
                        
        private ArrayList<ArrayList<BitSet>> transversalesTotales;
        
        public ParallelProcess(int [][]relacionCodificada, int numAtributos, int []elementos, int algoritmo, int consecuente, ParallelMain miMain){
            this.relacionCodificada=relacionCodificada;
            this.numAtributos=numAtributos;
            this.elementos=elementos;
            this.algoritmo=algoritmo;
            this.consecuente=consecuente;
            this.miMain=miMain;
        }

        public void run() {
            System.out.println("Hilo Corriendo!");
            


            System.out.println();
            System.out.println("Analisis: Refutaciones maximales encontradas");


            long tiempoInicio = System.currentTimeMillis();
            long totalTiempo = 0;

            //obtenemos las refutaciones
            ArrayList<Refutacion> refutaciones =
                obtenerRefutaciones(relacionCodificada, numAtributos, consecuente);


            System.out.println("busqueda de refutaciones (cuadratico) ok");


            String s = "Ejecutando algoritmo de ";

            switch (algoritmo) {
            case BERGE:
                s += "BERGE";
                break;
            case KAVVADIAS:
                s += "KAVVADIAS";
                break;
            case MURAKAMI:
                s += "MURAKAMI";
                break;
            default:
                break;
            }
            System.out.println(s);
           


                Hipergrafo hg = new Hipergrafo(numAtributos, refutaciones);
                 System.out.println("\nHipergrafo maximal con consecuente: " + hg.getConsecuente());
                System.out.println(hg.toString()); 
                
                hg.toComplemento();

                /*   System.out.println("\nHipergrafo maximal con consecuente: " + hg.getConsecuente());
                System.out.println(hg.toString());  */


                ArrayList<BitSet> transversales = null;

                switch (algoritmo) {
                case BERGE:
                    //transversales = buscaTransversales(hg, elementos, permutaciones);

                    break;
                case KAVVADIAS:
                    transversales = buscaTransversalesKavvadias(hg, hg.getConsecuente());

                    break;
                case MURAKAMI:
                    transversales = buscaTransversalesMurakami(hg, hg.getConsecuente());
                    break;
                default:
                    System.err.println("Debe seleccionar algoritmo de Hipergrafos");
                }


                //System.out.println("DFs encontradas");
                /* for (BitSet bits : transversales) {
                    for (int bit = bits.nextSetBit(0); bit >= 0; bit = bits.nextSetBit(bit + 1)) {
                        System.out.print((bit+1) + ", ");
                    }
                    System.out.print("--> " + (hg.getConsecuente()+1) + "\n");
                    
                } */
                //contadorDF += transversales.size();

            miMain.setTransversales(consecuente,transversales);
           /*  totalTiempo =
                    (algoritmo == KAVVADIAS || algoritmo == MURAKAMI) ? System.currentTimeMillis() -
                    tiempoInicio - 100 : System.currentTimeMillis() - tiempoInicio;

            System.out.println("UN TOTAL DE " + contadorDF + " DFs encontradas");

            System.out.println("El tiempo de demora es :" + totalTiempo + " miliseg"); */

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

        //funcion que obtiene las refutaciones desde la relacion codificada

        public static ArrayList<Refutacion> obtenerRefutaciones(int[][] relacion, int nAtributos, int consecuente) {
            int numTuplas = relacion.length;
            

            
                ArrayList<Refutacion> refutacionesPorConsecuente = new ArrayList<Refutacion>();


                //busqueda cuadratica de refutaciones
                for (int i = 0; i < numTuplas; i++) { // por cada tupla
                    for (int j = i + 1; j < numTuplas; j++) {
                        if (i != j) {

                            if (relacion[i][consecuente] != relacion[j][consecuente]) { //encontro posibles refutaciones

                                //armar conjunto maximal de atributos
                                BitSet conjuntoRefutacionMaximal = new BitSet(nAtributos);

                                for (int atributo = 0; atributo < nAtributos; atributo++) {
                                    if (atributo!= consecuente && relacion[i][atributo] == relacion[j][atributo])
                                        conjuntoRefutacionMaximal.set(atributo);
                                }
                                
                                if(!conjuntoRefutacionMaximal.isEmpty()){
                                /* System.out.println("Refutacion maximal encontrada! en ["+i+"] y ["+j+"]");
                                conjuntoRefutacionMaximal.set(consecuente);
                                
                                 for (int bit = conjuntoRefutacionMaximal.nextSetBit(0); bit >= 0;
                                     bit = conjuntoRefutacionMaximal.nextSetBit(bit + 1)) {
                                    System.out.print(bit + ", ");
                                    

                                }  */
                                //conjuntoRefutacionMaximal.set(consecuente);
                               // System.out.println("-/-> " + consecuente);
                                
                                Refutacion refutacion = new Refutacion(conjuntoRefutacionMaximal, consecuente, nAtributos);
                                agregarRefutacion(refutacion, refutacionesPorConsecuente);
                                }
                            }

                        }

                    }
                

                    
                }
            
            
            
            return refutacionesPorConsecuente;

            
        }


        // funcion que genera todas las combinaciones posibles de antecedentes
        static boolean[] control = new boolean[100];

        public static void generarPermutacionNoSust(int[] elementos, String actual, int cantidad,
                                                    ArrayList<int[]> combinaciones) {
            if (cantidad == 0) {
                // Hacer con la secuencia generada
                //System.out.println(actual);
                int[] conjunto = new int[actual.length()];

                char[] arr = actual.toCharArray();

                for (int i = 0; i < arr.length; i++) {
                    conjunto[i] = Integer.parseInt(arr[i] + "");
                    System.out.print(conjunto[i] + ", ");
                }
                System.out.println();
                Arrays.sort(conjunto);


                boolean ban = false;

                for (int[] ints : combinaciones) {
                    if (Arrays.equals(ints, conjunto)) {
                        ban = true;
                        break;
                    }
                }
                if (!ban)
                    combinaciones.add(conjunto);

            } else {
                for (int i = 0; i < elementos.length; i++) {
                    if (control[i] == true)
                        continue;
                    control[i] = true;
                    generarPermutacionNoSust(elementos, actual + elementos[i], cantidad - 1, combinaciones);
                    control[i] = false;
                }
            }
        }

        public static void generarPermutaciones(List<Integer> elementos, int w, ArrayList<BitSet> conjuntoDePartes) {


            IteradorCombinacion it = new IteradorCombinacion(elementos, w);
            Iterator s = it.iterator();
            //ArrayList l2 = new ArrayList();

            while (s.hasNext()) {


                BitSet bs = new BitSet();
                // bs.set(bitIndex);
                List<Integer> list = (List<Integer>)s.next();
                for (Integer i : list)
                    bs.set(i);
                conjuntoDePartes.add(bs);

            }
            s = null;
            it = null;


        }


        //funcion que agrega una nueva refutacion considerando la maximalidad de esta y las que ya estan

        private static void agregarRefutacion(Refutacion refutacion, ArrayList<Refutacion> refutacionesPorConsecuente) {

            if (refutacionesPorConsecuente.size() == 0) {
                refutacionesPorConsecuente.add(refutacion);
                return;
            }

            //boolean ban = false, max = false, imax = false, addRef = false;
            int i = 0;
            while (i < refutacionesPorConsecuente.size()) {

                //System.out.println("al comparar "+refutacionesPorConsecuente.get(i)+" con "+refutacion+" obtengo"+refutacionesPorConsecuente.get(i).isSubSet(refutacion));
                if (refutacion.isSubSet(refutacionesPorConsecuente.get(i))){
                    //System.out.println("ya esta " +refutacion);  
                    return;
                }else {
                    if (refutacionesPorConsecuente.get(i).isSubSet(refutacion)) {
                        /*   System.out.println("saco " +
                                           refutacionesPorConsecuente.get(i) +
                                           " y coloco " + refutacion);   */
                        refutacionesPorConsecuente.remove(i);
                        refutacionesPorConsecuente.add(refutacion);
                        return;
                    } else {
                        i++;


                    }
                }


            }

            //System.out.print("SE ADD REF");
            refutacionesPorConsecuente.add(refutacion);

        }

        //funciones para buscar transversales en un hipergrafo

        private static ArrayList<BitSet> buscaTransversales(Hipergrafo hg, int[] elementos,
                                                            ArrayList<BitSet> permutaciones) {
            ArrayList<BitSet> transversales = new ArrayList<BitSet>();
            for (int m = 1; m < elementos.length; m++) {

                hg.verificaTransversal(permutaciones.get(m - 1), transversales);

            }


            return transversales;


        }


        private static int[] generaCombinacionAtributos(int[] elementos, int consec) {
            int[] m = new int[elementos.length - 1];
            int n = 0;
            int i = 0;
            while (i < m.length) {
                if (i == consec) {
                    n++;
                    m[i] = n;
                    i++;
                    n++;
                } else {
                    m[i] = n;
                    i++;
                    n++;

                }
            }


            /*  for (int j = 0; j < m.length; j++) {
                System.out.print(m[j] + ", ");
            }
            System.out.println();  */

            return m;
        }

        private static ArrayList<BitSet> buscaTransversalesKavvadias(Hipergrafo hipergrafo, int consecuente) {
            ArrayList<BitSet> array = new ArrayList<BitSet>();

            try {
                BufferedWriter outfile = new BufferedWriter(new FileWriter("transversales/hg"));
                outfile.write(hipergrafo.toFile());
                //System.out.println("GRAPH TO FILE \n"+hipergrafo.toFile());
                outfile.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            StringBuffer args = new StringBuffer();
            args.append("./transversales/kavv ");
            args.append(" -i " + " transversales/hg");


            //System.out.println("ejecutar " + args);

            try {
                // Se lanza el ejecutable.

                Process p = Runtime.getRuntime().exec(args.toString());

                // Se obtiene el stream de salida del programa
                InputStream is = p.getInputStream();

                /* Se prepara un bufferedReader para poder leer la salida más comodamente. */
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                // Se lee la primera linea
                String aux = br.readLine();

                // Mientras se haya leido alguna linea

                while (aux != null) {
                    // Se escribe la linea en pantalla
                    //System.out.println (aux);
                    if (aux.charAt(0) != '#') {
                        BitSet li = new BitSet(hipergrafo.getNumAtributos());
                        StringTokenizer stk = new StringTokenizer(aux, ",");
                        String s = "";
                        while (stk.hasMoreTokens()) {
                            s = stk.nextToken();

                            if (!s.equals("")) {
                                li.set(Integer.parseInt(s));

                            }
                        }
                        array.add(li);
                        // y se lee la siguiente.
                    }
                    aux = br.readLine();
                }
            } catch (Exception e) {
                // Excepciones si hay algún problema al arrancar el ejecutable o al leer su salida.*/
                e.printStackTrace();
            }

            return array;
        }

        private static ArrayList<BitSet> buscaTransversalesMurakami(Hipergrafo hipergrafo, int i) {
            ArrayList<BitSet> array = new ArrayList<BitSet>();

            try {
                BufferedWriter outfile = new BufferedWriter(new FileWriter("transversales/murakami/hg"));
                outfile.write(hipergrafo.toFileSHD());
                outfile.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            StringBuffer args = new StringBuffer();
            args.append("./transversales/murakami/shd ");
            args.append(" 0 " + " transversales/murakami/hg result.dat");


            //System.out.println("ejecutar " + args);


            try {
                // Se lanza el ejecutable.

                Process p = Runtime.getRuntime().exec(args.toString());

                // Mientras se haya leido alguna linea


            } catch (Exception e) {
                // Excepciones si hay algún problema al arrancar el ejecutable o al leer su salida.
                e.printStackTrace();
            }

            try {

                FileInputStream fstream = new FileInputStream("transversales/murakami/result.dat");
                // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                //Read File Line By Line
                while ((strLine = br.readLine()) != null) {
                    // Print the content on the console

                    BitSet li = new BitSet(hipergrafo.getNumAtributos());
                    StringTokenizer stk = new StringTokenizer(strLine, " ");
                    String s = "";
                    while (stk.hasMoreTokens()) {
                        s = stk.nextToken();

                        if (!s.equals(""))
                            li.set(Integer.parseInt(s));
                    }
                    array.add(li);
                }
                in.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return array;
        }
    }

