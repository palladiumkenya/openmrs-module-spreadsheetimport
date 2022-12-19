-- update those with documented CCC numbers in the alien identifier from the CCA database
update migration_tr.tr_demographics m inner join kenyaemr_etl.etl_patient_demographics d on d.unique_patient_no = m.Alien_registration set m.patient_id = d.patient_id;

-- align forms in templates
update spreadsheetimport_template set target_form = (select form_id from form where uuid='117092aa-5355-11ec-bf63-0242ac130002') where id = 1;
update spreadsheetimport_template set target_form = (select form_id from form where uuid='820cbf10-54cd-11ec-bf63-0242ac130002') where id = 2;
update spreadsheetimport_template set target_form = (select form_id from form where uuid='8fb6dabd-9c14-4d17-baac-97afaf3d203d') where id = 3;
update spreadsheetimport_template set target_form = (select form_id from form where uuid='9a5d57b6-739a-11ea-bc55-0242ac130003') where id = 4; -- enrolment
update spreadsheetimport_template set target_form = (select form_id from form where uuid='9a5d58c4-739a-11ea-bc55-0242ac130003') where id = 5; -- outcome
update spreadsheetimport_template set target_form = (select form_id from form where uuid='33a3aab6-73ae-11ea-bc55-0242ac130003') where id = 6; -- followup


update spreadsheetimport_template_prespecified_value set value = (select form_id from form where uuid='117092aa-5355-11ec-bf63-0242ac130002') where template_id = 1 and database_table_dot_column = 'form.form_id';
update spreadsheetimport_template_prespecified_value set value = (select form_id from form where uuid='820cbf10-54cd-11ec-bf63-0242ac130002') where template_id = 2 and database_table_dot_column = 'form.form_id';
update spreadsheetimport_template_prespecified_value set value = (select form_id from form where uuid='8fb6dabd-9c14-4d17-baac-97afaf3d203d') where template_id = 3 and database_table_dot_column = 'form.form_id';
update spreadsheetimport_template_prespecified_value set value = (select form_id from form where uuid='9a5d57b6-739a-11ea-bc55-0242ac130003') where template_id = 4 and database_table_dot_column = 'form.form_id';
update spreadsheetimport_template_prespecified_value set value = (select form_id from form where uuid='9a5d58c4-739a-11ea-bc55-0242ac130003') where template_id = 5 and database_table_dot_column = 'form.form_id';
update spreadsheetimport_template_prespecified_value set value = (select form_id from form where uuid='33a3aab6-73ae-11ea-bc55-0242ac130003') where template_id = 6 and database_table_dot_column = 'form.form_id';


update spreadsheetimport_template_prespecified_value set value = (select encounter_type_id from encounter_type where uuid='11708f6c-5355-11ec-bf63-0242ac130002') where template_id = 1 and database_table_dot_column = 'encounter_type.encounter_type_id';
update spreadsheetimport_template_prespecified_value set value = (select encounter_type_id from encounter_type where uuid='820cbccc-54cd-11ec-bf63-0242ac130002') where template_id = 2 and database_table_dot_column = 'encounter_type.encounter_type_id';
update spreadsheetimport_template_prespecified_value set value = (select encounter_type_id from encounter_type where uuid='5cfe07dd-4714-40dc-964f-c1fb65387727') where template_id = 3 and database_table_dot_column = 'encounter_type.encounter_type_id';
update spreadsheetimport_template_prespecified_value set value = (select encounter_type_id from encounter_type where uuid='33a3a55c-73ae-11ea-bc55-0242ac130003') where template_id = 4 and database_table_dot_column = 'encounter_type.encounter_type_id';
update spreadsheetimport_template_prespecified_value set value = (select encounter_type_id from encounter_type where uuid='33a3a7be-73ae-11ea-bc55-0242ac130003') where template_id = 5 and database_table_dot_column = 'encounter_type.encounter_type_id';
update spreadsheetimport_template_prespecified_value set value = (select encounter_type_id from encounter_type where uuid='33a3a8e0-73ae-11ea-bc55-0242ac130003') where template_id = 6 and database_table_dot_column = 'encounter_type.encounter_type_id';

update spreadsheetimport_template_prespecified_value set value = (select program_id from program where uuid='117093ea-5355-11ec-bf63-0242ac130002') where template_id = 7 and database_table_dot_column = 'program.program_id';

update spreadsheetimport_template_column set database_table_dataset_index = NULL where template_id=7;

