/**
 * Autor: Iñigo Alonso Ruiz
 * Quality supervised by: F.J. Lopez Pellicer
 */

package YalmReader;
import java.util.List;
import static java.lang.String.format;

public final class crawlSystem {
	private String name;
	private String version;
    private List< String > seeds;
	private String rounds;
    private List< String > plugins;
    private String infoCrawled;
    private String maxFileLength;
    private String maxCrawlDelay;
    private String  linksLimitURL;
    private String internalLinks;
    private String queueMode;
    private Timeouts timeouts;

	/* Getters and setters */

	public List<String> getSeeds() {
		return seeds;
	}

	public void setSeeds(List<String> seeds) {
		this.seeds = seeds;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String nombre) {
		this.name = nombre;
	}

	public String getRounds() {
		return rounds;
	}

	public void setRounds(String rounds) {
		this.rounds = rounds;
	}

	public List<String> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<String> plugins) {
		this.plugins = plugins;
	}

	public String getInfoCrawled() {
		return infoCrawled;
	}

	public void setInfoCrawled(String infoCrawled) {
		this.infoCrawled = infoCrawled;
	}

	public String getMaxFileLength() {
		return maxFileLength;
	}

	public void setMaxFileLength(String maxFileLength) {
		this.maxFileLength = maxFileLength;
	}

	public String getMaxCrawlDelay() {
		return maxCrawlDelay;
	}

	public void setMaxCrawlDelay(String maxCrawlDelay) {
		this.maxCrawlDelay = maxCrawlDelay;
	}

	public String getLinksLimitURL() {
		return linksLimitURL;
	}

	public void setLinksLimitURL(String linksLimitURL) {
		this.linksLimitURL = linksLimitURL;
	}

	public String getInternalLinks() {
		return internalLinks;
	}

	public void setInternalLinks(String internalLinks) {
		this.internalLinks = internalLinks;
	}

	public String getQueueMode() {
		return queueMode;
	}

	public void setQueueMode(String queueMode) {
		this.queueMode = queueMode;
	}  

	public Timeouts getTimeouts() {
		return timeouts;
	}

	public void setTimeouts(Timeouts timeouts) {
		this.timeouts = timeouts;
	}

	@Override
	public String toString() {
        return new StringBuilder()
                .append( format( "Crawler system: %s version %s\n", getName(), getVersion() ) )
				.append( format( "	Seeds: %s\n", seeds ) )
				.append( format( "	Plugins: %s\n", plugins ) )
				.append( format( "	Links limit for URL: %s\n", rounds ) )
				.append( format( "	format of the info Crawled: %s\n", infoCrawled ) )
				.append( format( "	Number of rounds: %s\n", linksLimitURL ) )
				.append( format( "	Max Crawl delay to fetch: %s\n", maxCrawlDelay ) )
				.append( format( "	length limit for downloaded content: %s\n", maxFileLength ) )
				.append( format( "	links from the same host are ignored: %s\n", internalLinks ) )
				.append( format( "	queue Mode: %s\n", queueMode ) )
				.append( format( "	%s\n", timeouts ) )
                .toString();
      
   
	}

}