package refutacion;

import java.util.BitSet;

public class H {
    private BitSet X;
    private int numAtributos;
    private int consecuente;
    private boolean prima=false;
    
    public H(BitSet X, int consecuente, int numAtributos) {
        this.X=X;

        this.consecuente=consecuente;
        this.numAtributos=numAtributos;
    }
    
    public BitSet getX() {
            return X;
    }
    
    public String toString(){
        return prima?X+"-/->"+consecuente:X+"-->"+consecuente;
    }
    
    public void clear(){
            X.clear();
    
            
    }

    public void setConsecuente(int consecuente) {
    this.consecuente = consecuente;
    }

    public int getConsecuente() {
    return consecuente;
    }
    
    public boolean isSubSet(H A){
        
         BitSet resp1, resp2;
         resp1 = (BitSet)X.clone();
         resp2 = (BitSet)X.clone();
         resp1.and(A.getX());
         resp1.xor(resp2); 
    if(resp1.isEmpty()){
        return true;
    }else return false;
        
    
    }
    
    public void toComplemento() {
     
    X.flip(0, numAtributos);
        prima=!prima;
    X.flip(consecuente);
    

    }
    
    public String  toFile() {
        String s="";
        
        for(int j=0; j<numAtributos;j++)
                s+=X.get(j)? "0":"*";
            s+='\n';
       
        return s;
    }
    
    public String  toFileSHD() {
        String s="";
        
        
        for(int i=X.nextSetBit(0); i>=0; i=X.nextSetBit(i+1))
            s+=i+" ";
            s+='\n';
       
        return s;
    }
    
}
