//document.write("<link rel='stylesheet' href='/js/toastmessage/css/jquery.toastmessage.css'/>");
document.write("<script src='/js/websocket/sockjs-0.3.4.js'></script>");
document.write("<script src='/js/websocket/stomp.js'></script>");
//document.write("<script src='/js/toastmessage/jquery.toastmessage.js'></script>");

var stompClient = null;
/**
 * 连接
 * @param url 连接地址
 * @param destination 反馈地址
 */
function connect(url, destination) {
  var socket = new SockJS(url);
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    stompClient.subscribe(destination, function (result) {
     /* var data = eval("(" + $.parseJSON(result.body) + ")");
      $().toastmessage('showToast', {
        text: data.title,
        sticky: false,
        position: 'bottom-right',
        stayTime: 5000,
        type: 'notice'
      });

      $.ajax({
        url: "/count/unreaded",
        type: "GET",
        dataType: "json",
        contentType: "application/json",
        success: function (result) {
          $("#noticeNumber").text(result == null ? 0 : result);
        }
      });*/
    });
  });
}

/**
 * 断开连接
 */
function disconnect() {
  if (stompClient != null) {
    stompClient.disconnect();
  }
}

function sendNotice(type, branchToId, title, content) {
  stompClient.send("/notice/broadcast", {}, JSON.stringify({
    'type': type,
    'title': title,
    'branchToId': branchToId,
    'content': content
  }));
}


