

/*
    Add migration tables here

*/

drop database migration_tr;
create database migration_tr;

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


CREATE TABLE migration_tr.migration_etl_cca_covid_screening (
                                                                uuid CHAR(38) PRIMARY KEY ,
                                                                Person_Id INT(11), -- the original patient id in the cca database
                                                                Encounter_ID INT(11) default null,
                                                                visit_id INT(11) DEFAULT NULL,
                                                                patient_id INT(11) DEFAULT NULL ,
                                                                location_id INT(11) DEFAULT NULL,
                                                                Encounter_Date DATE,
                                                                encounter_provider INT(11),
                                                                date_created DATE,
                                                                onset_symptoms_date DATE,
                                                                fever INT(11) DEFAULT NULL,
                                                                cough INT(11) DEFAULT NULL,
                                                                runny_nose INT(11) DEFAULT NULL,
                                                                diarrhoea INT(11) DEFAULT NULL,
                                                                headache INT(11) DEFAULT NULL,
                                                                muscular_pain INT(11) DEFAULT NULL,
                                                                abdominal_pain INT(11) DEFAULT NULL,
                                                                general_weakness INT(11) DEFAULT NULL,
                                                                sore_throat INT(11) DEFAULT NULL,
                                                                breathing_difficulty INT(11) DEFAULT NULL,
                                                                nausea_vomiting INT(11) DEFAULT NULL,
                                                                altered_mental_status INT(11) DEFAULT NULL,
                                                                chest_pain INT(11) DEFAULT NULL,
                                                                joint_pain INT(11) DEFAULT NULL,
                                                                loss_of_taste_smell INT(11) DEFAULT NULL,
                                                                other_symptom INT(11) DEFAULT NULL,
                                                                specify_symptoms VARCHAR(255) DEFAULT NULL,
                                                                recent_travel INT(11) DEFAULT NULL,
                                                                contact_with_suspected_or_confirmed_case INT(11) DEFAULT NULL,
                                                                attended_large_gathering INT(11) DEFAULT NULL,
                                                                screening_department INT(11) DEFAULT NULL,
                                                                hiv_status INT(11) DEFAULT NULL,
                                                                in_tb_program INT(11) DEFAULT NULL,
                                                                pregnant INT(11) DEFAULT NULL,
                                                                vaccinated_for_covid INT(11) DEFAULT NULL,
                                                                covid_vaccination_status INT(11) DEFAULT NULL,
                                                                ever_tested_for_covid INT(11) DEFAULT NULL,
                                                                covid_test_date date,
                                                                eligible_for_covid_test INT(11) DEFAULT NULL,
                                                                consented_for_covid_test INT(11) DEFAULT NULL,
                                                                decline_reason VARCHAR(200) DEFAULT NULL,
                                                                voided INT(11),
                                                                CONSTRAINT unique_uuid UNIQUE(uuid),
                                                                INDEX(Person_Id),
                                                                INDEX(Encounter_Date),
                                                                INDEX(visit_id),
                                                                INDEX(Encounter_ID),
                                                                INDEX(patient_id),
                                                                INDEX(patient_id, Encounter_Date)
);


-- ------------ create table etl_covid_treatment followup-----------------------

-- ------------ create table etl_cca_covid_rdt_test-----------------------

-- noinspection SqlDialectInspection

