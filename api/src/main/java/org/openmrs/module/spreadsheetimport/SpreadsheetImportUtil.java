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

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openmrs.CareSetting;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.OrderType;
import org.openmrs.Patient;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;
import org.openmrs.api.FormService;
import org.openmrs.api.OrderService;
import org.openmrs.api.context.Context;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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

	public static final String LAB_ORDER_ENCOUNTER_TYPE_UUID = "e1406e88-e9a9-11e8-9f32-f2801f1b9fd1";
	public static final String LAB_RESULT_ENCOUNTER_TYPE_UUID = "17a381d1-7e29-406a-b782-aa903b963c28";
	public static final String LAB_RESULT_FORM_UUID = "7e603909-9ed5-4d0c-a688-26ecb05d8b6e";
	public static Concept vlTestConceptQualitative = Context.getConceptService().getConcept(1305);
	public static Concept LDLConcept = Context.getConceptService().getConcept(1302);
	public static Concept vlTestConceptQuantitative = Context.getConceptService().getConcept(856);
	public static EncounterType labEncounterType = Context.getEncounterService().getEncounterTypeByUuid(LAB_ORDER_ENCOUNTER_TYPE_UUID);
	public static EncounterType labResultEncounterType = Context.getEncounterService().getEncounterTypeByUuid(LAB_RESULT_ENCOUNTER_TYPE_UUID);
	public static Form labResultForm = Context.getFormService().getFormByUuid(LAB_RESULT_FORM_UUID);
	public static OrderType labType = Context.getOrderService().getOrderTypeByUuid(OrderType.TEST_ORDER_TYPE_UUID);

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
		System.out.println("Workbook properties: " + wb.getNumberOfSheets());
		Sheet sheet;
		if (!StringUtils.hasText(sheetName)) {
			sheet = wb.getSheetAt(0);
		} else {
			sheet = wb.getSheet(sheetName);
		}


		// Header row
		String templateName = template.getName();
		Row firstRow = null;
		// make sure the templates conform to the below naming convention
		if (templateName.equalsIgnoreCase("Update regimen lines")) {
			firstRow = sheet.getRow(4);
		} else if (templateName.equalsIgnoreCase("Update VL results")) {
			firstRow = sheet.getRow(0);
		}

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
		if (templateName.equalsIgnoreCase("Update regimen lines")) {
			updateRegimenLine(sheet);
		} else if (templateName.equalsIgnoreCase("Update VL results")) {
			importViralLoadResults(sheet);
		}


		
		// write back Excel file to a temp location
		File returnFile = File.createTempFile("sim", ".xls");
		FileOutputStream fos = new FileOutputStream(returnFile);
		//wb.write(fos);
		fos.close();
		
		return returnFile;
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

	private static void updateRegimenLine(Sheet sheet) {

		FormService formService = Context.getFormService();
		EncounterService encounterService = Context.getEncounterService();
		ConceptService conceptService = Context.getConceptService();
		String DRUG_REGIMEN_EDITOR_ENCOUNTER = "7dffc392-13e7-11e9-ab14-d663bd873d93";
		String DRUG_REGIMEN_EDITOR_FORM = "da687480-e197-11e8-9f32-f2801f1b9fd1";


		String ARV_TREATMENT_PLAN_EVENT_CONCEPT = "1255AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String TB_TREATMENT_PLAN_CONCEPT = "1268AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		/*List<SimpleObject> history = new ArrayList<SimpleObject>();
		String categoryConceptUuid = category.equals("ARV")? ARV_TREATMENT_PLAN_EVENT_CONCEPT : TB_TREATMENT_PLAN_CONCEPT;*/

		EncounterType et = encounterService.getEncounterTypeByUuid(DRUG_REGIMEN_EDITOR_ENCOUNTER);
		Form form = formService.getFormByUuid(DRUG_REGIMEN_EDITOR_FORM);
		String dateFormat = "dd/MM/yyyy";

		boolean start = true;
		int counter = 0;
		int dataRowNum = 4;

		for (Row row : sheet) {

			if (counter < dataRowNum) {
				counter++;
				continue;
			}

			counter++;

			if (start) {
				start = false;
				continue;
			}


			//Entry ID	Name	Unique Patient Number	DOB	Sex	Regimen	Date started on Regimen	Regimen Line

			int colEntryId = 0;//test lab name
			int colPatientName = 1;//specimen id
			int colUniquePatientNumber = 2;
			int colRegimen = 5;
			int colDateStartedOnRegimen = 6;
			int colRegimenLine = 7;

			DataFormatter formatter = new DataFormatter();
			String encounterId = formatter.formatCellValue(row.getCell(colEntryId));
			String patientName = formatter.formatCellValue(row.getCell(colPatientName));
			String uniquePatientNumber = formatter.formatCellValue(row.getCell(colUniquePatientNumber));
			String regimenName = formatter.formatCellValue(row.getCell(colRegimen));
			String regimenLine = formatter.formatCellValue(row.getCell(colRegimenLine));

			String regimenDate = formatter.formatCellValue(row.getCell(colDateStartedOnRegimen));
			regimenDate = regimenDate.replace(".", "/");
			uniquePatientNumber = uniquePatientNumber.replace("'", "");

			if (org.apache.commons.lang3.StringUtils.isBlank(regimenLine)) {

				System.out.print("Skipping this row. It has empty result ");
				System.out.print("Patient Name, Regimen date, regimen: ");
				System.out.println(patientName + " ," + regimenDate + ", " + regimenName );
				continue;
			}
			Patient p = checkIfPatientExists(uniquePatientNumber);
			if (p == null) {
				System.out.println("A patient with identifier " + uniquePatientNumber + " does not exists. Skipping this row");
				continue;
			} else {
				System.out.println("A patient with identifier " + uniquePatientNumber + " exists. Processing the row");
			}
			Date regimenEventDate = null;
			List<String> dateFormats = new ArrayList<String>();

			//dateFormats.add("dd-MM-yyyy");
			//dateFormats.add("dd/MM/yyyy");
			//dateFormats.add("dd-MMM-yyyy");

			/*for (String format : dateFormats) {
				try {
					regimenEventDate = new SimpleDateFormat(format).parse(regimenDate);
					break;
				} catch (ParseException e) {

				}
			}*/

			try {
				regimenEventDate = new SimpleDateFormat(dateFormat).parse(regimenDate);
			} catch (ParseException e) {

			}

			if (regimenDate == null) {
				System.out.println("Could not convert the regimen date. Skipping processing of the row");
				continue;
			}

			System.out.print("Patient Name, Regimen date, regimen: ");
			System.out.println(patientName + " ," + regimenDate + ", " + regimenName );

			Encounter regimenEventEncounter = getEncounterOnDate(et, form, p, regimenEventDate);

			if (regimenEventEncounter != null) {
				// compose the regimen line obs and add it to the encounter
				// create obs for regimen line

				System.out.println("Found an encounter on " + regimenEventDate);

				String regimenLineToSave = null;
				if (regimenLine.equals("Adult First line")) {
					regimenLineToSave = "AF";
				} else if (regimenLine.equals("Adult Second line")) {
					regimenLineToSave = "AS";
				} else if (regimenLine.equals("Adult Third line")) {
					regimenLineToSave = "AT";
				} else if (regimenLine.equals("Child First line")) {
					regimenLineToSave = "CF";
				} else if (regimenLine.equals("Child Second line")) {
					regimenLineToSave = "CS";
				} else if (regimenLine.equals("Child Third line")) {
					regimenLineToSave = "CT";
				}
				/**
				 * Adult First line
				 Adult Second line
				 Adult Third line
				 Child First line
				 Child Second line
				 Child Third line
				 */

				Obs regimenLineObs = new Obs();
				regimenLineObs.setConcept(conceptService.getConcept(163104)); // regimen line concept should be changed to correct one
				regimenLineObs.setDateCreated(new Date());
				regimenLineObs.setCreator(Context.getAuthenticatedUser());
				regimenLineObs.setObsDatetime(regimenEventEncounter.getEncounterDatetime());
				regimenLineObs.setValueText(regimenLineToSave);
				regimenLineObs.setPerson(p);

				regimenEventEncounter.addObs(regimenLineObs);
				encounterService.saveEncounter(regimenEventEncounter);
				System.out.println("successfully updated an encounter");

			}

			if (counter % 200 == 0) {
				Context.flushSession();
				Context.clearSession();

			}
		}
	}


	/**
	 * Imports an excel with viral load results from the lab system (EID/VL lab system)
	 * @param sheet
	 */
	public static void importViralLoadResults(Sheet sheet) {

		String dateFormat = "d-MMM-yy";// As provided in the results excel document

		boolean start = true;
		int counter = 0;

		for (Row row : sheet) {

			counter++;

			if (start) {
				start = false;
				continue;
			}

			// define indexes for the columns. element at position 0 is the entry id
			int colUniquePatientNumber = 1;
			int colDateVlOrdered = 6;
			int colVlResult = 7;

			DataFormatter formatter = new DataFormatter();
			String uniquePatientNumber = formatter.formatCellValue(row.getCell(colUniquePatientNumber));
			String vlResult = formatter.formatCellValue(row.getCell(colVlResult));

			String dateVlOrdered = formatter.formatCellValue(row.getCell(colDateVlOrdered));

			if (org.apache.commons.lang3.StringUtils.isBlank(vlResult)) {

				System.out.print("Skipping this row. It has empty result ");
				continue;
			}
			// check if patient really exists in the database
			Patient p = checkIfPatientExists(uniquePatientNumber);
			if (p == null) {
				System.out.println("A patient with identifier " + uniquePatientNumber + " does not exists. Skipping this row");
				continue;
			} else {
				System.out.println("A patient with identifier " + uniquePatientNumber + " exists. Processing the row");
			}
			// get vl order date
			Date vlOrderDate = null;

			try {
				vlOrderDate = new SimpleDateFormat(dateFormat).parse(dateVlOrdered);
			} catch (ParseException e) {

			}

			if (vlOrderDate == null) {
				System.out.println("Could not convert the vl order date. Skipping processing of the row " + dateVlOrdered);
				continue;
			}

			if (p != null && vlOrderDate != null && org.apache.commons.lang3.StringUtils.isNotBlank(vlResult)) {
				System.out.println("This row will process " );

				updateOrder(p, vlOrderDate, vlResult);
				if (counter % 200 == 0) {
					Context.flushSession();
					Context.clearSession();
				}
			} else {
				System.out.println("This row won't process " );

			}
		}
	}

	/**
	 * Updates an active order and sets results if provided
	 * @param patient
	 * @param vlOrderDate
	 * @param result
	 */
	public static void updateOrder(Patient patient, Date vlOrderDate, String result) {

		Map<String, Order> ordersToProcess = getOrdersToProcess(patient, labType, null, vlOrderDate, vlTestConceptQuantitative);
		Order orderToRetain = ordersToProcess.get("orderToRetain");
		Order orderToVoid = ordersToProcess.get("orderToVoid");

		Date orderDiscontinuationDate = aMomentBefore(new Date());

		if (ordersToProcess != null && ordersToProcess.size() > 0) {
			if (org.apache.commons.lang3.StringUtils.isNotBlank(result)) {

				if (orderToRetain == null || orderToVoid == null) { // just skip if any of the orders is null
					System.out.println("There is a null order in the package. This may be due to manual update by a provider or data clerk!");
					return;
				} else if (!orderToRetain.isActive() || !orderToVoid.isActive()) { // just skip if any of the orders is not active
					System.out.println("There is no order to key in results for. This may have been done manually by a provider or data clerk!");
					return;
				}

				Concept conceptToRetain = null;
				String lDLResult = "0.01";
				Obs o = new Obs();

				if (result.equals(lDLResult)) {
					conceptToRetain = vlTestConceptQualitative;
					o.setValueCoded(LDLConcept);
				} else {
					conceptToRetain = vlTestConceptQuantitative;
					o.setValueNumeric(Double.valueOf(result));
				}

				// In order to record results both qualitative (LDL) and quantitative,
				// every vl request saves two orders: one with 856(quantitative) for numeric values and another with 1305(quantitative) for LDL value
				// When recording result, it is therefore prudent to set result for one order and void the other one

				// logic that picks the right concept id for the result obs
				o.setConcept(conceptToRetain);
				o.setDateCreated(orderDiscontinuationDate);
				o.setCreator(Context.getUserService().getUser(1));
				o.setObsDatetime(orderToRetain.getDateActivated());
				o.setPerson(patient);
				o.setOrder(orderToRetain);

				Encounter enc = new Encounter();
				enc.setEncounterType(labEncounterType);
				enc.setEncounterDatetime(orderDiscontinuationDate);
				enc.setPatient(patient);
				enc.setCreator(Context.getUserService().getUser(1));

				enc.addObs(o);
				if (orderToRetain != null && orderToVoid != null && orderToRetain.isActive()) {

					try {

						Context.getEncounterService().saveEncounter(enc);
						Context.getOrderService().discontinueOrder(orderToRetain, "Results received", orderDiscontinuationDate, orderToRetain.getOrderer(),
								orderToRetain.getEncounter());
						Context.getOrderService().voidOrder(orderToVoid, "Duplicate VL order");
						System.out.println("Results updated successfully");

						//check if there is already a lab result form updated with the same vl result. if found, void it plus the encounter

						Encounter labResultsEncounter = getEncounterOnDate(labResultEncounterType, labResultForm, patient, vlOrderDate);
						if (labResultsEncounter != null) {
							//check to see if only vl was updated
							if (labResultsEncounter.getObs().size() == 0) { // void both obs and encounter
								for (Obs obs : labResultsEncounter.getObs()) {
									obs.setVoided(true);
									obs.setVoidReason("A duplicate obs has been created");
									obs.setDateVoided(new Date());
									Context.getObsService().saveObs(obs, "A duplicate obs has been created");
								}
								labResultsEncounter.setVoided(true);
								labResultsEncounter.setVoidReason("A duplicate encounter has been created");
								labResultsEncounter.setDateVoided(new Date());
								Context.getEncounterService().saveEncounter(labResultsEncounter);
							} else { // void only the vl obs
								for (Obs obs : labResultsEncounter.getObs()) {
									if (obs.getConcept().equals(conceptToRetain)) {
										obs.setVoided(true);
										obs.setVoidReason("A duplicate obs has been created");
										obs.setDateVoided(new Date());
										Context.getObsService().saveObs(obs, "A duplicate obs has been created");
										break;
									}
								}
							}
						}

						// this is really a hack to ensure that order date_stopped is filled, otherwise the order will remain active
						// the issue here is that even though disc order is created, the original order is not stopped
						// an alternative is to discontinue this order via REST which works well
					} catch (Exception e) {
						System.out.println("An error was encountered while updating orders for viral load");
						e.printStackTrace();
					}
				}
			}
		} else {
			System.out.println("There are no VL lab requests on the results date as indicated in the imported VL results document");
		}

	}

	/**
	 * Borrowed from OpenMRS core
	 * To support MySQL datetime values (which are only precise to the second) we subtract one
	 * second. Eventually we may move this method and enhance it to subtract the smallest moment the
	 * underlying database will represent.
	 *
	 * @param date
	 * @return one moment before date
	 */
	private static Date aMomentBefore(Date date) {
		return DateUtils.addSeconds(date, -1);
	}

	/**
	 * Returns an object indicating the order to retain and that to void
	 * @param patient
	 * @param orderType
	 * @param careSetting
	 * @param orderDate
	 * @param conceptToRetain
	 * @return
	 */
	private static Map<String, Order> getOrdersToProcess(Patient patient, OrderType orderType, CareSetting careSetting, Date orderDate, Concept conceptToRetain) {

		Map<String, Order> listToProcess = new HashMap<String, Order>();
		Concept conceptToVoid = conceptToRetain.equals(vlTestConceptQualitative) ? vlTestConceptQuantitative : vlTestConceptQualitative;
		List<Order> ordersOnSameDay = Context.getOrderService().getActiveOrders(patient, orderType, careSetting, orderDate);

		for (Order order : ordersOnSameDay) {
			if (order.getConcept().equals(conceptToVoid)) {
				listToProcess.put("orderToVoid", order);
			} else if (order.getConcept().equals(conceptToRetain)) {
				listToProcess.put("orderToRetain", order);
			}
		}
		return listToProcess;
	}

	/**
	 * Checks if a patient already has encounter of same type and form on same date
	 * @param enctype
	 * @param form
	 * @param patient
	 * @param date
	 * @return
	 */
	public static boolean hasEncounterOnDate(EncounterType enctype, Form form, Patient patient, Date date) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, date, date, Collections.singleton(form), Collections.singleton(enctype), null, null, null, false);
		return encounters.size() > 0;

	}

	/**
	 * Checks if a patient already has encounter of same type and form on same date
	 * @param enctype
	 * @param form
	 * @param patient
	 * @param date
	 * @return the encounter on the request date
	 */
	public static Encounter getEncounterOnDate(EncounterType enctype, Form form, Patient patient, Date date) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, date, date, Collections.singleton(form), Collections.singleton(enctype), null, null, null, false);
		if (encounters.size() > 0) {
			return encounters.get(0); // just return the first
		}
		return null;

	}

	/**
	 * Checks if a patient already has encounter of same type and form on same date
	 * @param enctype
	 * @param patient
	 * @param date
	 * @return the encounter on the request date
	 */
	public static Encounter getEncounterOnDate(EncounterType enctype, Patient patient, Date date) {
		List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, date, date, null, Collections.singleton(enctype), null, null, null, false);
		if (encounters.size() > 0) {
			return encounters.get(0); // just return the first
		}
		return null;

	}
}
