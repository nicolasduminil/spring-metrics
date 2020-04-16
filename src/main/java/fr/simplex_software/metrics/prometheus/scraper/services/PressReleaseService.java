package fr.simplex_software.metrics.prometheus.scraper.services;

import fr.simplex_software.metrics.prometheus.scraper.model.*;

import java.util.*;

public interface PressReleaseService
{
  Optional<PressRelease> getPressReleaseById (int pressReleaseId);
  List<PressRelease> getPressReleases();
  boolean deletePressReleaseById(int pressReleaseId);
  Optional<PressRelease> saveOrUpdatePressRelease(PressRelease pressRelease);
}