CREATE TABLE migration_tr.migration_etl_cca_covid_rdt_test (
                                                               uuid CHAR(38) PRIMARY KEY ,
                                                               Person_Id INT(11),
                                                               Encounter_ID INT(11) default null,
                                                               visit_id INT(11) DEFAULT NULL,
                                                               patient_id INT(11) DEFAULT NULL ,
                                                               location_id INT(11) DEFAULT NULL,
                                                               Encounter_Date DATE,
                                                               encounter_provider INT(11),
                                                               date_created DATE,
                                                               nationality INT(11),
                                                               passport_id_number VARCHAR(50),
                                                               sample_type INT(11),
                                                               test_reason INT(11),
                                                               test_reason_other VARCHAR(100),
                                                               ag_rdt_test_done INT(11),
                                                               ag_rdt_test_date DATE, -- Beginning of obs group
                                                               case_type INT(11),
                                                               assay_kit_name VARCHAR(40),
                                                               ag_rdt_test_type_coded INT(11),
                                                               ag_rdt_test_type_other VARCHAR(50),
                                                               kit_lot_number VARCHAR(50),
                                                               kit_expiry DATE,
                                                               test_result INT(11),
                                                               action_taken INT(11),
                                                               consented_for_covid_test INT(11),
                                                               decline_reason varchar(255),
                                                               voided INT(11),
                                                               INDEX(Encounter_Date),
                                                               INDEX(Person_Id),
                                                               INDEX(encounter_id),
                                                               INDEX(patient_id),
                                                               INDEX(patient_id, Encounter_Date)
);


-- ----------- create table etl_covid_clinical_review-----------------------

CREATE TABLE migration_tr.migration_etl_cca_covid_clinical_review (
                                                                      uuid CHAR(38) PRIMARY KEY,
                                                                      Person_Id INT(11),
                                                                      Encounter_ID INT(11) DEFAULT NULL,
                                                                      visit_id INT(11) DEFAULT NULL,
                                                                      patient_id INT(11) DEFAULT NULL ,
                                                                      location_id INT(11) DEFAULT NULL,
                                                                      Encounter_Date DATE,
                                                                      encounter_provider INT(11),
                                                                      date_created DATE,
                                                                      ag_rdt_test_result INT(11),
                                                                      case_classification INT(11),
                                                                      action_taken INT(11),
                                                                      hospital_referred_to VARCHAR(50),
                                                                      case_id VARCHAR(10),
                                                                      email VARCHAR(50),
                                                                      case_type INT(11),
                                                                      pcr_sample_collection_date DATE,
                                                                      pcr_result_date DATE,
                                                                      pcr_result INT(11),
                                                                      case_classification_after_positive_pcr INT(11),
                                                                      action_taken_after_pcr_result INT(11),
                                                                      notes VARCHAR(1024),
                                                                      voided INT(11),
                                                                      INDEX(Person_Id),
                                                                      INDEX(Encounter_Date),
                                                                      INDEX(visit_id),
                                                                      INDEX(encounter_id),
                                                                      INDEX(patient_id),
                                                                      INDEX(patient_id, Encounter_Date)
);



-- -------------------- create covid treatment enrollment  ---------------------

CREATE TABLE migration_tr.migration_etl_cca_covid_treatment_enrollment (
                                                                           uuid CHAR(38) PRIMARY KEY,
                                                                           Person_Id INT(11),
                                                                           Encounter_ID INT(11) DEFAULT NULL,
                                                                           visit_id INT(11) DEFAULT NULL,
                                                                           patient_id INT(11) DEFAULT NULL ,
                                                                           location_id INT(11) DEFAULT NULL,
                                                                           Encounter_Date DATE,
                                                                           encounter_provider INT(11),
                                                                           date_created DATE,
                                                                           passport_id_number VARCHAR(50),
                                                                           case_classification INT(11),
                                                                           patient_type INT(11),
                                                                           hospital_referred_from VARCHAR(50),
                                                                           date_tested_covid_positive DATE,
                                                                           action_taken INT(11),
                                                                           admission_date DATE,
                                                                           admission_unit INT(11),
                                                                           voided INT(11),
                                                                           INDEX(Person_Id),
                                                                           INDEX(Encounter_Date),
                                                                           INDEX(visit_id),
                                                                           INDEX(encounter_id),
                                                                           INDEX(patient_id),
                                                                           INDEX(patient_id, Encounter_Date)
);



-- --------------------------- create covid treatment outcome ------------------------

