package refutacion;



import java.util.BitSet;

/**
 * Objects of this class hold are functionale dependency. The class is used by
 * algorithms in TANEjava.java to calculate the cover and closure for a set F
 * of functional dependencies.
 * @author Tobias
 *
 */
public class Refutacion {
	protected BitSet X;
	
        protected int consecuente;
        protected int numAtributos;
	/**
	 * Empty constructor
	 *
	 */
	
    public Refutacion(BitSet X, int consecuente, int numAtributos) {
        this.X=X;

        this.consecuente=consecuente;
        this.numAtributos=numAtributos;
    }

   


    /**
     * Returns the left-hand-side of a functional dependency
     * @return ComparableSet<String> - the the left-hand-side attributes
     */
	public BitSet getX() {
		return X;
	}
	/**
	 * Returns the right-hand-side of a functional dependency
	 * @return ComparableSet<String> - the the right-hand-side attributes
	 */
	/* public ComparableSet<Integer> getY() {
		return Y;
	} */
	
	
	
	
	/**
	 * Prints a functional dependency.
	 */
	public String toString(){
		return X+"-/->"+consecuente;
	}
	/**
	 * Clears the attribues of the RHS and LHS candidates
	 *
	 */
	public void clear(){
		X.clear();
        
		
	}

    public void setConsecuente(int consecuente) {
        this.consecuente = consecuente;
    }

    public int getConsecuente() {
        return consecuente;
    }
    
    public boolean isSubSet(Refutacion A){
            
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
         //System.out.println("Grafo antiguo "+X);
        
        
       // X.flip(0, numAtributos);
        
        //X.set(consecuente, false);
        X.flip(0, numAtributos);
        
        
         //System.out.println("Grafo complemento "+X);
        

    }
    
    
}
/* 
public class Refutacion {
    private Antecedente antecedente;
    private Consecuente consecuente;
    
    private int totalAtributos;
    
    public Refutacion() {
        super();
    }
    
    public Refutacion(Antecedente antecedente, Consecuente consecuente, int totalAtributos){
        this.antecedente=antecedente;
        this.consecuente=consecuente;
        this.totalAtributos=totalAtributos;
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
    
    public String toString(){
        return antecedente.toString()+" -/-> "+consecuente.toString();
    }

    
    public boolean esMaximalDe(Refutacion refutacion2) {
        
        
        return antecedente.esSuperConjuntoDe(refutacion2.getAntecedente());
    }

    int[] getAntecedenteArray() {
        return antecedente.getAtributosArray(totalAtributos);
    }

    public void setTotalAtributos(int totalAtributos) {
        this.totalAtributos = totalAtributos;
    }

    public int getTotalAtributos() {
        return totalAtributos;
    }
} */
