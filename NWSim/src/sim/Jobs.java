package sim;

import java.util.ArrayList;

public class Jobs {

	static int job_id;

	ArrayList<Integer> array_task_id;
	public Jobs(int job_id) {
		array_task_id=new ArrayList<Integer>();
		Jobs.job_id=job_id;
	}

	public void identifytask()
	{
		//Pick values fom Lhm linked list and compare the last element of the value with job id and if equal enter it in array list

		//Run loop untill events, pick task_id and job id.



		for(int task=1;task<=Framework.id;task++)
		{

			/*Collection<Event> values = Engine.Lhm.values(); 
		ArrayList para = new ArrayList(values);       
		System.out.println(para);
		int last=para.size();*/

			Event extract= (Event) Engine.Lhm.get(task);

			int comp=extract.job_id;

			if(comp==job_id)
			{
				if(array_task_id.isEmpty()){    //This is for entering only one coflow in the job
					array_task_id.add(task);	
				}


			}
		}

		System.out.println("For job "+job_id+" all associated flows are:"+array_task_id);

	}

}
