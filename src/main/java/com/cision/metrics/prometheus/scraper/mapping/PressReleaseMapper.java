package com.cision.metrics.prometheus.scraper.mapping;

import com.cision.metrics.prometheus.scraper.data.*;
import com.cision.metrics.prometheus.scraper.model.*;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface PressReleaseMapper
{
  @Mappings({@Mapping(source = "pressReleaseName", target = "name")})
  PressRelease toPressRelease(PressReleaseEntity PressReleaseEntity);
  @InheritInverseConfiguration
  PressReleaseEntity fromPressRelease(PressRelease PressRelease);
}
