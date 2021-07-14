<%--
  The contents of this file are subject to the OpenMRS Public License
  Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
  http://license.openmrs.org

  Software distributed under the License is distributed on an "AS IS"
  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
  License for the specific language governing rights and limitations
  under the License.

  Copyright (C) OpenMRS, LLC.  All Rights Reserved.

--%>
<%@ include file="/WEB-INF/view/module/legacyui/template/include.jsp" %>
<openmrs:require privilege="Import Spreadsheet Import Templates" otherwise="/login.htm" redirect="/module/spreadsheetimport/spreadsheetimportImport.form"/>
<%@ include file="/WEB-INF/view/module/legacyui/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>
<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<form method="post" enctype="multipart/form-data">
    <form:errors path="*" cssClass="error"/>
	<input type="hidden" name="id" value="${template.id}"/> <br/>
	<b>${template.name}</b><br />
	Spreadsheet to upload: <input type="file" name="file" /> <br/>
	Sheet: <input type="text" name="sheet" value="Sheet1"/> <br/>
<!-- BEGIN: FOR TESTING ONLY -->
	<input type="checkbox" name="rollbackTransaction"/> Rollback transaction <br/>
<!-- END: FOR TESTING ONLY -->
	<input type="submit" value="Upload"/>
</form>

<%@ include file="/WEB-INF/view/module/legacyui/template/footer.jsp"%>
