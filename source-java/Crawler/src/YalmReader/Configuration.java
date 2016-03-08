/**
 * Autor: Iñigo Alonso Ruiz
 * Quality supervised by: F.J. Lopez Pellicer
 */

package YalmReader;

import static java.lang.String.format;

public final class Configuration {
	private dockerOS dockerOS;
	private crawlSystem crawlSystem;

	/* Getters and setters */
	
	public dockerOS getDockerOS() {
		return dockerOS;
	}

	public crawlSystem getCrawlSystem() {
		return crawlSystem;
	}

	public void setCrawlSystem(crawlSystem crawlSystem) {
		this.crawlSystem = crawlSystem;
	}

	public void setDockerOS(dockerOS dockerOS) {
		this.dockerOS = dockerOS;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(format("DockerOS: %s\n", dockerOS))
				.append(format("crawlSystem: %s\n", crawlSystem)).toString();
	}
}