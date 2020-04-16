package com.cision.metrics.prometheus.scraper.config;

import org.springframework.context.annotation.*;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.*;
import springfox.documentation.spring.web.plugins.*;
import springfox.documentation.swagger2.annotations.*;

import java.util.*;

@Configuration
@EnableSwagger2
public class SwaggerConfig
{
  ApiInfo apiInfo()
  {
    return new ApiInfo(      "Cision Press Release Operation API",
      "Rest API for demonstrating how to instrumentalize Spring Boot services with Prometheus.",
      "1.0","Terms of service",
      new Contact("Nicolas DUMINIL", "www.cision.com", "nicolas.duminil@cision.com"),
      "License of API", "API license URL", Collections.emptyList());
  }

  @Bean
  public Docket configureControllerPackageAndConvertors()
  {
    return new Docket(DocumentationType.SWAGGER_2)
      .select()
      .apis(RequestHandlerSelectors.any()).build()
      .directModelSubstitute(org.joda.time.LocalDate.class, java.sql.Date.class)
      .directModelSubstitute(org.joda.time.DateTime.class, java.util.Date.class)
      .apiInfo(apiInfo());
  }
}
