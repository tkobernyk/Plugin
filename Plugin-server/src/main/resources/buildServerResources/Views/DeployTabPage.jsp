<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="/plugins/Helix-Deploy.Plugin/Scripts/atmosphere.js"></script>
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
    getMessage();
});
</script>
<script type="text/javascript">
       function getMessage()
       {
           jQuery('#btnDeploy')
               .on('click', function ()
               {
                   var socket = atmosphere;
                   var data = {
                         BuildId: jQuery('#buildId').text(),
                         ProjectName: jQuery('#projectName').text(),
                         Environment: jQuery('#environment option:selected').text(),
                         Phase: jQuery('#Phase').text()
                     }
                   var request = {
                       url: window['base_uri'] + '/deploy/run.html?' +atmosphere.util.param(data),
                       contentType: "application/json",
                       logLevel: 'debug',
                       transport: 'sse',
                       method: "GET",
                       trackMessageLength: true,
                       reconnectInterval: 5000
                   };
                   request.onOpen = function (response)
                   {
                       console.log("onOpen");
                   };
                   request.onClientTimeout = function (r)
                   {
                       subSocket.push("request timeout");

                   };
                   request.onReopen = function (response)
                   {
                       console.log('reopened');
                   };
                   request.onTransportFailure = function (errorMsg, request)
                   {
                       console.log("onTransportFailure");
                   };
                   request.onMessage = function (response)
                   {
                       console.log("on message");
                       var message = response.responseBody;
                       jQuery('#output')
                           .append('<p>' + message + '</p>');
                   };
                   request.onClose = function (response)
                   {
                       console.log('onClose');
                   };
                   request.onError = function (response)
                   {
                       console.log('error');
                   };
                   subSocket = socket.subscribe(request);
               });
       };
</script>