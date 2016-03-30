package es.unizar.iaaa.crawler.butler.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import es.unizar.iaaa.crawler.butler.builders.AdapterBuilder;
import es.unizar.iaaa.crawler.butler.model.CrawlConfiguration;
import es.unizar.iaaa.crawler.butler.validator.ConfigurationValidator;
import es.unizar.iaaa.crawler.butler.validator.ValidationResult;

/**
 * Building commands. This class contains every command which deals with the building of the docker
 * and crawlsystem.
 */
@Component
public class BuildingCommands implements CommandMarker {

    static Logger LOGGER = LoggerFactory.getLogger(BuildingCommands.class);
    @Autowired
    private Operations ops;


    @Autowired
    private AdapterBuilder builder;

    @Autowired
    private ConfigurationValidator configurationValidator;

    @CliAvailabilityIndicator({"config"})
    public boolean configAvailable() {
        // always available
        return true;
    }

    @CliAvailabilityIndicator({"build"})
    public boolean buildAvailable() {
        // always available
        return true;
    }


    /**
     * Config command, the one which crates every configuration file
     */
    @CliCommand(value = "config", help = "If the configuration file is ok, creates every file needed for the crawling system")
    public String config(

            @CliOption(key = {"idUser"}, mandatory = true, help = "id of the user") final String idUser,
            @CliOption(key = {"idCrawl"}, mandatory = true, help = "id of the new crawler") final String idCrawl,
            @CliOption(key = {
                    "file"}, mandatory = true, help = "The name onf the configuration file") final String configuration) {
        String id = idUser + "_" + idCrawl;

        CrawlConfiguration config = ops.readConfiguration(configuration);
        ValidationResult result = configurationValidator.validate(config);
        if (!result.isOk()) {
            return result.getFirstErrorCode().name() + ": " + result.getFirstErrorValue().toString();
        }
        try {
            builder.createConfigurationFiles(ops.readConfiguration(configuration), id);
        } catch (IOException e) {
            LOGGER.warn("IOException: "+e.getMessage(), e);
            return "File not found";
        }
        return "Configured successfully";
    }

    /**
     * Creates the docker image
     */
    @CliCommand(value = "build", help = "the directory with the files must exist")
    public String build(

            @CliOption(key = {"idUser"}, mandatory = true, help = "id of the user") final String idUser,
            @CliOption(key = {
                    "idCrawl"}, mandatory = true, help = "id of the new crawler") final String idCrawl) {
        String id = idUser + "_" + idCrawl;
        String command = "docker build -t " + id + " " + id;

        File dir = new File(id);
        if (!dir.isDirectory()) {
            return "Files don't exist, please, try executing the config command";
        }
        try {
            // docker build -t nameOfImage directory
            ops.executeCommand(command, true);
        } catch (IOException e) {
            LOGGER.warn("IOException: "+e.getMessage(), e);
            return "File not found";
        }
        return "Image built successfully";
    }

}
