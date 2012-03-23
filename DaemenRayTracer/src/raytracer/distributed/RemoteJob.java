package raytracer.distributed;

public class RemoteJob {

	protected int numJob;
	private String command;

	public RemoteJob(int numJob, String command) {
		this.command = command;
		this.numJob = numJob;
	}
	
	public int getNumJob() {
		return numJob;
	}

	public void setNumJob(int numJob) {
		this.numJob = numJob;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void onFinished() {}
	
}
