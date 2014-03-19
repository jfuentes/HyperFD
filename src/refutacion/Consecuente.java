package refutacion;

public class Consecuente {
    private int atributo;
    public Consecuente() {
        super();
    }
    public Consecuente(int atributo){
        this.atributo=atributo;
        
    }

    public void setAtributo(int atributo) {
        this.atributo = atributo;
    }

    public int getAtributo() {
        return atributo;
    }
    
    public String toString(){
        return atributo+"";
    }
}