CREATE TABLE migration_tr.migration_etl_cca_covid_treatment_enrollment_outcome (
                                                                                   uuid CHAR(38) PRIMARY KEY,
                                                                                   Person_Id INT(11),
                                                                                   Encounter_ID INT(11) DEFAULT NULL,
                                                                                   visit_id INT(11) DEFAULT NULL,
                                                                                   patient_id INT(11) DEFAULT NULL ,
                                                                                   location_id INT(11) DEFAULT NULL,
                                                                                   Encounter_Date DATE,
                                                                                   encounter_provider INT(11),
                                                                                   date_created DATE,
                                                                                   outcome INT(11),
                                                                                   facility_transferred VARCHAR(50),
                                                                                   facility_referred VARCHAR(50),
                                                                                   comment VARCHAR(50),
                                                                                   voided INT(11),
                                                                                   INDEX(Person_Id),
                                                                                   INDEX(Encounter_Date),
                                                                                   INDEX(visit_id),
                                                                                   INDEX(encounter_id),
                                                                                   INDEX(patient_id),
                                                                                   INDEX(patient_id, Encounter_Date)
);

-- ----------------------------- create covid treatment followup -----------------------------------

CREATE TABLE migration_tr.migration_etl_cca_covid_treatment_followup (
                                                                         uuid CHAR(38) PRIMARY KEY,
                                                                         Person_Id INT(11),
                                                                         Encounter_ID INT(11) DEFAULT NULL ,
                                                                         visit_id INT(11) DEFAULT NULL,
                                                                         patient_id INT(11) DEFAULT NULL ,
                                                                         location_id INT(11) DEFAULT NULL,
                                                                         Encounter_Date DATE,
                                                                         encounter_provider INT(11),
                                                                         date_created DATE,
                                                                         day_of_followup int(11),
                                                                         temp VARCHAR(10),
                                                                         fever INT(11),
                                                                         cough INT(11),
                                                                         difficulty_breathing INT(11),
                                                                         sore_throat INT(11),
                                                                         sneezing INT(11),
                                                                         headache INT(11),
                                                                         referred_to_hosp INT(11),
                                                                         case_classification INT(11),
                                                                         patient_admitted INT(11),
                                                                         admission_unit INT(11),
                                                                         treatment_azithromycin INT(11),
                                                                         treatment_amoxicillin_clavulanic INT(11),
                                                                         treatment_amoxicillin INT(11),
                                                                         treatment_tocilizumab INT(11),
                                                                         treatment_dexamethasone INT(11),
                                                                         treatment_multivitamin INT(11),
                                                                         treatment_oxygen INT(11),
                                                                         treatment_other INT(11),
                                                                         on_ventilation INT(11),
                                                                         vaccinated INT(11),
                                                                         vaccination_status INT(11),
                                                                         treatment_received INT(11),
                                                                         notes varchar(200),
                                                                         voided INT(11),
                                                                         INDEX(Person_Id),
                                                                         INDEX(Encounter_Date),
                                                                         INDEX(visit_id),
                                                                         INDEX(encounter_id),
                                                                         INDEX(patient_id),
                                                                         INDEX(patient_id, Encounter_Date)
);



CREATE TABLE migration_tr.migration_cca_covid_program (
                                                          Person_Id INT(11) NOT NULL,
                                                          Encounter_Date DATE DEFAULT NULL,
                                                          patient_id INT(11) DEFAULT NULL,
                                                          Encounter_ID INT(11) DEFAULT NULL,
                                                          Program INT(11) DEFAULT NULL,
                                                          Date_Enrolled DATE,
                                                          Date_Completed DATE DEFAULT NULL,
                                                          Create_date DATE DEFAULT NULL,
                                                          Created_by INT(11) DEFAULT NULL,
                                                          INDEX(Person_Id),
                                                          INDEX(Encounter_Date),
                                                          INDEX(patient_id)
);

/*
	------------ -------------------------------- DML scripts
*/
-- ------------------------------ demographics -----------------------------


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

-- ------------------------------- covid screening ----------------------------

