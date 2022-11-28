package com.amitco.ciroproject.service;

import com.amitco.ciroproject.dto.CompanyWithScorePOJO;
import com.amitco.ciroproject.entity.Company;
import java.io.IOException;

public interface DisambiguationService {

  public float getScore(String companyName, String phoneNumber, String address) throws IOException;
  public CompanyWithScorePOJO findMatch(String companyName, String phoneNumber, String address)
      throws IOException;

  public void loadData() throws IOException;
}
