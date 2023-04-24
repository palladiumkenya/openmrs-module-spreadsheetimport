

/*
    Add migration tables here

*/

drop database migration_tr;
create database migration_tr;
use migration_st;
CREATE TABLE migration_tr.tr_demographics
(
    Person_Id             INT(11) PRIMARY KEY,
    Patient_Id            INT(11),
    First_Name            VARCHAR(100),
    Middle_Name           VARCHAR(100),
    Last_Name             VARCHAR(100),
    Nickname              VARCHAR(100),
    DOB                   DATE NULL,
    Exact_DOB             VARCHAR(100),
    Sex                   VARCHAR(50),
    UPN                   VARCHAR(300),
    Encounter_Date        DATE,
    Encounter_ID          VARCHAR(100),
    National_id_no        VARCHAR(100),
    Patient_clinic_number VARCHAR(100),
    Birth_certificate     VARCHAR(100),
    Birth_notification    VARCHAR(100),
    Hei_no                VARCHAR(100),
    Passport              VARCHAR(100),
    Alien_registration    VARCHAR(100),
    Phone_number          VARCHAR(100),
    Alternate_Phone_number VARCHAR(100),
    Postal_Address        VARCHAR(100),
    Email_address         VARCHAR(100),
    County                VARCHAR(100),
    Sub_county            VARCHAR(100),
    Ward                  VARCHAR(100),
    Village               VARCHAR(255),
    Landmark              VARCHAR(255),
    Nearest_Health_Centre VARCHAR(255),
    Next_of_kin           VARCHAR(255),
    Next_of_kin_phone     VARCHAR(255),
    Next_of_kin_relationship VARCHAR(255),
    Next_of_kin_address   VARCHAR(100),
    Marital_status        VARCHAR(255),
    Occupation            VARCHAR(255),
    Education_level       VARCHAR(255),
    Dead                  VARCHAR(100),
    Death_date            DATE DEFAULT NULL,
    Consent               VARCHAR(255),
    Consent_decline_reason VARCHAR(255),
    Patient_voided         INT(11),
    Person_voided          INT(11),
    CreateDate             DATE,
    CreatedBy              VARCHAR(100),
    voided                 INT(11),
    INDEX(Patient_Id),
    INDEX(Alien_registration) -- this is where CCC numbers are recorded in the cca database
);

CREATE TABLE migration_tr.etl_patient_triage (
     uuid CHAR(38),
     Person_Id INT(11), -- the original patient id in the cca database
     Encounter_ID INT(11),
     patient_id INT(11) DEFAULT NULL ,
     location_id INT(11) DEFAULT NULL,
     Encounter_Date DATE,
     visit_id INT(11),
     creator INT(11),
     date_created DATETIME,
     date_last_modified DATETIME,
     Visit_reason VARCHAR(500),
     Weight DOUBLE,
     Height DOUBLE,
     Systolic_pressure DOUBLE,
     Diastolic_pressure DOUBLE,
     Temperature DOUBLE,
     Pulse_rate DOUBLE,
     Respiratory_rate DOUBLE,
     Oxygen_saturation DOUBLE,
     Muac DOUBLE,
     nutritional_status INT(11) DEFAULT NULL,
     Last_menstrual_period DATE,
     hpv_vaccinated INT(11),
     Nurse_comments varchar(300) default null,
     voided INT(11),
     INDEX(Person_Id)
);

-- -------------------------- CREATE  ---------------------------------

DROP TABLE IF EXISTS migration_tr.etl_hiv_enrollment;
create table migration_tr.etl_hiv_enrollment(
    uuid char(38) ,
    Person_Id INT(11) DEFAULT NULL,
    patient_id INT(11) DEFAULT NULL,
    visit_id INT(11) DEFAULT NULL,
    Encounter_Date DATE,
    location_id INT(11) DEFAULT NULL,
    Encounter_ID INT(11) DEFAULT NULL,
    creator INT(11),
    Patient_Type INT(11),
    Date_first_enrolled_in_care DATE,
    Entry_point INT(11),
    Transfer_in_date DATE,
    facility_transferred_from VARCHAR(255),
    district_transferred_from VARCHAR(255),
    Date_started_art_at_transferring_facility DATE,
    Date_confirmed_hiv_positive DATE,
    Facility_confirmed_hiv_positive VARCHAR(255),
    previous_regimen VARCHAR(255),
    arv_status INT(11),
    ever_on_pmtct INT(11),
    ever_on_pep INT(11),
    ever_on_prep INT(11),
    ever_on_haart INT(11),
    name_of_treatment_supporter VARCHAR(255),
    relationship_of_treatment_supporter INT(11),
    treatment_supporter_telephone VARCHAR(100),
    treatment_supporter_address VARCHAR(100),
    in_school INT(11) DEFAULT NULL,
    orphan INT(11) DEFAULT NULL,
    date_of_discontinuation DATETIME,
    discontinuation_reason INT(11),
    date_created DATETIME NOT NULL,
    date_last_modified DATETIME,
    voided INT(11),
    Baseline_cd4_results varchar(100),
    Baseline_cd4_date DATE,
    Baseline_vl_results varchar(100),
    Baseline_vl_date DATE,
    Baseline_vl_ldl_results varchar(100),
    Baseline_vl_ldl_date DATE,
    TI_Facility varchar(100),
    index(Person_Id),
    index(patient_id)
);
create table migration_tr.etl_hts_initial_test (
       uuid  CHAR(38) NOT NULL,
       Person_Id INT(11) not null,
       visit_id INT(11) DEFAULT NULL,
       patient_id INT(11) DEFAULT NULL ,
       Encounter_ID INT(11) DEFAULT NULL,
       location_id INT(11) DEFAULT NULL,
       creator INT(11) NOT NULL,
       date_created DATETIME NOT NULL,
       Encounter_Date DATE,
       Pop_Type INT(11),
       Key_Pop_Type VARCHAR(50),
       priority_population_type VARCHAR(50),
       Ever_Tested INT(11),
       months_since_last_test INT(11),
       Patient_disabled INT(11),
       disability_type VARCHAR(255),
       Consented INT(11) DEFAULT NULL,
       Tested_As INT(11),
       setting VARCHAR(50),
       approach VARCHAR(50),
       HTS_Strategy INT(11),
       HTS_Entry_Point INT(11),
       hts_risk_category VARCHAR(50),
       hts_risk_score DOUBLE,
       Test_1_Kit_Name INT(11),
       Test_1_Lot_Number VARCHAR(50) DEFAULT NULL,
       Test_1_Expiry_Date DATE DEFAULT NULL,
       Test_1_Final_Result INT(11) DEFAULT NULL,
       Test_2_Kit_Name INT(11),
       Test_2_Lot_Number VARCHAR(50) DEFAULT NULL,
       Test_2_Expiry_Date DATE DEFAULT NULL,
       Test_2_Final_Result INT(11) DEFAULT NULL,
       Final_Result INT(11) DEFAULT NULL,
       syphillis_test_result VARCHAR(50) DEFAULT NULL,
       Result_given INT(11) DEFAULT NULL,
       Couple_Discordant INT(11) DEFAULT NULL,
       referral_for VARCHAR(100) DEFAULT NULL,
       referral_facility VARCHAR(200) DEFAULT NULL,
       other_referral_facility VARCHAR(200) DEFAULT NULL,
       neg_referral_for VARCHAR(255) DEFAULT NULL,
       neg_referral_specify VARCHAR(255) DEFAULT NULL,
       Tb_Screening_Results INT(11) DEFAULT NULL,
       Self_Tested INT(11) DEFAULT NULL,
       Remarks VARCHAR(500) DEFAULT NULL,
       voided INT(11),
       index(Person_Id),
       index(patient_id)
);

