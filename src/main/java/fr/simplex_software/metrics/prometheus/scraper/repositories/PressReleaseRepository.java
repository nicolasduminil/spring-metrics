package fr.simplex_software.metrics.prometheus.scraper.repositories;

import fr.simplex_software.metrics.prometheus.scraper.data.*;
import org.springframework.data.jpa.repository.*;

public interface PressReleaseRepository extends JpaRepository<PressReleaseEntity, Integer>
{
}
