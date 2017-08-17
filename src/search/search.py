import requests
import re
import configparser
import os
import chardet
import sys

print (os.getcwd())
os.chdir(sys.path[0])
print (os.getcwd())
cp = configparser.SafeConfigParser()
cp.read('configure.conf')
trytime = int(cp.get('my','trytime'))
trypage = int(cp.get('my','trypage'))
minlen = int(cp.get('my','minlen'))
maxlen = int(cp.get('my','maxlen'))
classlim = int(cp.get('my','classlim'))
functionlim = int(cp.get('my','functionlim'))
username = cp.get('my','username')
password = cp.get('my','password')


def login():
    url = 'https://github.com/login'
    head = {
        "user-agent":"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36",
    }
    s = requests.session()
    r = s.get(url,headers=head)
    inside = r.content
    inside = inside.decode(chardet.detect(inside)['encoding'])
    token = re.findall('<input name="authenticity_token" type="hidden" value="(.*?)" />', inside, re.S)
    payload = {
        'commit':'Sign in',
        'utf8':'%E2%9C%93',
        'authenticity_token':token[0],
        'login':username,
        'password':password
    }
    s.post('https://github.com/session',headers=head,data=payload)
    return s

def checksearch(html):
    #print(chardet.detect(html))
    try:
        html = html.decode(chardet.detect(html)['encoding'])
    except:
        print("Decode failed")
        return False
    pattern = "We could not perform this search|Whoa"
    check = re.compile(pattern)
    result = re.findall(check,html)
    if len(result) == 0:
        return True
    else:
        return False


def gethtmlpre(s,url):
    for t in range(trytime):
        page = s.get(url)
        html = page.content
        if checksearch(html):
            return html
    return ""


def gethtml(s,url):
    for t in range(trytime):
        page = s.get(url)
        html = page.content
        if checksearch(html):
            return html
    raise Exception("Search Failed")


def gethtmleasy(s,url):
    #print url
    for t in range(trytime):
        page = s.get(url)
        html = page.content
        if checksearch(html):
            return html,True
    return "",False


def getcodelist(s,url):
    html,flag = gethtmleasy(s,url)
    if len(html)==0:
        return []
    html = html.decode(chardet.detect(html)['encoding'])
    pattern = r"<a href=\"([\w/\-\.\%]+)\" title=\""
    getmatch = re.compile(pattern)
    result = re.findall(getmatch,html)
    #print(len(result))
    #if len(result)==0:
    #    print(html)
    return result

def getlist(s,url):
    ans = []
    for t in range(trypage):
        print("Getting codes from page "+str(t+1))
        ans.extend(getcodelist(s, url+"&p="+str(t+1)))
        #print(len(ans))
    return ans

def qualify(code):
    if (len(code)<minlen or len(code)>maxlen):
        return False
    """
    pattern = " class "
    getmatch = re.compile(pattern)
    result = re.findall(getmatch,code)
    if (len(result) > classlim):
        return False
    """
    outfile=open("Check.java","w",encoding='utf-8');
    #print(code)
    outfile.write(code)
    outfile.close()
    os.system('java -jar CheckerDemo.jar')
    readans=open("Check.out","r")
    ans=int(readans.read())
    if ans==1:
        return True
    else:
        return False

def getnewstr(s):
    return s.replace("/blob","");

def getcode(s,codename):
    url = "https://raw.githubusercontent.com"+getnewstr(codename)
    html,flag = gethtmleasy(s,url)
    #(chardet.detect(html))
    if flag==False:
        return ""
    html = html.decode(chardet.detect(html)['encoding'])
    return html