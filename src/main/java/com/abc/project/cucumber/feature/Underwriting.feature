Feature: Underwriting Scenario

Background: Initilize Browser
	Given User initiates browser
	When User Selects desired browser
			 	 | WindowsGC|
			 	 

@smoke
Scenario Outline: Adviser Policy Submission by using Excel data
	Given User is on Home Page with specified Scenarioname,Description and environment "<Scenario Name>","<Description>","<environment>"
  And Capture Screenshot
	And User enters mandatory details
	Examples:
    |Scenario Name |Description                                  |environment | 
    |SSGStd001     | SC0001_Life onln_Single Life_Level_WOP_FCa  |UAT01       |
    |SSGStd002     | SC0001_Life onln_Single Life_Level_WOP_FCa  |UAT01       |
    |SSGStd003     | SC0001_Life onln_Single Life_Level_WOP_FCa  |UAT01       |


Scenario Outline: Adviser Policy Submission
  Given User is on Home Page with specified Scenarioname,Description and environment "<Scenario Name>","<Description>","<environment>"
  #And Capture Screenshot
	And User enters "<username>" and "<password>"
	#And Capture Screenshot
	Then Advisory dashboard Should display
	#And Capture Screenshot
	And Click On Save&Exit Button
  And Click On Logout Button 

	Examples:
   |Scenario Name              |Description         |environment | username   | password   |
   | Adviser Policy Submission |with the given data |UAT01       | xzmig10281 | Password1  |
   | Adviser Policy Submission with data change|with the given data |UAT01       | xzmig26116 | Password$1 |   

