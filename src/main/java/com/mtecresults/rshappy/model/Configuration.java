package com.mtecresults.rshappy.model;

import lombok.Data;

@Data
public class Configuration {
    final int mylapsPort;
    final String runscoreAddress;
    final int runscorePort;
    final int sendTimeoutMS;
}
