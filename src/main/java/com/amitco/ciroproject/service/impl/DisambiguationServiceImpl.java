package com.amitco.ciroproject.service.impl;

import com.amitco.ciroproject.dto.CompanyWithScorePOJO;
import com.amitco.ciroproject.entity.Address;
import com.amitco.ciroproject.entity.Company;
import com.amitco.ciroproject.entity.Phone;
import com.amitco.ciroproject.service.DisambiguationService;
import com.amitco.ciroproject.utils.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

    float companyNameDistance = -1;
    float phoneDistance = -1;
    float addressNameDistance = -1;

    double minScore = -1;
    Company companyWithMinScore = null;

    for (int i = 0; i < companyList.size(); i++) {

      phoneDistance = -1;
      addressNameDistance = -1;

      companyNameDistance =
          LevenshteinDistance.getDefaultInstance().apply(
              StringUtils.cleanString(inCompanyName),
              StringUtils.cleanString((companyList.get(i)).getCompany_name()));
      for (Address address : companyList.get(i).getAddressList()) {
        float tmpAddressDistance = address.calculateDistance(inAddress);
        if(addressNameDistance == -1 || tmpAddressDistance < addressNameDistance)
             addressNameDistance = tmpAddressDistance;
      }
      for (Phone phone : companyList.get(i).getPhoneList()) {
        float tmpPhoneDistance =  phone.calculateDistance(inPhoneNumber);
         if (phoneDistance  == -1 || tmpPhoneDistance < phoneDistance)
               phoneDistance = tmpPhoneDistance;
      }
      double  combinedScore = (companyNameDistance*companyNameEffectiveWeight)
                  + (phoneDistance* phoneNumberEffectiveWeight )
                  + (addressNameDistance * addressNameEffectiveWeight);

      if ( minScore == -1 || combinedScore <= minScore){
        minScore = combinedScore;
        companyWithMinScore = companyList.get(i);

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

    File companyListFile = null;
    File addressListFile = null;
    File phonenumberListFile = null;

    try {
      companyListFile = companyReourceFile.getFile();
      addressListFile = addressReourceFile.getFile();
      phonenumberListFile = phoneNumberReourceFile.getFile();

      companyList = objectMapper.readValue(companyListFile, new TypeReference<List<Company>>(){});
      addressList = objectMapper.readValue(addressListFile, new TypeReference<List<Address>>(){});
      phoneList = objectMapper.readValue(phonenumberListFile, new TypeReference<List<Phone>>(){});


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
