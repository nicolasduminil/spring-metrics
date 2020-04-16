package fr.simplex_software.metrics.prometheus.scraper;

import fr.simplex_software.metrics.prometheus.scraper.model.*;
import lombok.extern.slf4j.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.web.client.*;
import org.springframework.http.*;
import org.springframework.test.context.junit4.*;
import org.springframework.web.client.*;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class TestPressReleaseIT
{
  private RestTemplate restTemplate;
  @Value("${metrics.test.host.ip}")
  private String testHostName;
  @Value("${metrics.test.host.tcp.port}")
  private String testTcpPort;
  private PressRelease pressRelease = null;
  private ResponseEntity<PressRelease> resp = null;
  private static int firstId = 0;

  @Before
  public void init()
  {
    restTemplate = new RestTemplateBuilder().rootUri("http://" + testHostName + ":" + testTcpPort + "/api").build();
  }

  @Test
  public void testAddPressRelease()
  {
    for (int i = 1; i <= 100; i++)
    {
      pressRelease = new PressRelease("name" + i, "author" + i, "publisher" + i);
      resp = restTemplate.postForEntity("/add", new HttpEntity<PressRelease>(pressRelease), PressRelease.class);
      assertEquals(resp.getStatusCode(), HttpStatus.OK);
      if (i == 1)
        firstId = resp.getBody().getPressReleaseId();
    }
  }

  @Test
  public void testGetAllPresReleases()
  {
    Collection<PressRelease> pressReleaseCollection = restTemplate.getForObject("/all", Collection.class);
    assertNotNull(pressReleaseCollection);
    assertTrue(pressReleaseCollection.size() > 0);
  }

  @Test
  public void testGetPressRelease()
  {
    for (int i=firstId; i <= firstId+99; i++)
    {
      resp = restTemplate.getForEntity("/pressRelease/" + i, null, PressRelease.class);
      assertEquals(resp.getStatusCode(), HttpStatus.OK);
    }
  }

  @Test
  public void testUpdatePressRelease()
  {
    for (int i = firstId; i <= firstId+100; i++)
    {
      pressRelease = new PressRelease("updated-name" + i, "updated-author" + i, "updated-publisher" + i);
      resp = restTemplate.exchange("/update", HttpMethod.PUT, new HttpEntity<PressRelease>(pressRelease), PressRelease.class);
      assertEquals(resp.getStatusCode(), HttpStatus.OK);
    }
  }

  @Test
  public void testXDeletePressRelease()
  {
    for (int i=firstId; i <= firstId+10; i++)
      restTemplate.delete("/delete/" + i);
  }
}
