package com.cision.metrics.prometheus.scraper.config;

import io.micrometer.core.instrument.*;
import org.springframework.boot.actuate.autoconfigure.metrics.*;
import org.springframework.context.annotation.*;

@Configuration
public class ScrapperConfig
{
  @Bean
  public MeterRegistryCustomizer meterRegistryCustomizer(MeterRegistry meterRegistry)
  {
    return meterRegistry1 -> meterRegistry.config().commonTags("application", "prometheus-metrics");
  }
}