@Testing
Scenario Outline: Adviser Policy Submission by using Feature File Test data
  Given User is on Home Page with specified Scenarioname,Description and environment "<Scenario Name>","<Description>","<environment>"
  And Capture Screenshot
	And User enters "<username>" and "<password>"
	Then Advisory dashboard Should display
	And Capture Screenshot
	
	
	And Click On Start Quote button
	And Capture Screenshot
	And Click the Personal Protection Option
	And Capture Screenshot
	And Select the panel"<Panel>" value from the Dropdown 
	And Capture Screenshot                   
	And Click On Panel Continue Button

	And Select the title"<Title>" from the Dropdown
    And Enter the firstname"<First Name>" of the Client
	And Enter the lastname"<Last Name>" of the Client      
    And Enter the DOB as "<Date>" , "<Month>" , "<Year>"          
    And Click the Gender Radio Button as Male         
    And Select the smokerstatus"<Smoker Status>" from the Dropdown          	            
    And Enter the occupation"<Occupation>" of the Client                      
    And Enter the postcode"<PostCode>" of the Client  
    And Capture Screenshot              
    And Click On Continue Button
    
    And select the Personal product as product selection
    And Click On Product selection Continue Button
    
    And Enter sumassured"<sum assured>" details              
    And Enter term"<term>" in years              
    And select Premium frequency as Monthly
    And select Waver of premium as No          
    And select Multi fracture cover as No
    And select Conversion option as No 
    And Capture Screenshot
    And Click on Get quote Button
    
    And Capture the Application reference Number
    And Check the Confirm all statements Check Box
    And Capture Screenshot
    And click on Quote summary Apply Button 
    
   
    And Select the nationality"<nationality>"              
    And Enter emailaddress"<email address>"              
   And Enter phonenumber"<phone number>"              
   And Click on Find Address Button of Post Code
   And Select the First address form the List              
   And click on Add address manually hyperlink
   And Enter surgeryname"<Surgery name>" of GP deatils                     
    And Enter telephonenumber"<Telephone Number>" of GP deatils              
    And Enter postcode"<Post Code>" of GP deatils              
    And Enter addressline"<Address Line>" of GP deatils              
    And Enter townorcity"<Town or City>" of GP deatils             
    And Enter country"<Country>" of GP deatils      
    And Capture Screenshot        
    And click on Life Assured details Continue Buttton
    And Capture Screenshot
    And click on Policy holderss Continue Buttton                              
    
    #Underwriting

    #MedicalConsent
    And Select Yes for Do you consent to your medical records being shared with Zurich as explained in the AMRA?
    And Select No for If we do ask your doctor for a medical report, would you like to see it before your doctor sends it to us?
    And Capture Screenshot
    And Click on Medical Consent Next Button

    #Height, weight and habits
    And Enter your height as "<Ft>" , "<Inches>"              
    And Enter your weight as "<St>" , "<Lbs>"              
    And Select No for Do you drink alcohol?
    And Select No for have you attended or been advised to attend an alcohol support group or counselling, or have you been told you have any liver damage?
    And Select No for have you used recreational drugs such as cannabis, ecstasy, cocaine, heroin, amphetamines, or anabolic steroids?
    And Capture Screenshot
    And Click on Height & Weight Next Button

    #Occupation
    And Select No for Does your occupation involve working externally at heights over Fifty ft?
    And Capture Screenshot
    And Click on occupationt Next Button

    #PastHealth
    And Select No for diabetes, raised blood glucose, or sugar in the urine?
    And Select No for any heart disease or disorder, such as heart attack or any other heart condition?
    And Select No for a disorder or abnormality of the blood vessels or arteries such as narrowing, blood clots or deep vein thrombosis (DVT)?
    And Select No for a stroke, transient ischaemic attack (TIA), brain aneurysm or any damage or surgery to the brain?
    And Select No for cancer, leukaemia, Hodgkins disease, melanoma, lymphoma, brain or spinal tumours or growths?
    And Select No for schizophrenia, bi-polar disorder, manic depression,any other mental health condition hospital or referral to a psychiatrist?
    And Select No for any disorder of the nervous system such as multiple sclerosis, dementia or memory loss?
    And Select No for any disease or disorder of the liver or pancreas such as any form of hepatitis, cirrhosis or pancreatitis?
    And Select No for a positive test for HIV or are you awaiting the results of an HIV test? 
    And Capture Screenshot
    And Click on pasthealth Next Button

    #RecentHealth
    And Select No for raised blood pressure or raised cholesterol?
    And Select No for anxiety, stress, depression, chronic fatigue, obsessive compulsive disorder, or other mental health condition?
    And Select No for any respiratory or lung disease or disorder such as asthma, bronchitis, or COPD?
    And Select No for any kidney disease or disorder such as any form of nephritis, cysts or recurrent kidney stones?
    And Select No for any thyroid disorder?
    And Select No for any disease or disorder of the stomach, bowel or digestive system such as ulcers, ulcerative colitis, or Crohns disease?
    And Select No for any tremor, numbness, loss of feeling or tingling in the limbs or face, blurred or double vision, seizure, or loss of muscle power?
    And Select No for any disease or disorder of the prostate or testicle, such as raised Prostate Specific Antigen (PSA)?
    And Select No for any disease or disorder of the bladder or urinary tract such as recurrent infections or protein or blood in the urine?
    And Select No for any lump, cyst, growth or polyp, or a mole or freckle that has bled or changed in appearance?
    And Select No for anaemia or other blood disorders such as haemochromatosis or haemophilia?
    And Select No for any disease or disorder of the back, bones or joints, such as arthritis, whiplash, sciatica, slipped disc, psoriasis or gout?
    And Select No for any disease or disorder of the eyes or ears such as visual impairment in one or both eyes, ringing in one or both ears, tinnitus, labyrinthitis or Menieres disease?
    And Capture Screenshot
    And Click on recenthealth Next Button

    #Currenthealthandfamilyhistory
    And Select No for In the last 2 years, appointments or investigations with your doctor or other medical professional?
    And Select No for In the last 2 years, have you had any medication or treatment that lasted more than 4 weeks?
    And Select No for In the last month have you had a positive test for Coronavirus (COVID-19), Long COVID or Post-COVID syndrome?
    And Select No for In the last 3 months, have you had any symptoms of ill health, such as unexplained bleeding, weight loss, change of bowel habit, or a cough thats lasted for 4 weeks or more?
    And Select No for Are you aware of any other symptoms that you are planning to seek medical advice for?
    And Select No for breast, bowel/colon, ovarian, prostate or other cancer?
    And Select No for diabetes, heart attack, angina, stroke or heart disease?
    And Select No for multiple sclerosis, dementia or Alzheimers disease, Parkinsons disease, polyposis coli or any other hereditary disorder?
    And Capture Screenshot
    And Click on currenthealthandfamilyhistory Next Button

    #Travel and activities
    And Select No for In the last 5 years, have you spent more than 30 consecutive days in Africa, Iraq, Syria or an area of civil unrest?
    And Select No for In the next 2 years, do you expect to travel outside the UK, EU, Commonwealth and Development Office (FCDO) have advised of travel restrictions?
    And Select No for Do you take part, or intend to take part in diving, caving, potholing, climbing or mountaineering, motor sport, or other hazardous pursuit?
    And Select No for In the last 5 years, have you been banned from driving?
    And Capture Screenshot
    And Click on travelandactivities Next Button

    #Other cover
    And Select No for Apart from this application, have you applied to Zurich for any life insurance, critical illness cover or income protection in the last years?
    And Select No for Will the amount of cover you are now applying for, exceed million life cover critical illness cover?
    And Capture Screenshot
    And Click on othercover Next Button

