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
<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="List Spreadsheet Import Templates" otherwise="/login.htm" redirect="/module/spreadsheetimport/spreadsheetimport.list"/>
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<h2>Spreadsheet Import Template</h2>

<p />

<div style="text-align: center;font-size: 18px">
	The file was processed successfully
</div>

<%@ include file="/WEB-INF/template/footer.jsp" %>