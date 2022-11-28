package com.amitco.ciroproject.controller;

import com.amitco.ciroproject.dto.CompanyWithScorePOJO;
import com.amitco.ciroproject.dto.FindMatchCriteria;
import com.amitco.ciroproject.entity.Company;
import com.amitco.ciroproject.service.DisambiguationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/demo")
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ServiceController {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceController.class);
  private final DisambiguationService disambiguationService;

  @Autowired
  public ServiceController(DisambiguationService demoService) {
    this.disambiguationService = demoService;
  }

  @PostMapping("/getscore")
  public float getScore(String companyName, String phoneNumber, String address)  {
    try {
      return disambiguationService.getScore(companyName, phoneNumber, address);
    }catch (Exception ex){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Something went wrong while finding score");
    }
  }

  @PostMapping("/findmatch")
  public CompanyWithScorePOJO findMatch(@RequestBody FindMatchCriteria matchCriteria)  {
    try{
      return disambiguationService.findMatch(matchCriteria.getCompanyName(),
          matchCriteria.getPhoneNumber(),matchCriteria.getAddress());
    }catch (Exception ex){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Something went wrong while finding Match");
    }
  }

  @GetMapping("/loaddata")
  public void loadData()  {
    try {
      disambiguationService.loadData();
    }catch (Exception ex){
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Something went wrong while processing data");
    }
  }
}
