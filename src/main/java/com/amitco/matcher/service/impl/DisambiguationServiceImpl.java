package com.amitco.matcher.service.impl;

import com.amitco.matcher.dto.CompanyWithScorePOJO;
import com.amitco.matcher.entity.Address;
import com.amitco.matcher.entity.Company;
import com.amitco.matcher.entity.Phone;
import com.amitco.matcher.service.DisambiguationService;
import com.amitco.matcher.utils.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.apache.commons.text.similarity.LevenshteinDistance;

@Service
public class DisambiguationServiceImpl implements DisambiguationService {

  Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Value("classpath:company.json")
  Resource companyReourceFile;

  @Value("classpath:address.json")
  Resource addressReourceFile;

  @Value("classpath:phone.json")
  Resource phoneNumberReourceFile;

  private List<Company> companyList;

  private List<Address> addressList;

  private List<Phone> phoneList;

  /*
      NOTE: Currently we are giving equal weightage to all three element match but following allow
            you to change the weightage below
  */

  @Value("${companyName.weight}")
  private float companyNameWeight;

  @Value("${addressString.weight}")
  private float addressNameWeight;

  @Value("${phoneString.weight}")
  private float phoneNumberWeight;




  private CompanyWithScorePOJO getBestMatch(String inCompanyName, String inPhoneNumber, String inAddress)
        throws IOException {

     float totalWeight = companyNameWeight + addressNameWeight + phoneNumberWeight;

     float companyNameEffectiveWeight = companyNameWeight / totalWeight;

     float addressNameEffectiveWeight = addressNameWeight / totalWeight;

     float phoneNumberEffectiveWeight = phoneNumberWeight / totalWeight;

      if(companyList == null){
        loadData();
      }

    float companyNameDistance;
    float phoneDistance;
    float addressNameDistance;

    double minScore = -1;
    Company companyWithMinScore = null;

    for (Company company : companyList) {

      phoneDistance = -1;
      addressNameDistance = -1;

      companyNameDistance =
          LevenshteinDistance.getDefaultInstance().apply(
              StringUtils.cleanString(inCompanyName),
              StringUtils.cleanString(company.getCompany_name()));
      for (Address address : company.getAddressList()) {
        float tmpAddressDistance = address.calculateDistance(inAddress);
        if (addressNameDistance == -1 || tmpAddressDistance < addressNameDistance)
          addressNameDistance = tmpAddressDistance;
      }
      for (Phone phone : company.getPhoneList()) {
        float tmpPhoneDistance = phone.calculateDistance(inPhoneNumber);
        if (phoneDistance == -1 || tmpPhoneDistance < phoneDistance)
          phoneDistance = tmpPhoneDistance;
      }
      double combinedScore = (companyNameDistance * companyNameEffectiveWeight)
          + (phoneDistance * phoneNumberEffectiveWeight)
          + (addressNameDistance * addressNameEffectiveWeight);

      if (minScore == -1 || combinedScore <= minScore) {
        minScore = combinedScore;
        companyWithMinScore = company;

      }

    }
    return new CompanyWithScorePOJO(companyWithMinScore, (float) ((float) 1 - minScore));

  }

  @Override
  public float getScore(String companyName, String phoneNumber, String address) throws IOException {
      return getBestMatch(companyName,phoneNumber, address).getScore();
  }

  @Override
  public CompanyWithScorePOJO findMatch(String companyName, String phoneNumber, String address)
      throws IOException {
    return  getBestMatch(companyName,phoneNumber, address);
  }

  @Override
  public void loadData() throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();

    File companyListFile, addressListFile, phoneNumberListFile;

    try {
      companyListFile = companyReourceFile.getFile();
      addressListFile = addressReourceFile.getFile();
      phoneNumberListFile = phoneNumberReourceFile.getFile();

      companyList = objectMapper.readValue(companyListFile, new TypeReference<List<Company>>(){});
      addressList = objectMapper.readValue(addressListFile, new TypeReference<List<Address>>(){});
      phoneList = objectMapper.readValue(phoneNumberListFile, new TypeReference<List<Phone>>(){});


      HashMap<String, List<Address>> addressMap = new HashMap<>(addressList.size());
      HashMap<String, List<Phone>> phoneMap = new HashMap<>(phoneList.size());

      for (Address address : addressList) {
        if (addressMap.get(address.getCompany_id()) == null) {
          List<Address> list = new ArrayList<>();
          list.add(address);
          addressMap.put(address.getCompany_id(), list);
        } else {
          addressMap.get(address.getCompany_id()).add(address);
        }
      }

      for (Phone phone : phoneList) {
        if (phoneMap.get(phone.getCompany_id()) == null) {
          List<Phone> list = new ArrayList<>();
          list.add(phone);
          phoneMap.put(phone.getCompany_id(), list);
        } else {
          phoneMap.get(phone.getCompany_id()).add(phone);
        }
      }
      for (Company company : companyList) {
        company.setAddressList(addressMap.get(company.getId()));
        company.setPhoneList(phoneMap.get(company.getId()));
      }

    } catch (Exception ex) {
        LOG.error("Something went wrong while loading data", ex);
        throw ex;
    }
  }
}
