package raytracer.distributed;

import java.io.File;
import java.util.ArrayList;



public class DistributedRayTracer extends DistributedExecutor {


	private static String jobName = "distributed";
	private static final int totJobs = 500;
	private static final int start = 0;
	
	private static ArrayList<Integer> failedJobs = new ArrayList<Integer>();
	
	public static void main(String[] args) { 
		title = "Distributed DaemenRayTracer";
		DistributedRayTracer dRayTracer = new DistributedRayTracer();
		dRayTracer.askLoginDetailsAndRun();
		
		if (failedJobs.size() > 0) {
			System.err.println("Following jobs were not correctly received: ");
			for (int id: failedJobs) {
				System.err.print(id+", ");
			}
		}
	}

	
	@Override
	protected RemoteJob[] getRemoteJobs() {
		
		RemoteJob[] remoteJobs = new RemoteJob[totJobs-start];
		for (int i = start; i < totJobs; i++) {
			
			String command = "cd ~/workspace/DaemenRayTracer; java -jar ~/workspace/DaemenRayTracer/DaRT.jar "
					+ jobName + " " + (i+1) + " " + totJobs;
			remoteJobs[i-start] = new RemoteJob(i,command) {
				@Override
				public void onFinished() {
					
//					try {
//						Thread.sleep(3000);	
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
					
					
					String name = jobName+"_"+(numJob+1)+"_"+totJobs+".dat.zip";
					File file = new File("/home/s0202187/workspace/DaemenRayTracer/result_data/"+name);
					File dest = new File("/tmp/DaRT/"+name);
					
					move(file,dest,numJob+1);
				}
			};
		}
		return remoteJobs;
	}
	
	@Override
	protected DistributedProgressUpdater getDistributedProgressUpdater () {
		return new DistributedProgressUpdater(totJobs) {
			@Override
			public int parseProgressFromStdOutLine(String line) {
				if (line.startsWith("-> Overall rendering progress: ")) {
					return Integer.parseInt(line
							.split("-> Overall rendering progress: ")[1]
							.split("%")[0]);
				} else {
					return -1;
				}
			}
		};
	}
	
	private void move(File afile, File bfile, int id) {
		
		while (!afile.isFile()) {
    		try {
        		Thread.sleep(500);
        	} catch (InterruptedException e) {
        		e.printStackTrace();
        	}
    	}
		
		try {
			String command = "mv "+afile.getAbsolutePath()+" "+bfile.getAbsolutePath();
			Process proc = Runtime.getRuntime().exec(command);
			
			proc.waitFor();
			
			if (proc.exitValue() != 0) {
				failedJobs.add(id);
				System.err.println("Lost job "+id+"!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
