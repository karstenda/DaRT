package raytracer.distributed;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class RemoteJobExecutor implements Runnable {

	private String host;
	private String username;
	private String password;
	private File keyfile;
	private RemoteJobDistributor distributor;
	private DistributedProgressUpdater progressUpdater;

	public RemoteJobExecutor(String host, String username, String password,
			File keyfile, RemoteJobDistributor distributor, DistributedProgressUpdater progressUpdater) {
		
		this.progressUpdater = progressUpdater;
		this.host = host;
		this.username = username;
		this.password = password;
		this.keyfile = keyfile;
		this.distributor = distributor;
	}

	
	public void execute(RemoteJob job) throws IOException{
		
			Connection connection = new Connection(host);
			connection.connect();

			boolean isAuthenticated = connection.authenticateWithPublicKey(
					username, keyfile, password);

			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");

			Session session = connection.openSession();

			System.out.println("---> RemoteJob "+job.getNumJob()+" started on " + host);

			session.execCommand(job.getCommand());

			// Block until done ...
			InputStream stdout = new StreamGobbler(session.getStdout());
			BufferedReader br = new BufferedReader(
					new InputStreamReader(stdout));
			String line;
			while ((line = br.readLine()) != null) {
					progressUpdater.setUpdateProgressFromStdOutLine(job.getNumJob(), line);
			}

			//onFinished
			job.onFinished();
			
			// Done
			System.out.println("---> RemoteJob "+job.getNumJob()+" on " + host + " is done.");
			session.close();
			connection.close();
	}
	
	@Override
	public void run() {
		
		while(!distributor.isFinished()) {
	
			if (distributor.hasMoreJobsAvailable()) {
				RemoteJob job = distributor.getNewJob();
				try {
					execute(job);
					distributor.submitDoneJob(job);
				} catch (Exception e) {
					System.err.println("ERROR: "+e.getMessage());
					distributor.submitFailedJob(job);
					return;
				}
			}
			
			if (!distributor.hasMoreJobsAvailable() && !distributor.isFinished()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}		
	}
}