insert into migration_tr.migration_etl_cca_covid_screening(
    uuid,
    Person_Id,
    visit_id,
    patient_id,
    location_id,
    Encounter_Date,
    encounter_provider,
    date_created,
    onset_symptoms_date,
    fever,
    cough,
    runny_nose,
    diarrhoea,
    headache,
    muscular_pain,
    abdominal_pain,
    general_weakness,
    sore_throat,
    breathing_difficulty,
    nausea_vomiting,
    altered_mental_status,
    chest_pain,
    joint_pain,
    loss_of_taste_smell,
    other_symptom,
    specify_symptoms,
    recent_travel,
    contact_with_suspected_or_confirmed_case,
    attended_large_gathering,
    screening_department,
    hiv_status,
    in_tb_program,
    pregnant,
    vaccinated_for_covid,
    covid_vaccination_status,
    ever_tested_for_covid,
    covid_test_date,
    eligible_for_covid_test,
    consented_for_covid_test,
    decline_reason,
    voided
)
select
    e.uuid,
    e.patient_id Person_Id,
    e.visit_id as visit_id,
    null,
    e.location_id,
    date(e.encounter_datetime) as Encounter_Date,
    e.creator as encounter_provider,
    e.date_created as date_created,
    max(if(o.concept_id=1730,date(o.value_datetime),null)) as onset_symptoms_date,
    max(if(o.concept_id=140238,o.value_coded,null)) as fever,
    max(if(o.concept_id=143264,o.value_coded,null)) as cough,
    max(if(o.concept_id=163336,o.value_coded,null)) as runny_nose,
    max(if(o.concept_id=142412,o.value_coded,null)) as diarrhoea,
    max(if(o.concept_id=5219,o.value_coded,null)) as headache,
    max(if(o.concept_id=160388,o.value_coded,null)) as muscular_pain,
    max(if(o.concept_id=1125,o.value_coded,null)) as abdominal_pain,
    max(if(o.concept_id=122943,o.value_coded,null)) as general_weakness,
    max(if(o.concept_id=162737,o.value_coded,null)) as sore_throat,

    -- max(if(o.concept_id=163741,o.value_coded,null)) as sore_throat,
    max(if(o.concept_id=164441,o.value_coded,null)) as breathing_difficulty,
    max(if(o.concept_id=122983,o.value_coded,null)) as nausea_vomiting,

    max(if(o.concept_id=6023,o.value_coded,null)) as altered_mental_status,
    max(if(o.concept_id=1123,o.value_coded,null)) as chest_pain,
    max(if(o.concept_id=160687,o.value_coded,null)) as joint_pain,
    max(if(o.concept_id=1729,o.value_coded,null)) as loss_of_taste_smell,

    max(if(o.concept_id=1838,o.value_coded,null)) as other_symptom,
    max(if(o.concept_id=160632,o.value_text,null)) as specify_symptoms,
    max(if(o.concept_id=162619,o.value_coded,null)) as recent_travel,
    max(if(o.concept_id=162633,o.value_coded,null)) as contact_with_suspected_or_confirmed_case,
    max(if(o.concept_id=165163,o.value_coded,null)) as attended_large_gathering,
    max(if(o.concept_id=164918,o.value_coded,null)) as screening_department,
    max(if(o.concept_id=1169,o.value_coded,null)) as hiv_status,
    max(if(o.concept_id=162309,o.value_coded,null)) as in_tb_program,
    max(if(o.concept_id=5272,o.value_coded,null)) as pregnant,
    max(if(o.concept_id=163100,o.value_coded,null)) as vaccinated_for_covid,
    max(if(o.concept_id=164134,o.value_coded,null)) as covid_vaccination_status,
    max(if(o.concept_id=165852,o.value_coded,null)) as ever_tested_for_covid,
    max(if(o.concept_id=159948,date(o.value_datetime),null)) as covid_test_date,
    max(if(o.concept_id=165087,o.value_coded,null)) as eligible_for_covid_test,
    max(if(o.concept_id=1710,o.value_coded,null)) as consented_for_covid_test,
    max(if(o.concept_id=161011, o.value_text, null)) as decline_reason,
    e.voided as voided
