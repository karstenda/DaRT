package raytracer.distributed;

public abstract class DistributedProgressUpdater {

	private int totPercentagesToDo;
	private int percentagesSoFar = 0;
	private int[] jobsProgresses;
	private long milliesSinceStart;
	private int interval;
	
	public DistributedProgressUpdater(int totJobs) {
		this.totPercentagesToDo = totJobs*100;
		this.jobsProgresses = new int[totJobs];
		this.milliesSinceStart = System.currentTimeMillis();
		this.interval = totPercentagesToDo/300;
	}

	public abstract int parseProgressFromStdOutLine(String line);
	
	public void setUpdateProgressFromStdOutLine(int numJob, String line) {
		int progress = parseProgressFromStdOutLine(line);
		if (progress > 0)
			setProgress(numJob,progress);
	}

	public synchronized void setProgress(int numJob, int progress) {
		percentagesSoFar -= this.jobsProgresses[numJob];
		this.jobsProgresses[numJob] = progress;
		percentagesSoFar += progress;
		
		if ((percentagesSoFar % 100) == 0) {
			updateProgress(percentagesSoFar,totPercentagesToDo);
		}
	}
	
	private void updateProgress(float i, float tot) {
		updateProgress(i/tot);
	}
	
	private void updateProgress(float prog) {
		int progress = (int) (prog*100f);
		float diff = (System.currentTimeMillis()-milliesSinceStart);
		float avgRayTime = diff/prog;
		long timeTodo = Math.round((avgRayTime*(1-prog))/1000);
		System.out.println("-> Overall progress: "+progress+"%     (ETL: "+formatMilliesToString(timeTodo)+")");
	}
	
	
	private String formatMilliesToString(long seconds) {
		long dagen = Math.round(seconds/(24*60*60)-0.5);
		seconds = seconds - dagen*24*60*60;
		long uren = Math.round((seconds/3600 -0.5));
		seconds = seconds - uren*60*60;
		long minuten = Math.round((seconds/60 -0.5));
		seconds = seconds - minuten*60;
		
		String string = seconds+" seconds";
		if (minuten != 0)
			string = minuten+" minutes, "+string;
		if (uren != 0)
			string = uren+" hours, "+string;
		if (dagen != 0)
			string = dagen+" days, "+string;
		return string;
	}
	
}
