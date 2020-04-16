package com.cision.metrics.prometheus.scraper.model;

import lombok.*;

import javax.persistence.*;
import java.io.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PressRelease implements Serializable
{
  private int pressReleaseId;
  private String name;
  private String author;
  private String publisher;

  public PressRelease(String name, String author, String publisher)
  {
    this.name = name;
    this.author = author;
    this.publisher = publisher;
  }
}