from migration_st.encounter e
    inner join migration_st.person p on p.person_id=e.patient_id and p.voided=0
    inner join
    (
    select form_id from migration_st.form where
    uuid in('117092aa-5355-11ec-bf63-0242ac130002')
    ) f on f.form_id=e.form_id
    left outer join migration_st.obs o on o.encounter_id=e.encounter_id and o.voided=0
    and o.concept_id in (159948,1730,1729,140238,122943,143264,163741,163336,164441,142412,122983,5219,6023,160388,
    1123,1125,160687,1838,160632,5272,162619,162633,164918,1169,162309,163100,164134,159948,165163,165087,1710,161011,162737,165852)
where e.voided=0
group by e.patient_id, e.encounter_id;


-- -------------------------- covid test ---------------------
insert into migration_tr.migration_etl_cca_covid_rdt_test(
    uuid,
    Person_Id,
    Encounter_ID,
    visit_id,
    patient_id,
    location_id,
    Encounter_Date,
    encounter_provider,
    date_created,
    consented_for_covid_test,
    decline_reason,
    nationality,
    passport_id_number,
    sample_type,
    test_reason,
    test_reason_other,
    ag_rdt_test_done,
    ag_rdt_test_date,
    case_type,
    assay_kit_name,
    ag_rdt_test_type_coded,
    ag_rdt_test_type_other,
    kit_lot_number,
    kit_expiry,
    test_result,
    action_taken,
    voided
)
select
    e.uuid,
    e.patient_id Person_Id,
    null as encounter_id,
    e.visit_id as visit_id,
    NULL,
    e.location_id,
    date(e.encounter_datetime) as Encounter_Date,
    e.creator as encounter_provider,
    e.date_created as date_created,
    max(if(o.concept_id=1710,o.value_coded ,null)) as consented_for_covid_test,
    max(if(o.concept_id=161011, o.value_text, null)) as decline_reason,
    max(if(o.concept_id=165847,o.value_coded,null)) as nationality,
    max(if(o.concept_id=163084,o.value_text,null)) as passport_id_number,
    max(if(o.concept_id=159959,o.value_coded,null)) as sample_type,
    max(if(o.concept_id=164126,o.value_coded,null)) as test_reason,
    max(if(o.concept_id=160632, o.value_text, null)) as test_reason_other,
    max(if(o.concept_id=165852,o.value_coded,null)) as ag_rdt_test_done,
    ag_rdt.ag_rdt_test_date,
    ag_rdt.case_type,
    ag_rdt.assay_kit_name,
    ag_rdt.ag_rdt_test_type_coded,
    ag_rdt.ag_rdt_test_type_other,
    ag_rdt.kit_lot_number,
    ag_rdt.kit_expiry,
    ag_rdt.test_result,
    ag_rdt.action_taken,
    e.voided
from migration_st.encounter e
    inner join migration_st.person p on p.person_id=e.patient_id and p.voided=0
    inner join migration_st.form f on f.form_id=e.form_id and f.uuid in ('820cbf10-54cd-11ec-bf63-0242ac130002')
    left outer join migration_st.obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in (1710,161011,165847,163084,159959,164126,165852,5622)
    left join migration_st.concept_name cn on cn.concept_id = o.value_coded and o.concept_id = 165847 and cn.concept_name_type='FULLY_SPECIFIED'
    left join (
    select
    o.obs_group_id obs_group_id,
    o.encounter_id,
    max(if(o.concept_id = 162078,date(o.value_datetime), null)) as ag_rdt_test_date,
    max(if(o.concept_id = 162084,o.value_coded, null)) as case_type,
    max(if(o.concept_id = 164963,o.value_text, null)) as assay_kit_name,
    max(if(o.concept_id = 1271,o.value_coded, null)) as ag_rdt_test_type_coded,
    max(if(o.concept_id = 165398,o.value_text, null)) as ag_rdt_test_type_other,
    max(if(o.concept_id = 166455, o.value_text, null)) as kit_lot_number,
    max(if(o.concept_id = 162502, date(o.value_datetime), null)) as kit_expiry,
    max(if(o.concept_id = 166638,o.value_coded, null)) as test_result,
    max(if(o.concept_id = 1272,o.value_coded, null)) as action_taken
    from migration_st.obs o
    inner join migration_st.person p on p.person_id=o.person_id and p.voided=0
    inner join migration_st.encounter e on e.encounter_id = o.encounter_id and e.voided=0
    inner join migration_st.form f on f.form_id=e.form_id and f.uuid in ('820cbf10-54cd-11ec-bf63-0242ac130002')
    where o.voided=0 and o.concept_id in(162078,162084,164963,1271,165398,166455,162502,166638,1272)  and e.voided=0 and o.obs_group_id is not null
    group by o.obs_group_id, o.encounter_id
    ) ag_rdt on ag_rdt.encounter_id = e.encounter_id
