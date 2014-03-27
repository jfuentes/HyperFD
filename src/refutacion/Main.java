
package refutacion;

import java.util.ArrayList;
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
        System.out.println("\nSearching Maximals Refutations (quadratic)...");
        long b2=0,srchRefTime=0,a2=System.currentTimeMillis();

        ArrayList<ArrayList<RowX>> refutaciones = new ArrayList<ArrayList<RowX>>(numAtributos);
        obtenerRefutaciones(relacionCodificada,refutaciones);

        b2=System.currentTimeMillis();
        srchRefTime=(b2-a2)/1000;
        System.out.println("Done.\t\t\t\t\t\t\t\t"+srchRefTime+" secs.");
        




        //////////////////////////////////////////////////////////////////////////////////
        //Searching Transversals
        String alg="MURAKAMI (Fijo)";
        /*switch (algoritmoTransversal){
            case SCHERSON: alg="SCHERSON"; break;
            case BERGE: alg="BERGE"; break;
            case KAVVADIAS: alg="KAVVADIAS"; break;
            case MURAKAMI: alg="MURAKAMI"; break;
            default: break;
        }*/
        System.out.println("\nSearching Transversals with Algorithm "+alg+"...");
        long b3=0,srchTransTime=0,a3=System.currentTimeMillis();

        int contadorDF=0,i=0;
        for (ArrayList<RowX> refutacionesPorConsecuente : refutaciones) {
            for (RowX refuta : refutacionesPorConsecuente) refuta.toComplemento();
            ArrayList<BitSet> transversales=buscaTransversalesMurakami(refutacionesPorConsecuente, numAtributos);
            //System.out.println(transversales.size()+" FD /t---> A["+i+"]");
            i++;
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
    private static int[][] codificaRelacion(String[][] relacion){//Funcion que codifica la relacion
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

                    //Building BitSet from two tuples s & t, if s[a]==t[a] --> bitset[a]=1
                    for (int atributo=numAtributos-1;atributo>=0;atributo--){
                        if(relacionCodificada[i][atributo]==relacionCodificada[j][atributo]){
                            parDeTuplas.set(atributo);
                        }
                    }

                    if (!parDeTuplas.isEmpty()){
                        for(int bit=parDeTuplas.nextClearBit(0); 0<=bit&&bit<numAtributos; bit=parDeTuplas.nextClearBit(bit+1)){
                            //System.out.println("Tupla["+i+","+j+"]: bit: "+bit+". Refutaciones: "+parDeTuplas);
                            agregarRefutacion(new RowX((BitSet)parDeTuplas.clone(), bit, numAtributos), refutaciones);
                        }
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


    private static ArrayList<BitSet> buscaTransversalesMurakami(ArrayList<RowX> refutacionesPorConsecuente,int numAtributos) {
        ArrayList<BitSet> array = new ArrayList<BitSet>();

        try {
            BufferedWriter outfile = new BufferedWriter(new FileWriter(System.getProperty("user.home")+"/HyperFD/transversales/murakami/hg"));
            String file = "";
            for (RowX refutacion : refutacionesPorConsecuente) file+=refutacion.toFileSHD();
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
        }catch (Exception e){
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
                while (stk.hasMoreTokens()){
                    s=stk.nextToken();
                    if (!s.equals("")) li.set(Integer.parseInt(s));
                }
                //System.out.println("DF : "+li);
                array.add(li);
            }
            in.close();
            fstream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return array;
    }
}
