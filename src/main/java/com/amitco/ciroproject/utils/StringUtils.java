package com.amitco.ciroproject.utils;

import java.util.Locale;

public class StringUtils {

  public static String cleanString( String inString){
    if( inString == null)
      return "";
    return inString.strip().replaceAll("[^a-zA-Z0-9]", "").toUpperCase(
        Locale.ROOT);
  }

  public static void main(String[] args) {
    System.out.println(cleanString("$% A B/8"));
  }
}