where e.voided=0
group by e.patient_id, e.encounter_id;

-- -------------------- clinical review -----------------------------

insert into migration_tr.migration_etl_cca_covid_clinical_review(
    uuid,
    Person_Id,
    Encounter_ID,
    visit_id,
    patient_id ,
    location_id,
    Encounter_Date,
    encounter_provider,
    date_created,
    ag_rdt_test_result,
    case_classification,
    action_taken,
    hospital_referred_to,
    case_id,
    email,
    case_type,
    pcr_sample_collection_date,
    pcr_result_date,
    pcr_result,
    case_classification_after_positive_pcr,
    action_taken_after_pcr_result,
    notes,
    voided
)
select
    e.uuid,
    e.patient_id Person_Id,
    null as Encounter_ID,
    e.visit_id as visit_id,
    null,
    e.location_id,
    date(e.encounter_datetime) as Encounter_Date,
    e.creator as encounter_provider,
    e.date_created as date_created,
    max(if(o.concept_id=165852,o.value_coded,null)) as ag_rdt_test_result,
    max(if(o.concept_id=159640 and o.obs_group_id is null,o.value_coded,null)) as case_classification,
    max(if(o.concept_id=1272,o.value_coded,null)) as action_taken,
    max(if(o.concept_id=162724,o.value_text,null)) as hospital_referred_to,
    pcr_test.case_id,
    pcr_test.email,
    pcr_test.case_type,
    pcr_test.pcr_sample_collection_date,
    pcr_test.pcr_result_date,
    pcr_test.pcr_result,
    pcr_test.case_classification_after_positive_pcr,
    pcr_test.action_taken_after_pcr_result,
    max(if(o.concept_id=161011,o.value_text,null)) as notes,
    e.voided
from migration_st.encounter e
    inner join migration_st.person p on p.person_id=e.patient_id and p.voided=0
    inner join migration_st.form f on f.form_id=e.form_id and f.uuid = '8fb6dabd-9c14-4d17-baac-97afaf3d203d'
    left outer join migration_st.obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in (165852,159640,1272,162724,161011)
    left join (
    select
    o.obs_group_id obs_group_id,
    o.encounter_id,
    max(if(o.concept_id = 162576,o.value_text, null)) as case_id,
    max(if(o.concept_id = 162725,o.value_text, null)) as email,
    max(if(o.concept_id = 162084, o.value_coded, null)) as case_type,
    max(if(o.concept_id = 162078,date(o.value_datetime), null)) as pcr_sample_collection_date,
    max(if(o.concept_id = 162079,date(o.value_datetime), null)) as pcr_result_date,
    max(if(o.concept_id = 166638,o.value_coded, null)) as pcr_result,
    max(if(o.concept_id = 159640,o.value_coded, null)) as case_classification_after_positive_pcr,
    max(if(o.concept_id = 160721,o.value_coded, null)) as action_taken_after_pcr_result
    from migration_st.obs o
    inner join migration_st.person p on p.person_id=o.person_id and p.voided=0
    inner join migration_st.encounter e on e.encounter_id = o.encounter_id and e.voided=0
    inner join migration_st.form f on f.form_id=e.form_id and f.uuid = '8fb6dabd-9c14-4d17-baac-97afaf3d203d'
    where o.voided=0 and o.concept_id in(162576,162725,162078,162084,162079,159640,166638,160721)  and e.voided=0 and o.obs_group_id is not null
    group by o.obs_group_id, o.encounter_id
    ) pcr_test on pcr_test.encounter_id = e.encounter_id
