public class Experimento{
	public static void main(String[] args) {
		int[] arreglo1=new int[3];
		arreglo1[0]=100;
		arreglo1[1]=110;
		arreglo1[2]=111;
		int[] arreglo2=new int[3];
		arreglo2[0]=500;
		arreglo2[1]=550;
		arreglo2[2]=555;

		System.out.println("Arreglo1: \n"+imprime(arreglo1));
		System.out.println("Arreglo2: \n"+imprime(arreglo2));

		arreglo1=arreglo2;
		System.out.println("\nArreglo1=Arreglo2;");
		System.out.println("Arreglo1: \n"+imprime(arreglo1));
		System.out.println("Arreglo2: \n"+imprime(arreglo2));

		arreglo1[1]=777;
		System.out.println("\nArreglo1[1]=777;");
		System.out.println("Arreglo1: \n"+imprime(arreglo1));
		System.out.println("Arreglo2: \n"+imprime(arreglo2));

		locambia(arreglo1);
		System.out.println("\nlocambia(Arreglo1);");
		System.out.println("Arreglo1: \n"+imprime(arreglo1));
		System.out.println("Arreglo2: \n"+imprime(arreglo2));
	}

	public static void locambia(int[] arr){
		arr[2]=999;
		return;
	}
	public static String imprime(int[] arr){
		String ret="";
		for(int i=0;i<arr.length;i++){
			ret+=Integer.toString(arr[i])+"  ";
		}
		return ret;
	}
}