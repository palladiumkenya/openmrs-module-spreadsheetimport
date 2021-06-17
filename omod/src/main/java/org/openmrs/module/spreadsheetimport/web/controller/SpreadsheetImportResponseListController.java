package org.openmrs.module.spreadsheetimport.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/module/spreadsheetimport/spreadsheetimport.response")
public class SpreadsheetImportResponseListController<E> {

	/**
	 * Logger for this class
	 */
	protected final Log log = LogFactory.getLog(getClass());

	public SpreadsheetImportResponseListController() {
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String listTemplates(ModelMap model) {
		model.addAttribute("serverResponse", "Document successfully processed");
		return "/module/spreadsheetimport/spreadsheetimportResponseList";
	}

	
}
