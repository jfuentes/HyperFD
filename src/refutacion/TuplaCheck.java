package refutacion;

public class TuplaCheck {
    private Antecedente antecedente;
    private Consecuente consecuente;
    
    public TuplaCheck() {
        super();
    }
    
    public TuplaCheck(Antecedente antecedente, Consecuente consecuente){
        this.antecedente=antecedente;
        this.consecuente=consecuente;
    }
    
    public void setAntecedente(Antecedente antecedente) {
        this.antecedente = antecedente;
    }

    public Antecedente getAntecedente() {
        return antecedente;
    }

    public void setConsecuente(Consecuente consecuente) {
        this.consecuente = consecuente;
    }

    public Consecuente getConsecuente() {
        return consecuente;
    }
}
