package sim.algorithms;

import java.io.IOException;

import sim.Engine;
import sim.Graph;

public class All implements Algorithm {

	Engine en;
	Graph g;
	public All(Engine en, Graph g) {
		this.en=en;
		this.g=g;
	}
	
	public void runAlgo() throws InterruptedException, IOException {

		FIFO fifo=new FIFO();
		fifo.runAlgo();
		en.send_mapper(en, g);
		SJF sjf=new SJF();
		sjf.runAlgo();
		PFS pfs=new PFS();
		pfs.runAlgo();
		EAA eaa=new EAA();
		eaa.runAlgo();
		ECE ece=new ECE();
		ece.runAlgo();


	}

}
