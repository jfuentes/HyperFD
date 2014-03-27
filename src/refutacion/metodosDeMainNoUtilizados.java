    //OTRAS FUNCIONES NO UTILIZADAS

    //Funciones para buscar transversales en un hipergrafo
    public static void buscaTransversalesScherson(ArrayList<RowX> refutacionesPorConsecuente,LinkedList<BitSet> transversalesMinimales, int numAtributos){
        LinkedList<BitSet> repositorioTransversales = new LinkedList<BitSet>();

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
    private static void McCluskey(LinkedList<BitSet> repositorioTransversales, LinkedList<BitSet> transversalesMinimales,int numAtributos) {
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
    public static void buscaTransversalesScherson(int fil, int total, ArrayList<RowX> refutacionesPorConsecuente,LinkedList<BitSet> transversales, BitSet or) {
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
    public static void agregaTransversal(LinkedList<BitSet> transversales, BitSet candidata) {
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
    private static LinkedList<BitSet> buscaTransversalesKavvadias(ArrayList<RowX> refutacionesPorConsecuente,int numAtributos) {
        LinkedList<BitSet> array = new LinkedList<BitSet>();

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