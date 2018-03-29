<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/include.jsp"%>
<%--<bs:linkCSS dynamic="${true}">
  ${teamcityPluginResourcesPath}Styles/bootstrap.min.css
</bs:linkCSS>--%>
<form action="${actionUrl}" id="frmSettingsForm" method="POST">
    <table>
        <tr>
            <td>
                <span id="powershallpath" class="label label-default">Powershell script path: </span>
            </td>
            <td>
                <input type="text" style="min-width:500px; width:500px;" id="txtPSScriptPath" value="${powershellpath}" placeholder="PowerShell script path" />
            </td>
        </tr>
            <c:choose>
                <c:when test="${not empty environments}">
                    <c:forEach var="environment" items="${environments}">
                        <tr>
                            <td>
                                <span id="environment" class="label label-default">Environment: </span>
                            </td>
                            <td>
                                <input type="text" id="txtEnvironment" value="${environment}" placeholder="Environment" />
                            </td>
                        <tr>
                    </c:forEach>
                </c:when>
            </c:choose>
        <tr>
            <td colspan="2">
                <input type="button" class="btn btn-default" id="btnSave" value="Save" />
            </td>
        </tr>
    </table>
</form>