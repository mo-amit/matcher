package com.amitco.ciroproject.entity;

import java.util.List;
import lombok.Data;

@Data
public class Company {

  String id;
  String company_name;
  List<Address> addressList;
  List<Phone> phoneList;
}
