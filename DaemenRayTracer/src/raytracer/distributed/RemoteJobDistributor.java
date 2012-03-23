package raytracer.distributed;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class RemoteJobDistributor {
	
	Queue<RemoteJob> jobsToDo;
	Map<Integer,RemoteJob> outstandingJobs;
	Map<Integer,RemoteJob> jobsDone;
	
	public RemoteJobDistributor(List<RemoteJob> jobsToDo) {
		this.jobsToDo = new LinkedList<RemoteJob>(jobsToDo);
		this.outstandingJobs = new HashMap<Integer,RemoteJob>();
		this.jobsDone = new HashMap<Integer,RemoteJob>();
		
	}
	
	public synchronized boolean hasMoreJobsAvailable() {
		return jobsToDo.size() != 0;
	}
	
	public synchronized boolean isFinished() {
		return (jobsToDo.size() == 0 && outstandingJobs.size() == 0);
	}
	
	public synchronized RemoteJob getNewJob() {
		RemoteJob job = jobsToDo.poll();
		outstandingJobs.put(job.getNumJob(), job);
		return job;
	}
	
	public synchronized void submitFailedJob(RemoteJob job) {
		outstandingJobs.remove(job.getNumJob());
		jobsToDo.add(job);
	}
	
	public synchronized void submitDoneJob(RemoteJob job) {
		outstandingJobs.remove(job.getNumJob());
		jobsDone.put(job.getNumJob(),job);
	}
}
