import os
import shutil
import re
import search
import requests
import chardet

def init():
    if (os.path.exists('code')):
        shutil.rmtree('code')
    os.makedirs('code')
    if (os.path.exists('badcode')):
        shutil.rmtree('badcode')
    os.makedirs('badcode')


def processquery(query):
    query.lower()
    query = re.sub(r"how\sto\s","",query)
    query = re.sub(r"how\scan\si","",query)
    query = re.sub(r"\s\s+"," ",query);
    return query;

def getunique(inlist):
    S={}
    anslist=[]
    for i in inlist:
        if (not i in S):
            S[i]=1
            anslist.append(i)
    return anslist

def getsearch(query):
    #print("Enter a query for JDK APIs")
    #print("Some examples: rename a file; save an image to a file; convert string to int ...")
    print("Your query is "+query)
    print("Preparing for searching...")
    query=processquery(query)
    url="http://211.249.63.55:86/deepapi/?ignore_unk=0&source="+query
    s = requests.session()
    html=search.gethtml(s,url)
    html = html.decode(chardet.detect(html)['encoding'])

    #print html
    pattern = r"\(u'([^)]+)\)"
    getmatch = re.compile(pattern)
    result = re.findall(getmatch,html)
    #print result

    if (len(result)==0):
        raise Exception("Serach Failed on DeepAPI")

    return result

def gettokens(ansstr):
    ansstr=re.sub(r"<init>","new",ansstr)
    print(ansstr)

    pattern = r"[a-zA-Z0-9<>]\.([a-zA-Z<>0-9]*[A-Za-z][a-zA-Z0-9<>]*)"
    getmatch = re.compile(pattern)
    result = re.findall(getmatch,ansstr)

    ans=[]
    ans.append("+.".join(result))
    result = getunique(result)
    ans.append("+.".join(result))
    for i in range(len(ans)):
        ans[i]='.'+ans[i]
    #print ansstr
    return ans