create table migration_tr.etl_hts_retest_test (
    uuid  CHAR(38) NOT NULL,
    Person_Id INT(11) not null,
    visit_id INT(11) DEFAULT NULL,
    patient_id INT(11) DEFAULT NULL ,
    Encounter_ID INT(11) DEFAULT NULL,
    location_id INT(11) DEFAULT NULL,
    creator INT(11) NOT NULL,
    date_created DATETIME NOT NULL,
    Encounter_Date DATE,
    Pop_Type INT(11),
    Key_Pop_Type VARCHAR(50),
    priority_population_type VARCHAR(50),
    Ever_Tested INT(11),
    months_since_last_test INT(11),
    Patient_disabled INT(11),
    disability_type VARCHAR(255),
    Consented INT(11) DEFAULT NULL,
    Tested_As INT(11),
    setting VARCHAR(50),
    approach VARCHAR(50),
    HTS_Strategy INT(11),
    HTS_Entry_Point INT(11),
    hts_risk_category VARCHAR(50),
    hts_risk_score DOUBLE,
    Test_1_Kit_Name INT(11),
    Test_1_Lot_Number VARCHAR(50) DEFAULT NULL,
    Test_1_Expiry_Date DATE DEFAULT NULL,
    Test_1_Final_Result INT(11) DEFAULT NULL,
    Test_2_Kit_Name INT(11),
    Test_2_Lot_Number VARCHAR(50) DEFAULT NULL,
    Test_2_Expiry_Date DATE DEFAULT NULL,
    Test_2_Final_Result INT(11) DEFAULT NULL,
    Final_Result INT(11) DEFAULT NULL,
    syphillis_test_result VARCHAR(50) DEFAULT NULL,
    Result_given INT(11) DEFAULT NULL,
    Couple_Discordant INT(11) DEFAULT NULL,
    referral_for VARCHAR(100) DEFAULT NULL,
    referral_facility VARCHAR(200) DEFAULT NULL,
    other_referral_facility VARCHAR(200) DEFAULT NULL,
    neg_referral_for VARCHAR(255) DEFAULT NULL,
    neg_referral_specify VARCHAR(255) DEFAULT NULL,
    Tb_Screening_Results INT(11) DEFAULT NULL,
    Self_Tested INT(11) DEFAULT NULL,
    Remarks VARCHAR(500) DEFAULT NULL,
    voided INT(11),
    index(Person_Id),
    index(patient_id)
);

CREATE TABLE migration_tr.hiv_program_enrollment
(
    Person_Id      INT(11) NOT NULL,
    Encounter_Date DATE    DEFAULT NULL,
    patient_id     INT(11) DEFAULT NULL,
    Encounter_ID   INT(11) DEFAULT NULL,
    Program        INT(11) DEFAULT NULL,
    Date_Enrolled  DATE,
    Date_Completed DATE    DEFAULT NULL,
    Create_date    DATE    DEFAULT NULL,
    Created_by     INT(11) DEFAULT NULL,
    INDEX (Person_Id),
    INDEX (Encounter_Date),
    INDEX (patient_id)
);


CREATE TABLE migration_tr.etl_patient_hiv_followup (
   uuid CHAR(38),
   Person_Id INT(11),
   Encounter_ID INT(11),
   patient_id INT(11) DEFAULT NULL ,
   location_id INT(11) DEFAULT NULL,
   Encounter_Date DATE,
   visit_id INT(11),
   creator INT(11),
   date_created DATETIME NOT NULL,
   Visit_scheduled INT(11),
   Visit_by INT(11),
   Visit_by_other varchar(100),
   weight DOUBLE,
   systolic_pressure DOUBLE,
   diastolic_pressure DOUBLE,
   height DOUBLE,
   temperature DOUBLE,
   pulse_rate DOUBLE,
   respiratory_rate DOUBLE,
   oxygen_saturation DOUBLE,
   muac DOUBLE,
   Nutritional_status INT(11) DEFAULT NULL,
   Population_type INT(11) DEFAULT NULL,
   key_population_type INT(11) DEFAULT NULL,
   Key_population_type_pwid INT(11) DEFAULT NULL,
   Key_population_type_msm INT(11) DEFAULT NULL,
   Key_population_type_fsw INT(11) DEFAULT NULL,
   Who_stage INT(11),
   who_stage_associated_oi VARCHAR(1000),
   Presenting_complaints INT(11) DEFAULT NULL,
   clinical_notes VARCHAR(600) DEFAULT NULL,
   on_anti_tb_drugs INT(11) DEFAULT NULL,
   on_ipt INT(11) DEFAULT NULL,
   ever_on_ipt INT(11) DEFAULT NULL,
   cough INT(11) DEFAULT -1,
   fever INT(11) DEFAULT -1,
   weight_loss_poor_gain INT(11) DEFAULT -1,
   night_sweats INT(11) DEFAULT -1,
   tb_case_contact INT(11) DEFAULT -1,
   lethargy INT(11) DEFAULT -1,
   screened_for_tb VARCHAR(50),
   spatum_smear_ordered INT(11) DEFAULT NULL,
   chest_xray_ordered INT(11) DEFAULT NULL,
   genexpert_ordered INT(11) DEFAULT NULL,
   spatum_smear_result INT(11) DEFAULT NULL,
   chest_xray_result INT(11) DEFAULT NULL,
   genexpert_result INT(11) DEFAULT NULL,
   referral INT(11) DEFAULT NULL,
   clinical_tb_diagnosis INT(11) DEFAULT NULL,
   contact_invitation INT(11) DEFAULT NULL,
   evaluated_for_ipt INT(11) DEFAULT NULL,
   Has_known_allergies INT(11) DEFAULT NULL,
   Has_Chronic_illnesses_cormobidities INT(11) DEFAULT NULL,
   Has_adverse_drug_reaction INT(11) DEFAULT NULL,
   Vaccinations_today_bcg INT(11) DEFAULT NULL,
   Vaccinations_today_pv INT(11) DEFAULT NULL,
   Vaccinations_today_penta INT(11) DEFAULT NULL,
   Vaccinations_today_pcv INT(11) DEFAULT NULL,
   Vaccinations_today_measles INT(11) DEFAULT NULL,
   Vaccinations_today_hbv INT(11) DEFAULT NULL,
   Vaccinations_today_flu INT(11) DEFAULT NULL,
   Vaccinations_today_other INT(11) DEFAULT NULL,
   Vaccinations_today_other_nc INT(11) DEFAULT NULL,
   substitution_first_line_regimen_date DATE ,
   substitution_first_line_regimen_reason INT(11),
   substitution_second_line_regimen_date DATE,
   substitution_second_line_regimen_reason INT(11),
   second_line_regimen_change_date DATE,
   second_line_regimen_change_reason INT(11),
   Pregnancy_status INT(11),
   breastfeeding INT(11),
   Wants_pregnancy INT(11) DEFAULT NULL,
   pregnancy_outcome INT(11),
   Anc_number VARCHAR(50),
   Anc_profile INT(11),
   Expected_delivery_date DATE,
   ever_had_menses INT(11),
   Last_menstrual_period DATE,
   menopausal INT(11),
   Gravida INT(11),
   Parity_term INT(11),
   Parity_abortion INT(11),
   Family_planning_status INT(11),
   family_planning_method INT(11),
   reason_not_using_family_planning INT(11),
   tb_status INT(11),
   started_anti_TB INT(11),
   tb_rx_date DATE,
   tb_treatment_no VARCHAR(50),
   general_examination VARCHAR(255),
   system_examination INT(11),
   skin_findings INT(11),
   eyes_findings INT(11),
   ent_findings INT(11),
   chest_findings INT(11),
   cvs_findings INT(11),
   abdomen_findings INT(11),
   cns_findings INT(11),
   genitourinary_findings INT(11),
   prophylaxis_given VARCHAR(50),
   Ctx_adherence INT(11),
   Ctx_dispensed INT(11),
   Dapsone_adherence INT(11),
   Dapsone_dispensed INT(11),
   Inh_dispensed INT(11),
   Arv_adherence INT(11),
   poor_arv_adherence_reason INT(11),
   poor_arv_adherence_reason_other VARCHAR(200),
   Pwp_disclosure INT(11),
   pwp_pead_disclosure INT(11),
   Pwp_partner_tested INT(11),
   Condom_provided INT(11),
   Screened_for_substance_abuse INT(11),
   Screened_for_sti INT(11),
   Cacx_screening INT(11),
   Sti_partner_notification INT(11),
   at_risk_population INT(11),
   system_review_finding INT(11),
   Next_appointment_date DATE,
   refill_date DATE,
   appointment_consent INT(11),
   Next_appointment_reason INT(11),
   Stability INT(11),
   Differentiated_care INT(11),
   voided INT(11),
   INDEX(Person_Id),
   INDEX(patient_id)
);
-- ----------------------------- beginning of extraction -----------------------------------------
insert into migration_tr.tr_demographics(
    Person_Id,
    First_Name,
    Middle_Name,
    Last_Name,
    Sex,
    DOB,
    Exact_DOB,
    dead,
    CreateDate,
    death_date
)
select
    p.person_id,
    p.given_name,
    p.middle_name,
    p.family_name,
    p.gender,
    p.birthdate,
    p.birthdate_estimated,
    p.dead,
    p.date_created,
    p.death_date
