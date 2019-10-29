package com.mtecresults.rshappy.view;

import com.mtecresults.rshappy.model.Configuration;

import javax.swing.*;

public class GUIConfiguration {

    public static Configuration run(final Configuration configuration) {
        JSpinner mylapsPort = new JSpinner(new SpinnerNumberModel(configuration.getMylapsPort(), 0, 60_000, 1));
        JTextField runscoreAddress = new JTextField(configuration.getRunscoreAddress());
        JSpinner runscorePort = new JSpinner(new SpinnerNumberModel(configuration.getRunscorePort(), 0, 60_000, 1));

        Object[] message = {
                "T+S Port: ", mylapsPort,
                "RunScore IP Address:", runscoreAddress,
                "RunScore (Open Protocol) Port:", runscorePort
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Enter all your values", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION){
            return new Configuration((int)mylapsPort.getValue(), runscoreAddress.getText(), (int)runscorePort.getValue(),
                    //just pass this value along, don't expose in GUI
                    configuration.getSendTimeoutMS());
        }
        else{
            return null;
        }
    }
}
