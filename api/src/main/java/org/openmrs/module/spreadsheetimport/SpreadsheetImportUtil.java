/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.spreadsheetimport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openmrs.Attributable;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PatientProgram;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.TestOrder;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderContext;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.hivtestingservices.api.HTSService;
import org.openmrs.module.hivtestingservices.api.PatientContact;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 *
 */
public class SpreadsheetImportUtil {
	
	/** Logger for this class and subclasses */
	protected static final Log log = LogFactory.getLog(SpreadsheetImportUtil.class);
	public static final String COVID_QUARANTINE_ENROLLMENT_ENCOUNTER = "33a3a55c-73ae-11ea-bc55-0242ac130003";
	public static final String COVID_QUARANTINE_OUTCOME_ENCOUNTER = "33a3a7be-73ae-11ea-bc55-0242ac130003";

	public static final String COVID_QUARANTINE_ENROLLMENT_FORM = "9a5d57b6-739a-11ea-bc55-0242ac130003";
	public static final String COVID_QUARANTINE_OUTCOME_FORM = "9a5d58c4-739a-11ea-bc55-0242ac130003";

	public static final String COVID_QUARANTINE_PROGRAM = "9a5d555e-739a-11ea-bc55-0242ac130003";
    public static final String COVID_19_TRAVEL_HISTORY_ENCOUNTER = "50a59411-921b-435a-9109-42aa68ee7aa7";
    public static final String COVID_19_TRAVEL_HISTORY_FORM = "87513b50-6ced-11ea-bc55-0242ac130003";
	public static final String CONSULTATION = "465a92f2-baf8-42e9-9612-53064be868e8";


	public static final String COVID_19_CASE_INVESTIGATION_FORM = "0fe60b26-8648-438b-afea-8841dcd993c6";
	public static final String COVID_OUTCOME_FORM = "8f4e3e83-c597-47ad-8999-b788e8255d20";
	public static final String COVID_19_CASE_INVESTIGATION_ENCOUNTER = "a4414aee-6832-11ea-bc55-0242ac130003";
	public static final String COVID_OUTCOME_ENCOUNTER = "7b118dac-6f61-4466-ad1a-7e01aca077ad";

	public static final String COVID_19_CASE_INVESTIGATION_PROGRAM = "e7ee7548-6958-4361-bed9-ee2614423947";
	public static final String COVID_19_LAB_TEST_CONCEPT = "165611AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String COVID_19_BASELINE_TEST_CONCEPT = "162080AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String COVID_19_1ST_FOLLOWUP_TEST_CONCEPT = "162081AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String COVID_19_2ND_FOLLOWUP_TEST_CONCEPT = "164142AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String COVID_19_3RD_FOLLOWUP_TEST_CONCEPT = "159490AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String COVID_19_4TH_FOLLOWUP_TEST_CONCEPT = "159489AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String COVID_19_5TH_FOLLOWUP_TEST_CONCEPT = "161893AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String REPORTING_COUNTY = "165197AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String REPORTING_SUB_COUNTY = "161551AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String HISTORY_OF_TRAVEL = "162619AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String REPORTING_HEALTH_FACILITY = "161550AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String YES_CONCEPT = "1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String NO_CONCEPT = "1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	public static final String CONTACT_WITH_SUSPECTED_CASE = "162633AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";




	/**
	 * Resolve template dependencies: 1. Generate pre-specified values which are necessary for
	 * template to be imported. 2. Create import indices which describe the order in which columns
	 * must be imported. 3. Generated dependencies between columns being imported and other columns
	 * which be must imported first.
	 * 
	 * @param template
	 * @throws Exception
	 */
	public static void resolveTemplateDependencies(SpreadsheetImportTemplate template) throws Exception {
		
		Set<SpreadsheetImportTemplatePrespecifiedValue> prespecifiedValues = new TreeSet<SpreadsheetImportTemplatePrespecifiedValue>();
		
		Map<String, Set<UniqueImport>> mapTnToUi = template.getMapOfColumnTablesToUniqueImportSet();
		Map<UniqueImport, Set<SpreadsheetImportTemplateColumn>> mapUiToCs = template.getMapOfUniqueImportToColumnSet();
		
		List<String> tableNamesSortedByImportIdx = new ArrayList<String>();
		
//		// special treatment: when there's a reference to person_id, but 
//		//  1) the current table is not encounter and 
//		//  2) there's no column of table person to be added
//		// then we should still add a person implicitly. This person record will use all default values
//		boolean hasToAddPerson = false;
//		for (UniqueImport key : mapUiToCs.keySet()) {
//			String tableName = key.getTableName();			
//			if (!("encounter".equals(tableName) || mapTnToUi.keySet().contains("person"))) {
//				hasToAddPerson = true;
//				break;
//			}
//		}
//		if (hasToAddPerson) {
//			UniqueImport ui = new UniqueImport("person", new Integer(-1));
//			mapTnToUi.put("person", new TreeSet<UniqueImport>());
//			mapUiToCs.put(ui, new TreeSet<SpreadsheetImportTemplateColumn>());
//		}
				
		// Find requirements
		for (UniqueImport key : mapUiToCs.keySet()) {
			String tableName = key.getTableName();
			
			Map<String, String> mapIkTnToCn = DatabaseBackend.getMapOfImportedKeyTableNameToColumnNamesForTable(tableName);
			
			if ("patient_identifier".equals(tableName))
				mapIkTnToCn.put("patient", "patient_id");
			
			// encounter_id is optional, so it won't be part of mapIkTnToCn
			// if we need to create new encounter for this row, then force it to be here
			if (template.isEncounter() && "obs".equals(tableName))
				mapIkTnToCn.put("encounter", "encounter_id");
			
			// we need special treatment for provider_id of Encounter
			// provider_id is of type person, but the meaning is different. During import, reference to person is considered patient,
			// but for provider_id of Encounter, it refers to a health practitioner
			if ("encounter".equals(tableName)) {
//				mapIkTnToCn.put("person", "provider_id"); 			// UPDATE: provider_id is no longer a foreign key for encounter
				mapIkTnToCn.put("location", "location_id");
				mapIkTnToCn.put("form", "form_id");
				
//				// if this is an encounter-based import, then pre-specify the form_id for the encounter
//				// 1. search for encounter column
//				SpreadsheetImportTemplateColumn encounterColumn = mapUiToCs.get(key).iterator().next();
//				// 2. prespecify form 				
//				SpreadsheetImportTemplatePrespecifiedValue v = new SpreadsheetImportTemplatePrespecifiedValue();
//				v.setTemplate(template);
//				v.setTableDotColumn("form.form_id");
//				v.setValue(template.getTargetForm());
//				SpreadsheetImportTemplateColumnPrespecifiedValue cpv = new SpreadsheetImportTemplateColumnPrespecifiedValue();
//				cpv.setColumn(encounterColumn);
//				cpv.setPrespecifiedValue(v);
//				prespecifiedValues.add(v);
			}
			
			// Ignore users tableName 
			mapIkTnToCn.remove("users");
			
			for (String necessaryTableName : mapIkTnToCn.keySet()) {

				String necessaryColumnName = mapIkTnToCn.get(necessaryTableName);

				// TODO: I believe patient and person are only tables with this relationship, if not, then this
				// needs to be generalized
				if (necessaryTableName.equals("patient") &&
					!mapTnToUi.containsKey("patient") &&
					mapTnToUi.containsKey("person")) {
					necessaryTableName = "person";
				}
				
				if (mapTnToUi.containsKey(necessaryTableName) && !("encounter".equals(tableName) && ("provider_id".equals(necessaryColumnName)))) {
					
					// Not already imported? Add
					if (!tableNamesSortedByImportIdx.contains(necessaryTableName)) {
						tableNamesSortedByImportIdx.add(necessaryTableName);
					}
					
					// Add column dependencies
					// TODO: really _table_ dependencies - for simplicity only use _first_ column
					// of each unique import
					Set<SpreadsheetImportTemplateColumn> columnsImportFirst = new TreeSet<SpreadsheetImportTemplateColumn>();
					for (UniqueImport uniqueImport : mapTnToUi.get(necessaryTableName)) {
						// TODO: hacky cast
						columnsImportFirst.add(((TreeSet<SpreadsheetImportTemplateColumn>)mapUiToCs.get(uniqueImport)).first());
					}
					for (SpreadsheetImportTemplateColumn columnImportNext : mapUiToCs.get(key)) {
						for (SpreadsheetImportTemplateColumn columnImportFirst : columnsImportFirst) {
							SpreadsheetImportTemplateColumnColumn cc = new SpreadsheetImportTemplateColumnColumn();
							cc.setColumnImportFirst(columnImportFirst);
							cc.setColumnImportNext(columnImportNext);
							cc.setColumnName(necessaryColumnName);
							columnImportNext.getColumnColumnsImportBefore().add(cc);
						}
					}
					
				} else {
					
					// Add pre-specified value
					SpreadsheetImportTemplatePrespecifiedValue v = new SpreadsheetImportTemplatePrespecifiedValue();
					v.setTemplate(template);
					v.setTableDotColumn(necessaryTableName + "." + necessaryTableName + "_id");
					for (SpreadsheetImportTemplateColumn column : mapUiToCs.get(key)) {
						SpreadsheetImportTemplateColumnPrespecifiedValue cpv = new SpreadsheetImportTemplateColumnPrespecifiedValue();
						cpv.setColumn(column);
						cpv.setPrespecifiedValue(v);
						
						
//						System.out.println("SpreadsheetImportUtils: " + v.getTableDotColumn() + " ==> " + v.getValue());
						
						cpv.setColumnName(necessaryColumnName);						
						v.getColumnPrespecifiedValues().add(cpv);
					}
					prespecifiedValues.add(v);
				}
			}
			
			// Add this tableName if not already added
			if (!tableNamesSortedByImportIdx.contains(tableName)) {
				tableNamesSortedByImportIdx.add(tableName);
			}
		}
		
		// Add all pre-specified values		
		template.getPrespecifiedValues().addAll(prespecifiedValues);
		
		// Set column import indices based on tableNameSortedByImportIdx
		int importIdx = 0;
		for (String tableName : tableNamesSortedByImportIdx) {
			for (UniqueImport uniqueImport : mapTnToUi.get(tableName)) {
				for (SpreadsheetImportTemplateColumn column : mapUiToCs.get(uniqueImport)) {
					column.setImportIdx(importIdx);
					importIdx++;
				}
			}
		}
	}
	
