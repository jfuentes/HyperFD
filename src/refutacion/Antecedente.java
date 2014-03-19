package refutacion;

import java.util.ArrayList;

public class Antecedente {
    private ArrayList<Integer> atributos;
    
    public Antecedente() {
        super();
        atributos= new ArrayList<Integer>();
    }
    
    public Antecedente(int atributo){
        atributos=new ArrayList<Integer>();
        atributos.add(atributo);
        
    }


    public void setAtributos(ArrayList<Integer> conjunto) {
        this.atributos = conjunto;
    }

    public ArrayList<Integer> getAtributos() {
        return atributos;
    }
    
    public void agregarAtributo(int atributo){
        if(!atributos.contains(atributo))atributos.add(atributo);
    }
    
    public String toString(){
        String s="";
        for (int i=0; i<atributos.size()-1;i++){
            s+=atributos.get(i)+", ";
        }
        s+=atributos.get(atributos.size()-1);
        return s;
    }

    public boolean esSuperConjuntoDe(Antecedente antecedente) {
        
        //return atributos.containsAll(antecedente.getAtributos()); 
        /* ArrayList<Integer> atributos2=antecedente.getAtributos();
        
        for (Integer atributo2 : atributos2) {
            if(!atributos.contains(atributo2)) return false;
        }
        return true; */
        return atributos.containsAll(antecedente.getAtributos());
        
            
    }

    int[] getAtributosArray(int totalAtributos) {
        int [] array= new int[totalAtributos]; 
        for (int i = 0; i < totalAtributos; i++) {
            array[i]=0;
        }

        for (Integer integer : atributos) {
            array[integer]=1;
        }
        return array;
    }
}
