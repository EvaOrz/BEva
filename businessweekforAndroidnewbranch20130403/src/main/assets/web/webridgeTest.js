
function queryLoginStatus()
{

    webridge.jsToNative('queryLoginStatus',null,function(result, error) {
    var person = result;
     var html = '<p>name: ' + person.eva + '</p>';
	                                html += '<p>phone: ' + person.eva + '</p>';
	                                document.getElementById('person').innerHTML = html;
                        alert(JSON.stringify({'result':result,'error':error}));
                        }
                        );
}

function login()
{
 alert('login');
    webridge.jsToNative('login',null,function(result, error) {
//                        alert(JSON.stringify({'result':result,'error':error}));
                        }
                        );
}

function share()
{
    webridge.jsToNative('share',{ 'content': '分享内容','link': '分享链接','thumb': '分享图标' },function(result, error) {
                        alert(JSON.stringify({'result':result,'error':error}));
                        }
                        );
}

function bindMobile()
{
    webridge.jsToNative('bind',{'type':'mobile'},function(result, error) {
                        alert(JSON.stringify({'result':result,'error':error}));
                        }
                        );
}

function bindEmail()
{
    webridge.jsToNative('bind',{'type':'email'},function(result, error) {
                        alert(JSON.stringify({'result':result,'error':error}));
                        }
                        );
}

function bindWeibo()
{
    webridge.jsToNative('bind',{'type':'weibo'},function(result, error) {
                        alert(JSON.stringify({'result':result,'error':error}));
                        }
                        );
}

function bindWeixin()
{
    webridge.jsToNative('bind',{'type':'weixin'},function(result, error) {
                        alert(JSON.stringify({'result':result,'error':error}));
                        }
                        );
}

function bindQQ()
{
    webridge.jsToNative('bind',{'type':'qq'},function(result, error) {
                        alert(JSON.stringify({'result':result,'error':error}));
                        }
                        );
}

function evalJS(command)
{
    webridge.jsToNative('evalJS',command,null);
}
