package com.appsecco.dvja.controllers;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PingAction extends BaseController {

    private String address;
    private String commandOutput;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCommandOutput() {
        return commandOutput;
    }

    public void setCommandOutput(String commandOutput) {
        this.commandOutput = commandOutput;
    }

    public String execute() {
        if(StringUtils.isEmpty(getAddress()))
            return INPUT;

        try {
            doExecCommand();
        } catch (Exception e) {
            addActionMessage("Error running command: " + e.getMessage());
        }

        return SUCCESS;
    }

    private void doExecCommand() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        //TODO Swap cmd.exe for relevant command runner for your OS if not on Windows
        String[] command ={
                "cmd.exe",
                "/c echo This is a very safe operation From: " + getAddress()
        };
        Process process = runtime.exec(command);

        BufferedReader  stdinputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        String output = "Output:\n\n";

        while((line = stdinputReader.readLine()) != null)
            output += line + "\n";

        output += "\n";
        output += "Error:\n\n";

        stdinputReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        while((line = stdinputReader.readLine()) != null)
            output += line + "\n";

        setCommandOutput(output);
    }
}