FROM (
         select
             p.person_id,
             pn.given_name,
             pn.middle_name,
             pn.family_name,
             p.gender,
             p.birthdate,
             p.birthdate_estimated,
             p.dead,
             p.date_created,
             p.death_date
         from migration_st.person p
                  left join migration_st.patient pa on pa.patient_id=p.person_id
                  left join migration_st.person_name pn on pn.person_id = p.person_id and pn.voided=0
         where p.voided=0 and p.birthdate is not null
         GROUP BY p.person_id
     ) p
ON DUPLICATE KEY UPDATE First_Name = p.given_name, Middle_Name=p.middle_name, Last_Name=p.family_name;

-- update etl_patient_demographics with patient attributes: birthplace, citizenship, mother_name, phone number and kin's details
update migration_tr.tr_demographics d
    left outer join
    (
        select
            pa.person_id,
            max(if(pat.uuid='8d8718c2-c2cc-11de-8d13-0010c6dffd0f', pa.value, null)) as birthplace,
            max(if(pat.uuid='8d871afc-c2cc-11de-8d13-0010c6dffd0f', pa.value, null)) as citizenship,
            max(if(pat.uuid='8d871d18-c2cc-11de-8d13-0010c6dffd0f', pa.value, null)) as Mother_name,
            max(if(pat.uuid='b2c38640-2603-4629-aebd-3b54f33f1e3a', pa.value, null)) as phone_number,
            max(if(pat.uuid='342a1d39-c541-4b29-8818-930916f4c2dc', pa.value, null)) as next_of_kin_contact,
            max(if(pat.uuid='d0aa9fd1-2ac5-45d8-9c5e-4317c622c8f5', pa.value, null)) as next_of_kin_relationship,
            max(if(pat.uuid='7cf22bec-d90a-46ad-9f48-035952261294', pa.value, null)) as next_of_kin_address,
            max(if(pat.uuid='830bef6d-b01f-449d-9f8d-ac0fede8dbd3', pa.value, null)) as next_of_kin_name,
            max(if(pat.uuid='b8d0b331-1d2d-4a9a-b741-1816f498bdb6', pa.value, null)) as email_address
        from migration_st.person_attribute pa
                 inner join
             (
                 select
                     pat.person_attribute_type_id,
                     pat.name,
                     pat.uuid
                 from migration_st.person_attribute_type pat
                 where pat.retired=0
             ) pat on pat.person_attribute_type_id = pa.person_attribute_type_id
                 and pat.uuid in (
                                  '8d8718c2-c2cc-11de-8d13-0010c6dffd0f', -- birthplace
                                  '8d871afc-c2cc-11de-8d13-0010c6dffd0f', -- citizenship
                                  '8d871d18-c2cc-11de-8d13-0010c6dffd0f', -- mother's name
                                  'b2c38640-2603-4629-aebd-3b54f33f1e3a', -- telephone contact
                                  '342a1d39-c541-4b29-8818-930916f4c2dc', -- next of kin's contact
                                  'd0aa9fd1-2ac5-45d8-9c5e-4317c622c8f5', -- next of kin's relationship
                                  '7cf22bec-d90a-46ad-9f48-035952261294', -- next of kin's address
                                  '830bef6d-b01f-449d-9f8d-ac0fede8dbd3', -- next of kin's name
                                  'b8d0b331-1d2d-4a9a-b741-1816f498bdb6', -- email address
                                  '848f5688-41c6-464c-b078-ea6524a3e971', -- unit
                                  '96a99acd-2f11-45bb-89f7-648dbcac5ddf', -- cadre
                                  '9f1f8254-20ea-4be4-a14d-19201fe217bf' -- rank

                     )
        where pa.voided=0
        group by pa.person_id
    ) att on att.person_id = d.Person_Id
set d.Phone_number=att.phone_number,
    d.Next_of_kin=att.next_of_kin_name,
    d.Next_of_kin_relationship=att.next_of_kin_relationship,
    d.Next_of_kin_phone=att.next_of_kin_contact,
    d.Email_address=att.email_address
;


update migration_tr.tr_demographics d
    join (select pi.patient_id,
                 coalesce (max(if(pit.uuid='05ee9cf4-7242-4a17-b4d4-00f707265c8a',pi.identifier,null)),max(if(pit.uuid='b51ffe55-3e76-44f8-89a2-14f5eaf11079',pi.identifier,null))) as upn,
                 max(if(pit.uuid='b4d66522-11fc-45c7-83e3-39a1af21ae0d',pi.identifier,null)) Patient_clinic_number,
                 max(if(pit.uuid='49af6cdc-7968-4abb-bf46-de10d7f4859f',pi.identifier,null)) National_id,
                 max(if(pit.uuid='be9beef6-aacc-4e1f-ac4e-5babeaa1e303',pi.identifier,null)) Passport_number,
                 max(if(pit.uuid='68449e5a-8829-44dd-bfef-c9c8cf2cb9b2',pi.identifier,null)) Birth_cert_number,
                 max(if(pit.uuid='1c7d0e5b-2068-4816-a643-8de83ab65fbf',pi.identifier,null)) alien_no,
                 max(if(pit.uuid='ca125004-e8af-445d-9436-a43684150f8b',pi.identifier,null)) driving_license_no,
                 max(if(pit.uuid='f85081e2-b4be-4e48-b3a4-7994b69bb101',pi.identifier,null)) national_unique_patient_identifier
          from migration_st.patient_identifier pi
                   join migration_st.patient_identifier_type pit on pi.identifier_type=pit.patient_identifier_type_id
          where voided=0
          group by pi.patient_id) pid on pid.patient_id=d.Person_Id
set d.UPN=pid.upn,
    d.National_id_no=pid.National_id,
    d.Passport=pid.Passport_number,
    d.Birth_certificate=pid.Birth_cert_number,
    d.Patient_clinic_number=pid.Patient_clinic_number,
    d.Alien_registration=pid.alien_no
;

update migration_tr.tr_demographics d
    join (select o.person_id as patient_id,
                 max(if(o.concept_id in(1054),o.value_coded,null))  as marital_status,
                 max(if(o.concept_id in(1712),o.value_coded,null))  as education_level,
                 max(if(o.concept_id in(1542),o.value_coded,null))  as occupation,
                 max(o.date_created) as date_created
          from migration_st.obs o
                   join migration_st.concept_name cn on cn.concept_id=o.value_coded and cn.concept_name_type='FULLY_SPECIFIED'
              and cn.locale='en'
          where o.concept_id in (1054,1712,1542) and o.voided=0
          group by person_id) pstatus on pstatus.patient_id=d.Person_Id