	private static String toString(List<String> list) {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			if (list.size() == 2 && i == 1) {
				result += " and ";
			} else if (list.size() > 2 && i == list.size() - 1) {
				result += ", and ";
			} else if (i != 0) {
				result += ", ";
			}
			result += list.get(i);
		}
		return result;
	}
	
	public static File importTemplate(SpreadsheetImportTemplate template, MultipartFile file, String sheetName,
	                                     List<String> messages, boolean rollbackTransaction) throws Exception {

		if (file.isEmpty()) {
			messages.add("file must not be empty");
			return null;
		}
		
		// Open file
		Workbook wb = WorkbookFactory.create(file.getInputStream());
		Sheet sheet;
		if (!StringUtils.hasText(sheetName)) {
			sheet = wb.getSheetAt(0);
		} else {
			sheet = wb.getSheet(sheetName);
		}
		
		// Header row
		Row firstRow = sheet.getRow(0);
		if (firstRow == null) {
			messages.add("Spreadsheet header row must not be null");
			return null;
		}
		
		List<String> columnNames = new Vector<String>();
		for (Cell cell : firstRow) {
			columnNames.add(cell.getStringCellValue());
		}
		if (log.isDebugEnabled()) {
			log.debug("Column names: " + columnNames.toString());
		}
		
		// Required column names
		List<String> columnNamesOnlyInTemplate = new Vector<String>();
		columnNamesOnlyInTemplate.addAll(template.getColumnNamesAsList());
		columnNamesOnlyInTemplate.removeAll(columnNames);
		/*if (columnNamesOnlyInTemplate.isEmpty() == false) {
			messages.add("required column names not present: " + toString(columnNamesOnlyInTemplate));
			return null;
		}*/
		
		// Extra column names?
		List<String> columnNamesOnlyInSheet = new Vector<String>();
		columnNamesOnlyInSheet.addAll(columnNames);
		columnNamesOnlyInSheet.removeAll(template.getColumnNamesAsList());
		if (columnNamesOnlyInSheet.isEmpty() == false) {
			messages.add("Extra column names present, these will not be processed: " + toString(columnNamesOnlyInSheet));
		}
		
		// Process rows
		//importQuarantineList(sheet);
		importPositiveCases(sheet);

		
		// write back Excel file to a temp location
		File returnFile = File.createTempFile("sim", ".xls");
		FileOutputStream fos = new FileOutputStream(returnFile);
		//wb.write(fos);
		fos.close();
		
		return returnFile;
	}

	private static void importQuarantineList(Sheet sheet) {

		boolean start = true;
		int counter = 0;
		for (Row row : sheet) {
			if (start) {
				start = false;
				continue;
			}
			int colFacilityName = 1;
			int colClientName = 2;
			int colAge = 4;
			int colSex = 6;
			int colNationalId = 7;
			int colPhone = 8;
			int colCountryofOrigin = 9;
			int colTravelingFrom = 10;
			int colNationality = 11;
			int colNoKName = 15;
			//int colNoKName = 13;
			int colNoKContact = 16;
			//int colNoKContact = 14;
			int colArrivalDate = 17;
			//int colArrivalDate = 15;
			counter++;

			DataFormatter formatter = new DataFormatter();
			String facilityName = formatter.formatCellValue(row.getCell(colFacilityName));
			String clientName = formatter.formatCellValue(row.getCell(colClientName));
			String ageStr = formatter.formatCellValue(row.getCell(colAge));
			Integer age = ageStr != null && !ageStr.equals("") ? Integer.valueOf(ageStr) : 99;
			String sex = formatter.formatCellValue(row.getCell(colSex));
			String nationalId = formatter.formatCellValue(row.getCell(colNationalId));
			String phone = formatter.formatCellValue(row.getCell(colPhone));
			String countryofOrigin = formatter.formatCellValue(row.getCell(colCountryofOrigin));
			String travelingFrom = formatter.formatCellValue(row.getCell(colTravelingFrom));
			String nationality = formatter.formatCellValue(row.getCell(colNationality));
			String noKName = formatter.formatCellValue(row.getCell(colNoKName));
			String noKContact = formatter.formatCellValue(row.getCell(colNoKContact));
			String arrivalDate = formatter.formatCellValue(row.getCell(colArrivalDate));
			arrivalDate = arrivalDate.replace(".", "/");

			Patient p = checkIfPatientExists(nationalId);
			if (p != null) {
				System.out.println("A patient with identifier " + nationalId + " already exists. Skipping this row");
				continue;
			}
			Date admissionDate = null;
			List<String> dateFormats = new ArrayList<String>();

            dateFormats.add("dd-MM-yyyy");
			dateFormats.add("dd/MM/yyyy");
			dateFormats.add("dd-MMM-yyyy");

			for (String format : dateFormats) {
				try {
					admissionDate = new SimpleDateFormat(format).parse(arrivalDate);
					break;
				} catch (ParseException e) {

				}
			}
			if (admissionDate == null) {
				admissionDate = new Date();

			}

			System.out.print("Facility Name, Client, age, nationalId, arrivalDate: ");
			System.out.println(facilityName + " ," + clientName + ", " + age + ", " + nationalId + ", " + arrivalDate + " ");
			phone = phone.replace("-", "");
			nationalId.replace("-","");
			facilityName.replace("'","\'");

			Patient patient = createPatient(clientName, age != null ? age : 99, sex, nationalId);
			patient = addPersonAttributes(patient, phone, noKName, noKContact);
			patient = addPersonAddresses(patient, nationality, null, null, null, null);
			patient = saveAndenrollPatientInCovidQuarantine(patient, admissionDate, facilityName, null , null);

			if (travelingFrom.equals("") && !countryofOrigin.equals("")) {
			    travelingFrom = countryofOrigin;
            }
            if (travelingFrom != null && !travelingFrom.equals("") && patient != null) {
                updateTravelInfo(patient, admissionDate, travelingFrom, null);
            }

			if (counter % 200 == 0) {
				Context.flushSession();
				Context.clearSession();

			}
		}
	}

	private static void importLabResults(Sheet sheet) {

		ConceptService conceptService = Context.getConceptService();
		EncounterService encounterService = Context.getEncounterService();
		String TEST_ORDER_TYPE_UUID = "52a447d3-a64a-11e3-9aeb-50e549534c5e";
		String LAB_ENCOUNTER_TYPE_UUID = "e1406e88-e9a9-11e8-9f32-f2801f1b9fd1";
		String COVID_19_CASE_INVESTIGATION = "a4414aee-6832-11ea-bc55-0242ac130003";
		Concept covidTestConcept = conceptService.getConcept(165611);
		Concept covidPosConcept = conceptService.getConcept(703);
		Concept covidNegConcept = conceptService.getConcept(664);
		Concept covidIndeterminateConcept = conceptService.getConcept(1138);
		EncounterType labEncounterType = encounterService.getEncounterTypeByUuid(LAB_ENCOUNTER_TYPE_UUID);


		boolean start = true;
		int counter = 0;
		for (Row row : sheet) {
			if (start) {
				start = false;
				continue;
			}
			int colTestLabName = 0;//test lab name
			int colOrderId = 1;//specimen id
			int colQuarantineId = 2;// quarantine identifier
			int colClientName = 3;//name
			int colAge = 4;//age
			int colSex = 5;//sex
			int colNationalId = 6;//id/passport
			int colPhone = 7;//phone number
			int colCounty = 8;//county
			int colSubCounty = 9;//subcounty
			int colTravelHistory = 10;//travel history
			int colArrivalDate = 11;
			int colTravelingFrom = 12;
			int colContactWithConfirmedCase = 13;// history of contact with confirmed case
			int colFacilityName = 15;
			int colVillageEstate = 19;

			int colCountryofOrigin = 9;//arrival date
			int colNationality = 11;
			int colHorBaseResult = 16;
			int colTestTypeCol1 = 17;
			int colTestDate = 18;
			int colTestLabConfirmationDate = 20;
			int colFollowup1TestDate = 21;
			int colFollowup1TestResult = 22;
			int colFollowup2TestDate = 23;
			int colFollowup2TestResult = 24;
			int colFollowup3TestDate = 25;
			int colFollowup3TestResult = 26;
			int colSampleCollectionHealthFacility = 27;
			int colTestResultCol2 = 31;
			//int colNoKName = 15;
			//int colNoKName = 13;
			//int colNoKContact = 16;
			//int colNoKContact = 14;
			//int colArrivalDate = 15;
			counter++;

			DataFormatter formatter = new DataFormatter();
			String labName = formatter.formatCellValue(row.getCell(colTestLabName));
			String orderId = formatter.formatCellValue(row.getCell(colOrderId));
			String quarantineId = formatter.formatCellValue(row.getCell(colQuarantineId));
			String county = formatter.formatCellValue(row.getCell(colCounty));
			String subCounty = formatter.formatCellValue(row.getCell(colSubCounty));
			String travelHistory = formatter.formatCellValue(row.getCell(colTravelHistory));
			String quarantineFacilityName = formatter.formatCellValue(row.getCell(colFacilityName));
			String clientName = formatter.formatCellValue(row.getCell(colClientName));
			String ageStr = formatter.formatCellValue(row.getCell(colAge));
			Integer age = ageStr != null && !ageStr.equals("") ? Integer.valueOf(ageStr) : 99;
			String sex = formatter.formatCellValue(row.getCell(colSex));
			String nationalId = formatter.formatCellValue(row.getCell(colNationalId));
			String phone = formatter.formatCellValue(row.getCell(colPhone));
			String countryofOrigin = formatter.formatCellValue(row.getCell(colCountryofOrigin));
			String travelingFrom = formatter.formatCellValue(row.getCell(colTravelingFrom));
			String nationality = formatter.formatCellValue(row.getCell(colNationality));
			String contactWithConfirmedCase = formatter.formatCellValue(row.getCell(colContactWithConfirmedCase));
			String healthFacility = formatter.formatCellValue(row.getCell(colSampleCollectionHealthFacility));
			String horBaseResult = formatter.formatCellValue(row.getCell(colHorBaseResult));


			String arrivalDate = formatter.formatCellValue(row.getCell(colArrivalDate));
			String baselineLabDate = formatter.formatCellValue(row.getCell(colTestLabConfirmationDate));
			arrivalDate = arrivalDate.replace(".", "/");

			if (org.apache.commons.lang3.StringUtils.isBlank(horBaseResult)) {

				System.out.print("Skipping this row. It has empty result ");
				System.out.print("Facility Name, Client, age, nationalId, arrivalDate: ");
				System.out.println(quarantineFacilityName + " ," + clientName + ", " + age + ", " + nationalId + ", " + arrivalDate + " ");
				continue;
			}
			Patient p = checkIfPatientExists(nationalId);
			if (p != null) {
				System.out.println("A patient with identifier " + nationalId + " already exists. Skipping this row");
				continue;
			}
			Date admissionDate = null;
			Date baselineLabTestDate = null;
			List<String> dateFormats = new ArrayList<String>();

			dateFormats.add("dd-MM-yyyy");
			dateFormats.add("dd/MM/yyyy");
			dateFormats.add("dd-MMM-yyyy");

			for (String format : dateFormats) {
				try {
					admissionDate = new SimpleDateFormat(format).parse(arrivalDate);
					break;
				} catch (ParseException e) {

				}
			}

				/*try {
					baselineLabTestDate = new SimpleDateFormat("M/dd/yyyy").parse(baselineLabDate);
					break;
				} catch (ParseException e) {

				}*/

			/*if (admissionDate == null) {
				if (baselineLabTestDate != null) {
					admissionDate = baselineLabTestDate;
				} else {
					admissionDate = new Date();
				}

			}*/

			if (admissionDate == null) {
				admissionDate = new Date();
			}

			System.out.print("Facility Name, Client, age, nationalId, arrivalDate: ");
			System.out.println(quarantineFacilityName + " ," + clientName + ", " + age + ", " + nationalId + ", " + arrivalDate + " ");
			phone = phone.replace("-", "");
			nationalId.replace("-","");
			quarantineFacilityName.replace("'","\'");

			Patient patient = createPatient(clientName, age != null ? age : 99, sex, nationalId);
			patient = addPersonAttributes(patient, phone, null, null);
			patient = addPersonAddresses(patient, nationality, county, subCounty, null, null);
			if (org.apache.commons.lang3.StringUtils.isNotBlank(quarantineFacilityName)) {
				patient = saveAndenrollPatientInCovidQuarantine(patient, admissionDate, quarantineFacilityName, null, null);
			}

			String hasTravelHistory = null;
			String historyOfContactWithCase = null;
			if ((org.apache.commons.lang3.StringUtils.isNotBlank(travelHistory) && travelHistory.equalsIgnoreCase("Y")) ||
					org.apache.commons.lang3.StringUtils.isNotBlank(travelingFrom) || org.apache.commons.lang3.StringUtils.isNotBlank(arrivalDate)) {
				hasTravelHistory = "Yes";
			} else {
				hasTravelHistory = "No";
			}

			if (org.apache.commons.lang3.StringUtils.isNotBlank(contactWithConfirmedCase) && contactWithConfirmedCase.equalsIgnoreCase("Y")){
				historyOfContactWithCase = "Yes";
			} else if (org.apache.commons.lang3.StringUtils.isNotBlank(contactWithConfirmedCase) && contactWithConfirmedCase.equalsIgnoreCase("N")){
				historyOfContactWithCase = "No";
			}

			if (org.apache.commons.lang3.StringUtils.isBlank(healthFacility) && org.apache.commons.lang3.StringUtils.isNotBlank(quarantineFacilityName)) {
				healthFacility = quarantineFacilityName;
			}

			horBaseResult = horBaseResult.equalsIgnoreCase("Positive") ? "Positive" : "Negative";
			//enrollInCovidCaseInvestigationProgram(patient, admissionDate, county, subCounty, healthFacility, hasTravelHistory, historyOfContactWithCase, labName, horBaseResult);
			/*if (travelingFrom.equals("") && !countryofOrigin.equals("")) {
				travelingFrom = countryofOrigin;
			}
			if (travelingFrom != null && !travelingFrom.equals("") && patient != null) {
				updateTravelInfo(patient, admissionDate, travelingFrom);
			}*/

			if (counter % 200 == 0) {
				Context.flushSession();
				Context.clearSession();

			}
		}
	}

	private static void importPositiveCases(Sheet sheet) {

		ConceptService conceptService = Context.getConceptService();
		EncounterService encounterService = Context.getEncounterService();
		String TEST_ORDER_TYPE_UUID = "52a447d3-a64a-11e3-9aeb-50e549534c5e";
		String LAB_ENCOUNTER_TYPE_UUID = "e1406e88-e9a9-11e8-9f32-f2801f1b9fd1";
		String COVID_19_CASE_INVESTIGATION = "a4414aee-6832-11ea-bc55-0242ac130003";
		Concept covidTestConcept = conceptService.getConcept(165611);
		Concept covidPosConcept = conceptService.getConcept(703);
		Concept covidNegConcept = conceptService.getConcept(664);
		Concept covidIndeterminateConcept = conceptService.getConcept(1138);
		EncounterType labEncounterType = encounterService.getEncounterTypeByUuid(LAB_ENCOUNTER_TYPE_UUID);


		boolean start = true;
		int counter = 0;
		for (Row row : sheet) {
			if (start) {
				start = false;
				continue;
			}
			int colCaseId = 0;
			int colClientName = 1;
			int colNationalId = 2;// any identifier
			int colAge = 3;
			int colSex = 4;
			int colPhone = 5;
			int colOccupation = 6;
			int colCountyOfResidence = 7;
			int colCounty = 8;
			int colNationality = 9;
			int colSubCounty = 10;
			int colWard = 11;
			int colVillageEstate = 12;
			int colTravelHistory = 13;
			int colTravelingFrom = 14;
			int colImported = 15;
			int colFlightNo = 16;
			int colArrivalDate = 17;
			int colContactWithConfirmedCase = 18;
			int colHasSymptoms = 19;
			int colSymptomsOnsetDate = 21;
			int colCough = 22;
			int colFever = 23;
			int colDifficultyBreathing = 24;
			int colOtherSymptoms = 25;
			int colVisitedHealthFacility = 26;
			int colDateVisitedHealthFacility = 27;
			int colVisitedHealthFacilityName = 28;
			int colHospitalization = 29;
			int colDateOfAdmission = 30;
			int colBaselineSampleDate = 32;
			int colBaselineResult = 33;
			int colTestLabName = 34;
			int colDateConfirmedPositive = 35;

			int colFollowup1TestDate = 36;
			int colFollowup1TestResult = 37;
			int colFollowup2TestDate = 38;
			int colFollowup2TestResult = 39;
			int colFollowup3TestDate = 40;
			int colFollowup3TestResult = 41;
			int colOutcome = 42;
			int colOutcomeDate = 43;
			int colQuarantineFacility = 44;

			counter++;

			DataFormatter formatter = new DataFormatter();
			String caseId = formatter.formatCellValue(row.getCell(colCaseId));
			String labName = formatter.formatCellValue(row.getCell(colTestLabName));
			String countyOfResidence = formatter.formatCellValue(row.getCell(colCountyOfResidence));
			String county = formatter.formatCellValue(row.getCell(colCounty));
			String subCounty = formatter.formatCellValue(row.getCell(colSubCounty));
			String ward = formatter.formatCellValue(row.getCell(colWard));
			String villageEstate = formatter.formatCellValue(row.getCell(colVillageEstate));
			String sourceOfInfection = formatter.formatCellValue(row.getCell(colImported));
			String travelHistory = formatter.formatCellValue(row.getCell(colTravelHistory));
			String flightNumber = formatter.formatCellValue(row.getCell(colFlightNo));
			String occupation = formatter.formatCellValue(row.getCell(colOccupation));
			String clientName = formatter.formatCellValue(row.getCell(colClientName));
			String ageStr = formatter.formatCellValue(row.getCell(colAge));
			Integer age = ageStr != null && !ageStr.equals("") ? Integer.valueOf(ageStr) : 99;
			String sex = formatter.formatCellValue(row.getCell(colSex));
			String nationalId = formatter.formatCellValue(row.getCell(colNationalId));
			String phone = formatter.formatCellValue(row.getCell(colPhone));
			String travelingFrom = formatter.formatCellValue(row.getCell(colTravelingFrom));
			String nationality = formatter.formatCellValue(row.getCell(colNationality));
			String contactWithConfirmedCase = formatter.formatCellValue(row.getCell(colContactWithConfirmedCase));
			String healthFacility = formatter.formatCellValue(row.getCell(colVisitedHealthFacilityName));
			String hasSymptoms = formatter.formatCellValue(row.getCell(colHasSymptoms));
			String symptomsOnsetDate = formatter.formatCellValue(row.getCell(colSymptomsOnsetDate));
			String hasCough = formatter.formatCellValue(row.getCell(colCough));
			String hasFever = formatter.formatCellValue(row.getCell(colFever));
			String hasDifficultyBreathing = formatter.formatCellValue(row.getCell(colDifficultyBreathing));
			String hasOtherSymptoms = formatter.formatCellValue(row.getCell(colOtherSymptoms));
			String visitedHealthFacility = formatter.formatCellValue(row.getCell(colVisitedHealthFacility));
			String dateVisitedHealthFacility = formatter.formatCellValue(row.getCell(colDateVisitedHealthFacility));
			String hospitalization = formatter.formatCellValue(row.getCell(colHospitalization));
			String hospitalAdmissionDate = formatter.formatCellValue(row.getCell(colDateOfAdmission));

			String baselineSampleDate = formatter.formatCellValue(row.getCell(colBaselineSampleDate));
			String baselineResultDate = formatter.formatCellValue(row.getCell(colDateConfirmedPositive));
			String baselineResult = formatter.formatCellValue(row.getCell(colBaselineResult));
			String firstFollowupDate = formatter.formatCellValue(row.getCell(colFollowup1TestDate));
			String firstFollowupResult = formatter.formatCellValue(row.getCell(colFollowup1TestResult));
			String secondFollowupDate = formatter.formatCellValue(row.getCell(colFollowup2TestDate));
			String secondFollowupResult = formatter.formatCellValue(row.getCell(colFollowup2TestResult));
			String thirdFollowupDate = formatter.formatCellValue(row.getCell(colFollowup3TestDate));
			String thirdFollowupResult = formatter.formatCellValue(row.getCell(colFollowup3TestResult));
			String outcome = formatter.formatCellValue(row.getCell(colOutcome));
			String outcomeDate = formatter.formatCellValue(row.getCell(colOutcomeDate));
			String quarantineFacility = formatter.formatCellValue(row.getCell(colQuarantineFacility));


			String arrivalDate = formatter.formatCellValue(row.getCell(colArrivalDate));
			arrivalDate = arrivalDate.replace("\\", "");
			hospitalAdmissionDate = hospitalAdmissionDate.replace("\\", "");
			symptomsOnsetDate = symptomsOnsetDate.replace("\\", "");
			baselineResultDate = baselineResultDate.replace("\\", "");
			baselineSampleDate = baselineSampleDate.replace("\\", "");
			firstFollowupDate = firstFollowupDate.replace("\\", "");
			secondFollowupDate = secondFollowupDate.replace("\\", "");
			thirdFollowupDate = thirdFollowupDate.replace("\\", "");
			outcomeDate = outcomeDate.replace("\\", "");

			if (org.apache.commons.lang3.StringUtils.isBlank(baselineResult)) {

				System.out.print("Skipping this row. It has empty result ");
				System.out.print("Facility Name, Client, age, nationalId, arrivalDate: ");
				System.out.println(healthFacility + " ," + clientName + ", " + age + ", " + nationalId + ", " + arrivalDate + " ");
				continue;
			}
			Patient p = checkIfPatientExists(nationalId);
			if (p == null) {
				p = checkIfPatientExists(caseId);
			}
			if (p != null) {
				System.out.println("A patient with identifier " + (org.apache.commons.lang3.StringUtils.isNotBlank(nationalId) ? nationalId : caseId) +  " already exists. Skipping this row");
				continue;
			}
			Date admissionDate = null;
			Date dateOfArrival = null;
			Date baselineLabTestDate = null;
			Date dateConfirmedPositive = null;
			Date firstFollowupLabTestDate = null;
			Date secondFollowupLabTestDate = null;
			Date thirdFollowupLabTestDate = null;
			Date dateOfSymptomOnset = null;
			Date dateOfOutcome = null;
			List<String> dateFormats = new ArrayList<String>();

			//dateFormats.add("dd-MM-yyyy");
			//dateFormats.add("d/MM/yyyy");
			dateFormats.add("dd/MMM/yyyy");

			for (String format : dateFormats) {
				try {
					if (org.apache.commons.lang3.StringUtils.isNotBlank(hospitalAdmissionDate)) {
						admissionDate = new SimpleDateFormat(format).parse(hospitalAdmissionDate);
					}
					break;
				} catch (ParseException e) {

				}
			}

			for (String format : dateFormats) {
				try {
					if (org.apache.commons.lang3.StringUtils.isNotBlank(arrivalDate)) {
						dateOfArrival = new SimpleDateFormat(format).parse(arrivalDate);
					}
						break;
					} catch(ParseException e){

					}
			}

			for (String format : dateFormats) {
				try {
					if (org.apache.commons.lang3.StringUtils.isNotBlank(baselineSampleDate)) {
						baselineLabTestDate = new SimpleDateFormat(format).parse(baselineSampleDate);
					}
					break;
				} catch(ParseException e){

				}
			}

			for (String format : dateFormats) {
				try {
					if (org.apache.commons.lang3.StringUtils.isNotBlank(symptomsOnsetDate)) {
						dateOfSymptomOnset = new SimpleDateFormat(format).parse(symptomsOnsetDate);
					}
					break;
				} catch(ParseException e){

				}
			}

			for (String format : dateFormats) {
				try {
					if (org.apache.commons.lang3.StringUtils.isNotBlank(firstFollowupDate)) {
						firstFollowupLabTestDate = new SimpleDateFormat(format).parse(firstFollowupDate);
					}
					break;
				} catch(ParseException e){

				}
			}

			for (String format : dateFormats) {
				try {
					if (org.apache.commons.lang3.StringUtils.isNotBlank(secondFollowupDate)) {
						secondFollowupLabTestDate = new SimpleDateFormat(format).parse(secondFollowupDate);
					}
					break;
				} catch(ParseException e){

				}
			}

			for (String format : dateFormats) {
				try {
					if (org.apache.commons.lang3.StringUtils.isNotBlank(thirdFollowupDate)) {
						thirdFollowupLabTestDate = new SimpleDateFormat(format).parse(thirdFollowupDate);
					}
					break;
				} catch(ParseException e){

				}
			}

			for (String format : dateFormats) {
				try {
					if (org.apache.commons.lang3.StringUtils.isNotBlank(outcomeDate)) {
						dateOfOutcome = new SimpleDateFormat(format).parse(outcomeDate);
					}
					break;
				} catch(ParseException e){

				}
			}

			for (String format : dateFormats) {
				try {
					if (org.apache.commons.lang3.StringUtils.isNotBlank(baselineResultDate)) {
						dateConfirmedPositive = new SimpleDateFormat(format).parse(baselineResultDate);
					}
					break;
				} catch(ParseException e){

				}
			}


			if (admissionDate == null) {
				admissionDate = new Date();
			}

			if (baselineLabTestDate == null && dateConfirmedPositive != null) {
				baselineLabTestDate = dateConfirmedPositive;
			}

			if (dateConfirmedPositive == null && baselineLabTestDate != null) {
				dateConfirmedPositive = baselineLabTestDate;
			}
			System.out.print("Facility Name, Client, age, nationalId, arrivalDate: ");
			System.out.println(healthFacility + " ," + clientName + ", " + age + ", " + nationalId + ", " + arrivalDate + " ");
			phone = phone.replace("-", "");
			nationalId.replace("-","");
			healthFacility.replace("'","\'");
			Date dateOfDeath = null;
			if (dateOfOutcome != null && org.apache.commons.lang3.StringUtils.isNotBlank(outcome) && (outcome.equalsIgnoreCase("DEAD"))) {
				dateOfDeath = dateOfOutcome;
			}

			Patient patient = createPatient(clientName, age != null ? age : 99, sex, nationalId, caseId, dateOfDeath);
			patient = addPersonAttributes(patient, phone, null, null);
			patient = addPersonAddresses(patient, nationality, countyOfResidence, subCounty, ward, villageEstate);
			if (org.apache.commons.lang3.StringUtils.isNotBlank(quarantineFacility)) {
				patient = saveAndenrollPatientInCovidQuarantine(patient, baselineLabTestDate, quarantineFacility, dateConfirmedPositive, healthFacility);
			}

			String hasTravelHistory = null;
			String historyOfContactWithCase = null;
			if (org.apache.commons.lang3.StringUtils.isNotBlank(travelHistory) && travelHistory.equalsIgnoreCase("Yes")) {
				hasTravelHistory = "Yes";
			} else if (org.apache.commons.lang3.StringUtils.isNotBlank(travelHistory) && travelHistory.equalsIgnoreCase("No")) {
				hasTravelHistory = "No";
			}

			if (org.apache.commons.lang3.StringUtils.isNotBlank(contactWithConfirmedCase) && contactWithConfirmedCase.equalsIgnoreCase("No")){
				historyOfContactWithCase = "No";
			} else if(org.apache.commons.lang3.StringUtils.isNotBlank(contactWithConfirmedCase) && contactWithConfirmedCase.equalsIgnoreCase("Yes")) {
				historyOfContactWithCase = "Yes";
			}

			baselineResult = baselineResult.equalsIgnoreCase("Positive") ? "Positive" : "Negative";
			enrollInCovidCaseInvestigationProgram(patient, baselineLabTestDate, county, null, sourceOfInfection, healthFacility, hasTravelHistory, historyOfContactWithCase, labName, baselineLabTestDate, dateConfirmedPositive, baselineResult, hasSymptoms,
					dateOfSymptomOnset, hasCough, hasFever, hasDifficultyBreathing, hasOtherSymptoms, null, null, hospitalization, occupation);


			if (travelingFrom != null && !travelingFrom.equals("") && patient != null && dateOfArrival != null) {
				updateTravelInfo(patient, dateOfArrival, travelingFrom, flightNumber);
			}

			if (firstFollowupLabTestDate != null && org.apache.commons.lang3.StringUtils.isNotBlank(firstFollowupResult)) {
				saveLabOrder(patient, null, labName, firstFollowupLabTestDate,firstFollowupLabTestDate, "1stFollowup", firstFollowupResult);
			}

			if (secondFollowupLabTestDate != null && org.apache.commons.lang3.StringUtils.isNotBlank(secondFollowupResult)) {
				saveLabOrder(patient, null, labName, secondFollowupLabTestDate, secondFollowupLabTestDate, "2ndFollowup", secondFollowupResult);
			}

			if (thirdFollowupLabTestDate != null && org.apache.commons.lang3.StringUtils.isNotBlank(thirdFollowupResult)) {
				saveLabOrder(patient, null, labName, thirdFollowupLabTestDate, thirdFollowupLabTestDate, "3rdFollowup", thirdFollowupResult);
			}

			// add patient patient contact

			if (patient != null && org.apache.commons.lang3.StringUtils.isNotBlank(contactWithConfirmedCase) && !contactWithConfirmedCase.equalsIgnoreCase("No")) {
				// get index case
				Patient indexCase = checkIfPatientExists(contactWithConfirmedCase.trim());
				if (indexCase != null) {
					PatientContact patientContact = createPatientContact(clientName, age != null ? age : 99, sex);
					if (patientContact != null) {
						patientContact.setPatientRelatedTo(indexCase);
						patientContact.setPatient(patient);
						patientContact.setIpvOutcome("CHT");
						patientContact.setPnsApproach(1060); // default to living together
						patientContact.setVoided(false);
						patientContact.setContactListingDeclineReason("Primary");
						Context.getService(HTSService.class).savePatientContact(patientContact);
						addRelationship(indexCase, patient);
						System.out.println("Successfully added patient contact " + clientName + " for case " + contactWithConfirmedCase);
					}
				}
			}

			if (dateOfOutcome != null && org.apache.commons.lang3.StringUtils.isNotBlank(outcome) && (outcome.equalsIgnoreCase("DISCHARGE") || outcome.equalsIgnoreCase("DEAD"))) {
				discontinueCaseFromCovidInvestigationProgram(patient, dateOfOutcome.before(dateConfirmedPositive) ? dateConfirmedPositive : dateOfOutcome , outcome);
			}
			if (counter % 200 == 0) {
				Context.flushSession();
				Context.clearSession();

			}
		}
	}

	private static PatientContact createPatientContact(String fullName, Integer age, String sex) {

		PatientContact patientContact = null;


		fullName = fullName.replace(".","");
		fullName = fullName.replace(",", "");
		fullName = fullName.replace("'", "");
		fullName = fullName.replace("  ", " ");

		String fName = "", mName = "", lName = "";
		if (fullName != null && !fullName.equals("")) {

			String [] nameParts = fullName.trim().split(" ");
			fName = nameParts[0].trim();
			if (nameParts.length > 1) {
				lName = nameParts[1].trim();
			} else {
				lName = nameParts[0].trim();
			}
			if (nameParts.length > 2) {
				mName = nameParts[2].trim();
			}

			fName = fName != null && !fName.equals("") ? fName : "";
			mName = mName != null && !mName.equals("") ? mName : "";
			lName = lName != null && !lName.equals("") ? lName : "";
			patientContact = new PatientContact();
			if (sex == null || sex.equals("") || StringUtils.isEmpty(sex)) {
				sex = "U";
			}
			patientContact.setSex(sex);
			patientContact.setFirstName(fName);
			patientContact.setLastName(lName);
			if (mName != null && !mName.equals("")) {
				patientContact.setMiddleName(mName);
			}


			if (age == null) {
				age = 100;
			}
			Calendar effectiveDate = Calendar.getInstance();
			effectiveDate.set(2020, 3, 1, 0, 0);

			Calendar computedDob = Calendar.getInstance();
			computedDob.setTimeInMillis(effectiveDate.getTimeInMillis());
			computedDob.add(Calendar.YEAR, -age);

			if (computedDob != null) {
				patientContact.setBirthDate(computedDob.getTime());
			}

		}
		return patientContact;
	}

	private static void addRelationship(Person indexCase, Person contact) {
		String traveledTogetherRelType = "8ea992ac-6ed3-11ea-bc55-0242ac130003";

		Person personA = null, personB = null;
		RelationshipType type = null;

		personA = contact;
		personB = indexCase;
		type = Context.getPersonService().getRelationshipTypeByUuid(traveledTogetherRelType);

		Relationship rel = new Relationship();
		rel.setRelationshipType(type);
		rel.setPersonA(personA);
		rel.setPersonB(personB);

		Context.getPersonService().saveRelationship(rel);
	}

	private static Patient createPatient(String fullName, Integer age, String sex, String idNo, String caseId, Date dateOfDeath) {

		Patient patient = null;
		String PATIENT_CLINIC_NUMBER = "b4d66522-11fc-45c7-83e3-39a1af21ae0d";

		String PASSPORT_NUMBER = "e1e80daa-6d7e-11ea-bc55-0242ac130003";

		fullName = fullName.replace(".","");
		fullName = fullName.replace(",", "");
		fullName = fullName.replace("'", "");
		fullName = fullName.replace("  ", " ");

		String fName = "", mName = "", lName = "";
		if (fullName != null && !fullName.equals("")) {

			String [] nameParts = fullName.trim().split(" ");
			fName = nameParts[0].trim();
			if (nameParts.length > 1) {
				lName = nameParts[1].trim();
			} else {
				lName = nameParts[0].trim();
			}
			if (nameParts.length > 2) {
				mName = nameParts[2].trim();
			}

			fName = fName != null && !fName.equals("") ? fName : "";
			mName = mName != null && !mName.equals("") ? mName : "";
			lName = lName != null && !lName.equals("") ? lName : "";
			patient = new Patient();
			if (sex == null || sex.equals("") || StringUtils.isEmpty(sex)) {
				sex = "U";
			}
			patient.setGender(sex);
			PersonName pn = new PersonName();//Context.getPersonService().parsePersonName(fullName);
			pn.setGivenName(fName);
			pn.setFamilyName(lName);
			if (mName != null && !mName.equals("")) {
				pn.setMiddleName(mName);
			}
			System.out.print("Person name: " + pn);

			patient.addName(pn);

			if (age == null) {
				age = 100;
			}
			Calendar effectiveDate = Calendar.getInstance();
			effectiveDate.set(2020, 3, 1, 0, 0);

			Calendar computedDob = Calendar.getInstance();
			computedDob.setTimeInMillis(effectiveDate.getTimeInMillis());
			computedDob.add(Calendar.YEAR, -age);

			if (computedDob != null) {
				patient.setBirthdate(computedDob.getTime());
			}

			if (dateOfDeath != null) {
				patient.setDead(true);
				patient.setDeathDate(dateOfDeath);
				patient.setCauseOfDeath(Context.getConceptService().getConcept(113021));
			}

			patient.setBirthdateEstimated(true);

			System.out.println(", ID No: " + idNo + ", Case ID: " + caseId);

			PatientIdentifier openMRSID = generateOpenMRSID();
			boolean preferredIdentifierSet = false;

			if (caseId != null && !caseId.equals("")) {
				PatientIdentifierType caseIdType = Context.getPatientService().getPatientIdentifierTypeByUuid(PATIENT_CLINIC_NUMBER);

				PatientIdentifier caseNumber = new PatientIdentifier();
				caseNumber.setIdentifierType(caseIdType);
				caseNumber.setIdentifier(caseId);
				caseNumber.setPreferred(true);
				patient.addIdentifier(caseNumber);
				preferredIdentifierSet = true;
			}

			if (idNo != null && !idNo.equals("")) {
				PatientIdentifierType upnType = Context.getPatientService().getPatientIdentifierTypeByUuid(PASSPORT_NUMBER);

				PatientIdentifier upn = new PatientIdentifier();
				upn.setIdentifierType(upnType);
				upn.setIdentifier(idNo);
				if (!preferredIdentifierSet) {
					upn.setPreferred(true);
				}
				patient.addIdentifier(upn);
			}

			if (!preferredIdentifierSet){
				openMRSID.setPreferred(true);
			}
			patient.addIdentifier(openMRSID);

		}
		return patient;
	}

	private static Patient createPatient(String fullName, Integer age, String sex, String idNo) {

		Patient patient = null;
		String PASSPORT_NUMBER = "e1e80daa-6d7e-11ea-bc55-0242ac130003";

		fullName = fullName.replace(".","");
		fullName = fullName.replace(",", "");
		fullName = fullName.replace("'", "");
		fullName = fullName.replace("  ", " ");

		String fName = "", mName = "", lName = "";
		if (fullName != null && !fullName.equals("")) {

			String [] nameParts = fullName.trim().split(" ");
			fName = nameParts[0].trim();
			if (nameParts.length > 1) {
				lName = nameParts[1].trim();
			} else {
				lName = nameParts[0].trim();
			}
			if (nameParts.length > 2) {
				mName = nameParts[2].trim();
			}

			fName = fName != null && !fName.equals("") ? fName : "";
			mName = mName != null && !mName.equals("") ? mName : "";
			lName = lName != null && !lName.equals("") ? lName : "";
			patient = new Patient();
			if (sex == null || sex.equals("") || StringUtils.isEmpty(sex)) {
				sex = "U";
			}
			patient.setGender(sex);
			PersonName pn = new PersonName();//Context.getPersonService().parsePersonName(fullName);
			pn.setGivenName(fName);
			pn.setFamilyName(lName);
			if (mName != null && !mName.equals("")) {
				pn.setMiddleName(mName);
			}
			System.out.print("Person name: " + pn);

			patient.addName(pn);

			if (age == null) {
				age = 100;
			}
			Calendar effectiveDate = Calendar.getInstance();
			effectiveDate.set(2020, 3, 1, 0, 0);

			Calendar computedDob = Calendar.getInstance();
			computedDob.setTimeInMillis(effectiveDate.getTimeInMillis());
			computedDob.add(Calendar.YEAR, -age);

			if (computedDob != null) {
				patient.setBirthdate(computedDob.getTime());
			}

			patient.setBirthdateEstimated(true);

			System.out.println(", ID No: " + idNo);

			PatientIdentifier openMRSID = generateOpenMRSID();

			if (idNo != null && !idNo.equals("")) {
				PatientIdentifierType upnType = Context.getPatientService().getPatientIdentifierTypeByUuid(PASSPORT_NUMBER);

				PatientIdentifier upn = new PatientIdentifier();
				upn.setIdentifierType(upnType);
				upn.setIdentifier(idNo);
				upn.setPreferred(true);
				patient.addIdentifier(upn);
			} else {
				openMRSID.setPreferred(true);
			}
			patient.addIdentifier(openMRSID);

		}
		return patient;
	}

	public static Patient checkIfPatientExists(String identifier) {

		if (identifier != null) {
			List<Patient> patientsAlreadyAssigned = Context.getPatientService().getPatients(null, identifier.trim(), null, false);
			if (patientsAlreadyAssigned.size() > 0) {
				return patientsAlreadyAssigned.get(0);
			}
		}
		return null;
	}


	private static Patient addPersonAttributes(Patient patient, String phone, String nokName, String nokPhone) {

		String NEXT_OF_KIN_CONTACT = "342a1d39-c541-4b29-8818-930916f4c2dc";
		String NEXT_OF_KIN_NAME = "830bef6d-b01f-449d-9f8d-ac0fede8dbd3";
		String TELEPHONE_CONTACT = "b2c38640-2603-4629-aebd-3b54f33f1e3a";


		PersonAttributeType phoneType = Context.getPersonService().getPersonAttributeTypeByUuid(TELEPHONE_CONTACT);
		PersonAttributeType nokNametype = Context.getPersonService().getPersonAttributeTypeByUuid(NEXT_OF_KIN_NAME);
		PersonAttributeType nokContacttype = Context.getPersonService().getPersonAttributeTypeByUuid(NEXT_OF_KIN_CONTACT);

		if (phone != null && !phone.equals("")) {
			PersonAttribute attribute = new PersonAttribute(phoneType, phone);

			try {
				Object hydratedObject = attribute.getHydratedObject();
				if (hydratedObject == null || "".equals(hydratedObject.toString())) {
					// if null is returned, the value should be blanked out
					attribute.setValue("");
				} else if (hydratedObject instanceof Attributable) {
					attribute.setValue(((Attributable) hydratedObject).serialize());
				} else if (!hydratedObject.getClass().getName().equals(phoneType.getFormat())) {
					// if the classes doesn't match the format, the hydration failed somehow
					// TODO change the PersonAttribute.getHydratedObject() to not swallow all errors?
					throw new APIException();
				}
			} catch (APIException e) {
				//.warn("Got an invalid value: " + value + " while setting personAttributeType id #" + paramName, e);
				// setting the value to empty so that the user can reset the value to something else
				attribute.setValue("");
			}
			patient.addAttribute(attribute);
		}

		if (nokName != null && !nokName.equals("")) {
			PersonAttribute attribute = new PersonAttribute(nokNametype, nokName);

			try {
				Object hydratedObject = attribute.getHydratedObject();
				if (hydratedObject == null || "".equals(hydratedObject.toString())) {
					// if null is returned, the value should be blanked out
					attribute.setValue("");
				} else if (hydratedObject instanceof Attributable) {
					attribute.setValue(((Attributable) hydratedObject).serialize());
				} else if (!hydratedObject.getClass().getName().equals(nokNametype.getFormat())) {
					// if the classes doesn't match the format, the hydration failed somehow
					// TODO change the PersonAttribute.getHydratedObject() to not swallow all errors?
					throw new APIException();
				}
			} catch (APIException e) {
				//.warn("Got an invalid value: " + value + " while setting personAttributeType id #" + paramName, e);
				// setting the value to empty so that the user can reset the value to something else
				attribute.setValue("");
			}
			patient.addAttribute(attribute);
		}

		if (nokPhone != null && !nokPhone.equals("")) {
			PersonAttribute attribute = new PersonAttribute(nokContacttype, nokPhone);

			try {
				Object hydratedObject = attribute.getHydratedObject();
				if (hydratedObject == null || "".equals(hydratedObject.toString())) {
					// if null is returned, the value should be blanked out
					attribute.setValue("");
				} else if (hydratedObject instanceof Attributable) {
					attribute.setValue(((Attributable) hydratedObject).serialize());
				} else if (!hydratedObject.getClass().getName().equals(nokContacttype.getFormat())) {
					// if the classes doesn't match the format, the hydration failed somehow
					// TODO change the PersonAttribute.getHydratedObject() to not swallow all errors?
					throw new APIException();
				}
			} catch (APIException e) {
				//.warn("Got an invalid value: " + value + " while setting personAttributeType id #" + paramName, e);
				// setting the value to empty so that the user can reset the value to something else
				attribute.setValue("");
			}
			patient.addAttribute(attribute);
		}
		return patient;
	}

	private static Patient addPersonAddresses(Patient patient, String nationality, String county, String subCounty, String ward, String villageEstate) {

		Set<PersonAddress> patientAddress = patient.getAddresses();
		if (patientAddress.size() > 0) {
			for (PersonAddress address : patientAddress) {
				if (nationality != null) {
					address.setCountry(nationality);
				}
				if (county != null) {
					address.setCountyDistrict(county);
				}
				if (subCounty != null) {
					address.setStateProvince(subCounty);
				}
				if (ward != null) {
					address.setAddress4(ward);
				}

				if (villageEstate != null) {
					address.setCityVillage(villageEstate);
				}
				patient.addAddress(address);
			}
		} else {
			PersonAddress pa = new PersonAddress();
			if (nationality != null) {
				pa.setCountry(nationality);
			}
			if (county != null) {
				pa.setCountyDistrict(county);
			}
			if (subCounty != null) {
				pa.setStateProvince(subCounty);
			}
			if (ward != null) {
				pa.setAddress4(ward);
			}

			if (villageEstate != null) {
				pa.setCityVillage(villageEstate);
			}
			patient.addAddress(pa);
		}
		return patient;
	}

	private static void updateTravelInfo(Patient patient, Date admissionDate, String from, String flightNumber) {

		Encounter enc = new Encounter();
		enc.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(COVID_19_TRAVEL_HISTORY_ENCOUNTER));
		enc.setEncounterDatetime(admissionDate);
		enc.setPatient(patient);
		enc.addProvider(Context.getEncounterService().getEncounterRole(1), Context.getProviderService().getProvider(1));
		enc.setForm(Context.getFormService().getFormByUuid(COVID_19_TRAVEL_HISTORY_FORM));


		// set traveled from
		ConceptService conceptService = Context.getConceptService();
		Obs o = new Obs();
		o.setConcept(conceptService.getConcept(165198));
		o.setDateCreated(new Date());
		o.setCreator(Context.getUserService().getUser(1));
		o.setLocation(enc.getLocation());
		o.setObsDatetime(admissionDate);
		o.setPerson(patient);
		o.setValueText(from);

		// date of arrival
        Obs ad = new Obs();
        ad.setConcept(conceptService.getConcept(160753));
        ad.setDateCreated(new Date());
        ad.setCreator(Context.getUserService().getUser(1));
        ad.setLocation(enc.getLocation());
        ad.setObsDatetime(admissionDate);
        ad.setPerson(patient);
        ad.setValueDatetime(admissionDate);

		// default all to flight

		Obs o1 = new Obs();
		o1.setConcept(conceptService.getConcept(1375));
		o1.setDateCreated(new Date());
		o1.setCreator(Context.getUserService().getUser(1));
		o1.setLocation(enc.getLocation());
		o1.setObsDatetime(admissionDate);
		o1.setPerson(patient);
		o1.setValueCoded(conceptService.getConcept(1378));

		if (org.apache.commons.lang3.StringUtils.isNotBlank(flightNumber)) {
			Obs fNo = new Obs();
			fNo.setConcept(conceptService.getConcept(162086));
			fNo.setDateCreated(new Date());
			fNo.setCreator(Context.getUserService().getUser(1));
			fNo.setLocation(enc.getLocation());
			fNo.setObsDatetime(admissionDate);
			fNo.setPerson(patient);
			fNo.setValueText(flightNumber);
			enc.addObs(fNo);
		}
		enc.addObs(o);
		enc.addObs(ad);
		enc.addObs(o1);

		Context.getEncounterService().saveEncounter(enc);

	}

	private static void discontinueCaseFromCovidInvestigationProgram(Patient patient, Date discontinuationDate, String discontinuationReason) {

		PatientProgram lastEnrollment = getActiveProgram(patient, COVID_19_CASE_INVESTIGATION_PROGRAM);
		if (lastEnrollment != null) {
			lastEnrollment.setDateCompleted(discontinuationDate);
			Context.getProgramWorkflowService().savePatientProgram(lastEnrollment);
		}

		Encounter enc = new Encounter();
		enc.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(COVID_OUTCOME_ENCOUNTER));
		enc.setEncounterDatetime(discontinuationDate);
		enc.setPatient(patient);
		enc.addProvider(Context.getEncounterService().getEncounterRole(1), Context.getProviderService().getProvider(1));
		enc.setForm(Context.getFormService().getFormByUuid(COVID_OUTCOME_FORM));


		// set discontinuation reason
		ConceptService conceptService = Context.getConceptService();
		Obs o = new Obs();
		o.setConcept(conceptService.getConcept(161555));
		o.setDateCreated(new Date());
		o.setCreator(Context.getUserService().getUser(1));
		o.setLocation(enc.getLocation());
		o.setObsDatetime(discontinuationDate);
		o.setPerson(patient);
		if (org.apache.commons.lang3.StringUtils.isNotBlank(discontinuationReason)) {
			if (discontinuationReason.equalsIgnoreCase("DISCHARGE")) {
				o.setValueCoded(conceptService.getConcept(664));
			} else if (discontinuationReason.equalsIgnoreCase("DEAD")) {
				o.setValueCoded(conceptService.getConcept(160034));
			}
		}
		enc.addObs(o);
		Context.getEncounterService().saveEncounter(enc);

	}

	/**
	 * Checks if a contact is enrolled in a program
	 * @param patient
	 * @return
	 */
	public static PatientProgram getActiveProgram(Patient patient, String programUUID) {
		ProgramWorkflowService service = Context.getProgramWorkflowService();
		List<PatientProgram> programs = service.getPatientPrograms(patient, service.getProgramByUuid(programUUID), null, null, null,null, true);
		return programs.size() > 0 ? programs.get(programs.size() - 1) : null;
	}

	private static Patient saveAndenrollPatientInCovidQuarantine(Patient patient, Date admissionDate, String quarantineCenter, Date discontinuationDate, String referralFacility) {

		Encounter enc = new Encounter();
		enc.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(COVID_QUARANTINE_ENROLLMENT_ENCOUNTER));
		enc.setEncounterDatetime(admissionDate);
		enc.setPatient(patient);
		enc.addProvider(Context.getEncounterService().getEncounterRole(1), Context.getProviderService().getProvider(1));
		enc.setForm(Context.getFormService().getFormByUuid(COVID_QUARANTINE_ENROLLMENT_FORM));

		// set quarantine center
		ConceptService conceptService = Context.getConceptService();
		Obs o = new Obs();
		o.setConcept(conceptService.getConcept(162724));
		o.setDateCreated(new Date());
		o.setCreator(Context.getUserService().getUser(1));
		o.setLocation(enc.getLocation());
		o.setObsDatetime(admissionDate);
		o.setPerson(patient);
		o.setValueText(quarantineCenter);

		// default all admissions type to new
		Obs o1 = new Obs();
		o1.setConcept(conceptService.getConcept(161641));
		o1.setDateCreated(new Date());
		o1.setCreator(Context.getUserService().getUser(1));
		o1.setLocation(enc.getLocation());
		o1.setObsDatetime(admissionDate);
		o1.setPerson(patient);
		o1.setValueCoded(conceptService.getConcept(164144));
		enc.addObs(o);
		enc.addObs(o1);

		Context.getPatientService().savePatient(patient);
		Context.getEncounterService().saveEncounter(enc);
		// enroll in quarantine program
		PatientProgram pp = new PatientProgram();
		pp.setPatient(patient);
		pp.setProgram(Context.getProgramWorkflowService().getProgramByUuid(COVID_QUARANTINE_PROGRAM));
		pp.setDateEnrolled(admissionDate);
		if (discontinuationDate != null) {
			pp.setDateCompleted(discontinuationDate);
		}
		pp.setDateCreated(new Date());
		Context.getProgramWorkflowService().savePatientProgram(pp);

		if (discontinuationDate != null) {
			Encounter discEnc = new Encounter();
			discEnc.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(COVID_QUARANTINE_OUTCOME_ENCOUNTER));
			discEnc.setEncounterDatetime(discontinuationDate);
			discEnc.setPatient(patient);
			discEnc.addProvider(Context.getEncounterService().getEncounterRole(1), Context.getProviderService().getProvider(1));
			discEnc.setForm(Context.getFormService().getFormByUuid(COVID_QUARANTINE_OUTCOME_FORM));


			// set discontinuation reason to referral
			Obs oDiscReason = new Obs();
			oDiscReason.setConcept(conceptService.getConcept(161555));
			oDiscReason.setDateCreated(discontinuationDate);
			oDiscReason.setCreator(Context.getUserService().getUser(1));
			oDiscReason.setLocation(enc.getLocation());
			oDiscReason.setObsDatetime(discontinuationDate);
			oDiscReason.setPerson(patient);
			oDiscReason.setValueCoded(conceptService.getConcept(164165));
			discEnc.addObs(oDiscReason);

			Obs oReferral = new Obs();
			oReferral.setConcept(conceptService.getConcept(159623));
			oReferral.setDateCreated(discontinuationDate);
			oReferral.setCreator(Context.getUserService().getUser(1));
			oReferral.setLocation(enc.getLocation());
			oReferral.setObsDatetime(discontinuationDate);
			oReferral.setPerson(patient);
			oReferral.setValueCoded(conceptService.getConcept(1185));
			discEnc.addObs(oReferral);

			if (org.apache.commons.lang3.StringUtils.isNotBlank(referralFacility)) {
				Obs oReferralFacility = new Obs();
				oReferralFacility.setConcept(conceptService.getConcept(161562));
				oReferralFacility.setDateCreated(discontinuationDate);
				oReferralFacility.setCreator(Context.getUserService().getUser(1));
				oReferralFacility.setLocation(enc.getLocation());
				oReferralFacility.setObsDatetime(discontinuationDate);
				oReferralFacility.setPerson(patient);
				oReferralFacility.setValueText(referralFacility);
				discEnc.addObs(oReferralFacility);
			}
			Context.getEncounterService().saveEncounter(discEnc);

		}

		return patient;
	}

	private static Patient enrollInCovidCaseInvestigationProgram(Patient patient, Date admissionDate, String county, String subCounty, String detectionPoint, String healthFacility, String travelHistory, String contactWithCase, String labName, Date baselineLabTestDate, Date dateConfirmedPositive, String labResult, String hasSymptoms, Date symptomsOnsetDate, String hasCough,String hasFever,String hasDifficultyBreathing,String hasOtherSymptoms,String visitedHealthFacility,String dateVisitedHealthFacility,String hospitalization, String occupation) {


		Integer symptomaticConcept = 1729;//1065,1066,1067
		Integer symptomOnsetDateConcept = 1730;
		Integer coughConcept = 143264;
		Integer feverConcept = 140238;
		Integer difficultyBreathingConcept = 164441;
		Integer otherSymptomsConcept = 1838;
		Integer otherSymptomsAnsConcept = 139548;
		Integer otherSymptomsTextConcept = 160632;
		Integer admittedToHospitalConcept = 163403;//1065,1066,1067
		Integer admissionDateConcept = 1640;
		Integer hospitalAdmittedConcept = 162724;
		Integer detectionPointConcept = 161010;
		Integer poeConcept = 165651;
		Integer communityConcept = 163488;
		Encounter enc = new Encounter();
		enc.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(COVID_19_CASE_INVESTIGATION_ENCOUNTER));
		enc.setEncounterDatetime(admissionDate);
		enc.setPatient(patient);
		enc.addProvider(Context.getEncounterService().getEncounterRole(1), Context.getProviderService().getProvider(1));
		enc.setForm(Context.getFormService().getFormByUuid(COVID_19_CASE_INVESTIGATION_FORM));


		// set county
		ConceptService conceptService = Context.getConceptService();
		Obs oC = new Obs();
		oC.setConcept(conceptService.getConceptByUuid(REPORTING_COUNTY));
		oC.setDateCreated(new Date());
		oC.setCreator(Context.getUserService().getUser(1));
		oC.setLocation(enc.getLocation());
		oC.setObsDatetime(admissionDate);
		oC.setPerson(patient);
		oC.setValueText(county);
		enc.addObs(oC);

		// set subcounty
		if (subCounty != null && !subCounty.equals("")) {
			Obs oSc = new Obs();
			oSc.setConcept(conceptService.getConceptByUuid(REPORTING_SUB_COUNTY));
			oSc.setDateCreated(new Date());
			oSc.setCreator(Context.getUserService().getUser(1));
			oSc.setLocation(enc.getLocation());
			oSc.setObsDatetime(admissionDate);
			oSc.setPerson(patient);
			oSc.setValueText(subCounty);
			enc.addObs(oSc);
		}
		// set health facility
		if (healthFacility != null && !healthFacility.equals("")) {
			Obs f = new Obs();
			f.setConcept(conceptService.getConceptByUuid(REPORTING_HEALTH_FACILITY));
			f.setDateCreated(new Date());
			f.setCreator(Context.getUserService().getUser(1));
			f.setLocation(enc.getLocation());
			f.setObsDatetime(admissionDate);
			f.setPerson(patient);
			f.setValueText(healthFacility);
			enc.addObs(f);
		}

		// detection point
		if (detectionPoint != null && !detectionPoint.equals("")) {
			Obs poe = new Obs();
			poe.setConcept(conceptService.getConcept(detectionPointConcept));
			poe.setDateCreated(new Date());
			poe.setCreator(Context.getUserService().getUser(1));
			poe.setLocation(enc.getLocation());
			poe.setObsDatetime(admissionDate);
			poe.setPerson(patient);
			poe.setValueCoded(conceptService.getConcept(detectionPoint.equals("Imported") ? poeConcept : communityConcept));
			enc.addObs(poe);
		}

		// travel history
		if (travelHistory != null && !travelHistory.equals("")) {
			Obs t = new Obs();
			t.setConcept(conceptService.getConceptByUuid(HISTORY_OF_TRAVEL));
			t.setDateCreated(new Date());
			t.setCreator(Context.getUserService().getUser(1));
			t.setLocation(enc.getLocation());
			t.setObsDatetime(admissionDate);
			t.setPerson(patient);
			t.setValueCoded(conceptService.getConceptByUuid(travelHistory.equals("Yes") ? YES_CONCEPT : NO_CONCEPT));
			enc.addObs(t);
		}

		// history of contact with confirmed case
		if (contactWithCase != null && !contactWithCase.equals("")) {
			Obs c = new Obs();
			c.setConcept(conceptService.getConceptByUuid(CONTACT_WITH_SUSPECTED_CASE));
			c.setDateCreated(new Date());
			c.setCreator(Context.getUserService().getUser(1));
			c.setLocation(enc.getLocation());
			c.setObsDatetime(admissionDate);
			c.setPerson(patient);
			c.setValueCoded(conceptService.getConceptByUuid(contactWithCase.equals("Yes") ? YES_CONCEPT : NO_CONCEPT));
			enc.addObs(c);
		}


		// has symptoms
		if (hasSymptoms != null && !hasSymptoms.equals("")) {
			Obs oHasSymptoms = new Obs();
			oHasSymptoms.setConcept(conceptService.getConcept(symptomaticConcept));
			oHasSymptoms.setDateCreated(new Date());
			oHasSymptoms.setCreator(Context.getUserService().getUser(1));
			oHasSymptoms.setLocation(enc.getLocation());
			oHasSymptoms.setObsDatetime(admissionDate);
			oHasSymptoms.setPerson(patient);
			oHasSymptoms.setValueCoded(conceptService.getConceptByUuid(hasSymptoms.equals("Yes") ? YES_CONCEPT : NO_CONCEPT));
			enc.addObs(oHasSymptoms);
		}

		// symptoms onset date
		if (symptomsOnsetDate != null) {
			Obs oOnsetDate = new Obs();
			oOnsetDate.setConcept(conceptService.getConcept(symptomOnsetDateConcept));
			oOnsetDate.setDateCreated(new Date());
			oOnsetDate.setCreator(Context.getUserService().getUser(1));
			oOnsetDate.setLocation(enc.getLocation());
			oOnsetDate.setObsDatetime(admissionDate);
			oOnsetDate.setPerson(patient);
			oOnsetDate.setValueDate(symptomsOnsetDate);
			enc.addObs(oOnsetDate);
		}
		// cough
		if (hasCough != null && !hasCough.equals("")) {
			Obs oCough = new Obs();
			oCough.setConcept(conceptService.getConcept(coughConcept));
			oCough.setDateCreated(new Date());
			oCough.setCreator(Context.getUserService().getUser(1));
			oCough.setLocation(enc.getLocation());
			oCough.setObsDatetime(admissionDate);
			oCough.setPerson(patient);
			oCough.setValueCoded(conceptService.getConceptByUuid(hasCough.equals("Yes") ? YES_CONCEPT : NO_CONCEPT));
			enc.addObs(oCough);
		}

		// fever
		if (hasFever != null && !hasFever.equals("")) {
			Obs oFever = new Obs();
			oFever.setConcept(conceptService.getConcept(feverConcept));
			oFever.setDateCreated(new Date());
			oFever.setCreator(Context.getUserService().getUser(1));
			oFever.setLocation(enc.getLocation());
			oFever.setObsDatetime(admissionDate);
			oFever.setPerson(patient);
			oFever.setValueCoded(conceptService.getConceptByUuid(hasFever.equals("Yes") ? YES_CONCEPT : NO_CONCEPT));
			enc.addObs(oFever);
		}

		// difficulty breathing
		if (contactWithCase != null && !contactWithCase.equals("")) {
			Obs oDifficultyBreathing = new Obs();
			oDifficultyBreathing.setConcept(conceptService.getConcept(difficultyBreathingConcept));
			oDifficultyBreathing.setDateCreated(new Date());
			oDifficultyBreathing.setCreator(Context.getUserService().getUser(1));
			oDifficultyBreathing.setLocation(enc.getLocation());
			oDifficultyBreathing.setObsDatetime(admissionDate);
			oDifficultyBreathing.setPerson(patient);
			oDifficultyBreathing.setValueCoded(conceptService.getConceptByUuid(hasDifficultyBreathing.equals("Yes") ? YES_CONCEPT : NO_CONCEPT));
			enc.addObs(oDifficultyBreathing);
		}

		//other symptoms
		if (hasOtherSymptoms != null && !hasOtherSymptoms.equals("")) {
			Obs oOtherSymptom = new Obs();
			oOtherSymptom.setConcept(conceptService.getConcept(otherSymptomsConcept));
			oOtherSymptom.setDateCreated(new Date());
			oOtherSymptom.setCreator(Context.getUserService().getUser(1));
			oOtherSymptom.setLocation(enc.getLocation());
			oOtherSymptom.setObsDatetime(admissionDate);
			oOtherSymptom.setPerson(patient);
			oOtherSymptom.setValueCoded(conceptService.getConcept(otherSymptomsAnsConcept));

			Obs oOtherSymptomsList = new Obs();
			oOtherSymptomsList.setConcept(conceptService.getConcept(otherSymptomsTextConcept));
			oOtherSymptomsList.setDateCreated(new Date());
			oOtherSymptomsList.setCreator(Context.getUserService().getUser(1));
			oOtherSymptomsList.setLocation(enc.getLocation());
			oOtherSymptomsList.setObsDatetime(admissionDate);
			oOtherSymptomsList.setPerson(patient);
			oOtherSymptomsList.setValueText(hasOtherSymptoms);
			enc.addObs(oOtherSymptom);
			enc.addObs(oOtherSymptomsList);
		}

		//because of typos, we'll store indicated occupation as other
		if (occupation != null && !occupation.equals("")) {
			Obs oOccupation = new Obs();
			oOccupation.setConcept(conceptService.getConcept(1542));
			oOccupation.setDateCreated(new Date());
			oOccupation.setCreator(Context.getUserService().getUser(1));
			oOccupation.setLocation(enc.getLocation());
			oOccupation.setObsDatetime(admissionDate);
			oOccupation.setPerson(patient);
			oOccupation.setValueCoded(conceptService.getConcept(5622));

			Obs oOtherOccupation = new Obs();
			oOtherOccupation.setConcept(conceptService.getConcept(161011));
			oOtherOccupation.setDateCreated(new Date());
			oOtherOccupation.setCreator(Context.getUserService().getUser(1));
			oOtherOccupation.setLocation(enc.getLocation());
			oOtherOccupation.setObsDatetime(admissionDate);
			oOtherOccupation.setPerson(patient);
			oOtherOccupation.setValueText(occupation);
			enc.addObs(oOccupation);
			enc.addObs(oOtherOccupation);
		}

		//hospitalization
		if (org.apache.commons.lang3.StringUtils.isNotBlank(hospitalization) || org.apache.commons.lang3.StringUtils.isNotBlank(healthFacility)) {
			Obs oHosp = new Obs();
			oHosp.setConcept(conceptService.getConcept(admittedToHospitalConcept));
			oHosp.setDateCreated(new Date());
			oHosp.setCreator(Context.getUserService().getUser(1));
			oHosp.setLocation(enc.getLocation());
			oHosp.setObsDatetime(admissionDate);
			oHosp.setPerson(patient);
			oHosp.setValueCoded(conceptService.getConceptByUuid(hospitalization.equals("Yes") ? YES_CONCEPT : NO_CONCEPT));
			enc.addObs(oHosp);
		}

		// admission date
		if (admissionDate != null) {
			Obs oAdm = new Obs();
			oAdm.setConcept(conceptService.getConcept(admissionDateConcept));
			oAdm.setDateCreated(new Date());
			oAdm.setCreator(Context.getUserService().getUser(1));
			oAdm.setLocation(enc.getLocation());
			oAdm.setObsDatetime(admissionDate);
			oAdm.setPerson(patient);
			oAdm.setValueDate(admissionDate);
			enc.addObs(oAdm);
		}

		// name of hospital admitted
		if (healthFacility != null && !healthFacility.equals("")) {
			Obs oAdmHosp = new Obs();
			oAdmHosp.setConcept(conceptService.getConcept(hospitalAdmittedConcept));
			oAdmHosp.setDateCreated(new Date());
			oAdmHosp.setCreator(Context.getUserService().getUser(1));
			oAdmHosp.setLocation(enc.getLocation());
			oAdmHosp.setObsDatetime(admissionDate);
			oAdmHosp.setPerson(patient);
			oAdmHosp.setValueText(healthFacility);
			enc.addObs(oAdmHosp);
		}

		Context.getPatientService().savePatient(patient);
		Context.getEncounterService().saveEncounter(enc);
		// enroll in covid-19 program
		PatientProgram pp = new PatientProgram();
		pp.setPatient(patient);
		pp.setProgram(Context.getProgramWorkflowService().getProgramByUuid(COVID_19_CASE_INVESTIGATION_PROGRAM));
		pp.setDateEnrolled(admissionDate);
		pp.setDateCreated(new Date());
		//Context.getEncounterService().saveEncounter(enc);
		Context.getProgramWorkflowService().savePatientProgram(pp);

		// save baseline lab
		saveLabOrder(patient, enc, labName, baselineLabTestDate, dateConfirmedPositive, "baseline", labResult);

		return patient;
	}

	private static void saveLabOrder (Patient patient, Encounter encounter, String testingLab, Date orderDate, Date resultDate, String orderReason, String labResult ) {

		ConceptService conceptService = Context.getConceptService();
		String TEST_ORDER_TYPE_UUID = "52a447d3-a64a-11e3-9aeb-50e549534c5e";
		String LAB_ENCOUNTER_TYPE_UUID = "e1406e88-e9a9-11e8-9f32-f2801f1b9fd1";
		String COVID_19_CASE_INVESTIGATION = "a4414aee-6832-11ea-bc55-0242ac130003";
		Concept covidTestConcept = conceptService.getConcept(165611);
		Concept covidPosConcept = conceptService.getConcept(703);
		Concept covidNegConcept = conceptService.getConcept(664);
		Concept covidIndeterminateConcept = conceptService.getConcept(1138);
		EncounterType labEncounterType = Context.getEncounterService().getEncounterTypeByUuid(LAB_ENCOUNTER_TYPE_UUID);


		Encounter enc = new Encounter();
		enc.setEncounterType(labEncounterType);
		enc.setEncounterDatetime(orderDate);
		enc.setPatient(patient);
		enc.addProvider(Context.getEncounterService().getEncounterRole(1), Context.getProviderService().getProvider(1));
		Encounter savedEnc = Context.getEncounterService().saveEncounter(enc);


		Order anOrder = new TestOrder();
		anOrder.setPatient(patient);
		anOrder.setCareSetting(new CareSetting());
		anOrder.setConcept(conceptService.getConceptByUuid(COVID_19_LAB_TEST_CONCEPT));
		anOrder.setDateActivated(orderDate);
		anOrder.setCommentToFulfiller(testingLab != null ? testingLab : "NIC"); //place holder for now
		anOrder.setInstructions("OP and NP Swabs");
		anOrder.setOrderer(Context.getProviderService().getProvider(1));
		anOrder.setEncounter(savedEnc);
		anOrder.setCareSetting(Context.getOrderService().getCareSetting(1));
		if (orderReason.equals("baseline")) {
			anOrder.setOrderReason(conceptService.getConceptByUuid(COVID_19_BASELINE_TEST_CONCEPT));
		} else if (orderReason.equals("1stFollowup")) {
			anOrder.setOrderReason(conceptService.getConceptByUuid(COVID_19_1ST_FOLLOWUP_TEST_CONCEPT));
		} else if (orderReason.equals("2ndFollowup")) {
			anOrder.setOrderReason(conceptService.getConceptByUuid(COVID_19_2ND_FOLLOWUP_TEST_CONCEPT));
		} else if (orderReason.equals("3rdFollowup")) {
			anOrder.setOrderReason(conceptService.getConceptByUuid(COVID_19_3RD_FOLLOWUP_TEST_CONCEPT));
		} else if (orderReason.equals("4thFollowup")) {
			anOrder.setOrderReason(conceptService.getConceptByUuid(COVID_19_4TH_FOLLOWUP_TEST_CONCEPT));
		} else if (orderReason.equals("5thFollowup")) {
			anOrder.setOrderReason(conceptService.getConceptByUuid(COVID_19_5TH_FOLLOWUP_TEST_CONCEPT));
		} else {
			anOrder.setOrderReason(conceptService.getConceptByUuid(COVID_19_BASELINE_TEST_CONCEPT));
		}
		OrderContext orderContext = null;

		Order od = Context.getOrderService().saveOrder(anOrder, orderContext);

		Encounter resEnc = new Encounter();
		resEnc.setEncounterType(labEncounterType);
		resEnc.setEncounterDatetime(resultDate);
		resEnc.setPatient(patient);
		resEnc.setCreator(Context.getUserService().getUser(1));

		Obs res = new Obs();
		res.setConcept(covidTestConcept);
		res.setDateCreated(new Date());
		res.setCreator(Context.getUserService().getUser(1));
		res.setObsDatetime(resultDate);
		res.setPerson(od.getPatient());
		res.setOrder(od);
		res.setValueCoded(labResult.equals("Negative") ? covidNegConcept : labResult.equals("Positive") ? covidPosConcept : covidIndeterminateConcept);
		resEnc.addObs(res);

		try {
			Context.getEncounterService().saveEncounter(resEnc);
			Context.getOrderService().discontinueOrder(od, "Results received", new Date(), od.getOrderer(),
					od.getEncounter());
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	private static PatientIdentifier generateOpenMRSID() {
		PatientIdentifierType openmrsIDType = Context.getPatientService().getPatientIdentifierTypeByUuid("dfacd928-0370-4315-99d7-6ec1c9f7ae76");
		String generated = Context.getService(IdentifierSourceService.class).generateIdentifier(openmrsIDType, "Registration");
		PatientIdentifier identifier = new PatientIdentifier(generated, openmrsIDType, getDefaultLocation());
		return identifier;
	}

	public static Location getDefaultLocation() {
		try {
			Context.addProxyPrivilege(PrivilegeConstants.VIEW_LOCATIONS);
			Context.addProxyPrivilege(PrivilegeConstants.VIEW_GLOBAL_PROPERTIES);
			String GP_DEFAULT_LOCATION = "kenyaemr.defaultLocation";
			GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(GP_DEFAULT_LOCATION);
			return gp != null ? ((Location) gp.getValue()) : null;
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.VIEW_LOCATIONS);
			Context.removeProxyPrivilege(PrivilegeConstants.VIEW_GLOBAL_PROPERTIES);
		}

	}

}
