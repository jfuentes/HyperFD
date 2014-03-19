package refutacion;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class Hipergrafo {
  
   private int numAtributos;
    private int consecuente;
    private  BitSet [] hiperaristasBit;

    public Hipergrafo() {
        super();
    }
    
    public Hipergrafo(int numAtributos) {
        this.numAtributos=numAtributos;
    }

    public Hipergrafo(int nAtributos, ArrayList<Refutacion> refutaciones) {
        
       this.numAtributos= nAtributos;
       
        int fil = 0;
     
        
        if(refutaciones.size()>0)
        this.consecuente=refutaciones.get(0).getConsecuente();
        
        
        //nuevo enfoque
        hiperaristasBit= new BitSet[refutaciones.size()];
       
        
        fil=0;
        for (Refutacion refutacion: refutaciones) {
            
                
                hiperaristasBit[fil]=refutacion.getX();
                
            
            
            fil++;

        }
        
         //System.out.println("Hiper: \n"+this.toString()+ " "); 
        
    }

   

    //no muy necesaria
   /*  Hipergrafo(int numAtributos,ArrayList<Refutacion> refutaciones) {
        hiperaristas = new int[refutaciones.size()][numAtributos];
        int fil = 0,i=0;


        for (Refutacion refutacion : refutaciones) {
            TreeSet antecedente = refutacion.getX().getTreeSet();
            Iterator ir =antecedente.iterator();
            i=0;
            while(ir.hasNext()){
                hiperaristas[fil][i] = (Integer)ir.next();
                i++;
            }
            
            fil++;

        }
    } */

    public String toString() {
         /* String s = new String();
        for (int i = 0; i < hiperaristas.length; i++) {
            for (int j = 0; j < hiperaristas[0].length; j++) {
                s += hiperaristas[i][j] + " ";
            }
            s += "\n";
        }
        return s;  */
        String s="";
        for(int i=0; i<hiperaristasBit.length; i++)
            s+=hiperaristasBit[i].toString()+"\n";
        
        return s; 

    }

    public void verificaTransversal(BitSet conjuntoDePartes,
                                    ArrayList<BitSet> transversales) {
        
        for(int i=0; i<hiperaristasBit.length;i++){
            conjuntoDePartes.and(hiperaristasBit[i]);
            if(!conjuntoDePartes.isEmpty()) 
                agregaTransversal(transversales, hiperaristasBit[i]);
        }
                   
                            
                        
                        
    }

    private void agregaTransversal(ArrayList<BitSet> transversales,
                                   BitSet A) {
        //boolean posibleTrans=false;
        for(int i=0; i<transversales.size(); i++){
            BitSet b=transversales.get(i);
            b.and(A);
            if(b.isEmpty()){
                b.xor(A);
                b.xor(A);
                if(b.isEmpty()) transversales.add(A); //transversal minimal nueva, se agrega 
            }
            b.xor(b);
            if(b.isEmpty()){
                transversales.remove(i);
                transversales.add(A); // A es subconjunto de b, sale b y entra A
            }
                
            
        }    
    }

    

    public void toComplemento() {
       /*  System.out.println("Grafo antiguo");
        for(BitSet b: hiperaristasBit)
            System.out.println(b); */
        
        for (int i = 0; i < hiperaristasBit.length; i++) {
            hiperaristasBit[i].flip(0, numAtributos);
            hiperaristasBit[i].set(consecuente, false);
        }
        
        
        /* System.out.println("Grafo complemento");
        for(BitSet b: hiperaristasBit)
            System.out.println(b); */

    }

    public void setConsecuente(int consecuente) {
        this.consecuente = consecuente;
    }

    public int getConsecuente() {
        return consecuente;
    }

    public String  toFile() {
        String s="";
        for(int i=0; i< hiperaristasBit.length; i++){
            BitSet bit=hiperaristasBit[i];
            for(int j=0; j<numAtributos;j++)
                s+=bit.get(j)? "0":"*";
            s+='\n';
        }
        return s;
    }

    public String toFileSHD() {
        String s="";
            for(int i=0; i< hiperaristasBit.length; i++){
                BitSet bit=hiperaristasBit[i];
                for(int j=0; j<bit.size();j++)
                    s+=bit.get(j)? j+" ":"";
            s+='\n';
        }
        return s;
    }
    
    public int getNumAtributos(){
        return numAtributos;
    }
}
