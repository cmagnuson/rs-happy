package com.mtecresults.rshappy;

import com.mtecresults.mylapstcpserver.controller.MyLapsTCPServer;
import lombok.extern.java.Log;
import picocli.CommandLine;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log
@CommandLine.Command(description = "MyLaps TCP to RunScore Open Converter - rs-happy",
		name = "rs-happy", mixinStandardHelpOptions = true, version = "rs-happy " + Version.BUILD_VERSION + " " + Version.BUILD_DATE)
public class Driver implements Callable<Void> {

	@CommandLine.Option(names = {"--runscore-address"}, description = "RunScore IP/Hostname")
	protected String runscoreAddress = "localhost";

	@CommandLine.Option(names = {"--runscore-port"}, description = "RunScore Port")
	protected int runscorePort = 4001;

	@CommandLine.Option(names = {"--mylaps-port"}, description = "MyLaps Listen Port")
	protected int mylapsPort = 3097;

	@CommandLine.Option(names = {"--send-timeout"}, description = "Send Timeout (ms)")
	protected int sendTimeoutMS = 5_000;

	@CommandLine.Option(names = {"--debug"}, description = "Enable Debug Mode")
	protected boolean debug = false;

	@Override
	public Void call() throws Exception {
		Logger.getGlobal().setLevel(debug ? Level.FINER : Level.INFO);
		System.setProperty("java.util.logging.SimpleFormatter.format", 
	            "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
				
		log.info("rs-happy T+S to RunScore running");
		log.info("Listening for T+S feed on port: "+mylapsPort);
		log.info("Playing RunScore Open feed to: "+runscoreAddress+":"+runscorePort);

		TimeToRunScore timeToRunScore = new TimeToRunScore(mylapsPort, runscoreAddress, runscorePort, sendTimeoutMS);
		new MyLapsTCPServer(timeToRunScore);

		return null;
	}

	public static void main(String[] args) {
		CommandLine.call(new Driver(), args);
	}
}
