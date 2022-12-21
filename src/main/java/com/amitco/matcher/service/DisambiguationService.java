package com.amitco.matcher.service;

import com.amitco.matcher.dto.CompanyWithScorePOJO;
import java.io.IOException;

public interface DisambiguationService {

  float getScore(String companyName, String phoneNumber, String address) throws IOException;
  CompanyWithScorePOJO findMatch(String companyName, String phoneNumber, String address)
      throws IOException;

  void loadData() throws IOException;
}
