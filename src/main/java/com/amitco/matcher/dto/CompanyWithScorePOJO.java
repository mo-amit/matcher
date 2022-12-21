package com.amitco.matcher.dto;

import com.amitco.matcher.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyWithScorePOJO {

  public Company company;
  float  score;

}
