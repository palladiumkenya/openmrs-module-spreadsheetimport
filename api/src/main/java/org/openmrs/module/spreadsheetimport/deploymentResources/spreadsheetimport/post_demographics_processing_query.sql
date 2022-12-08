



-- update patient_id columns in the datasets

update migration_tr.migration_etl_cca_covid_screening s inner join migration_tr.tr_demographics d on s.Person_Id = d.Person_Id set s.patient_id = d.patient_id where d.patient_id is not null;

update migration_tr.migration_etl_cca_covid_rdt_test s inner join migration_tr.tr_demographics d on s.Person_Id = d.Person_Id set s.patient_id = d.patient_id where d.patient_id is not null;

update migration_tr.migration_etl_cca_covid_clinical_review s inner join migration_tr.tr_demographics d on s.Person_Id = d.Person_Id set s.patient_id = d.patient_id where d.patient_id is not null;

-- align forms in templates
update spreadsheetimport_template set target_form = (select form_id from form where uuid='117092aa-5355-11ec-bf63-0242ac130002') where id = 1;
update spreadsheetimport_template set target_form = (select form_id from form where uuid='820cbf10-54cd-11ec-bf63-0242ac130002') where id = 2;
update spreadsheetimport_template set target_form = (select form_id from form where uuid='8fb6dabd-9c14-4d17-baac-97afaf3d203d') where id = 3;


update spreadsheetimport_template_prespecified_value set value = (select form_id from form where uuid='117092aa-5355-11ec-bf63-0242ac130002') where template_id = 1 and database_table_dot_column = 'form.form_id';
update spreadsheetimport_template_prespecified_value set value = (select form_id from form where uuid='820cbf10-54cd-11ec-bf63-0242ac130002') where template_id = 2 and database_table_dot_column = 'form.form_id';
update spreadsheetimport_template_prespecified_value set value = (select form_id from form where uuid='8fb6dabd-9c14-4d17-baac-97afaf3d203d') where template_id = 3 and database_table_dot_column = 'form.form_id';


update spreadsheetimport_template_prespecified_value set value = (select encounter_type_id from encounter_type where uuid='11708f6c-5355-11ec-bf63-0242ac130002') where template_id = 1 and database_table_dot_column = 'encounter_type.encounter_type_id';
update spreadsheetimport_template_prespecified_value set value = (select encounter_type_id from encounter_type where uuid='820cbccc-54cd-11ec-bf63-0242ac130002') where template_id = 2 and database_table_dot_column = 'encounter_type.encounter_type_id';
update spreadsheetimport_template_prespecified_value set value = (select encounter_type_id from encounter_type where uuid='5cfe07dd-4714-40dc-964f-c1fb65387727') where template_id = 3 and database_table_dot_column = 'encounter_type.encounter_type_id';



-- update created_by column in encounter based datasets



-- update created_by column in encounter based datasets -- check for users not in the users table

-- update migration_tr.tr_hiv_followup a set a.Created_by = 1 where a.created_by not in (select OpenMRS_User_Id from migration_tr.tr_users );



