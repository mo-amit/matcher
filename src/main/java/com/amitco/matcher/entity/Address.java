package com.amitco.matcher.entity;

import com.amitco.matcher.utils.StringUtils;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

  String id;
  String company_id;
  String line1;
  String line2;
  String city;
  String state;
  String postal_code;
  String country;

  String cleanLine1;
  String cleanLine2;
  String cleanCity;
  String cleanState;
  String cleanPostal_code;
  String cleanCountry;



  public void setLine1(String line1) {
    if(line1!=null)
      this.line1 = line1;
    else
      this.line1="";
  }

  public void setLine2(String line2) {
    if(line2!=null)
      this.line2 = line2;
    else
      this.line2="";  }

  public void setCity(String city) {
    if ( city != null)
      this.city = city;
    else
      this.city = "";
  }

  public void setState(String state) {
    if( state != null)
      this.state = state;
    else
      this.state = "";
  }

  public void setPostal_code(String postal_code) {
    if ( postal_code != null)
      this.postal_code = postal_code;
    else
      this.postal_code = "";
  }

  public void setCountry(String country) {
    if (this.country != null)
      this.country = country;
    else
      this.country = "";
  }



  public float calculateDistance(String address) {


    if(this.cleanLine1 == null) this.cleanLine1 = StringUtils.cleanString(line1);
    if(this.cleanLine2 == null) this.cleanLine2 = StringUtils.cleanString(line2);
    if(this.cleanCity == null)  this.cleanCity = StringUtils.cleanString(city);
    if(this.cleanState == null) this.cleanState = StringUtils.cleanString(state);
    if(this.cleanPostal_code == null) this.cleanPostal_code = StringUtils.cleanString(postal_code);
    if(this.cleanCountry == null) this.cleanCountry = StringUtils.cleanString(country);

    Set<String> cleanCombinedAddresses = new HashSet<>(4);
    cleanCombinedAddresses.add(cleanLine1+cleanLine2+cleanCity+cleanState+cleanCountry+cleanPostal_code);
    cleanCombinedAddresses.add(cleanLine1+cleanLine2+cleanCity+cleanState+cleanPostal_code+cleanCountry);
    cleanCombinedAddresses.add (cleanLine2+cleanLine1+cleanCity+cleanState+cleanPostal_code+cleanCountry);
    cleanCombinedAddresses.add(cleanLine2+cleanLine1+cleanCity+cleanState+cleanCountry+cleanPostal_code);
    String cleanInputString = StringUtils.cleanString(address);

    float minDistance = -1;
    float currentDistance;
    float currentWeightedDistance;
    for (String combinedAddress : cleanCombinedAddresses) {
      currentDistance = LevenshteinDistance.getDefaultInstance().apply(combinedAddress, cleanInputString);
      if( cleanInputString.length() !=0 ){

        if( combinedAddress.length() != 0) {
          currentWeightedDistance =  currentDistance/
              (combinedAddress.length()>cleanInputString.length()?
                  combinedAddress.length():
                  cleanInputString.length()
              );

        }else{
          currentWeightedDistance =  currentDistance/cleanInputString.length();
        }

      }else {
         if( combinedAddress.length() != 0){

           currentWeightedDistance =  currentDistance/combinedAddress.length();
         }else {
           currentWeightedDistance =  0;
         }
      }
      if( minDistance == -1 || currentWeightedDistance < minDistance){

        minDistance =currentWeightedDistance;
      }

    }
    return minDistance;
  }
}
