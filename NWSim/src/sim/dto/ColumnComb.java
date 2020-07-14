package sim.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * To create the combination of elements from an arraylist.
 * 
 * @see Serializable
 * @author Sadiya
 *
 */
public class ColumnComb implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1058486567683333347L;
	
	ArrayList<Float> combinations;
	
	/**
	 * Creates a column instance for a matrix
	 * @param builder -required to create instance
	 */
	private ColumnComb(Builder builder) {
		this.combinations = builder.combinations;
	}
	
	public ArrayList<Float> getCombinations() {
		return combinations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((combinations == null) ? 0 : combinations.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColumnComb other = (ColumnComb) obj;
		if (combinations == null) {
			if (other.combinations != null)
				return false;
		} else if (!combinations.equals(other.combinations))
			return false;
		return true;
	}
	
	
	
	@Override
	public String toString() {
		return "ColumnComb [combinations=" + combinations + "]";
	}	


	/**
	 * It is a builder class to built collumns.
	 * @author Sadiya
	 *
	 */
	public static class Builder{
		// r is the number of combinations
		private int r;
		// List of combinations column value
		private ArrayList<Float> combinations;
		
		/**
		 * This is the constructor creatorbuilder class for collumns
		 * @param r - number of combination
		 */
		public Builder(int r) {
			this.r = r;
			this.combinations = new ArrayList<>(r);
		}
		
		/**
		 * To built first set of rows
		 * @param row- first r row values
		 * @return - buildr instance
		 */
		public Builder setRows(float row){
			IntStream.range(0, r-1).forEach(i -> {
				combinations.add(i,row);
			});
			return this;
		}
		
		/**
		 * This method to set instance of column
		 * @param column- passed last set of combination
		 * @return - builder instance
		 */
		public Builder setColumns(float column){
			combinations.add(r-1,column);
			return this;
		}
		
		/**
		 * This method creates a column instance
		 * @return -column instance
		 */
		public ColumnComb build(){
			return new ColumnComb(this);
		}
		
		
	}

	
	

}
