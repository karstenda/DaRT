package raytracer.engine;

public class ProgressUpdater {

	
	private long milliesSinceStart = System.currentTimeMillis();
	private String subject;
	
	public ProgressUpdater(String subject) {
		this.subject = subject;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void reset() {
		milliesSinceStart = System.currentTimeMillis();
	}
	
	public void updateProgress(float i, float length) {
		updateProgress(i/length);
	}
	
	public void updateProgress(float prog) {
		int progress = (int) (prog*100f);
		float diff = (System.currentTimeMillis()-milliesSinceStart);
		float avgRayTime = diff/prog;
		long timeTodo = Math.round((avgRayTime*(1-prog))/1000);
		System.out.println("-> "+subject+" progress: "+progress+"%     (ETL: "+formatMilliesToString(timeTodo)+")");
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
