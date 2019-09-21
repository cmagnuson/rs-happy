import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.mtecresults.mylapstcpserver.controller.MyLapsTCPServer;
import com.mtecresults.mylapstcpserver.controller.ServerDataHandler;
import com.mtecresults.mylapstcpserver.domain.Passing;


public class TimeToFile extends ServerDataHandler implements Runnable {

	private final ArrayBlockingQueue<ChipTime> toProcess = new ArrayBlockingQueue<ChipTime>(10000000);

	private final ArrayBlockingQueue<String> toProcessLocation = new ArrayBlockingQueue<String>(10000);

	private static final Logger log = Logger.getLogger(TimeToFile.class.toString());

	private static final String RS_EXTENSION = "_LOG.TXT";

	public boolean addTimes(Collection<ChipTime> times){
		return toProcess.addAll(times);
	}

	@Override
	public void run() {
		while(true){
			if(!toProcess.isEmpty()){
				ArrayList<ChipTime> times = new ArrayList<>();
				toProcess.drainTo(times);

				ArrayListMultimap<String, ChipTime> locationsToChips = ArrayListMultimap.create();
				for(ChipTime ct: times){
					locationsToChips.put(ct.getLocationName(), ct);
				}

				for(String locationName: locationsToChips.keySet()){
					if(!writeToFile(locationName, locationsToChips.get(locationName))){
						boolean success = toProcess.addAll(locationsToChips.get(locationName));
						if(!success){
							log.severe("Unable to retry "+locationsToChips.get(locationName).size()+" times for location: "+locationName);
						}
					}
				}
			}
			if(!toProcessLocation.isEmpty()){
				ArrayList<String> locations = new ArrayList<String>();
				toProcessLocation.drainTo(locations);
				for(String locationName: locations){
					List<ChipTime> emptyList = Collections.emptyList();
					writeToFile(locationName, emptyList);
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.warning("Unexpected interruption in TimeToFile");
			}
		}			
	}

	private boolean writeToFile(String filename, Collection<ChipTime> times) {
		String filePath = Driver.defaultPath + File.separator + filename + RS_EXTENSION;

		try(FileWriter fw = new FileWriter(filePath, true)){
			for(ChipTime ct: times){
				fw.append(ct.toFormattedString());
			}
			return true;
		}
		catch(FileNotFoundException fnfe){
			log.info("Unable to write file: "+filename+" probably being read by RunScore, will retry");
			return false;
		}
		catch(IOException io){
			log.log(Level.SEVERE, "Error writing times to file for location: "+filename, io);
			return false;
		}
	}

	@Override
	public void handlePassings(Collection<Passing> passings) {
		List<ChipTime> chips = new ArrayList(passings.size());
		for(Passing p: passings){
			chips.add(ChipTime.fromPassing(p));
		}
		addTimes(chips);
	}

	@Override
	public String getServerName() {
		return "rs-happy";
	}

	@Override
	public int getServerPort() {
		return 3097;
	}
}
