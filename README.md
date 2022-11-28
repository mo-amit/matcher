1. Running the application 

 After downloading the git repo. Go to termninal and change directory to the project's root. 
 Type 
     `gradlew bootRun` ( gradle.bat for windows)

 The server by default listens on port 5000. You can go to application.properties (located in <projectroot>/src/main/resources/) to change the default port 

2. Testing application run following using postman or using `curl` on commmand line 
   `curl -X POST 'http://localhost:5000/demo/findmatch' -H 'Content-Type: application/json'  -d '{"companyName": "Karen D. Sakuma D.D.S., P.S.", "address" : "5525 Lakeview Dr kirkland wa 98003" , "phoneNumber" : "4258270426"}'`
   
  Above command should return a json object in following format 
   `{ company : <best matched company detail> , score : <float value of matched score between 0-1>}`
 
Note: 1 means complete match 0 means no match. 

3. Matching Algorithm 
   
    1. Find the best match between company name, phone number string & address string 
    2. Score each match (more on this later)
    3. Multiply the score of each individual element ( i.e. company name, phone & address) with weightage attributed to each entity from "application.properties" file. (more on this later) 
    4. Add the weighted score of each element. This essentially measures level of "non-matchingness"
    5. Substract "non-matchingness" from 1 to get the matchingness score. 
    
3.1 To score each match against the input we take advantage of Levinshtein Distance metric between the two string. I wasted a bunch of time initially trying to tweak an implementation I found online but then decided to go with apache commons (https://commons.apache.org/proper/commons-text/apidocs/org/apache/commons/text/similarity/LevenshteinDistance.html). 
Since Levinshtein distance takes two strings and measures number of changes one has to make to first string for it to be the same as second string, it essentially is the best measure of "non-matcingness" I know of.

3.2 Weighting different elements of address. It seems naive to assume that all attributes of the address i.e address, phone and name, are equally important. It stands to reason that phone ( and arguably name) is much more important. However since people tend to be loosey goosey about names ( like using Dr. or Doctor interchangably), I decided to give higher weightage to phone number match. However such weightage can be easily changed at runtime by changing following values in `application.properties` 
`companyName.weight=1
addressString.weight=1
phoneString.weight=2`
    
   
      