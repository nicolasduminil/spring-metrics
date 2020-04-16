package com.cision.metrics.prometheus.scraper.repositories;

import com.cision.metrics.prometheus.scraper.data.*;
import com.cision.metrics.prometheus.scraper.model.*;
import org.springframework.data.jpa.repository.*;

public interface PressReleaseRepository extends JpaRepository<PressReleaseEntity, Integer>
{
}
