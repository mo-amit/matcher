package com.amitco.ciroproject.entity;

import com.amitco.ciroproject.utils.StringUtils;
import java.util.Locale;
import lombok.Data;
import org.apache.commons.text.similarity.LevenshteinDistance;

@Data
public class Phone {

  String id;
  String company_id;
  String phone_number;

  public void setPhone_number(String phone_number){
    if ( phone_number != null)
      this.phone_number = phone_number;
    else
      this.phone_number = "";
  }

  public float calculateDistance(String inPhoneNumber) {

    String cleanPhoneNumber = StringUtils.cleanString(phone_number);

    String cleanedInPhonenumber =StringUtils.cleanString(inPhoneNumber);

    float currentDistance =  LevenshteinDistance.getDefaultInstance().apply(cleanPhoneNumber, cleanedInPhonenumber);

    float currentWeightedDistance;

    if( cleanedInPhonenumber.length() !=0 ){

      if( cleanPhoneNumber.length() != 0) {
        currentWeightedDistance =  currentDistance/
            (cleanPhoneNumber.length()>cleanedInPhonenumber.length()?
                cleanPhoneNumber.length():
                cleanedInPhonenumber.length()
            );

      }else{
        currentWeightedDistance =  currentDistance/cleanedInPhonenumber.length();
      }

    }else {
      if( cleanPhoneNumber.length() != 0){

        currentWeightedDistance =  currentDistance/cleanPhoneNumber.length();
      }else {
        currentWeightedDistance =  0;
      }
    }
      return currentWeightedDistance;
  }
}