set d.Marital_status=pstatus.marital_status,
    d.Education_level=pstatus.education_level,
    d.Occupation=pstatus.occupation
;


update migration_tr.tr_demographics d
    left outer join
    (
        select
            person_id,
            country,
            county_district as County,
            state_province as Sub_county,
            address4 as Ward,
            city_village as Village,
            address2 as Landmark,
            address1 as Postal_Address
        from migration_st.person_address pa
        where pa.voided=0
        group by pa.person_id
    ) addr on addr.person_id = d.Person_Id
set d.County=addr.County,
    d.Sub_county=addr.Sub_county,
    d.Ward=addr.Ward,
    d.Village=addr.Village,
    d.Landmark=addr.Landmark,
    d.Postal_Address=addr.Postal_Address
;


INSERT INTO migration_tr.etl_hts_initial_test (
    uuid,
    Person_Id,
    location_id,
    creator,
    date_created,
    Encounter_Date,
    Pop_Type,
    Key_Pop_Type,
    priority_population_type,
    Ever_Tested,
    months_since_last_test,
    patient_disabled,
    disability_type,
    Consented,
    Tested_As,
    setting,
    approach,
    HTS_Strategy,
    HTS_Entry_Point,
    hts_risk_category,
    hts_risk_score,
    Test_1_Kit_Name,
    Test_1_Lot_Number,
    Test_1_Expiry_Date,
    Test_1_Final_Result,
    Test_2_Kit_Name,
    Test_2_Lot_Number,
    Test_2_Expiry_Date,
    Test_2_Final_Result,
    Final_Result,
    syphillis_test_result,
    Result_given,
    couple_discordant,
    referral_for,
    referral_facility,
    other_referral_facility,
    neg_referral_for,
    neg_referral_specify,
    Tb_Screening_Results,
    Self_Tested ,
    Remarks,
    voided
)
select
    e.uuid,
    e.patient_id as Person_Id,
    e.location_id,
    e.creator,
    e.date_created,
    e.encounter_datetime as Encounter_Date,
    max(if(o.concept_id=164930,o.value_coded,null)) as Pop_Type,
    max(if(o.concept_id=160581 and o.value_coded in(105,160578,160579,165100,162277,5622), o.value_coded,null)) as Key_Pop_Type,
    max(if(o.concept_id=160581 and o.value_coded in(159674,162198,160549,162277,1175,165192), o.value_coded,null)) as priority_population_type,
    max(if(o.concept_id=164401,o.value_coded,null)) as Ever_Tested,
    max(if(o.concept_id=159813,o.value_numeric,null)) as months_since_last_test,
    max(if(o.concept_id=164951,o.value_coded,null)) as patient_disabled,
    concat_ws(',',nullif(max(if(o.concept_id=162558 and o.value_coded = 120291,"Hearing impairment",'')),''),
              nullif(max(if(o.concept_id=162558 and o.value_coded =147215,"Visual impairment",'')),''),
              nullif(max(if(o.concept_id=162558 and o.value_coded =151342,"Mentally Challenged",'')),''),
              nullif(max(if(o.concept_id=162558 and o.value_coded = 164538,"Physically Challenged",'')),''),
              nullif(max(if(o.concept_id=162558 and o.value_coded = 5622,"Other",'')),''),
              nullif(max(if(o.concept_id=160632,o.value_text,'')),'')) as disability_type,
    max(if(o.concept_id=1710,o.value_coded,null)) as Consented,
    max(if(o.concept_id=164959,o.value_coded,null)) as Tested_As,
    max(if(o.concept_id=165215,o.value_coded,null)) as setting,
    max(if(o.concept_id=163556,o.value_coded,null)) as approach,
    max(if(o.concept_id=164956,o.value_coded,null)) as HTS_Strategy,
    max(if(o.concept_id=160540,o.value_coded,null)) as HTS_Entry_Point,
    max(if(o.concept_id=167163,o.value_coded,null)) as hts_risk_category,
    max(if(o.concept_id=167162,o.value_numeric,null)) as hts_risk_score,
    max(if(t.test_1_result is not null, t.kit_name, null)) as Test_1_Kit_Name,
    max(if(t.test_1_result is not null, t.lot_no, null)) as Test_1_Lot_Number,
    max(if(t.test_1_result is not null, t.expiry_date, null)) as Test_1_Expiry_Date,
    max(if(t.test_1_result is not null, t.test_1_result, null)) as Test_1_Final_Result,
    max(if(t.test_2_result is not null, t.kit_name, null)) as Test_2_Kit_Name,
    max(if(t.test_2_result is not null, t.lot_no, null)) as Test_2_Lot_Number,
    max(if(t.test_2_result is not null, t.expiry_date, null)) as Test_2_Expiry_Date,
    max(if(t.test_2_result is not null, t.test_2_result, null)) as Test_2_Final_Result,
    max(if(o.concept_id=159427,o.value_coded,null)) as Final_Result,
    max(if(o.concept_id=299,o.value_coded,null)) as syphillis_test_result,
    max(if(o.concept_id=164848,o.value_coded,null)) as Result_given,
    max(if(o.concept_id=6096, o.value_coded,null)) as couple_discordant,
    max(if(o.concept_id=1887, o.value_coded ,null)) as referral_for,
    max(if(o.concept_id=160481, o.value_coded ,null)) as referral_facility,
    max(if(o.concept_id=161550,trim(o.value_text),null)) as other_referral_facility,
    concat_ws(',', max(if(o.concept_id = 1272 and o.value_coded = 165276, 'Risk reduction counselling', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 159612, 'Safer sex practices', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 162223, 'VMMC', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 190, 'Condom use counselling', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 1691, 'Post-exposure prophylaxis', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 167125, 'Prevention and treatment of STIs', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 118855, 'Substance abuse and mental health treatment', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 141814, 'Prevention of GBV', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 1370, 'HIV testing and re-testing', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 166536, 'Pre-Exposure Prophylaxis', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 5622, 'Other', null))) as neg_referral_for,
    max(if(o.concept_id=164359,trim(o.value_text),null)) as neg_referral_specify,
    max(if(o.concept_id=1659,o.value_coded ,null)) as Tb_Screening_Results,
    max(if(o.concept_id=164952, o.value_coded ,null)) as Self_Tested,
    max(if(o.concept_id=163042,trim(o.value_text),null)) as Remarks,
    e.voided
from encounter e
         inner join person p on p.person_id=e.patient_id and p.voided=0
         inner join form f on f.form_id=e.form_id and f.uuid in ("402dc5d7-46da-42d4-b2be-f43ea4ad87b0","b08471f6-0892-4bf7-ab2b-bf79797b8ea4")
         inner join obs o on o.encounter_id = e.encounter_id and o.concept_id in (162084, 164930, 160581, 164401, 164951, 162558,160632, 1710, 164959, 164956,
                                                                                  160540,159427, 164848, 6096, 1659, 164952, 163042, 159813,165215,163556,161550,1887,1272,164359,160481,229,167163,167162)
         inner join (
    select
        o.person_id,
        o.encounter_id,
        o.obs_group_id,
        max(if(o.concept_id=1040, o.value_coded ,null)) as test_1_result ,
        max(if(o.concept_id=1326, o.value_coded ,null)) as test_2_result ,
        max(if(o.concept_id=164962, o.value_coded ,null)) as kit_name ,
        max(if(o.concept_id=164964,trim(o.value_text),null)) as lot_no,
        max(if(o.concept_id=162502,date(o.value_datetime),null)) as expiry_date
    from obs o
             inner join encounter e on e.encounter_id = o.encounter_id
             inner join form f on f.form_id=e.form_id and f.uuid in ("402dc5d7-46da-42d4-b2be-f43ea4ad87b0")
    where o.concept_id in (1040, 1326, 164962, 164964, 162502) and o.voided=0
    group by e.encounter_id, o.obs_group_id
) t on e.encounter_id = t.encounter_id
where e.voided=0
group by e.encounter_id;