where e.voided=0
group by e.patient_id, e.encounter_id;

-- ------------------------------------ populate covid treatment enrollment -----------------------------------------------------------------

insert into migration_tr.migration_etl_cca_covid_treatment_enrollment(
    uuid,
    Person_Id,
    Encounter_ID,
    visit_id,
    patient_id ,
    location_id,
    Encounter_Date,
    encounter_provider,
    date_created,
    passport_id_number,
    case_classification,
    patient_type,
    hospital_referred_from,
    date_tested_covid_positive,
    action_taken,
    admission_date,
    admission_unit,
    voided
)
select
    e.uuid,
    e.patient_id Person_Id,
    null as Encounter_ID,
    e.visit_id as visit_id,
    e.patient_id,
    e.location_id,
    date(e.encounter_datetime) as Encounter_Date,
    e.creator as encounter_provider,
    e.date_created as date_created,
    max(if(o.concept_id=163084,o.value_text,null)) as passport_id_number,
    max(if(o.concept_id=159640 and o.obs_group_id is null,o.value_coded,null)) as case_classification,
    max(if(o.concept_id=161641,o.value_coded,null)) as patient_type,
    max(if(o.concept_id=161550,o.value_text,null)) as hospital_referred_from,
    max(if(o.concept_id=159948,date(o.value_datetime),null)) as date_tested_covid_positive,
    max(if(o.concept_id = 1272,o.value_coded, null)) as action_taken,
    max(if(o.concept_id=1640,date(o.value_datetime),null)) as admission_date,
    max(if(o.concept_id = 161010,o.value_coded, null)) as admission_unit,
    e.voided
from migration_st.encounter e
    inner join migration_st.person p on p.person_id=e.patient_id and p.voided=0
    inner join migration_st.form f on f.form_id=e.form_id and f.uuid = '9a5d57b6-739a-11ea-bc55-0242ac130003'
    left outer join migration_st.obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in (163084,159640,161641,161550,159948,1272,1640,161010)
where e.voided=0
group by e.patient_id, e.encounter_id;


-- ---------------------------------------------- populate covid treatment outcome --------------------------------------

insert into migration_tr.migration_etl_cca_covid_treatment_enrollment_outcome(
    uuid,
    Person_Id,
    Encounter_ID,
    visit_id,
    patient_id ,
    location_id,
    Encounter_Date,
    encounter_provider,
    date_created,
    outcome,
    facility_transferred,
    facility_referred,
    comment,
    voided
)
select
    e.uuid,
    e.patient_id Person_Id,
    null as Encounter_ID,
    e.visit_id as visit_id,
    e.patient_id,
    e.location_id,
    date(e.encounter_datetime) as Encounter_Date,
    e.creator as encounter_provider,
    e.date_created as date_created,
    max(if(o.concept_id=161555,o.value_coded,null)) as outcome,
    max(if(o.concept_id=159495,o.value_text,null)) as facility_transferred,
    max(if(o.concept_id=161562,o.value_text,null)) as facility_referred,
    max(if(o.concept_id = 160632,o.value_text,null)) as comment,
    e.voided
from migration_st.encounter e
    inner join migration_st.person p on p.person_id=e.patient_id and p.voided=0
    inner join migration_st.form f on f.form_id=e.form_id and f.uuid = '9a5d58c4-739a-11ea-bc55-0242ac130003'
    left outer join migration_st.obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in (161555,159495,161562,160632)
where e.voided=0
group by e.patient_id, e.encounter_id;


-- ---------------------------------------- populate covid treatment follow up --------------------------------------------------


