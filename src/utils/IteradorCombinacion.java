package utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class IteradorCombinacion implements Iterable<List<Integer>> {
	private List<Integer> lista;
	private Integer k;

	public IteradorCombinacion(List<Integer> s, Integer k) {
		lista = s;
		this.k = k;
	}

	
	public Iterator<List<Integer>> iterator() {

		return new IteradorCombn(lista, k);
	}

        //clase que realiza las combinaciones
	private class IteradorCombn implements Iterator<List<Integer>> {
		private int actualSize, maxresult;
		private Integer curIndex;
		private Integer[] result;
		private int[] indices;
		private Integer[] arrayList;
		private List<Integer> elem = null;

		public IteradorCombn(List<Integer> s, Integer k) {
			actualSize = k;// desde d�nde
			curIndex = 0;
			maxresult = k;
			arrayList = new Integer[s.size()];
			for (int i = 0; i < arrayList.length; i++) { // la lista s la vuelca en arrayList
				arrayList[i] = s.get(i);
			}
			this.result = new Integer[actualSize < s.size() ? actualSize : s.size()]; 
			//el tama�o de result va a ser el valor menor entre actualSize y el tama�o de s
			indices = new int[result.length];

			for (int i = 0; i < result.length; i++) {
				indices[i] = result.length - 2 - i;
			}
		}

		public boolean hasNext() {
			elem = null;
			while ((elem == null && curIndex != -1)) {

				indices[curIndex]++;
				if (indices[curIndex] == (curIndex == 0 ? arrayList.length: indices[curIndex - 1])) {
					
					indices[curIndex] = indices.length - curIndex - 2;
					curIndex--;
				} else {

					result[curIndex] = arrayList[indices[curIndex]];
					
					if (curIndex < indices.length - 1)
						curIndex++;
					else {
						elem = new LinkedList<Integer>();
						for (Integer s : result) {
							elem.add(s);
						}

					}
				}
			}
			if (elem == null) {
				if (actualSize < maxresult) {
					actualSize++;
					this.result = new Integer[actualSize < arrayList.length ? actualSize
							: arrayList.length];
					indices = new int[result.length];

					for (int i = 0; i < result.length; i++) {

						indices[i] = result.length - 2 - i;
					}
					curIndex = 0;

					return this.hasNext();
				} else {
					return false;
				}
			} else {
				return true;
			}
		}

		@Override
		public List<Integer> next() {
			return elem;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub

		}
	}
}
