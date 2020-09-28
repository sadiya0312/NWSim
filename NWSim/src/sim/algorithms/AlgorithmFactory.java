/**
 * 
 */
package sim.algorithms;

import java.io.FileNotFoundException;
import java.io.IOException;

import sim.Engine;
import sim.Graph;
import sim.exceptions.CsvException;

/**
 * @author Sadiya
 *
 */
public class AlgorithmFactory {

	/**
	 * 
	 */
	private static AlgorithmFactory instance;
	private static Object LOCK = new Object();
	
	Engine en;
	Graph g;
	
	private AlgorithmFactory(Engine en,Graph g) {
		this.en=en;
		this.g=g;
	}
	
	public static AlgorithmFactory getInstance(Engine en,Graph g){
		if(instance == null){
			synchronized (LOCK) {
				if(instance == null){
					instance = new AlgorithmFactory(en, g);
				}
			}
			
		}
		
		return instance;
	}
	
	public enum AlgoType {
		FIFO,
		SJF,
		PFS,
		EEA,
		All, 
		EAA,
        ECE;
    }
	
	public Algorithm getAlgorithm(AlgoType type) throws FileNotFoundException, CsvException, IOException{
		switch (type) {
		case FIFO:
			return new FIFO();
			
		case SJF:
			return new SJF();
			
		case PFS:
			return new PFS();
			
		case EAA:
			return new EAA();

			case ECE:
				return new ECE();
		
		case All:
		   return new All(en,g);
		    
		default:
			return null;
		}
		
	}
}
