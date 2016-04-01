package es.unizar.iaaa.crawler.butler.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Crawler commands. This class contains every command which deals with the
 * crawl system once it's built and created.
 */

@Component
public class CrawlerCommands implements CommandMarker {

	private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerCommands.class);
	@Autowired
	private Operations ops;

	@CliAvailabilityIndicator({ "start" })
	public boolean startAvailable() {
		// always available
		return true;
	}

	@CliAvailabilityIndicator({ "finished" })
	public boolean finAvailable() {
		// always available
		return true;
	}

	@CliAvailabilityIndicator({ "extract" })
	public boolean extractAvailable() {
		// always available
		return true;
	}

	@CliAvailabilityIndicator({ "run" })
	public boolean stopNutchAvailable() {
		// always available
		return true;
	}

	@CliAvailabilityIndicator({ "info" })
	public boolean infoAvailable() {
		// always available
		return true;
	}

	/**
	 * Start docker container
	 */
	@CliCommand(value = "start", help = "the docker image must be created")
	public String start(

			@CliOption(key = { "idUser" }, mandatory = true, help = "id of the user") final String idUser,
			@CliOption(key = { "idCrawl" }, mandatory = true, help = "id of the new crawler") final String idCrawl) {
		String response = "";
		if (!ops.dockerIsRunning()) {
			return "Docker is not running, please start it with sudo service docker start";
		}
		try {
			String id = idUser + "_" + idCrawl;

			if (ops.imageExists(idUser, idCrawl)) {
				// docker run -i -d nameOfImage nameOfContainer
				String comando = "";
				// if it was stopped it has to be restarted
				if (ops.containerStopped(idUser, idCrawl)) {
					comando = "docker restart " + id;
					response += "Container restarted";

				} else if (ops.containerPaused(idUser, idCrawl)) {
					comando = "docker unpause " + id;
					response += "Container unpaused";
				} else {

					comando = "docker run -i -d --name=\"" + id + "\" " + id;
					response += "Container started";

				}
				ops.executeCommand(comando, true);
			} else {
				response = "Docker image don't exist, please, try executing the build command";
			}

		} catch (Exception e) {
			response = "Files not found";
		}

		return response;
	}

	/**
	 * Run the crawl in the docker container
	 */
	@CliCommand(value = "run", help = "the docker cointainer must be running")
	public String run(

			@CliOption(key = { "idUser" }, mandatory = true, help = "id of the user") final String idUser,
			@CliOption(key = { "idCrawl" }, mandatory = true, help = "id of the new crawler") final String idCrawl) {
		String response;
		if (!ops.dockerIsRunning()) {
			return "Docker is not running, please start it with sudo service docker start";
		}
		if (ops.containerExists(idUser, idCrawl) && ops.containerRunning(idUser, idCrawl)) {
			try {
				String id = idUser + "_" + idCrawl;

				// docker exec idContainer sh crawler/run.sh
				String comando = "docker exec -d " + id + " sh crawler/run.sh";
				ops.executeCommand(comando, true);
				response = "Crawler started";

			} catch (Exception e) {
				response = "Docker container don't exist, please, try executing the start command";
			}
		} else {
			response = "Docker container don't exist, please, try executing the start command";
		}

		return response;
	}

	/**
	 * Extracts the info crawled in the docker container to an output path
	 */
	@CliCommand(value = "extract", help = "Extracts the info crawled in the docker container to an output path")
	public String extract(

			@CliOption(key = { "idUser" }, mandatory = true, help = "id of the user") final String idUser,
			@CliOption(key = { "path" }, mandatory = true, help = "output path") final String path,
			@CliOption(key = { "idCrawl" }, mandatory = true, help = "id of the new crawler") final String idCrawl) {
		String id = idUser + "_" + idCrawl;
		String command1 = "docker exec " + id + " sh crawler/juntarSalidas.sh";
		String command2 = "docker cp " + id + ":root/crawler/salida/salida " + path;
		if (!ops.dockerIsRunning()) {
			return "Docker is not running, please start it with sudo service docker start";
		}
		if (!ops.containerExists(idUser, idCrawl) || !ops.containerRunning(idUser, idCrawl)) {
			return "Docker container don't exist, please, try executing the start command";
		}

		try {
			ops.executeCommand(command1, true);
		} catch (IOException e) {
			LOGGER.warn("IOException: " + e.getMessage(), e);
			return "Docker exec failed";
		}

		try {
			ops.executeCommand(command2, true);
		} catch (IOException e) {
			LOGGER.warn("IOException: " + e.getMessage(), e);
			return "Docker cp failed";
		}

		return "Extracted correctly";
	}

	/**
	 * Returns true only if the crawler has finished
	 */
	@CliCommand(value = "finished", help = "Returns true only if the crawler has finished")
	public String finished(

			@CliOption(key = { "idUser" }, mandatory = true, help = "id of the user") final String idUser,
			@CliOption(key = { "idCrawl" }, mandatory = true, help = "id of the new crawler") final String idCrawl) {
		String id = idUser + "_" + idCrawl;
		String command = "docker exec " + id + " ls crawler/FIN";
		if (!ops.dockerIsRunning()) {
			return "Docker is not running, please start it with sudo service docker start";
		}
		if (!ops.containerExists(idUser, idCrawl) || !ops.containerRunning(idUser, idCrawl)) {
			return "Docker container don't exist, please, try executing the start command";
		}

		// docker exec idContainer sh crawler/juntarSalidas.sh
		String s;
		try (BufferedReader out = ops.executeCommand(command, false)) {
			while ((s = out.readLine()) != null) {
				if (s.contains("FIN"))
					return "Yes, the crawler has finished";
			}
		} catch (IOException e) {
			LOGGER.warn("IOException: " + e.getMessage(), e);
			return "I don't known";
		}
		return "The crawler hasn't finished yet";
	}

	/**
	 * Gives information about the crawl, if the crawl is actually running
	 */
	@CliCommand(value = "info", help = "information about the crawl")
	public String info(

			@CliOption(key = { "idUser" }, mandatory = true, help = "id of the user") final String idUser,
			@CliOption(key = { "idCrawl" }, mandatory = true, help = "id of the new crawler") final String idCrawl) {
		String id = idUser + "_" + idCrawl;
		String command = "docker exec " + id + " ls crawler/STARTED";
		if (!ops.dockerIsRunning()) {
			return "Docker is not running, please start it with sudo service docker start";
		}
		if (!ops.containerExists(idUser, idCrawl) || !ops.containerRunning(idUser, idCrawl)) {
			return "Docker container don't exist, please, try executing the start command";
		}

		// docker exec idContainer sh crawler/juntarSalidas.sh
		String s;
		try (BufferedReader out = ops.executeCommand(command, false)) {
			while ((s = out.readLine()) != null) {
				if (s.contains("STARTED"))
					return "The crawler is running";
			}
		} catch (IOException e) {
			LOGGER.warn("IOException: " + e.getMessage(), e);
			return "I don't known";
		}
		return "The crawler is not running";
	}

}