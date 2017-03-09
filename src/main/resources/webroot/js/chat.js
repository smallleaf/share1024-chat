$(function () {


    var host = location.href.replace(/http:\/\//i, "");


    window.CHAT = {
        serverAddr: "ws://" + host + "im",

        socket: null,
        nickname: null,
        //登录
        login: function () {
            $("#error-msg").empty();
            var _reg = /^\S{1,10}/;
            var nickname = $("#nickname").val();
            if (!_reg.test($.trim(nickname))) {
                $("#error-msg").html("昵称必须在10个字以内");
                return false;
            }
            $("#nickname").val("");
            $("#loginbox").hide();
            $("#chatbox").show();
            this.init(nickname);

        },

        //登出
        logout: function () {
            location.reload();
        },

        //发送消息
        sendText: function () {

        },

        //初始化
        init: function (nickname) {

            //初始化
            CHAT.nickname = nickname;
            $("#shownikcname").html(nickname);

            var message = $("#send-message");

            // 自动获得焦点
            message.focus();
            //message.KEYDOWN(function(e){
            //    if((e.ctrlKey && e.which == 13) || e.which == 10){
            //        CHAT.sendText();
            //    }
            //});

            //消息添加到聊天面板
            //专门处理服务端发来的消息
            var appendTopanel = function (msg) {
                //用正则来解析自定义协议
                var regx = /^\[(.*)\](\s\-\s(.*))?/g;
                var group = '', header = "", content = "", cmd = "", time = 0, sender = "";
                while (group = regx.exec(msg)) {
                    header = group[1];
                    content = group[3];
                }
                //alert(header + "," + content);
                var headers = header.split("][");
                cmd = headers[0];
                time = headers[1];
                sender = headers[2];

                if (cmd == "SYSTEM") {
                    var online = headers[2];
                    $("#onlinecount").html("" + online);

                    addSystemTip(content);
                }
            }

            //添加系统提示
            //动态创建一个HTML元素
            var addSystemTip = function (c) {
                var html = "";
                html += '<div class="msg-system">';
                html += c;
                html += '</div>';
                var section = document.createElement('section');
                section.className = 'system J-mjrlinkWrap J-cutMsg';
                section.innerHTML = html;

                $("#onlinemsg").append(section);
            };

            if (!window.WebSocket) {
                window.WebSocket = window.MozWebSocket;
            }

            if (window.WebSocket) {
                CHAT.socket = new WebSocket(CHAT.serverAddr);
                CHAT.socket.onmessage = function (e) {
                    appendTopanel(e.data);
                    //console.log("获取消息:" + e.data);
                }
                CHAT.socket.onopen = function () {
                    CHAT.socket.send("[LOGIN][" + new Date().getTime() + "][" + CHAT.nickname + "]");
                    //console.log("服务器建立连接");
                    //socket.send("[LOGIN][122112][xiaoyezi]");
                }
                CHAT.socket.onclose = function () {
                    console.log("服务器连接关闭");
                }
            } else {
                alert("浏览器不支持websocket")
            }
        }
    }

})