insert into migration_tr.migration_etl_cca_covid_treatment_followup (
    uuid,
    Person_Id,
    Encounter_ID,
    visit_id,
    patient_id,
    location_id,
    Encounter_Date,
    encounter_provider,
    date_created,
    day_of_followup,
    temp,
    fever,
    cough,
    difficulty_breathing,
    sore_throat,
    sneezing,
    headache,
    referred_to_hosp,
    case_classification,
    patient_admitted,
    admission_unit,
    treatment_azithromycin,
    treatment_amoxicillin_clavulanic,
    treatment_amoxicillin,
    treatment_tocilizumab,
    treatment_dexamethasone,
    treatment_multivitamin,
    treatment_oxygen,
    treatment_other,
    treatment_received,
    voided
)
select
    e.uuid,
    e.patient_id Person_Id,
    null as Encounter_ID,
    e.visit_id as visit_id,
    e.patient_id,
    e.location_id,
    date(e.encounter_datetime) as Encounter_Date,
    e.creator as encounter_provider,
    e.date_created as date_created,
    max(if(o.concept_id=165416,o.value_numeric,null)) as day_of_followup,
    max(if(o.concept_id=5088,o.value_numeric,null)) as temp,
    max(if(o.concept_id=140238,o.value_coded,null)) as fever,
    max(if(o.concept_id=143264,o.value_coded,null)) as cough,
    max(if(o.concept_id=164441,o.value_coded,null)) as diffiulty_breathing,
    max(if(o.concept_id=162737,o.value_coded,null)) as sore_throat,
    max(if(o.concept_id=163336,o.value_coded,null)) as sneezing,
    max(if(o.concept_id=5219,o.value_coded,null)) as headache,
    max(if(o.concept_id=1788,o.value_coded,null)) as referred_to_hosp,
    max(if(o.concept_id=159640,o.value_coded,null)) as case_classification,
    max(if(o.concept_id=162477,o.value_coded,null)) as patient_admitted,
    max(if(o.concept_id=161010,o.value_coded,null)) as admission_unit,
    -- -------------------
    max(if(o.concept_id=159369 and o.value_coded=71780,o.value_coded,null)) as treatment_azithromycin,
    max(if(o.concept_id=159369 and o.value_coded=450,o.value_coded,null)) as treatment_amoxicillin_clavulanic,
    max(if(o.concept_id=159369 and o.value_coded=71160,o.value_coded,null)) as treatment_amoxicillin,
    max(if(o.concept_id=159369 and o.value_coded=165872,o.value_coded,null)) as treatment_tocilizumab,

    max(if(o.concept_id=159369 and o.value_coded=74609,o.value_coded,null)) as treatment_dexamethasone,
    max(if(o.concept_id=159369 and o.value_coded=461,o.value_coded,null)) as treatment_multivitamin,
    max(if(o.concept_id=159369 and o.value_coded=81341,o.value_coded,null)) as treatment_oxygen,
    max(if(o.concept_id=159369 and o.value_coded=5622,o.value_coded,null)) as treatment_other,
    -- --------------
    group_concat(if(o.concept_id=159369,o.value_coded,null)) as treatment_received,
    e.voided as voided
from migration_st.encounter e
    inner join migration_st.person p on p.person_id=e.patient_id and p.voided=0
    inner join
    (
    select form_id, uuid,name from migration_st.form where
    uuid in('33a3aab6-73ae-11ea-bc55-0242ac130003')
    ) f on f.form_id=e.form_id
    left outer join migration_st.obs o on o.encounter_id=e.encounter_id and o.voided=0 and o.concept_id in (165416,5088,140238,143264,164441,162737,163336,5219,1788,159640,162477,161010,159369)
where e.voided=0
group by e.patient_id, e.encounter_id;


-- create covid program enrollment

INSERT INTO migration_tr.migration_cca_covid_program (
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
         INNER JOIN migration_st.program p on p.program_id=pp.program_id and p.uuid = '117093ea-5355-11ec-bf63-0242ac130002';






































