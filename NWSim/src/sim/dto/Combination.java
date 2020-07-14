package sim.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Combination implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5553837764268913024L;
	private static Combination instance;
	private static final Object LOCK = new Object();
	private Set<ColumnComb> columnCombs; 
	
	private Combination() {
		columnCombs = new HashSet<>();
	}
	
	public static Combination getInstance(){
		if(instance == null){
			synchronized (LOCK) {
				if(instance == null){
					instance = new Combination();
				}
			}
		}
		return instance;
	}
	
     public Set<ColumnComb> getColumnCombs() {
		return columnCombs;
	}

	int factorial(int n) 
	  { 
	  
	    return (n == 1 || n == 0) ? 1 : n * factorial(n - 1); 
	  
	  } 
    
    /**
     * This method helps in building  nCr combinations of efficiency list 
     * and store those combinations in fullcombine set.
     * 
     * @param effciencyList
     * @param n - total number of elements
     * @param r - size of each combinations
     */
    public void buildCombination(ArrayList<Float> effciencyList, int n, int r) 
    { 
    	/* While(intlist.size()>=r)
    	 * 1.Pick first r elements from the effciencyList
    	 * 2.Loop f from 0-r*r imes
    	 * 3.Lopp i from 0-r times
    	 * 4.{ Loop j from 0-r times
    	 * 5.  Store (intlist(i),intlist(i),intlist(j)) in innermost array
    	 * 6. } Store innerlist.add(innermost)
    	 * 7. } Store combination(innerlist)
    	 * 8. delete intlist(f)
    	 * 9. (set)fullcombine.add(combination);
    	 * 10. }
    	 */
    	/*while (effciencyList.size() <= r) {*/
    		/*
    		 * condition to r*r if r*r equal to effciencyList.size()
    		 * 3*3 9 but list has size = 8 looping  0 to < 9 with 8 then throw 
    		 */
    		/*
    		 * 1 element * other element
    		 */
 
    	effciencyList.forEach(rowEfficiency -> {
    		
    		effciencyList.forEach(columnEfficiency -> {
    			columnCombs.add(new ColumnComb.Builder(r).setRows(rowEfficiency).setColumns(columnEfficiency).build());
    		});
    		
    	});
    		/*effciencyList.forEach(rowEffciency -> {
    			effciencyList.forEach(columnEfficiency -> {
    				columnCombs.add();
    			});
    		});*/
		/*}*/
    	
    	
    	
         
    }

	@Override
	public String toString() {
		return "Combination [columnCombs=" + columnCombs + "]";
	} 
    
    
    

}
