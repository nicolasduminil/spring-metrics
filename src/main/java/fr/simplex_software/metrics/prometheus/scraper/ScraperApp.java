package fr.simplex_software.metrics.prometheus.scraper;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

@SpringBootApplication
public class ScraperApp
{
  public static void main(String[] args)
  {
    SpringApplication.run(ScraperApp.class, args);
  }
}