#Underwriting summary

    #Summary
    And Capture Screenshot
    And Click on summary Confirm Button
    
    #Decision
    And Capture Screenshot
    And Capture Decisions from decision page
    And Click on decision continue Button

#Payment

    #Payment Details
    And Select the Preferred "<Collection Day>" from the Dropdown            
    And Click on Payer Lifeone Radio Button
    And Enter the accholdername"<Acc Holder Name>"            
    And Enter Sortcode as "<Sort one>" , "<Sort two>" , "<Sort three>"           
    And Enter accno"<Account number>"            
    And Check the I confirm that the payer is the account holder Check Box
    And Click on paymentdeatils Validate Button
    And Capture Screenshot
    And Click on paymentdeatils Continue Button

    #Direct Debit Confirmation
    And Capture Screenshot
    And Click on directdebitconfirmation Continue Button
    
    #Confirmation and Verification
    And Check the Confirmation and Verification of Identity Check Box
    And Capture Screenshot
    And Click on Confirmation and Verificatio Continue Button

#Trust details

    And Click on Trust details No Radio Button
    And Capture Screenshot
    And Click on Trustdetails Continue Button

#Start Date

    And Enter the start date as "<StartDate Day>" , "<StartDate Month>" , "<StartDate Year>"           
    And Click the no marketing Check Box
    And Please tick the box to confirm the above Check Box
    And Capture Screenshot
    And Click on startdate Issue my policy Button

#Confirmation
 
    And Capture the Application Policy Number
    And Capture Screenshot
    And Click on confirmation Return to dashboard Button
              
#Log out    
    #And Click On Save&Exit Button
    And Click On Logout Button  
    
Examples: 
|Scenario Name                               |Description         |environment|username   |password  |Panel                 |Title|First Name    |Last Name    |Date |Month |Year |Smoker Status |Occupation  |PostCode  |sum assured  |term  |nationality  |email address            |phone number  |Surgery name                    |Telephone Number|Post Code  |Address Line             |Town or City  |Country       |Ft  |Inches|St  |Lbs   |Collection Day|Acc Holder Name  |Sort one   |Sort two |Sort three |Account number|StartDate Day |StartDate Month |StartDate Year |
| Adviser Policy Submission                  |with the given data |UAT01      |xzmig10281 |Password1 |Deal 1 Standard 4 year|Mr   |SSGStdOneFNA  |SSGStdOneLNA |11   |10    |1976 |Never used    |Lawyer      |AB16 7BA  |350000       |10    |British      |zurichmailtest@gmail.com |1234567890    |The Junction Medical Practice   |0207 2729105    |SN1 4GB    |244 Tufnell Park Road    |London        |Greater London|5   | 10   |11   | 7   |10            |James Gosling    |07         |01       |16         |00003036      |15            |10              |2024           |
| Adviser Policy Submission with data change |with the given data |UAT01      |xzmig16959 |Password$1|Deal 1 Standard 4 Year|Mr   |SSGStdTwoFNA  |SSGStdTwoLNA |11   |10    |1976 |Never used    |Lawyer      |AB16 7BA  |350000       |10    |British      |zurichmailtest@gmail.com |1234567890    |The Junction Medical Practice   |0207 2729105    |SN1 4GB    |244 Tufnell Park Road    |London        |Greater London|5   | 10   |11   | 7   |10            |James Gosling    |07         |01       |16         |00003036      |15            |10              |2024           |
  
        
  