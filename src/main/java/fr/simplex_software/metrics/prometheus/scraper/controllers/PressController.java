package fr.simplex_software.metrics.prometheus.scraper.controllers;

import fr.simplex_software.metrics.prometheus.scraper.model.*;
import fr.simplex_software.metrics.prometheus.scraper.services.*;
import io.micrometer.core.annotation.*;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class PressController
{
  private PressReleaseService pressReleaseService;

  @Autowired
  public PressController(PressReleaseService pressReleaseService)
  {
    this.pressReleaseService = pressReleaseService;
  }

  @GetMapping("/all")
  @Timed(value = "metrics.pressRelease.getAll", histogram = true, percentiles = {0.95, 0.99}, extraTags = {"version", "1.0"})
  public Collection<PressRelease> getAllPressReleases()
  {
    Timer timer = Metrics.globalRegistry.getRegistries().iterator().next().find("metrics.pressRelease.getAll").tags("application", "prometheus-metrics", "version", "1.0").timer();
    if (timer != null)
      log.debug("### getAllPressrelease {}, {}", timer.count());
    return pressReleaseService.getPressReleases();
  }

  @GetMapping("/pressRelease/{id}")
  @Timed(value = "metrics.pressRelease.getById", histogram = true, percentiles = {0.95, 0.99}, extraTags = {"version", "1.0"})
  public ResponseEntity<PressRelease> getPressRelease(@PathVariable Integer id)
  {
    Timer timer = Metrics.globalRegistry.getRegistries().iterator().next().find("metrics.pressRelease.getById").tags("application", "prometheus-metrics", "version", "1.0").timer();
    if (timer != null)
      log.debug("### getPressrelease {}, {}", timer.count());
    return pressReleaseService.getPressReleaseById(id).map(pressRelease ->
    {
      return ResponseEntity.ok(pressRelease);
    }).orElseGet(() ->
    {
      return new ResponseEntity<PressRelease>(HttpStatus.NOT_FOUND);
    });
  }

  @PostMapping("/add")
  @Timed(value = "metrics.pressRelease.add", histogram = true, percentiles = {0.95, 0.99}, extraTags = {"version", "1.0"})
  public ResponseEntity<PressRelease> addPressRelease(@RequestBody PressRelease pressRelease)
  {
    Timer timer = Metrics.globalRegistry.getRegistries().iterator().next().find("metrics.pressRelease.add").tags("application", "prometheus-metrics", "version", "1.0").timer();
    if (timer != null)
      log.debug("### addPressrelease {}, {}", timer.count());
    return pressReleaseService.saveOrUpdatePressRelease(pressRelease).map(p ->
    {
      return ResponseEntity.ok(p);
    }).orElseGet(() ->
    {
      return new ResponseEntity<PressRelease>(HttpStatus.EXPECTATION_FAILED);
    });
  }

  @PutMapping("/update")
  @Timed(value = "metrics.pressRelease.update", histogram = true, percentiles = {0.95, 0.99}, extraTags = {"version", "1.0"})
  public ResponseEntity<PressRelease> editPressRelease(@RequestBody PressRelease pressRelease)
  {
    Timer timer = Metrics.globalRegistry.getRegistries().iterator().next().find("metrics.pressRelease.update").tags("application", "prometheus-metrics", "version", "1.0").timer();
    if (timer != null)
      log.debug("### editPressrelease {}, {}", timer.count());
    return pressReleaseService.saveOrUpdatePressRelease(pressRelease).map(p ->
    {
      return ResponseEntity.ok(p);
    }).orElseGet(() ->
    {
      return new ResponseEntity<PressRelease>(HttpStatus.EXPECTATION_FAILED);
    });
  }

  @DeleteMapping("/delete/{id}")
  @Timed(value = "metrics.pressRelease.delete", histogram = true, percentiles = {0.95, 0.99}, extraTags = {"version", "1.0"})
  public ResponseEntity<String> deletePressRelease(@PathVariable("id") int pressReleaseId)
  {
    Timer timer = Metrics.globalRegistry.getRegistries().iterator().next().find("metrics.pressRelease.delete").tags("application", "prometheus-metrics", "version", "1.0").timer();
    if (timer != null)
      log.debug("### deletePressrelease {}, {}", pressReleaseId, timer.count());
    return pressReleaseService.deletePressReleaseById(pressReleaseId) ? ResponseEntity.ok("PressRelease is removed") : new ResponseEntity<String>(HttpStatus.EXPECTATION_FAILED);
  }
}
