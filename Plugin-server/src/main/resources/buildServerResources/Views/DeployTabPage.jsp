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
    jQuery('#btnDeploy').on('click', function() {
        /*var deploy = new Object();
        deploy.buildId = jQuery('#buildId').text();
        deploy.projectName = jQuery('#projectName').text();
        deploy.environment = jQuery("#environment option:selected").text()*/
        var dataRequestObject = {
            BuildId:jQuery('#buildId').text(),
            ProjectName:jQuery('#projectName').text(),
            Environment:jQuery("#environment option:selected").text(),
            Phase:jQuery('#Phase').text()
        };
        jQuery.ajax({
          url: "/deploy/run.html",
          type: "POST",
          data: dataRequestObject,
          success: function(data,status) {
            console.log(status);
          },
          error: function(error,status,xhr){
            console.log(status);
          }
        });
        getMessage();
    });
</script>
<script type="text/javascript">

  function getMessage(){
    var request = { url: window['base_uri'] + '/websocketHandler',
            contentType : "application/json",
            transport : 'websocket'};

        request.onOpen = function(response) {
           alert('Hello');
        };

        request.onMessage = function (response) {
            var message = response.responseBody;
            jQuery('#output').html(message);
        }
        var subSocket = atmosphere.subscribe(request);
   }

</script>