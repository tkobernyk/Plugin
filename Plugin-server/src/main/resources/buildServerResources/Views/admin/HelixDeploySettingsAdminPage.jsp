<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/include.jsp"%>
<%--<bs:linkCSS dynamic="${true}">
  ${teamcityPluginResourcesPath}Styles/bootstrap.min.css
</bs:linkCSS>--%>
<form action="/saveHelixDeploySettings" id="frmSettingsForm" method="POST">
    <table>
        <tr>
            <td>
                <span id="powershallpath" class="label label-default">Powershell script path: </span>
            </td>
            <td>
                <input type="text" style="min-width:500px; width:500px;" id="txtPSScriptPath" value="${powershellpath}" placeholder="PowerShell script path" />
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <input type="button" class="btn btn-default" id="btnSave" value="Save" />
            </td>
        </tr>
    </table>
</form>
<script>
jQuery( "#btnSave" ).on( "click", function() {
  var data = {powerShellScriptPath: jQuery("#txtPSScriptPath").val()}
  BS.ajaxRequest(window['base_uri'] + '/saveHelixDeploySettings.html', {
                method: 'post',
                parameters: jQuery.param(data),
                contentType: 'application/json',
                onComplete: function (response) {
                  console.log("done");
                }
           });
  console.log( jQuery(this).serialize() );
});
</script>
