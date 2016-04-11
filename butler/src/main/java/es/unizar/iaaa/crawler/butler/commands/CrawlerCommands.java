package es.unizar.iaaa.crawler.butler.commands;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import es.unizar.iaaa.crawler.butler.index.IndexFiles;
import es.unizar.iaaa.crawler.butler.index.SearchFiles;
import es.unizar.iaaa.crawler.butler.model.SearchResult;

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

	@CliAvailabilityIndicator({ "index" })
	public boolean indexAvailable() {
		// always available
		return true;
	}

	@CliAvailabilityIndicator({ "search" })
	public boolean searchAvailable() {
		// always available
		return true;
	}

	@CliAvailabilityIndicator({ "finished" })
	public boolean finAvailable() {
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

			@CliOption(key = { "idProject" }, mandatory = true, help = "id of the idProject") final String idProject,
			@CliOption(key = { "imageName" }, mandatory = true, help = "name of the image") final String imageName,
			@CliOption(key = {
					"containerName" }, mandatory = true, help = "name of the container") final String containerName) {
		String response = "";
		String idImage = idProject + "_" + imageName;
		String idcontainer = idProject + "_" + imageName + "_" + containerName;

		if (!ops.dockerIsRunning()) {
			return "Docker is not running, please start it with sudo service docker start";
		}
		if (ops.containerRunning(idcontainer)) {
			return "The container is already running";
		}
		try {

			if (ops.imageExists(idImage)) {
				// docker run -i -d nameOfImage nameOfContainer
				String comando = "";
				// if it was stopped it has to be restarted
				if (ops.containerStopped(idcontainer)) {
					comando = "docker restart " + idcontainer;
					response += "Container restarted";

				} else if (ops.containerPaused(idcontainer)) {
					comando = "docker unpause " + idcontainer;
					response += "Container unpaused";
				} else {

					comando = "docker run -i -d --name=\"" + idcontainer + "\" " + idImage;
					response += "Container started";

				}
				ops.executeCommand(comando, true);
			} else {
				response = "Docker image don't exist, please, try executing the build command";
			}
			LOGGER.info(response + " " + idcontainer);

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
			@CliOption(key = { "idProject" }, mandatory = true, help = "id of the idProject") final String idProject,
			@CliOption(key = { "imageName" }, mandatory = true, help = "name of the image") final String imageName,
			@CliOption(key = {
					"containerName" }, mandatory = true, help = "name of the container") final String containerName) {
		String response = "";
		String idImage = idProject + "_" + imageName;
		String idcontainer = idProject + "_" + imageName + "_" + containerName;

		if (!ops.dockerIsRunning()) {
			return "Docker is not running, please start it with sudo service docker start";
		}
		if (ops.containerExists(idcontainer) && ops.containerRunning(idcontainer)) {
			try {

				// docker exec idContainer sh crawler/run.sh
				String comando = "docker exec -d " + idcontainer + " sh crawler/run.sh";
				ops.executeCommand(comando, true);
				response = "Crawler started";
				LOGGER.info(response + " " + idcontainer);

			} catch (Exception e) {
				LOGGER.warn("IOException: " + e.getMessage(), e);
				response = "Docker container don't exist, please, try executing the start command";
			}
		} else {
			response = "Docker container don't exist, please, try executing the start command";
		}

		return response;
	}

	/**
	 * Extracts the info crawled in the docker container and index it
	 */
	@CliCommand(value = "index", help = "Extracts the info crawled in the docker container and index it")
	public String index(
			@CliOption(key = { "idProject" }, mandatory = true, help = "id of the idProject") final String idProject,
			@CliOption(key = { "imageName" }, mandatory = true, help = "name of the image") final String imageName,
			@CliOption(key = {
					"containerName" }, mandatory = true, help = "name of the container") final String containerName) {
		String response = "";
		String idImage = idProject + "_" + imageName;
		String idcontainer = idProject + "_" + imageName + "_" + containerName;

		String command1 = "docker exec " + idcontainer + " sh crawler/juntarSalidas.sh";
		String indexPath = idcontainer + "_index";
		Path outputPath = Paths.get(indexPath);

		// If the folder exists, delete it (rewrite the index)
		try {
			FileUtils.deleteDirectory(outputPath.toFile());
			Files.createDirectory(outputPath);

		} catch (IOException e1) {
			LOGGER.warn("IOException: " + e1.getMessage(), e1);
			return "Failing creating the index folder";
		}
		String command2 = "docker cp " + idcontainer + ":root/crawler/salida/salida " + indexPath + "/output.txt";
		if (!ops.dockerIsRunning()) {
			return "Docker is not running, please start it with sudo service docker start";
		}
		if (!ops.containerExists(idcontainer) || !ops.containerRunning(idcontainer)) {
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

		// Index
		IndexFiles nuevo = new IndexFiles();
		nuevo.index(idcontainer + "_index/index", new File(idcontainer + "_index/output.txt"));

		// Ahora este índice está más actualizado o igual que el de docker, así
		// que se borra que el indice está pendiente
		// en el contendor respecto a el del sistema
		command1 = "docker exec " + idcontainer + " rm crawler/IndexPending";
		try {
			ops.executeCommand(command1, false);
		} catch (IOException e) {
			LOGGER.warn("IOException: " + e.getMessage(), e);
			return "Docker exec failed";
		}
		LOGGER.info("Indexed correctly " + idcontainer);

		return "Indexed correctly";
	}

	/**
	 * Returns true only if the crawler has finished
	 */
	@CliCommand(value = "finished", help = "Returns true only if the crawler has finished")
	public String finished(
			@CliOption(key = { "idProject" }, mandatory = true, help = "id of the idProject") final String idProject,
			@CliOption(key = { "imageName" }, mandatory = true, help = "name of the image") final String imageName,
			@CliOption(key = {
					"containerName" }, mandatory = true, help = "name of the container") final String containerName) {
		String response = "";
		String idImage = idProject + "_" + imageName;
		String idcontainer = idProject + "_" + imageName + "_" + containerName;

		String command = "docker exec " + idcontainer + " ls crawler";
		if (!ops.dockerIsRunning()) {
			return "Docker is not running, please start it with sudo service docker start";
		}
		if (!ops.containerExists(idcontainer) || !ops.containerRunning(idcontainer)) {
			return "Docker container don't exist, please, try executing the start command";
		}

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
			@CliOption(key = { "idProject" }, mandatory = true, help = "id of the idProject") final String idProject,
			@CliOption(key = { "imageName" }, mandatory = true, help = "name of the image") final String imageName,
			@CliOption(key = {
					"containerName" }, mandatory = true, help = "name of the container") final String containerName) {
		String response = "";
		String idImage = idProject + "_" + imageName;
		String idcontainer = idProject + "_" + imageName + "_" + containerName;

		String command = "docker exec " + idcontainer + " ls crawler";
		if (!ops.dockerIsRunning()) {
			return "Docker is not running, please start it with sudo service docker start";
		}
		if (!ops.containerExists(idcontainer) || !ops.containerRunning(idcontainer)) {
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

	/**
	 * search in the index a query
	 */
	@CliCommand(value = "search", help = "search in the index a query, you can specify the maximun number of results with the 'top' argument")
	public String search(
			@CliOption(key = { "idProject" }, mandatory = true, help = "id of the idProject") final String idProject,
			@CliOption(key = { "imageName" }, mandatory = true, help = "name of the image") final String imageName,
			@CliOption(key = {
					"top" }, mandatory = false, help = "number of Max results that will be shown") final Integer max,
			@CliOption(key = {
					"query" }, mandatory = true, help = "the query is going to be search") final String query,

			@CliOption(key = {
					"containerName" }, mandatory = true, help = "name of the container") final String containerName) {
		String response = "";
		String idImage = idProject + "_" + imageName;
		String idcontainer = idProject + "_" + imageName + "_" + containerName;

		if (!ops.dockerIsRunning()) {
			return "Docker is not running, please start it with sudo service docker start";
		}
		if (!ops.containerExists(idcontainer) || !ops.containerRunning(idcontainer)) {
			return "Docker container don't exist, please, try executing the start command";
		}
		// Check if there is any index locally pending in the container
		String command = "docker exec " + idcontainer + " ls crawler";
		String s;
		try (BufferedReader out = ops.executeCommand(command, false)) {
			while ((s = out.readLine()) != null) {
				if (s.contains("IndexPending")) {
					// copy the index files
					String indexPath = idcontainer + "_index";
					Path outputPath = Paths.get(indexPath);
					try {
						FileUtils.deleteDirectory(outputPath.toFile());
						Files.createDirectory(outputPath);

					} catch (IOException e1) {
						return "Failing managing the index folder";
					}
					// copy the index to the system
					command = "docker cp " + idcontainer + ":root/crawler/index " + indexPath + "/index";
					ops.executeCommand(command, true);
					// It is not pending now
					command = "docker exec " + idcontainer + " rm crawler/IndexPending";
					ops.executeCommand(command, false);
				}

			}
		} catch (IOException e) {
			LOGGER.warn("IOException: " + e.getMessage(), e);
		}

		SearchFiles searcher = new SearchFiles();
		try {
			ArrayList<SearchResult> result = searcher.search(idcontainer + "_index/", query);
			// return result.size() > 0 ? "best match "+result.get(0).getUrl() :
			// "no matches";
			if (result.size() > 0) {
				// paging results
				for (int i = 0; i < result.size() && (max == null || i < max); i++) {
					System.out.println(result.get(i).getUrl());
				}
			}
			return "Results shown";
		} catch (Exception e) {
			LOGGER.warn("Exception : " + e.getMessage(), e);
			return "Search failed, try indexing first";
		}

	}

}