
import urllib.request

def get_price(code):
        url = 'http://hq.sinajs.cn/?list=%s' % code
        req = urllib.request.Request(url)
#�������Ҫ���ô��������set_proxy�Ͳ��õ����ˡ����ڹ�˾����Ҫ���������������������������set_proxy...
        req.set_proxy('proxy.XXX.com:911', 'http')
        content = urllib.request.urlopen(req).read()
        str = content.decode('gbk')
        data = str.split('"')[1].split(',')
        name = "%-6s" % data[0]
        price_current = "%-6s" % float(data[3])
        change_percent = ( float(data[3]) - float(data[2]) )*100 / float(data[2])
        change_percent = "%-6s" % round (change_percent, 2)
        print("��Ʊ����:{0} �ǵ���:{1} ���¼�:{2}".format(name, change_percent, price_current) )

def get_all_price(code_list):
    for code in code_list:
        get_price(code)

code_list = ['sz300036', 'sz000977', 'sh600718', 'sh600452', 'sh600489']
get_all_price(code_list)
