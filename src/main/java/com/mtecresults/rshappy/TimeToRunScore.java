package com.mtecresults.rshappy;

import com.google.common.base.Charsets;
import com.google.common.collect.*;
import com.mtecresults.mylapstcpserver.controller.ServerDataHandler;
import com.mtecresults.mylapstcpserver.domain.DataHandlingException;
import com.mtecresults.mylapstcpserver.domain.Passing;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Log
public class TimeToRunScore extends ServerDataHandler {

    private final int mylapsPort;
    private final String runscoreAddress;
    private final int runscorePort;
    private final int socketTimeout;

    private final Object socketLocker = new Object();
    private final SetMultimap<String, String> locationsMap = MultimapBuilder.hashKeys().hashSetValues().build();

    private Socket sendSocket;
    private BufferedWriter writer;

    private final static ThreadLocal<SimpleDateFormat> hhmmss100Format = ThreadLocal.withInitial(() -> {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);
        //sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf;
    });

    @Override
    public void handlePassings(Collection<Passing> passings) throws DataHandlingException {
        log.fine("Send data to: " + runscoreAddress + " Data: " + passings);
        try {
            sendToServer(passings);
        }
        catch(IOException io){
            throw new DataHandlingException("Error sending to RunScore, probably network drop", io);
        }
    }

    private void sendToServer(Collection<Passing> passings) throws IOException {
        checkSocket();

        for (Passing p : passings) {
            Set<String> runscoreLocations = getLocationsForPassing(p);
            for(String runscoreLocation: runscoreLocations) {
                String runscoreFormat = passingToRunScoreFormat(p, runscoreLocation);
                writer.write(runscoreFormat);
            }
        }
        writer.flush();
    }

    private Set<String> getLocationsForPassing(Passing p){
        String passingLocation = p.getLocationName();
        if(!locationsMap.containsKey(passingLocation)){
            String[] runscoreLocations = passingLocation.split("\\Q-\\E")[0].split("\\Q+\\E");
            locationsMap.putAll(passingLocation, Lists.newArrayList(runscoreLocations));

            log.info("RunScore Location Mapping Added:");
            for(String runscoreLocation: runscoreLocations){
                log.info("\t" + passingLocation + " -> " + runscoreLocation);
            }
        }
        return locationsMap.get(passingLocation);
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

    private String passingToRunScoreFormat(Passing p, String runscoreLocation){
        String chipcodeInt = p.getChipcode();
        try{
            chipcodeInt  = "" + Integer.valueOf(chipcodeInt);
        }
        catch(NumberFormatException nfe){
            //oops, this is CC or PC.  Leave as is
            return "RSCI," + chipcodeInt + "," + getFormattedTimeHHMMSS100(p.getTimeMillis()) + "," + runscoreLocation + "\r\n";
        }
        return "RSBI," + chipcodeInt + "," + getFormattedTimeHHMMSS100(p.getTimeMillis()) + "," + runscoreLocation + "\r\n";
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
