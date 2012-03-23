package raytracer.distributed;


public class RemoteTerminator extends DistributedExecutor {
	
	
	public static void main (String[] args) {
		title = "Remote Terminator";
		RemoteTerminator terminator = new RemoteTerminator();
		terminator.askLoginDetailsAndRun();
	}
	
	@Override
	protected RemoteJob[] getRemoteJobs() {
		
		RemoteJob[] remoteJobs = new RemoteJob[hosts.length];
		for (int i = 0; i < hosts.length; i++) {
			String command = "skill -KILL -u s0202187";
			remoteJobs[i] =new RemoteJob(i,command);
		}
		return remoteJobs;
	}

	@Override
	protected DistributedProgressUpdater getDistributedProgressUpdater() {
		return  new DistributedProgressUpdater(hosts.length) {
			@Override
			public int parseProgressFromStdOutLine(String line) {
				return 100;
			}
		};
	}
}