INSERT INTO migration_tr.etl_hts_retest_test (
    uuid,
    Person_Id,
    location_id,
    creator,
    date_created,
    Encounter_Date,
    Pop_Type,
    Key_Pop_Type,
    priority_population_type,
    Ever_Tested,
    months_since_last_test,
    patient_disabled,
    disability_type,
    Consented,
    Tested_As,
    setting,
    approach,
    HTS_Strategy,
    HTS_Entry_Point,
    hts_risk_category,
    hts_risk_score,
    Test_1_Kit_Name,
    Test_1_Lot_Number,
    Test_1_Expiry_Date,
    Test_1_Final_Result,
    Test_2_Kit_Name,
    Test_2_Lot_Number,
    Test_2_Expiry_Date,
    Test_2_Final_Result,
    Final_Result,
    syphillis_test_result,
    Result_given,
    couple_discordant,
    referral_for,
    referral_facility,
    other_referral_facility,
    neg_referral_for,
    neg_referral_specify,
    Tb_Screening_Results,
    Self_Tested ,
    Remarks,
    voided
)
select
    e.uuid,
    e.patient_id as Person_Id,
    e.location_id,
    e.creator,
    e.date_created,
    e.encounter_datetime as Encounter_Date,
    max(if(o.concept_id=164930,o.value_coded,null)) as Pop_Type,
    max(if(o.concept_id=160581 and o.value_coded in(105,160578,160579,165100,162277,5622), o.value_coded,null)) as Key_Pop_Type,
    max(if(o.concept_id=160581 and o.value_coded in(159674,162198,160549,162277,1175,165192), o.value_coded,null)) as priority_population_type,
    max(if(o.concept_id=164401,o.value_coded,null)) as Ever_Tested,
    max(if(o.concept_id=159813,o.value_numeric,null)) as months_since_last_test,
    max(if(o.concept_id=164951,o.value_coded,null)) as patient_disabled,
    concat_ws(',',nullif(max(if(o.concept_id=162558 and o.value_coded = 120291,"Hearing impairment",'')),''),
              nullif(max(if(o.concept_id=162558 and o.value_coded =147215,"Visual impairment",'')),''),
              nullif(max(if(o.concept_id=162558 and o.value_coded =151342,"Mentally Challenged",'')),''),
              nullif(max(if(o.concept_id=162558 and o.value_coded = 164538,"Physically Challenged",'')),''),
              nullif(max(if(o.concept_id=162558 and o.value_coded = 5622,"Other",'')),''),
              nullif(max(if(o.concept_id=160632,o.value_text,'')),'')) as disability_type,
    max(if(o.concept_id=1710,o.value_coded,null)) as Consented,
    max(if(o.concept_id=164959,o.value_coded,null)) as Tested_As,
    max(if(o.concept_id=165215,o.value_coded,null)) as setting,
    max(if(o.concept_id=163556,o.value_coded,null)) as approach,
    max(if(o.concept_id=164956,o.value_coded,null)) as HTS_Strategy,
    max(if(o.concept_id=160540,o.value_coded,null)) as HTS_Entry_Point,
    max(if(o.concept_id=167163,o.value_coded,null)) as hts_risk_category,
    max(if(o.concept_id=167162,o.value_numeric,null)) as hts_risk_score,
    max(if(t.test_1_result is not null, t.kit_name, null)) as Test_1_Kit_Name,
    max(if(t.test_1_result is not null, t.lot_no, null)) as Test_1_Lot_Number,
    max(if(t.test_1_result is not null, t.expiry_date, null)) as Test_1_Expiry_Date,
    max(if(t.test_1_result is not null, t.test_1_result, null)) as Test_1_Final_Result,
    max(if(t.test_2_result is not null, t.kit_name, null)) as Test_2_Kit_Name,
    max(if(t.test_2_result is not null, t.lot_no, null)) as Test_2_Lot_Number,
    max(if(t.test_2_result is not null, t.expiry_date, null)) as Test_2_Expiry_Date,
    max(if(t.test_2_result is not null, t.test_2_result, null)) as Test_2_Final_Result,
    max(if(o.concept_id=159427,o.value_coded,null)) as Final_Result,
    max(if(o.concept_id=299,o.value_coded,null)) as syphillis_test_result,
    max(if(o.concept_id=164848,o.value_coded,null)) as Result_given,
    max(if(o.concept_id=6096, o.value_coded,null)) as couple_discordant,
    max(if(o.concept_id=1887, o.value_coded ,null)) as referral_for,
    max(if(o.concept_id=160481, o.value_coded ,null)) as referral_facility,
    max(if(o.concept_id=161550,trim(o.value_text),null)) as other_referral_facility,
    concat_ws(',', max(if(o.concept_id = 1272 and o.value_coded = 165276, 'Risk reduction counselling', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 159612, 'Safer sex practices', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 162223, 'VMMC', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 190, 'Condom use counselling', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 1691, 'Post-exposure prophylaxis', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 167125, 'Prevention and treatment of STIs', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 118855, 'Substance abuse and mental health treatment', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 141814, 'Prevention of GBV', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 1370, 'HIV testing and re-testing', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 166536, 'Pre-Exposure Prophylaxis', null)),
              max(if(o.concept_id = 1272 and o.value_coded = 5622, 'Other', null))) as neg_referral_for,
    max(if(o.concept_id=164359,trim(o.value_text),null)) as neg_referral_specify,
    max(if(o.concept_id=1659,o.value_coded ,null)) as Tb_Screening_Results,
    max(if(o.concept_id=164952, o.value_coded ,null)) as Self_Tested,
    max(if(o.concept_id=163042,trim(o.value_text),null)) as Remarks,
    e.voided
from encounter e
         inner join person p on p.person_id=e.patient_id and p.voided=0
         inner join form f on f.form_id=e.form_id and f.uuid in ("402dc5d7-46da-42d4-b2be-f43ea4ad87b0","b08471f6-0892-4bf7-ab2b-bf79797b8ea4")
         inner join obs o on o.encounter_id = e.encounter_id and o.concept_id in (162084, 164930, 160581, 164401, 164951, 162558,160632, 1710, 164959, 164956,
                                                                                  160540,159427, 164848, 6096, 1659, 164952, 163042, 159813,165215,163556,161550,1887,1272,164359,160481,229,167163,167162)
         inner join (
    select
        o.person_id,
        o.encounter_id,
        o.obs_group_id,
        max(if(o.concept_id=1040, o.value_coded ,null)) as test_1_result ,
        max(if(o.concept_id=1326, o.value_coded ,null)) as test_2_result ,
        max(if(o.concept_id=164962, o.value_coded ,null)) as kit_name ,
        max(if(o.concept_id=164964,trim(o.value_text),null)) as lot_no,
        max(if(o.concept_id=162502,date(o.value_datetime),null)) as expiry_date
    from obs o
             inner join encounter e on e.encounter_id = o.encounter_id
             inner join form f on f.form_id=e.form_id and f.uuid in ("b08471f6-0892-4bf7-ab2b-bf79797b8ea4")
    where o.concept_id in (1040, 1326, 164962, 164964, 162502) and o.voided=0
    group by e.encounter_id, o.obs_group_id
) t on e.encounter_id = t.encounter_id
where e.voided=0
group by e.encounter_id;

