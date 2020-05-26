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
import org.apache.poi.ss.usermodel.DateUtil;
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
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.TestOrder;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.OrderContext;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.util.PrivilegeConstants;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
	public static final String COVID_QUARANTINE_ENROLLMENT_FORM = "9a5d57b6-739a-11ea-bc55-0242ac130003";
	public static final String COVID_QUARANTINE_PROGRAM = "9a5d555e-739a-11ea-bc55-0242ac130003";
    public static final String COVID_19_TRAVEL_HISTORY_ENCOUNTER = "50a59411-921b-435a-9109-42aa68ee7aa7";
    public static final String COVID_19_TRAVEL_HISTORY_FORM = "87513b50-6ced-11ea-bc55-0242ac130003";
	public static final String CONSULTATION = "465a92f2-baf8-42e9-9612-53064be868e8";


	public static final String COVID_19_CASE_INVESTIGATION_FORM = "0fe60b26-8648-438b-afea-8841dcd993c6";
	public static final String COVID_19_CASE_INVESTIGATION_ENCOUNTER = "a4414aee-6832-11ea-bc55-0242ac130003";
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
		importLabResults(sheet);

		
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
			patient = saveAndenrollPatientInCovidQuarantine(patient, admissionDate, facilityName);

			if (travelingFrom.equals("") && !countryofOrigin.equals("")) {
			    travelingFrom = countryofOrigin;
            }
            if (travelingFrom != null && !travelingFrom.equals("") && patient != null) {
                updateTravelInfo(patient, admissionDate, travelingFrom);
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
				patient = saveAndenrollPatientInCovidQuarantine(patient, admissionDate, quarantineFacilityName);
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
			enrollInCovidCaseInvestigationProgram(patient, admissionDate, county, subCounty, healthFacility, hasTravelHistory, historyOfContactWithCase, labName, horBaseResult);
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

	private static Patient addPersonAddresses(Patient patient, String nationality, String county, String subCounty, String ward, String postaladdress) {

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

				if (postaladdress != null) {
					address.setAddress1(postaladdress);
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

			if (postaladdress != null) {
				pa.setAddress1(postaladdress);
			}
			patient.addAddress(pa);
		}
		return patient;
	}

	private static void updateTravelInfo(Patient patient, Date admissionDate, String from) {

		Encounter enc = new Encounter();
		enc.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(COVID_19_TRAVEL_HISTORY_ENCOUNTER));
		enc.setEncounterDatetime(admissionDate);
		enc.setPatient(patient);
		enc.addProvider(Context.getEncounterService().getEncounterRole(1), Context.getProviderService().getProvider(1));
		enc.setForm(Context.getFormService().getFormByUuid(COVID_19_TRAVEL_HISTORY_FORM));


		// set traveled from
		ConceptService conceptService = Context.getConceptService();
		Obs o = new Obs();
		o.setConcept(conceptService.getConcept("165198"));
		o.setDateCreated(new Date());
		o.setCreator(Context.getUserService().getUser(1));
		o.setLocation(enc.getLocation());
		o.setObsDatetime(admissionDate);
		o.setPerson(patient);
		o.setValueText(from);

		// date of arrival
        Obs ad = new Obs();
        ad.setConcept(conceptService.getConcept("160753"));
        ad.setDateCreated(new Date());
        ad.setCreator(Context.getUserService().getUser(1));
        ad.setLocation(enc.getLocation());
        ad.setObsDatetime(admissionDate);
        ad.setPerson(patient);
        ad.setValueDatetime(admissionDate);

		// default all to flight
		Obs o1 = new Obs();
		o1.setConcept(conceptService.getConcept("1375"));
		o1.setDateCreated(new Date());
		o1.setCreator(Context.getUserService().getUser(1));
		o1.setLocation(enc.getLocation());
		o1.setObsDatetime(admissionDate);
		o1.setPerson(patient);
		o1.setValueCoded(conceptService.getConcept("1378"));
		enc.addObs(o);
		enc.addObs(ad);
		enc.addObs(o1);

		Context.getEncounterService().saveEncounter(enc);

	}


	private static Patient saveAndenrollPatientInCovidQuarantine(Patient patient, Date admissionDate, String quarantineCenter) {

		Encounter enc = new Encounter();
		enc.setEncounterType(Context.getEncounterService().getEncounterTypeByUuid(COVID_QUARANTINE_ENROLLMENT_ENCOUNTER));
		enc.setEncounterDatetime(admissionDate);
		enc.setPatient(patient);
		enc.addProvider(Context.getEncounterService().getEncounterRole(1), Context.getProviderService().getProvider(1));
		enc.setForm(Context.getFormService().getFormByUuid(COVID_QUARANTINE_ENROLLMENT_FORM));


		// set quarantine center
		ConceptService conceptService = Context.getConceptService();
		Obs o = new Obs();
		o.setConcept(conceptService.getConcept("162724"));
		o.setDateCreated(new Date());
		o.setCreator(Context.getUserService().getUser(1));
		o.setLocation(enc.getLocation());
		o.setObsDatetime(admissionDate);
		o.setPerson(patient);
		o.setValueText(quarantineCenter);

		// default all admissions type to new
		Obs o1 = new Obs();
		o1.setConcept(conceptService.getConcept("161641"));
		o1.setDateCreated(new Date());
		o1.setCreator(Context.getUserService().getUser(1));
		o1.setLocation(enc.getLocation());
		o1.setObsDatetime(admissionDate);
		o1.setPerson(patient);
		o1.setValueCoded(conceptService.getConcept("164144"));
		enc.addObs(o);
		enc.addObs(o1);

		Context.getPatientService().savePatient(patient);
		Context.getEncounterService().saveEncounter(enc);
		// enroll in quarantine program
		PatientProgram pp = new PatientProgram();
		pp.setPatient(patient);
		pp.setProgram(Context.getProgramWorkflowService().getProgramByUuid(COVID_QUARANTINE_PROGRAM));
		pp.setDateEnrolled(admissionDate);
		pp.setDateCreated(new Date());
		Context.getProgramWorkflowService().savePatientProgram(pp);

		return patient;
	}

	private static Patient enrollInCovidCaseInvestigationProgram(Patient patient, Date admissionDate, String county, String subCounty, String healthFacility, String travelHistory, String contactWithCase, String labName, String labResult) {

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
		saveLabOrder(patient, enc, labName, admissionDate, "baseline", labResult);

		return patient;
	}

	private static void saveLabOrder (Patient patient, Encounter encounter, String testingLab, Date encDate, String orderReason, String labResult ) {

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
		enc.setEncounterDatetime(encDate);
		enc.setPatient(patient);
		enc.addProvider(Context.getEncounterService().getEncounterRole(1), Context.getProviderService().getProvider(1));
		Encounter savedEnc = Context.getEncounterService().saveEncounter(enc);


		Order anOrder = new TestOrder();
		anOrder.setPatient(patient);
		anOrder.setCareSetting(new CareSetting());
		anOrder.setConcept(conceptService.getConceptByUuid(COVID_19_LAB_TEST_CONCEPT));
		anOrder.setDateActivated(encDate);
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
		resEnc.setEncounterDatetime(new Date());
		resEnc.setPatient(patient);
		resEnc.setCreator(Context.getUserService().getUser(1));

		Obs res = new Obs();
		res.setConcept(covidTestConcept);
		res.setDateCreated(new Date());
		res.setCreator(Context.getUserService().getUser(1));
		res.setObsDatetime(new Date());
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
