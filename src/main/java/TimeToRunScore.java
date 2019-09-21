import com.google.common.base.Charsets;
import com.mtecresults.mylapstcpserver.controller.ServerDataHandler;
import com.mtecresults.mylapstcpserver.domain.Passing;
import lombok.Data;
import lombok.extern.java.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

@Data
@Log
public class TimeToRunScore extends ServerDataHandler {

    private final int mylapsPort;
    private final String runscoreAddress;
    private final int runscorePort;
    private final int socketTimeout;

    private final Object socketLocker = new Object();
    private Socket sendSocket;
    private BufferedWriter writer;

    private final static ThreadLocal<SimpleDateFormat> hhmmss100Format = ThreadLocal.withInitial(() -> {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf;
    });

    @Override
    public void handlePassings(Collection<Passing> passings) throws RuntimeException {
        log.fine("Send data to: " + runscoreAddress + " Data: " + passings);
        try {
            sendToServer(passings);
        }
        catch(IOException io){
            throw new RuntimeException(io);
        }
    }

    private void sendToServer(Collection<Passing> passings) throws IOException {
        checkSocket();

        for (Passing p : passings) {
            String runscoreFormat = passingToRunScoreFormat(p);
            log.warning("Passing: "+runscoreFormat);
            writer.write(runscoreFormat);
        }
        writer.flush();
    }

    private void checkSocket() throws IOException {
        synchronized (socketLocker) {
            if (sendSocket == null || (sendSocket.isClosed() || !sendSocket.isConnected() || sendSocket.isOutputShutdown())) {
                sendSocket = new Socket();
                sendSocket.connect(new InetSocketAddress(runscoreAddress, runscorePort), socketTimeout);
                writer = new BufferedWriter(new OutputStreamWriter(sendSocket.getOutputStream(), Charsets.UTF_8));
            }
        }
    }

    private String passingToRunScoreFormat(Passing p){
        String chipcodeInt = p.getChipcode();
        try{
            chipcodeInt  = "" + Integer.valueOf(chipcodeInt);
        }
        catch(NumberFormatException nfe){
            //oops, this is CC or PC.  Leave as is
        }
        return "RSBI," + chipcodeInt + "," + getFormattedTimeHHMMSS100(p.getTimeMillis()) + "," + p.getLocationName() + "\r\n";
    }


    public static String getFormattedTimeHHMMSS100(long time) {
        return hhmmss100Format.get().format(new Date(time));
    }

    @Override
    public String getServerName() {
        return "rs-happy";
    }

    @Override
    public int getServerPort() {
        return mylapsPort;
    }
}
