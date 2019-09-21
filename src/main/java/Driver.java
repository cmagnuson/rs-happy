
import com.mtecresults.mylapstcpserver.controller.MyLapsTCPServer;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log
public class Driver {

	public static String defaultPath = Paths.get(".").toAbsolutePath().normalize().toString();
	
	public static void main(String[] args) throws IOException {
		Logger.getGlobal().setLevel(Level.INFO);
		System.setProperty("java.util.logging.SimpleFormatter.format", 
	            "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
				
		log.info("rs-happy T+S to RunScore running");
		log.info("Saving to files to: "+defaultPath);

		TimeToRunScore timeToRunScore = new TimeToRunScore(3098, "localhost", 4001, 5_000);
		new MyLapsTCPServer(timeToRunScore);
	}
}
