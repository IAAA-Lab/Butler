/**
 * Autor: Iñigo Alonso Ruiz
 * Quality supervised by: F.J. Lopez Pellicer
 */

package es.unizar.iaaa.crawler.butler.builders;

import es.unizar.iaaa.crawler.butler.model.CrawlConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/*
 * Builds the system. Depending the configuration the adapter will choose the correct builders
 */
@Component
public class AdapterBuilder {

    @Autowired
    private NutchBuilder builder;

    @Autowired
    private DockerBuilder dockerBuilder;

    @Autowired
    private ApplicationContext ctx;


    // TODO @Iñigo Documentar en inglés
    /* Crear ficheros de configuration dependiendo del sistema de crawling */
    public void createConfigurationFiles(CrawlConfiguration configuration, String outputDir) {


        // TODO @Iñigo Documentar en inglés
                /*
                 * Aquí si hubiera varias posibilidades de sistemas de crawling
				 * o OS's para docker, el adaptador
				 */
        File theDir = new File(outputDir);
        theDir.mkdir();
        // TODO @Iñigo Documentar en inglés
                /*
				 * Primero se llama al builder de nutch y después al de docker
				 */
        builder.createNutchSite(configuration, outputDir);

        try {
            dockerBuilder.crearDockerfile(configuration, ctx.getResource("classpath:/templates").getFile(), outputDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
