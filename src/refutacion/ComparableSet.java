package refutacion;


import java.util.*;
/**
 * A ComparableSet extends the java.util.TreeSet. Normal TreeSets are not 'comparable' among others
 * and therfore sets of sets are not supported out of the box in Java. Using this class it is possible to define e.g:
 * ComparableSet<ComparableSet<String>>
 * @author Tobias
 *
 * @param <T> T must be class which has implemented the Comparable interface (e.g. String, Integer, Date,...)
 */
public class ComparableSet<T extends Comparable<? super T>> extends TreeSet<T> implements
		Comparable<ComparableSet<T>> {
	private static final long serialVersionUID = 1L;
	/**
	 * Return a TreeSet
	 * @return a TreeSet
	 */
	public TreeSet<T> getTreeSet() {
		return this;
	}
	/**Standard constructor
	 */
	public ComparableSet() {
		
	}
	/**
	 * Overloaded constructor. Initalizes the ComparableSet with all elements of the TreeSet 
	 * @param set - a TreeSet as argument 
	 */
	public ComparableSet(TreeSet<T> set) {
		addAll(set);
	}
	/**
	 * Makes a copy of the ComparableSet
	 * @return
	 */
	public ComparableSet<T> deepCopy() {
		return new ComparableSet<T>(new java.util.TreeSet<T>(this));
	}
	/**
	 * Cheks if a element is in the ComparableSet
	 * @param element
	 * @return
	 */
	public boolean member(T element) {
		return contains(element);
	}
	/**
	 * Returns true if the overgiven set is a subset the set
	 * @param set
	 * @return
	 */
	public boolean isSubset(ComparableSet<T> set) {
		return set.getTreeSet().containsAll(this);
	}

	public int compareTo(ComparableSet<T> comparableSet) {
		TreeSet<T> set = comparableSet.getTreeSet();
		Iterator<T> iterFirst = iterator();
		Iterator<T> iterSecond = set.iterator();
		
		while (iterFirst.hasNext() && iterSecond.hasNext()) {
			T first = iterFirst.next();
			T second = iterSecond.next();
			int cmp = first.compareTo(second);
			if (cmp == 0) {
				continue;
			}
			return cmp;
		}
		if (iterFirst.hasNext()) {
			return 1;
		}
		if (iterSecond.hasNext()) {
			return -1;
		}
		return 0;
	}
	/**
	 * Returns the mathematical union of two sets
	 * @param comparableSet
	 * @return - the 
	 */
	public ComparableSet<T> union(ComparableSet<T> comparableSet) {
		TreeSet<T> union = new TreeSet<T>(this);
		union.addAll(comparableSet.getTreeSet());
		return new ComparableSet<T>(union);
	}
	/**
	 *  Returns the mathematical intersection of two sets
	 * @param comparableSet
	 * @return
	 */
	public ComparableSet<T> intersection(ComparableSet<T> comparableSet) {
		TreeSet<T> intersection = new TreeSet<T>(this);
		intersection.retainAll(comparableSet.getTreeSet());
		return new ComparableSet<T>(intersection);
	}
	/**
	 *  Returns the mathematical difference of two sets
	 * @param comparableSet
	 * @return
	 */
	public ComparableSet<T> difference(ComparableSet<T> comparableSet) {
		TreeSet<T> difference = new TreeSet<T>(this);
		difference.removeAll(comparableSet.getTreeSet());
		return new ComparableSet<T>(difference);
	}
	/**
	 * Removes one element of the set
	 */ 
	public ComparableSet<T> without(T element) {
		TreeSet<T> without = new TreeSet<T>(this);
		//if set is empty and remove is called we get an 'NoSuchElementException' exception
		if(!without.isEmpty())
			without.remove(element);
		return new ComparableSet<T>(without);
	}
	/**
	 * Return a String representaion of the set
	 * e.g: if the ComparableSet contains [A,B,C] it returns "ABC" 
	 * @return
	 */
	public String serialize(){
		
		Iterator itThis = iterator();
		
		String result ="";
		while(itThis.hasNext()){
			result += itThis.next();
		}
		return result;
	}
	/**
	 * Returns a set representation of the set without the brackets
	 * e.g: if the ComparableSet contains [A,B,C] it returns "A,B,C" 
	 * @return
	 */
	public String serializeWithoutBrackets() {

		Iterator itThis = iterator();

		StringBuffer result = new StringBuffer();
		while (itThis.hasNext()) {
			result.append(itThis.next() + ",");
		}
		result.deleteCharAt(result.length() - 1); // remove last ','
		return result.toString();

	}
}

