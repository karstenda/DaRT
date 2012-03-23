package raytracer.distributed;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public abstract class DistributedExecutor {

	protected static final String[] hosts = new String[] {
		
//		"verviers.cs.kotnet.kuleuven.be",
//		"marche.cs.kotnet.kuleuven.be",
//		"peer.cs.kotnet.kuleuven.be", 
//		"kortrijk.cs.kotnet.kuleuven.be",
//		"dilbeek.cs.kotnet.kuleuven.be",
//		"andenne.cs.kotnet.kuleuven.be",
//		"bastogne.cs.kotnet.kuleuven.be",
//		"kortrijk.cs.kotnet.kuleuven.be",
//		"beringen.cs.kotnet.kuleuven.be",
////		"bilzen.cs.kotnet.kuleuven.be",
//		"ciney.cs.kotnet.kuleuven.be",
////		"knokke.cs.kotnet.kuleuven.be",
//		"jemeppe.cs.kotnet.kuleuven.be",
//		"herstal.cs.kotnet.kuleuven.be",
////		"orval.cs.kotnet.kuleuven.be",
//		"ninove.cs.kotnet.kuleuven.be",
//		"mol.cs.kotnet.kuleuven.be",
//		"seraing.cs.kotnet.kuleuven.be",
////		"tubize.cs.kotnet.kuleuven.be",
//		"virton.cs.kotnet.kuleuven.be",

//		"charleroi.cs.kotnet.kuleuven.be",
//		"maaseik.cs.kotnet.kuleuven.be",
//		"durbuy.cs.kotnet.kuleuven.be", 
//		"eupen.cs.kotnet.kuleuven.be",
//		"waver.cs.kotnet.kuleuven.be",
//		"temse.cs.kotnet.kuleuven.be",
//		"gent.cs.kotnet.kuleuven.be",
//		"laarne.cs.kotnet.kuleuven.be",
//		"ronse.cs.kotnet.kuleuven.be",
//		"ieper.cs.kotnet.kuleuven.be",
//		"tremelo.cs.kotnet.kuleuven.be",
//		"olen.cs.kotnet.kuleuven.be",
//		"dour.cs.kotnet.kuleuven.be",
//		"bierbeek.cs.kotnet.kuleuven.be",
//		"nijvel.cs.kotnet.kuleuven.be",
//		"torhout.cs.kotnet.kuleuven.be",
//		"spa.cs.kotnet.kuleuven.be",
//		"martelange.cs.kotnet.kuleuven.be",
//		"lier.cs.kotnet.kuleuven.be",
//		"turnhout.cs.kotnet.kuleuven.be",
	
		"yvoir.cs.kotnet.kuleuven.be",
		"heers.cs.kotnet.kuleuven.be",
		"genk.cs.kotnet.kuleuven.be",
		"veurne.cs.kotnet.kuleuven.be",
		"dinant.cs.kotnet.kuleuven.be",
		"bevekom.cs.kotnet.kuleuven.be",
		"tienen.cs.kotnet.kuleuven.be",
		"aarlen.cs.kotnet.kuleuven.be",
		"schoten.cs.kotnet.kuleuven.be",
		"aalst.cs.kotnet.kuleuven.be",
		"mechelen.cs.kotnet.kuleuven.be",
		"heist.cs.kotnet.kuleuven.be",
		"riemst.cs.kotnet.kuleuven.be",
		"musson.cs.kotnet.kuleuven.be",
		"vloesberg.cs.kotnet.kuleuven.be",
		"hove.cs.kotnet.kuleuven.be",
		"diest.cs.kotnet.kuleuven.be",
		"doornik.cs.kotnet.kuleuven.be",
		"binche.cs.kotnet.kuleuven.be",
		"balen.cs.kotnet.kuleuven.be",
		
		
//		"zwalm.cs.kotnet.kuleuven.be",
//		"hamme.cs.kotnet.kuleuven.be",
//		"malle.cs.kotnet.kuleuven.be",
//		"alken.cs.kotnet.kuleuven.be",
//		"voeren.cs.kotnet.kuleuven.be",
//		"hastiere.cs.kotnet.kuleuven.be",
//		"rochefort.cs.kotnet.kuleuven.be",
//		"komen.cs.kotnet.kuleuven.be",
//		"borgworm.cs.kotnet.kuleuven.be",
//		"aubel.cs.kotnet.kuleuven.be",
//		"stavelot.cs.kotnet.kuleuven.be",
//		"libramont.cs.kotnet.kuleuven.be",
//		"herent.cs.kotnet.kuleuven.be",
//		"lanaken.cs.kotnet.kuleuven.be",
//		"fleurus.cs.kotnet.kuleuven.be",
//		"vielsalm.cs.kotnet.kuleuven.be",
		
		
	};
	
	

	
	protected static String keyLocation = "/home/s0202187/.ssh/id_rsa";
	protected static File keyfile = new File(keyLocation);
	protected static String keyfilePass;
	protected static String username;
	
	protected static String title= "RemoteExecutor";
	
	protected abstract RemoteJob[] getRemoteJobs();
	
	protected abstract DistributedProgressUpdater getDistributedProgressUpdater();
	
	public void run(String userName, String keyFilePass) {
		username = userName;
		keyfilePass = keyFilePass;
		startExecuting(getRemoteJobs());
	}
	
	public void askLoginDetailsAndRun() {
		
		final JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final JTextField usernameField = new JTextField(20);
		usernameField.setText("s0202187");
		final JPasswordField passwordField = new JPasswordField(20);

		JButton button = new JButton("Start");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(false);
				run(usernameField.getText(),new String(passwordField.getPassword()));
			}
		});
		
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new GridLayout(2, 2));
		innerPanel.add(new JLabel("username: "));
		innerPanel.add(usernameField);
		innerPanel.add(new JLabel("password private key file: "));
		innerPanel.add(passwordField);
		
		Container container = frame.getContentPane();
		container.add(new JLabel("using private key: "+keyLocation), BorderLayout.NORTH);
		container.add(innerPanel, BorderLayout.CENTER);
		container.add(button, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
	}
	
	
	private void startExecuting(RemoteJob[] jobs) {

		
		// Start timing
		System.out.println("=====> CONNECTING TO ALL HOSTS <=======");
		long startMillies = System.currentTimeMillis();
		
		RemoteJobDistributor distributor = new RemoteJobDistributor(Arrays.asList(jobs));
		DistributedProgressUpdater progressUpdater = getDistributedProgressUpdater();
		
		// Register all the remotejobs in the threadpool
		ExecutorService pool = Executors.newFixedThreadPool(hosts.length);
		for (int i = 0; i < hosts.length; i++) {
			pool.submit(new RemoteJobExecutor(hosts[i], username, keyfilePass, keyfile, distributor,progressUpdater));
		}
		pool.shutdown();
		
		// Start all the RemoteJobs
		try {
			// Wait a while for existing tasks to terminate
			if (!pool.awaitTermination(7, TimeUnit.DAYS)) {

				// Cancel currently executing tasks
				pool.shutdownNow();

				// Wait a while for tasks to respond to being cancelled
				if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
					System.err.println("Pool did not terminate");
				}

			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			pool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
			ie.printStackTrace();
		}
		
		
		
		System.out.println("=======> All RemoteJobs are done <======");
		long seconds = Math.round((System.currentTimeMillis()-startMillies)/1000);
		System.out.println("Time passed sine: "+formatMilliesToString(seconds));
		System.exit(0);
	}
	
	private static String formatMilliesToString(long seconds) {
		
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
