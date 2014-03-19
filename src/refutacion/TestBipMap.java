package refutacion;

import java.io.PrintStream;

import java.util.BitSet;

public class TestBipMap {
    public TestBipMap() {
        super();
    }
    
    public static void main(String []a){
        BitSet array= new BitSet(7);
        //for(int i=0; i<7; i++) array.set(i);
        array.set(0);
        array.set(1);
        array.set(4);
        System.out.println(array.toString());
        
        BitSet array2= new BitSet(7);
        //for(int i=0; i<7; i++) array.set(i);
       // array2.set(1);
        array2.set(0);
        array2.set(1);
        
        System.out.println(array2.toString());
        
        BitSet array3= new BitSet();
        //array3=array.;
        BitSet resultado=(BitSet) array.clone();
        resultado.and(array2);
        if(resultado.isEmpty()){
            
            System.out.println("Son conjuntos distintos");
        }else{
            BitSet resultado2=(BitSet) resultado.clone();
            resultado2.xor(array);
            if(resultado2.isEmpty()) System.out.println("Array1 es subconjunto de array2");
            else{
                resultado.xor(array2);
                if(resultado.isEmpty()) System.out.println("Array2 es subconjunto de array1");
            }
        }
        
    }
}
