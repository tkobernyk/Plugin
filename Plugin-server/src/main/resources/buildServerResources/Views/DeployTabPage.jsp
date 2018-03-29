<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
            //jQuery('#output').html(data);
            connect();
            s
          },
          error: function(error,status,xhr){
            console.log(status);
          }
        });
    });
</script>
<script type="text/javascript">
    var stompClient = null;

    function connect() {
        var socket = new SockJS('/deploy/run/chat.html');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function(frame) {
            setConnected(true);
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/messages', function(data) {
                showMessageOutput(JSON.parse(data.body));
            });
        });
    }

    function disconnect() {
        if(stompClient != null) {
            stompClient.disconnect();
        }
        setConnected(false);
        console.log("Disconnected");
    }

    function showMessageOutput(data) {
        jQuery('#output').html(data);
    }
</script>