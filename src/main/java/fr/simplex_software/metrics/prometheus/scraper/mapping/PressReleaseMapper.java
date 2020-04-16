package fr.simplex_software.metrics.prometheus.scraper.mapping;

import fr.simplex_software.metrics.prometheus.scraper.data.*;
import fr.simplex_software.metrics.prometheus.scraper.model.*;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface PressReleaseMapper
{
  @Mappings({@Mapping(source = "pressReleaseName", target = "name")})
  PressRelease toPressRelease(PressReleaseEntity PressReleaseEntity);
  @InheritInverseConfiguration
  PressReleaseEntity fromPressRelease(PressRelease PressRelease);
}
