package refutacion;

import java.util.ArrayList;
import java.util.BitSet;

public class Prueba {
    public Prueba() {
        super();
    }
    
    public static void main(String [] args){
        BitSet []hg= new BitSet[2];
        BitSet or= new BitSet(5);
        BitSet bs1= new BitSet(5);
        bs1.set(2);
        bs1.set(3);
        bs1.set(4);
        BitSet bs2= new BitSet(5);
        bs2.set(0);
        bs2.set(2);
        bs2.set(4);
        
        hg[0]=bs1;
        hg[1]=bs2;
        
        
       ArrayList<BitSet> transversales= new ArrayList<BitSet>();
        verificaNivel(0,2,hg,transversales,or);
        System.out.println("Transversales minimales:");
        for(BitSet transversal: transversales){
            System.out.println(transversal);
        }
        
    }
    
    public static void verificaNivel(int fil, int total, BitSet  [] hg, ArrayList<BitSet> transversales, BitSet or){
        boolean anterior=false;
        if(fil==total){
           System.out.println("transversal: "+or);
           agregaTransversal(transversales,or);
           return;
        }else{
        for (int bit = hg[fil].nextSetBit(0); bit >= 0  ; bit = hg[fil].nextSetBit(bit + 1)){
               //System.out.println("verificalndo nivel "+fil+" con bit "+bit);
                if(or.get(bit)) anterior=true;
                else  or.set(bit);
                verificaNivel(fil+1,total, hg, transversales, or);
                if(!anterior) or.set(bit, false);
               anterior=false;
        }
        }
    }
    
    public static void agregaTransversal(ArrayList<BitSet> transversales, BitSet candidata){
        BitSet resp1, resp2, resp3, resp4;
        if(transversales.size()==0) transversales.add((BitSet)candidata.clone());
        else
        for(int i=0; i<transversales.size(); i++){
            
            resp1 = (BitSet)transversales.get(i).clone();
            resp2 = (BitSet)transversales.get(i).clone();
            resp1.and(candidata);
            resp3=(BitSet)resp1.clone();
            resp3.xor(resp2); 
            if(resp3.isEmpty()){// si A subset b
                return;
             
            }else{ //si candata es subconjunto de lo que tengo
                resp1.xor(candidata);
                if(resp1.isEmpty()){ //Si b subset A
                System.out.println("candidata "+candidata+" es subconjunto de "+transversales.get(i)+", se intercambian" );
                transversales.remove(i);
                
                while(i<transversales.size()){
                    resp1 = (BitSet)transversales.get(i).clone();
                    resp2 = (BitSet)transversales.get(i).clone();
                    resp1.and(candidata);
                   
                    resp1.xor(candidata); 
                    if(resp1.isEmpty()){
                        System.out.println("Elimine otro subconjunto de candidata");
                        transversales.remove(i);
                        
                    }else
                    i++;
                }
                
                transversales.add((BitSet)candidata.clone());
                
                return;
                }
            }
            
            
        }
        System.out.println("Se agrega transversal "+candidata);
        transversales.add((BitSet)candidata.clone());
        return;
    }
    
}