INSERT INTO migration_tr.etl_patient_triage(
    uuid,
    Person_Id,
    visit_id,
    Encounter_Date,
    location_id,
    encounter_id,
    creator,
    date_created,
    Visit_reason,
    Weight,
    Height,
    Systolic_pressure,
    Diastolic_pressure,
    Temperature,
    Pulse_rate,
    Respiratory_rate,
    Oxygen_saturation,
    Muac,
    nutritional_status,
    Last_menstrual_period,
    hpv_vaccinated,
    voided
)
select
    e.uuid,
    e.patient_id as Person_Id,
    e.visit_id,
    date(e.encounter_datetime) as Encounter_Date,
    null as location_id,
    null as encounter_id,
    e.creator,
    e.date_created as date_created,
    max(if(o.concept_id=160430,trim(o.value_text),null)) as Visit_reason,
    max(if(o.concept_id=5089,o.value_numeric,null)) as Weight,
    max(if(o.concept_id=5090,o.value_numeric,null)) as Height,
    max(if(o.concept_id=5085,o.value_numeric,null)) as Systolic_pressure,
    max(if(o.concept_id=5086,o.value_numeric,null)) as Diastolic_pressure,
    max(if(o.concept_id=5088,o.value_numeric,null)) as Temperature,
    max(if(o.concept_id=5087,o.value_numeric,null)) as Pulse_rate,
    max(if(o.concept_id=5242,o.value_numeric,null)) as Respiratory_rate,
    max(if(o.concept_id=5092,o.value_numeric,null)) as Oxygen_saturation,
    max(if(o.concept_id=1343,o.value_numeric,null)) as Muac,
    max(if(o.concept_id=163300,o.value_coded,null)) as nutritional_status,
    max(if(o.concept_id=1427,date(o.value_datetime),null)) as Last_menstrual_period,
    max(if(o.concept_id=160325,o.value_coded,null)) as hpv_vaccinated,
    e.voided as voided
from encounter e
    inner join person p on p.person_id=e.patient_id and p.voided=0
    inner join
    (
    select encounter_type_id, uuid, name from encounter_type where uuid in('d1059fb9-a079-4feb-a749-eedd709ae542')
    ) et on et.encounter_type_id=e.encounter_type
    left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0
    and o.concept_id in (160430,5089,5090,5085,5086,5088,5087,5242,5092,1343,163300,1427,160325)
where e.voided=0
group by e.patient_id, Encounter_Date
;

insert into migration_tr.etl_hiv_enrollment (
    Person_Id,
    uuid,
    Encounter_Date,
    location_id,
    Encounter_ID,
    creator,
    date_created,
    Patient_Type,
    Date_first_enrolled_in_care,
    Entry_point,
    Transfer_in_date,
    facility_transferred_from,
    district_transferred_from,
    previous_regimen,
    Date_started_art_at_transferring_facility,
    Date_confirmed_hiv_positive,
    Facility_confirmed_hiv_positive,
    arv_status,
    ever_on_pmtct,
    ever_on_pep,
    ever_on_prep,
    ever_on_haart,
    name_of_treatment_supporter,
    relationship_of_treatment_supporter,
    treatment_supporter_telephone,
    treatment_supporter_address,
    in_school,
    orphan,
    date_of_discontinuation,
    discontinuation_reason,
    voided
)
select
    e.patient_id,
    e.uuid,
    e.encounter_datetime as Encounter_Date,
    null as location_id,
    null as Encounter_ID,
    e.creator,
    e.date_created,
    max(if(o.concept_id in (164932), o.value_coded, if(o.concept_id=160563 and o.value_coded=1065, 160563, null))) as Patient_Type ,
    max(if(o.concept_id=160555,o.value_datetime,null)) as Date_first_enrolled_in_care ,
    max(if(o.concept_id=160540,o.value_coded,null)) as Entry_point,
    max(if(o.concept_id=160534,o.value_datetime,null)) as Transfer_in_date,
    max(if(o.concept_id=160535,left(trim(o.value_text),100),null)) as facility_transferred_tcingfrom,
    max(if(o.concept_id=161551,left(trim(o.value_text),100),null)) as district_transferred_from,
    max(if(o.concept_id=164855,o.value_coded,null)) as previous_regimen,
    max(if(o.concept_id=159599,o.value_datetime,null)) as Date_started_art_at_transferring_facility,
    max(if(o.concept_id=160554,o.value_datetime,null)) as Date_confirmed_hiv_positive,
    max(if(o.concept_id=160632,left(trim(o.value_text),100),null)) as Facility_confirmed_hiv_positive,
    max(if(o.concept_id=160533,o.value_coded,null)) as arv_status,
    max(if(o.concept_id=1148,o.value_coded,null)) as ever_on_pmtct,
    max(if(o.concept_id=1691,o.value_coded,null)) as ever_on_pep,
    max(if(o.concept_id=165269,o.value_coded,null)) as ever_on_prep,
    max(if(o.concept_id=1181,o.value_coded,null)) as ever_on_haart,
    max(if(o.concept_id=160638,left(trim(o.value_text),100),null)) as name_of_treatment_supporter,
    max(if(o.concept_id=160640,o.value_coded,null)) as relationship_of_treatment_supporter,
    max(if(o.concept_id=160642,left(trim(o.value_text),100),null)) as treatment_supporter_telephone ,
    max(if(o.concept_id=160641,left(trim(o.value_text),100),null)) as treatment_supporter_address,
    max(if(o.concept_id=5629,o.value_coded,null)) as in_school,
    max(if(o.concept_id=1174,o.value_coded,null)) as orphan,
    max(if(o.concept_id=164384, o.value_datetime, null)) as date_of_discontinuation,
    max(if(o.concept_id=161555, o.value_coded, null)) as discontinuation_reason,
    e.voided
from encounter e
         inner join
     (
         select encounter_type_id, uuid, name from encounter_type where uuid='de78a6be-bfc5-4634-adc3-5f1a280455cc'
     ) et on et.encounter_type_id=e.encounter_type
         inner join person p on p.person_id=e.patient_id and p.voided=0
         left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0
    and o.concept_id in (160555,160540,160534,160535,161551,159599,160554,160632,160533,160638,160640,160642,160641,164932,160563,5629,1174,1088,161555,164855,164384,1148,1691,165269,1181)
where e.voided=0
group by e.patient_id, e.encounter_id;


-- create covid program enrollment

INSERT INTO migration_tr.hiv_program_enrollment (
    Person_Id,
    Encounter_Date,
    patient_id,
    Encounter_ID,
    Program,
    Date_Enrolled,
    Date_Completed,
    Create_date,
    Created_by
)
select
    pp.patient_id as Person_Id,
    pp.date_enrolled as Encounter_Date,
    null as patient_id,
    null as Encounter_ID,
    null as Program,
    pp.date_enrolled as Date_Enrolled,
    pp.date_completed as Date_Completed,
    pp.date_created as Create_date,
    pp.creator as Created_by
FROM migration_st.patient_program pp
         INNER JOIN migration_st.program p on p.program_id=pp.program_id and p.uuid = 'dfdc6d40-2f2f-463d-ba90-cc97350441a8';


