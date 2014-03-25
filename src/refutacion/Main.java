
package refutacion;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.StringTokenizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.csvreader.CsvReader;
import de.htwdd.ga.util.BitSetUtil;

//Main Ordenado Y listo para subir
//LOL
public class Main {
    private static final int SCHERSON = 0;
    private static final int BERGE = 1;
    private static final int KAVVADIAS = 2;
    private static final int MURAKAMI = 3;

    public Main() {}

    //Compilation
    //javac -d "/home/username/HyperFD/classes" -classpath "/home/username/HyperFD/classes:/home/username/HyperFD/lib/javacsv.jar:/home/username/HyperFD/lib/ga-frame.jar" -encoding MacRoman -sourcepath "/home/username/HyperFD/src" -g -Xlint:all -Xlint:-cast -Xlint:-empty -Xlint:-fallthrough -Xlint:-path -Xlint:-processing -Xlint:-serial -Xlint:-unchecked "/home/username/HyperFD/src/refutacion/RowX.java" "/home/username/HyperFD/src/refutacion/TuplaCheck.java" "/home/username/HyperFD/src/refutacion/Antecedente.java" "/home/username/HyperFD/src/refutacion/Main.java" "/home/username/HyperFD/src/utils/IteradorCombinacion.java" "/home/username/HyperFD/src/refutacion/Hipergrafo.java" "/home/username/HyperFD/src/refutacion/Refutacion.java" "/home/username/HyperFD/src/refutacion/Prueba.java" "/home/username/HyperFD/src/refutacion/Kavvadias.java" "/home/username/HyperFD/src/refutacion/Consecuente.java"
    //Execution
    //java -client -classpath "/home/username/.adf:/home/username/HyperFD/classes:/home/username/HyperFD/lib/javacsv.jar:/home/username/HyperFD/lib/ga-frame.jar" refutacion.Main MURAKAMI train50X500.csv 50 500
    
