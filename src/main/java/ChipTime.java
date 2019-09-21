import com.mtecresults.mylapstcpserver.domain.Passing;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ChipTime {

	private static final ThreadLocal<SimpleDateFormat> timeFormat = new ThreadLocal<SimpleDateFormat>(){
		@Override
		public SimpleDateFormat get() {
			SimpleDateFormat sd = new SimpleDateFormat("HH:mm:ss.SSS");
			//sd.setTimeZone(TimeZone.getTimeZone("UTC"));
			return sd;
		}
	};

	private final String chipCode;
	private final String time;
	private final String locationName;
	
	public ChipTime(String chipCode, String time, String locationName) {
		String chipCodeTemp = chipCode;
		try {
			Integer i = Integer.parseInt(chipCode);
			chipCodeTemp = ""+i;
		}
		catch(NumberFormatException nfe){
			//ignore
		}
		this.chipCode = chipCodeTemp;
		this.time = time;
		this.locationName = locationName;
	}

	public static ChipTime fromPassing(Passing p){
		return new ChipTime(p.getChipcode(), timeFormat.get().format(new Date(p.getTimeMillis())), p.getLocationName());
	}



	public final String getLocationName() {
		return locationName;
	}
	
	public String toFormattedString() {
		return "RSBCI,"+chipCode+","+ time +","+ locationName +"\r\n";
	}
}
