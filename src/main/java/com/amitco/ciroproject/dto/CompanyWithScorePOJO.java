package com.amitco.ciroproject.dto;

import com.amitco.ciroproject.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompanyWithScorePOJO {

  public Company company;
  float  score;

}