    public static void main(String[] args) {
        //args[0] Algorithm. args[1] data source. args[2] # of attributes. args[3] # of tuples
        for(int z=0;z<150;z++) System.out.println();//Clear Screen





        //////////////////////////////////////////////////////////////////////////////////
        //Briefing
        if (args.length != 4){//If has been executed without or whit many parameters
            System.err.println("Error, you must execute the program with parameters: <[BERGE | KAVVADIAS | MURAKAMI | SCHERSON]>  <relation>  <number of attributes>  <number of tuples>");
            System.exit(0);
        }
        // Setting the chosen Algorithm
        int algoritmoTransversal = 0;
        if (args[0].equals("BERGE")) algoritmoTransversal = BERGE;
        else if (args[0].equals("KAVVADIAS")) algoritmoTransversal = KAVVADIAS;
        else if (args[0].equals("MURAKAMI"))  algoritmoTransversal = MURAKAMI;
        else if (args[0].equals("SCHERSON"))  algoritmoTransversal = SCHERSON;
        //Setting URL of Data Source, number of attributes and number of tuples
        String fuente=args[1];
        String nomRelacion = System.getProperty("user.home")+"/HyperFD/datasets/"+args[1];
        int numAtributos = Integer.parseInt(args[2]);
        int numTuplas = Integer.parseInt(args[3]);
        System.out.println("Datasource: "+fuente+".\n");





        //////////////////////////////////////////////////////////////////////////////////
        //Encoding
        System.out.print("Encoding...");
        long b1=0,encodTime=0,a1=System.currentTimeMillis();

        String[][] relacion = null;
        try {
            relacion = cargaRelacion(nomRelacion, numTuplas, numAtributos);
        } catch (Exception e) {
            System.out.println(e.toString());
            System.exit(0);
        }
        int[][] relacionCodificada = codificaRelacion(relacion);

        b1=System.currentTimeMillis();
        encodTime=(b1-a1)/1000;
        System.out.println("Done.\t\t\t\t\t\t\t\t"+encodTime+" secs.");





        //███████████████████████████████████████████████████████████████████████████████
        //Searching Maximals Refutations
        int contadorDF=0;
        System.out.println("\nSearching Maximals Refutations (quadratic)...");
        long b2=0,srchRefTime=0,a2=System.currentTimeMillis();

        ArrayList<ArrayList<RowX>> refutaciones = new ArrayList<ArrayList<RowX>>(numAtributos);
        obtenerRefutaciones(relacionCodificada,refutaciones);

        b2=System.currentTimeMillis();
        srchRefTime=(b2-a2)/1000;
        System.out.println("Done.\t\t\t\t\t\t\t\t"+srchRefTime+" secs.");
        




        //////////////////////////////////////////////////////////////////////////////////
        //Searching Transversals
        String alg="";
        switch (algoritmoTransversal){
            case SCHERSON: alg="SCHERSON"; break;
            case BERGE: alg="BERGE"; break;
            case KAVVADIAS: alg="KAVVADIAS"; break;
            case MURAKAMI: alg="MURAKAMI"; break;
            default: break;
        }
        System.out.println("\nSearching Transversals with Algorithm "+alg+"...");
        long b3=0,srchTransTime=0,a3=System.currentTimeMillis();

        for (ArrayList<RowX> refutacionesPorConsecuente : refutaciones) {
            for (RowX refuta : refutacionesPorConsecuente) {
                refuta.toComplemento();
            }
            ArrayList<BitSet> transversales = null;
            switch (algoritmoTransversal) {
                case SCHERSON:
                    transversales = new ArrayList<BitSet>();
                    buscaTransversalesScherson(refutacionesPorConsecuente, transversales, numAtributos);
                    break;
                case BERGE:
                    System.out.println("This Algorithm does not do anything :P");
                    break;
                case KAVVADIAS:
                    transversales = buscaTransversalesKavvadias(refutacionesPorConsecuente, numAtributos);
                    break;
                case MURAKAMI:
                    transversales = buscaTransversalesMurakami(refutacionesPorConsecuente, numAtributos);
                    break;
                default:
                    System.err.println("Debe seleccionar algoritmo de Hipergrafos");
            }
            contadorDF += transversales.size();
        }

        b3 = System.currentTimeMillis();
        srchTransTime=(b3-a3)/1000;
        System.out.println("Done.\t\t\t\t\t\t\t\t"+srchTransTime+" secs.");





        //////////////////////////////////////////////////////////////////////////////////
        //Showing & loging final Results
        System.out.println("\nIt has been found " + contadorDF + " FDs.\n\n\n");
        try {
            BufferedWriter outfile = new BufferedWriter(new FileWriter(System.getProperty("user.home")+"/HyperFD/logs", true));
            outfile.write("***************************************\n");
            outfile.write("Datasource:             "+fuente+".\n");
            outfile.write("Algotithm:              "+alg+".\n");
            outfile.write("FDs Found:              "+contadorDF+".\n");
            outfile.write("Encoding Time:          "+encodTime+" secs.\n");
            outfile.write("Srch Refutations Time:  "+srchRefTime+" secs.\n");
            outfile.write("Srch Transversals Time: "+srchTransTime+" secs.\n");
            outfile.write("***************************************\n\n");
            outfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////  METHODS  ////////////////////////////////////////

    public static String[][] cargaRelacion(String nomRelacion, int numTuplas, int numAtributos) throws Exception {
        CsvReader reader = null;
        int fil = 0;
        String[][] relacion = new String[numTuplas][numAtributos];

        // instancio el objeto readerCSV
        reader = new CsvReader(nomRelacion);
        // asigno separador de valores punto y coma, si no lo cambian queda por defecto la coma
        //reader.setDelimiter('\t'); //espacio en blanco
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

    //Funcion que codifica la relacion
    private static int[][] codificaRelacion(String[][] relacion){
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

        System.out.println("\nCodification numbers not shown");
        /*
        //mostrar la relacion codificada
        for (int i = 0; i < codificada.length; i++) {
            for (int k = 0; k < codificada[0].length; k++) {
                System.out.print(" "+codificada[i][k]);
            }
            System.out.println();

        }
        */


        return codificada;
    }

    //Funcion que obtiene las refutaciones desde la relacion codificada
    public static void obtenerRefutaciones(int[][] relacionCodificada,ArrayList<ArrayList<RowX>> refutaciones){
        int numAtributos=relacionCodificada[0].length;
        int numTuplas=relacionCodificada.length;

        for(int i=0;i<numAtributos;i++){
            refutaciones.add(i, new ArrayList<RowX>());
        }

        //busqueda cuadratica de refutaciones
        for(int i=0;i<numTuplas;i++){//i will be the index of the first tuple to compare
            for(int j=i+1;j<numTuplas;j++){//j start from the next tuple (i+1), it will be the index of the second tuple to compare
                if (i!=j){
                    BitSet parDeTuplas = new BitSet(numAtributos);

                    for (int atributo=numAtributos-1;atributo>=0;atributo--) {
                        if(
                            relacionCodificada[i][atributo]==relacionCodificada[j][atributo]
                        ) parDeTuplas.set(atributo);
                    }
                    if (!parDeTuplas.isEmpty())
                        for (int bit = parDeTuplas.nextClearBit(0); bit >= 0 && bit < numAtributos;
                             bit = parDeTuplas.nextClearBit(bit + 1)) {
                            //System.out.println("tupla [" + i + "," + j + "]: bit:" + bit + " refutaciones: " +
                            // parDeTuplas);
                            agregarRefutacion(new RowX((BitSet)parDeTuplas.clone(), bit, numAtributos), refutaciones);
                        }
                }
            }
            //Display percentage done
            float percent=(float)((i+1)*100)/numTuplas;
            System.out.print("\rSearching Refutations: "+percent+"%");
        }
        System.out.println();
    }

    //Funcion que agrega una nueva refutacion considerando la maximalidad de esta y las que ya estan
    private static void agregarRefutacion(RowX refutacion, ArrayList<ArrayList<RowX>> refutaciones){

        ArrayList<RowX> refutacionesPorConsecuente = refutaciones.get(refutacion.getConsecuente());
        if (refutacionesPorConsecuente.size() == 0) {
            refutacionesPorConsecuente.add(refutacion);
            return;
        }

        //boolean ban = false, max = false, imax = false, addRef = false;
        int i = 0;
        while (i < refutacionesPorConsecuente.size()) {

            //System.out.println("al comparar "+refutacionesPorConsecuente.get(i)+" con "+refutacion+" obtengo"+refutacionesPorConsecuente.get(i).isSubSet(refutacion));
            if (refutacion.isSubSet(refutacionesPorConsecuente.get(i))) {
                //System.out.println("ya esta " +refutacion);
                return;
            } else {
                if (refutacionesPorConsecuente.get(i).isSubSet(refutacion)) {
                    /* System.out.println("saco " +
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

        // System.out.println("SE ADD REF "+refutacion);
        refutacionesPorConsecuente.add(refutacion);
    }

    //Funciones para buscar transversales en un hipergrafo
    public static void buscaTransversalesScherson(ArrayList<RowX> refutacionesPorConsecuente,ArrayList<BitSet> transversalesMinimales, int numAtributos){
        ArrayList<BitSet> repositorioTransversales = new ArrayList<BitSet>();

        if (refutacionesPorConsecuente.size() <= 1) {
            if (refutacionesPorConsecuente.size() == 1)
                transversalesMinimales.add(refutacionesPorConsecuente.get(0).getX());
            return;
        } else {
            BitSet primera = refutacionesPorConsecuente.get(0).getX();
            BitSet segunda = refutacionesPorConsecuente.get(1).getX();
            for (int bit = segunda.nextSetBit(0); bit >= 0; bit = segunda.nextSetBit(bit + 1)) {
                for (int bit2 = primera.nextSetBit(0); bit2 >= 0; bit2 = primera.nextSetBit(bit2 + 1)) {
                    BitSet t = new BitSet(numAtributos);
                    t.set(bit);
                    t.set(bit2);
                    repositorioTransversales.add(t);
                }
            }

            int primerasTransversales = repositorioTransversales.size();

            BitSet refutacion, transv;
            for (int i = 2; i < refutacionesPorConsecuente.size(); i++) {
                refutacion = refutacionesPorConsecuente.get(i).getX();
                int contBit = 0;
                for (int bit = refutacion.nextSetBit(0); bit >= 0; bit = refutacion.nextSetBit(bit + 1)) {
                    contBit++;
                    if (refutacion.cardinality() == contBit) {
                        for (int j = 0; j < primerasTransversales; j++)
                            repositorioTransversales.get(j).set(bit);
                    } else {
                        for (int j = 0; j < primerasTransversales; j++) {

                            transv = repositorioTransversales.get(j);

                            BitSet t = (BitSet)transv.clone();
                            t.set(bit);
                            repositorioTransversales.add(t);

                        }
                    }
                }

            }
        }

        /* System.out.println("transversales totales:");
        for (BitSet tran : repositorioTransversales) {
            System.out.println(tran);
        } */

        McCluskey(repositorioTransversales, transversalesMinimales, numAtributos);
    }

    private static void McCluskey(ArrayList<BitSet> repositorioTransversales, ArrayList<BitSet> transversalesMinimales,int numAtributos) {
        try {
            BufferedWriter outfile = new BufferedWriter(new FileWriter("mccluskey/file_input"));
            String file = "";
            file += (numAtributos + 1) + "\n";
            //file+=repositorioTransversales.size()+"\n";
            //file+="*****\n";
            for (BitSet refutacion : repositorioTransversales)
                file += BitSetUtil.bitSetToInt(refutacion, 0, 32) + "\n";
            outfile.write(file);
            //System.out.println("GRAPH TO FILE \n"+hipergrafo.toFile());
            outfile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuffer args = new StringBuffer();
        args.append("./mccluskey/quine_mc_cluskey_transversals");
        args.append(" mccluskey/file_input");


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

            while (aux != null && aux.length() > 0) {
                // Se escribe la linea en pantalla
                //System.out.println (aux);
                BitSet li = new BitSet(numAtributos);
                BitSetUtil.intToBitSet(li, 0, numAtributos + 1, Integer.parseInt(aux));

                transversalesMinimales.add(li);
                // y se lee la siguiente.

                aux = br.readLine();
            }
        } catch (Exception e) {
            // Excepciones si hay algún problema al arrancar el ejecutable o al leer su salida.*/
            e.printStackTrace();
        }
    }

    public static void buscaTransversalesScherson(int fil, int total, ArrayList<RowX> refutacionesPorConsecuente,ArrayList<BitSet> transversales, BitSet or) {
        boolean anterior = false;
        if (fil == total) {
            //System.out.println("transversal: "+or);
            agregaTransversal(transversales, or);
            return;
        } else {
            BitSet hiperarista = refutacionesPorConsecuente.get(fil).getX();
            for (int bit = hiperarista.nextSetBit(0); bit >= 0; bit = hiperarista.nextSetBit(bit + 1)) {
                //System.out.println("verificalndo nivel "+fil+" con bit "+bit);
                if (or.get(bit))
                    anterior = true;
                else
                    or.set(bit);
                buscaTransversalesScherson(fil + 1, total, refutacionesPorConsecuente, transversales, or);
                if (!anterior)
                    or.set(bit, false);
                anterior = false;
            }
        }
    }

    public static void agregaTransversal(ArrayList<BitSet> transversales, BitSet candidata) {
        BitSet resp1, resp2, resp3;
        if (transversales.size() == 0)
            transversales.add((BitSet)candidata.clone());
        else
            for (int i = 0; i < transversales.size(); i++) {

                resp1 = (BitSet)transversales.get(i).clone();
                resp2 = (BitSet)transversales.get(i).clone();
                resp1.and(candidata);
                resp3 = (BitSet)resp1.clone();
                resp3.xor(resp2);
                if (resp3.isEmpty()) { // si A subset b
                    return;

                } else { //si candata es subconjunto de lo que tengo
                    resp1.xor(candidata);
                    if (resp1.isEmpty()) { //Si b subset A
                        //System.out.println("candidata "+candidata+" es subconjunto de "+transversales.get(i)+", se intercambian" );
                        transversales.remove(i);

                        while (i < transversales.size()) {
                            resp1 = (BitSet)transversales.get(i).clone();
                            resp2 = (BitSet)transversales.get(i).clone();
                            resp1.and(candidata);

                            resp1.xor(candidata);
                            if (resp1.isEmpty()) {
                                //System.out.println("Elimine otro subconjunto de candidata");
                                transversales.remove(i);

                            } else
                                i++;
                        }

                        transversales.add((BitSet)candidata.clone());

                        return;
                    }
                }


            }
        //System.out.println("Se agrega transversal "+candidata);
        transversales.add((BitSet)candidata.clone());
        return;
    }

    private static ArrayList<BitSet> buscaTransversalesKavvadias(ArrayList<RowX> refutacionesPorConsecuente,int numAtributos) {
        ArrayList<BitSet> array = new ArrayList<BitSet>();

        try {
            BufferedWriter outfile = new BufferedWriter(new FileWriter(System.getProperty("user.home")+"/HyperFD/transversales/hg"));
            String file = "";
            for (RowX refutacion : refutacionesPorConsecuente)
                file += refutacion.toFile();
            outfile.write(file);
            //System.out.println("GRAPH TO FILE \n"+hipergrafo.toFile());
            outfile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuffer args = new StringBuffer();
        args.append(System.getProperty("user.home")+"/HyperFD/transversales/./kavv ");
        args.append(" -i " + System.getProperty("user.home")+"/HyperFD/transversales/hg");


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

            while (aux != null && aux.length() > 0) {
                // Se escribe la linea en pantalla
                //System.out.println (aux);
                if (aux.charAt(0) != '#') {
                    BitSet li = new BitSet(numAtributos);
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
	    is.close();
        } catch (Exception e) {
            // Excepciones si hay algún problema al arrancar el ejecutable o al leer su salida.*/
            e.printStackTrace();
        }

        return array;
    }

    private static ArrayList<BitSet> buscaTransversalesMurakami(ArrayList<RowX> refutacionesPorConsecuente, int numAtributos) {
        ArrayList<BitSet> array = new ArrayList<BitSet>();

        try {
            BufferedWriter outfile = new BufferedWriter(new FileWriter(System.getProperty("user.home")+"/HyperFD/transversales/murakami/hg"));
            String file = "";
            for (RowX refutacion : refutacionesPorConsecuente)
                file += refutacion.toFileSHD();
            outfile.write(file);
            outfile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuffer args = new StringBuffer();
        args.append(System.getProperty("user.home")+"/HyperFD/transversales/murakami/./shd");
        args.append(" D " + System.getProperty("user.home")+"/HyperFD/transversales/murakami/hg /home/onlycparra/HyperFD/transversales/murakami/result.dat");


        //System.out.println("ejecutar " + args);


        try {
            // Se lanza el ejecutable.

            Process p = Runtime.getRuntime().exec(args.toString());
	    p.waitFor();
            // Mientras se haya leido alguna linea


        } catch (Exception e) {
            // Excepciones si hay algún problema al arrancar el ejecutable o al leer su salida.
            e.printStackTrace();
        }

        try {

            FileInputStream fstream = new FileInputStream(System.getProperty("user.home")+"/HyperFD/transversales/murakami/result.dat");
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console

                BitSet li = new BitSet(numAtributos);
                StringTokenizer stk = new StringTokenizer(strLine, " ");
                String s = "";
                while (stk.hasMoreTokens()) {
                    s = stk.nextToken();

                    if (!s.equals(""))
                        li.set(Integer.parseInt(s));
                }
                //System.out.println("DF : "+li);
                array.add(li);
            }
            in.close();
	    fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return array;
    }
}
