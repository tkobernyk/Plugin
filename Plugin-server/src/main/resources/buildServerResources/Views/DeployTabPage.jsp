<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
    .error { color: red; }
</style>
<table class="deploy-runner form-group">
    <tr class="row borderBottom">
        <td style="padding: 5px">Build Id:</td>
        <td style="padding: 5px">
            <span id="buildId" class="label label-default">${buildId}</span>
        </td>
    </tr>
    <tr>
        <td style="padding: 5px">Project Name:</td>
        <td style="padding: 5px">
            <span id="projectName" class="label label-default">${projectName}</span>
        </td>
    </tr>
    <tr>
        <td style="padding: 5px">Phase #:</td>
        <td style="padding: 5px">
            <span id="Phase" class="label label-default">${phase}</span>
        </td>
    </tr>
    <tr>
        <td style="padding: 5px" class="col-xs-6" colspan="2">
            <select id="environment" class="selectpicker form-control">
                <optgroup label="Enviroment">
                    <c:if test="${not empty environments}">
                        <c:forEach var="environment" items="${environments}">
                            <option value="${environment}">${environment}</option>
                         </c:forEach>
                    </c:if>
                </optgroup>
            </select>
        </td>
    </tr>
    <tr>
        <td style="padding: 5px" class="col-xs-6" colspan="2">
            <input type="button" ${status} class="btn btn-default" id="btnDeploy" value="deploy" />
        </td>
    </tr>
</table>
<div id="output">

</div>
<script>
jQuery( document ).ready(function() {
    console.log( "ready!" );
    var data = {
         BuildId: jQuery('#buildId').text(),
         ProjectName: jQuery('#projectName').text(),
         Environment: jQuery('#environment option:selected').text(),
         Phase: jQuery('#Phase').text()
     }

    jQuery("#btnDeploy").on("click", function(event) {
        triggerMessage();
        BS.ajaxRequest(window['base_uri'] + '/deploy/run.html', {
              method: 'post',
              parameters: jQuery.param(data),
              onComplete: function (response) {
                console.log("/deploy/run.html:onComplete()");
              }
         });
         jQuery("#btnDeploy").prop("disabled", true );
    });
});
</script>
<script type="text/javascript">
function triggerMessage(){
    var poller = BS.periodicalExecutor(function () {
    var result = $j.Deferred();
    console.log("triggerMessage()");
    BS.ajaxRequest(window['base_uri'] + '/messages/getMessage.html', {
      method: 'post',
      onComplete: function (response) {
         console.log("/messages/getMessage.html:onComplete()");
         if(response.responseText !== "")
         {
            jQuery("#output").append("<div>" + response.responseText + "</div>");
            jQuery("html, body").animate({ scrollTop: jQuery(document).height() }, 1000);
         }
         if (response && response.status != 200) {
           BS.ServerLink.waitUntilServerIsAvailable(BS.SubscriptionManager.start);
           poller.stop();
           poller = null;
           jQuery("#btnDeploy").prop("disabled", false )
           return;
         }
         var messages = response.responseText.trim();
         result.resolve();
      }
    });
    return result.promise();
    }, BS.internalProperty('teamcity.ui.pollInterval') * 250);
    poller.start();
}

</script>