
import com.mtecresults.mylapstcpserver.controller.MyLapsTCPServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Driver {

	private static Logger log = Logger.getLogger(Driver.class.toString());

	public static String defaultPath = Paths.get(".").toAbsolutePath().normalize().toString();
	
	public static void main(String[] args) throws IOException {
		Logger.getGlobal().setLevel(Level.INFO);
		System.setProperty("java.util.logging.SimpleFormatter.format", 
	            "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
				
		log.info("TCP to File running");
		log.info("Saving to files to: "+defaultPath);

		TimeToFile timeToFile = new TimeToFile();
		new Thread(timeToFile).start();

		new MyLapsTCPServer(timeToFile);
	}
}
