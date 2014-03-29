package refutacion;

import java.util.BitSet;

public class RowX{
    private BitSet X;
    private int numAtributos;
    private int consecuente;
    private boolean prima=false;
    
    public RowX(){

    }
    public RowX(RowX other){
        this.X=(BitSet)other.getX().clone();
        this.numAtributos=other.getNumAtributos();
        this.consecuente=other.getConsecuente();
        this.prima=other.getPrima();
    }
    public RowX(BitSet X, int consecuente, int numAtributos) {
        this.X=X;
        this.numAtributos=numAtributos;
        this.consecuente=consecuente;
    }
    public RowX clone(){
        RowX ret=new RowX();
        ret.X=(BitSet)this.X.clone();
        ret.numAtributos=this.numAtributos;
        ret.consecuente=this.consecuente;
        ret.prima=this.prima;
        return ret;
    }
    public boolean isSubSet(RowX A){
        BitSet resp1, resp2;
        resp1 = (BitSet)X.clone();
        resp2 = (BitSet)X.clone();
        resp1.and(A.getX());
        resp1.xor(resp2); 
        if(resp1.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    public void toComplemento(){
        X.flip(0, numAtributos);
        prima=!prima;
        X.flip(consecuente);
    }
    public void clear(){
        X.clear();
    }
    public void setConsecuente(int consecuente) {
        this.consecuente = consecuente;
    }


    public BitSet getX() {
        return X;
    }
    public int getNumAtributos() {
        return numAtributos;
    }
    public int getConsecuente() {
        return consecuente;
    }
    public boolean getPrima() {
        return prima;
    }





    public String toString(){
        return prima?X+"-/->"+consecuente:X+"-->"+consecuente;
    }
    public String toFile(){
        String s="";
        for(int j=0; j<numAtributos;j++)
            s+=X.get(j)? "0":"*";
        s+='\n';
        return s;
    }
    public String toFileSHD(){
        String s="";
        for(int i=X.nextSetBit(0); i>=0; i=X.nextSetBit(i+1))
            s+=i+" ";
        s+='\n';
        return s;
    }
}