INSERT INTO migration_tr.etl_patient_hiv_followup(
    uuid,
    Person_Id,
    Encounter_Date,
    location_id,
    encounter_id,
    creator,
    date_created,
    Visit_scheduled,
    Visit_by,
    Visit_by_other,
    weight,
    systolic_pressure,
    diastolic_pressure,
    height,
    temperature,
    pulse_rate,
    respiratory_rate,
    oxygen_saturation,
    muac,
    Nutritional_status,
    Population_type,
    key_population_type,
    Who_stage,
    who_stage_associated_oi,
    Presenting_complaints,
    clinical_notes,
    on_anti_tb_drugs,
    on_ipt,
    ever_on_ipt,
    cough,
    fever,
    weight_loss_poor_gain,
    night_sweats,
    tb_case_contact,
    lethargy,
    screened_for_tb,
    spatum_smear_ordered,
    chest_xray_ordered,
    genexpert_ordered,
    spatum_smear_result,
    chest_xray_result,
    genexpert_result,
    referral,
    clinical_tb_diagnosis,
    contact_invitation,
    evaluated_for_ipt,
    Has_known_allergies,
    Has_Chronic_illnesses_cormobidities,
    Has_adverse_drug_reaction,
    Pregnancy_status,
    breastfeeding,
    Wants_pregnancy,
    pregnancy_outcome,
    Anc_number,
    Expected_delivery_date,
    ever_had_menses,
    Last_menstrual_period,
    menopausal,
    Gravida,
    Parity_term,
    Parity_abortion,
    Family_planning_status,
    family_planning_method,
    reason_not_using_family_planning,
    tb_status,
    started_anti_TB,
    tb_rx_date,
    tb_treatment_no,
    general_examination,
    system_examination,
    skin_findings,
    eyes_findings,
    ent_findings,
    chest_findings,
    cvs_findings,
    abdomen_findings,
    cns_findings,
    genitourinary_findings,
    prophylaxis_given,
    Ctx_adherence,
    Ctx_dispensed,
    Dapsone_adherence,
    Dapsone_dispensed,
    Inh_dispensed,
    Arv_adherence,
    poor_arv_adherence_reason,
    poor_arv_adherence_reason_other,
    Pwp_disclosure,
    pwp_pead_disclosure,
    Pwp_partner_tested,
    Condom_provided,
    Screened_for_substance_abuse,
    Screened_for_sti,
    Cacx_screening,
    Sti_partner_notification,
    at_risk_population,
    system_review_finding,
    Next_appointment_date,
    refill_date,
    appointment_consent,
    Next_appointment_reason,
    Stability,
    Differentiated_care,
    voided
)
select
    e.uuid,
    e.patient_id,
    date(e.encounter_datetime) as Encounter_Date,
    null as location_id,
    null as encounter_id,
    e.creator,
    e.date_created as date_created,
    max(if(o.concept_id=1246,o.value_coded,null)) as Visit_scheduled ,
    max(if(o.concept_id=161643,o.value_coded,null)) as Visit_by,
    null as Visit_by_other,
    max(if(o.concept_id=5089,o.value_numeric,null)) as weight,
    max(if(o.concept_id=5085,o.value_numeric,null)) as systolic_pressure,
    max(if(o.concept_id=5086,o.value_numeric,null)) as diastolic_pressure,
    max(if(o.concept_id=5090,o.value_numeric,null)) as height,
    max(if(o.concept_id=5088,o.value_numeric,null)) as temperature,
    max(if(o.concept_id=5087,o.value_numeric,null)) as pulse_rate,
    max(if(o.concept_id=5242,o.value_numeric,null)) as respiratory_rate,
    max(if(o.concept_id=5092,o.value_numeric,null)) as oxygen_saturation,
    max(if(o.concept_id=1343,o.value_numeric,null)) as muac,
    max(if(o.concept_id=163300,o.value_coded,null)) as Nutritional_status,
    max(if(o.concept_id=164930,o.value_coded,null)) as Population_type,
    max(if(o.concept_id=160581,o.value_coded,null)) as key_population_type,
    max(if(o.concept_id=5356,o.value_coded,null)) as who_stage,
    concat_ws(',',nullif(max(if(o.concept_id=167394 and o.value_coded =5006 ,'Asymptomatic','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =130364,'Persistent generalized lymphadenopathy)','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =159214,'Unexplained severe weight loss','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =5330,'Minor mucocutaneous manifestations','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =117543,'Herpes zoster','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =5012,'Recurrent upper respiratory tract infections','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =5018,'Unexplained chronic diarrhoea','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =5027,'Unexplained persistent fever','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =5337,'Oral hairy leukoplakia','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =42,'Pulmonary tuberculosis','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =5333,'Severe bacterial infections such as empyema or pyomyositis or meningitis','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =133440,'Acute necrotizing ulcerative stomatitis or gingivitis or periodontitis','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =148849,'Unexplained anaemia','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =823,'HIV wasting syndrome','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =137375,'Pneumocystis jirovecipneumonia PCP','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =1215,'Recurrent severe bacterial pneumonia','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =1294,'Cryptococcal meningitis','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =990,'Toxoplasmosis of the brain','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =143929,'Chronic orolabial, genital or ano-rectal herpes simplex','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =110915,'Kaposi sarcoma KS','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =160442,'HIV encephalopathy','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =5042,'Extra pulmonary tuberculosis','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =143110,'Cryptosporidiosis with diarrhoea','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =136458,'Isosporiasis','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =5033,'Cryptococcosis extra pulmonary','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =160745,'Disseminated non-tuberculous mycobacterial infection','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =154119,'Cytomegalovirus CMV retinitis','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =5046,'Progressive multifocal leucoencephalopathy','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =131357,'Any disseminated mycosis','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =146513,'Candidiasis of the oesophagus or airways','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =160851,'Non-typhoid salmonella NTS septicaemia','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =155941,'Lymphoma cerebral or B cell Non-Hodgkins Lymphoma','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =116023,'Invasive cervical cancer','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =123084,'Visceral leishmaniasis','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =153701,'Symptomatic HIV-associated nephropathy','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =825,'Unexplained asymptomatic hepatosplenomegaly','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =1249,'Papular pruritic eruptions','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =113116,'Seborrheic dermatitis','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =132387,'Fungal nail infections','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =148762,'Angular cheilitis','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =159344,'Linear gingival erythema','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =1212,'Extensive HPV or molluscum infection','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =159912,'Recurrent oral ulcerations','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =1210,'Parotid enlargement','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =127784,'Recurrent or chronic upper respiratory infection','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =134722,'Unexplained moderate malnutrition','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =163282,'Unexplained persistent fever','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =5334,'Oral candidiasis','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =160515,'Severe recurrent bacterial pneumonia','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =135458,'Lymphoid interstitial pneumonitis (LIP)','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =163712,'HIV-related cardiomyopathy','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =162331,'Unexplained severe wasting or severe malnutrition','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =130021,'Pneumocystis pneumonia','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =146518,'Candida of trachea, bronchi or lungs','')),''),
              nullif(max(if(o.concept_id=167394 and o.value_coded =143744,'Acquired recto-vesicular fistula','')),'')) as who_stage_associated_oi,
    max(if(o.concept_id=1154,o.value_coded,null)) as Presenting_complaints,
    null as clinical_notes, -- max(if(o.concept_id=160430,left(trim(o.value_text),600),null)) as clinical_notes ,
    max(if(o.concept_id=164948,o.value_coded,null)) as on_anti_tb_drugs ,
    max(if(o.concept_id=164949,o.value_coded,null)) as on_ipt ,
    max(if(o.concept_id=164950,o.value_coded,null)) as ever_on_ipt ,
    max(if(o.concept_id=1729 and o.value_coded =159799,o.value_coded,null)) as cough,
    max(if(o.concept_id=1729 and o.value_coded =1494,o.value_coded,null)) as fever,
    max(if(o.concept_id=1729 and o.value_coded =832,o.value_coded,null)) as weight_loss_poor_gain,
    max(if(o.concept_id=1729 and o.value_coded =133027,o.value_coded,null)) as night_sweats,
    max(if(o.concept_id=1729 and o.value_coded =124068,o.value_coded,null)) as tb_case_contact,
    max(if(o.concept_id=1729 and o.value_coded =116334,o.value_coded,null)) as lethargy,
    max(if(o.concept_id=1729 and o.value_coded in(159799,1494,832,133027,124068,116334,1066),'Yes','No'))as screened_for_tb,
    max(if(o.concept_id=1271 and o.value_coded =307,o.value_coded,null)) as spatum_smear_ordered ,
    max(if(o.concept_id=1271 and o.value_coded =12,o.value_coded,null)) as chest_xray_ordered ,
    max(if(o.concept_id=1271 and o.value_coded = 162202,o.value_coded,null)) as genexpert_ordered ,
    max(if(o.concept_id=307,o.value_coded,null)) as spatum_smear_result ,
    max(if(o.concept_id=12,o.value_coded,null)) as chest_xray_result ,
    max(if(o.concept_id=162202,o.value_coded,null)) as genexpert_result ,
    max(if(o.concept_id=1272,o.value_coded,null)) as referral ,
    max(if(o.concept_id=163752,o.value_coded,null)) as clinical_tb_diagnosis ,
    max(if(o.concept_id=163414,o.value_coded,null)) as contact_invitation ,
    max(if(o.concept_id=162275,o.value_coded,null)) as evaluated_for_ipt ,
    max(if(o.concept_id=160557,o.value_coded,null)) as Has_known_allergies ,
    max(if(o.concept_id=162747,o.value_coded,null)) as has_chronic_illnesses_cormobidities ,
    max(if(o.concept_id=121764,o.value_coded,null)) as Has_adverse_drug_reaction ,
    max(if(o.concept_id=5272,o.value_coded,null)) as Pregnancy_status,
    max(if(o.concept_id=5632,o.value_coded,null)) as breastfeeding,
    max(if(o.concept_id=164933,o.value_coded,null)) as Wants_pregnancy,
    max(if(o.concept_id=161033,o.value_coded,null)) as pregnancy_outcome,
    max(if(o.concept_id=163530,o.value_text,null)) as Anc_number,
    max(if(o.concept_id=5596,date(o.value_datetime),null)) as Expected_delivery_date,
    max(if(o.concept_id=162877,o.value_coded,null)) as ever_had_menses,
    max(if(o.concept_id=1427,date(o.value_datetime),null)) as Last_menstrual_period,
    max(if(o.concept_id=160596,o.value_coded,null)) as menopausal,
    max(if(o.concept_id=5624,o.value_numeric,null)) as Gravida,
    -- max(if(o.concept_id=1053,o.value_numeric,null)) as Parity_term ,
    max(if(o.concept_id=160080,o.value_numeric,null)) as Parity_term,
    max(if(o.concept_id=1823,o.value_numeric,null)) as Parity_abortion ,
    max(if(o.concept_id=160653,o.value_coded,null)) as Family_planning_status,
    max(if(o.concept_id=374,o.value_coded,null)) as family_planning_method,
    max(if(o.concept_id=160575,o.value_coded,null)) as reason_not_using_family_planning ,
    max(if(o.concept_id=1659,o.value_coded,null)) as tb_status,
    max(if(o.concept_id=162309,o.value_coded,null)) as started_anti_TB,
    max(if(o.concept_id=1113,o.value_datetime,null)) as tb_rx_date,
    max(if(o.concept_id=161654,trim(o.value_text),null)) as tb_treatment_no,
    concat_ws(',',nullif(max(if(o.concept_id=162737 and o.value_coded =1107 ,'None','')),''),
              nullif(max(if(o.concept_id=162737 and o.value_coded =136443,'Jaundice','')),''),
              nullif(max(if(o.concept_id=162737 and o.value_coded =460,'Oedema','')),''),
              nullif(max(if(o.concept_id=162737 and o.value_coded = 5334,'Oral Thrush','')),''),
              nullif(max(if(o.concept_id=162737 and o.value_coded = 5245,'Pallor','')),''),
              nullif(max(if(o.concept_id=162737 and o.value_coded = 140125,'Finger Clubbing','')),''),
              nullif(max(if(o.concept_id=162737 and o.value_coded = 126952,'Lymph Node Axillary','')),''),
              nullif(max(if(o.concept_id=162737 and o.value_coded = 143050,'Cyanosis','')),''),
              nullif(max(if(o.concept_id=162737 and o.value_coded = 126939,'Lymph Nodes Inguinal','')),''),
              nullif(max(if(o.concept_id=162737 and o.value_coded = 823,'Wasting','')),''),
              nullif(max(if(o.concept_id=162737 and o.value_coded = 142630,'Dehydration','')),''),
              nullif(max(if(o.concept_id=162737 and o.value_coded = 116334,'Lethargic','')),'')) as general_examination,
    max(if(o.concept_id=159615,o.value_coded,null)) as system_examination,
    max(if(o.concept_id=1120,o.value_coded,null)) as skin_findings,
    max(if(o.concept_id=163309,o.value_coded,null)) as eyes_findings,
    max(if(o.concept_id=164936,o.value_coded,null)) as ent_findings,
    max(if(o.concept_id=1123,o.value_coded,null)) as chest_findings,
    max(if(o.concept_id=1124,o.value_coded,null)) as cvs_findings,
    max(if(o.concept_id=1125,o.value_coded,null)) as abdomen_findings,
    max(if(o.concept_id=164937,o.value_coded,null)) as cns_findings,
    max(if(o.concept_id=1126,o.value_coded,null)) as genitourinary_findings,
    max(if(o.concept_id=1109,o.value_coded,null)) as prophylaxis_given,
    max(if(o.concept_id=161652,o.value_coded,null)) as Ctx_adherence,
    max(if(o.concept_id=162229 or (o.concept_id=1282 and o.value_coded = 105281),o.value_coded,null)) as Ctx_dispensed,
    max(if(o.concept_id=164941,o.value_coded,null)) as Dapsone_adherence,
    max(if(o.concept_id=164940 or (o.concept_id=1282 and o.value_coded = 74250),o.value_coded,null)) as Dapsone_dispensed,
    max(if(o.concept_id=162230,o.value_coded,null)) as Inh_dispensed,
    max(if(o.concept_id=1658,o.value_coded,null)) as Arv_adherence,
    max(if(o.concept_id=160582,o.value_coded,null)) as poor_arv_adherence_reason,
    null as poor_arv_adherence_reason_other, -- max(if(o.concept_id=160632,trim(o.value_text),null)) as poor_arv_adherence_reason_other,
    max(if(o.concept_id=159423,o.value_coded,null)) as Pwp_disclosure,
    max(if(o.concept_id=5616,o.value_coded,null)) as pwp_pead_disclosure,
    max(if(o.concept_id=161557,o.value_coded,null)) as Pwp_partner_tested,
    max(if(o.concept_id=159777,o.value_coded,null)) as Condom_provided ,
    max(if(o.concept_id=112603,o.value_coded,null)) as Screened_for_substance_abuse ,
    max(if(o.concept_id=161558,o.value_coded,null)) as Screened_for_sti,
    max(if(o.concept_id=164934,o.value_coded,null)) as Cacx_screening,
    max(if(o.concept_id=164935,o.value_coded,null)) as Sti_partner_notification,
    max(if(o.concept_id=160581,o.value_coded,null)) as at_risk_population,
    max(if(o.concept_id=159615,o.value_coded,null)) as system_review_finding,
    max(if(o.concept_id=5096,o.value_datetime,null)) as Next_appointment_date,
    max(if(o.concept_id=162549,o.value_datetime,null)) as refill_date,
    max(if(o.concept_id=166607,o.value_coded,null)) as appointment_consent,
    max(if(o.concept_id=160288,o.value_coded,null)) as Next_appointment_reason,
    max(if(o.concept_id=1855,o.value_coded,null)) as Stability,
    max(if(o.concept_id=164947,o.value_coded,null)) as Differentiated_care,
    e.voided as voided
from encounter e
         inner join person p on p.person_id=e.patient_id and p.voided=0
         inner join form f on f.form_id = e.form_id and f.uuid in ('22c68f86-bbf0-49ba-b2d1-23fa7ccf0259','23b4ebbd-29ad-455e-be0e-04aa6bc30798')
         left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0
    and o.concept_id in (1282,1246,161643,5089,5085,5086,5090,5088,5087,5242,5092,1343,5356,167394,5272,5632, 161033,163530,5596,1427,5624,1053,160653,374,160575,1659,161654,161652,162229,162230,1658,160582,160632,159423,5616,161557,159777,112603,161558,160581,5096,163300, 164930, 160581, 1154, 160430,162877, 164948, 164949, 164950, 1271, 307, 12, 162202, 1272, 163752, 163414, 162275, 160557, 162747,
                         121764, 164933, 160080, 1823, 164940, 164934, 164935, 159615, 160288, 1855, 164947,162549,162877,160596,1109,1113,162309,1729,162737,159615,1120,163309,164936,1123,1124,1125,164937,1126,166607)
where e.voided=0
group by e.patient_id,Encounter_Date;





































