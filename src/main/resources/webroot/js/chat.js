$(function () {

    if (!window.WebSocket) {
        window.WebSocket = window.MozWebSocket;
    }

    if (window.WebSocket) {
        var socket = new WebSocket("ws://localhost:8080/im");
        socket.onmessage = function (e) {
            console.log("获取消息:" + e.data);
        }
        socket.onopen = function () {
            console.log("服务器建立连接");
            socket.send("你好");
        }
        socket.onclose = function () {
            console.log("服务器连接关闭");
        }
    } else {
        alert("浏览器不支持websocket")
    }
})