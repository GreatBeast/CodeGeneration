import search
import sys
import init

if len(sys.argv)<2:
    raise Exception("Query is empty")

num=int(sys.argv[1])

Input=open("tokens.txt","r")
token=""
for i in range(num):
    token=Input.readline()

tokens=init.gettokens(token)

print("Search token is "+tokens[0])
otp=open("api.txt", "w", encoding='utf-8')
otp.write(tokens[0])
otp.close()

s=search.login()
url = "https://github.com/search?l=Java&type=Code&utf8=%E2%9C%93&q="+tokens[0]

print("Searching on github")
html = search.gethtmlpre(s,url)

if (html==""):
    print("Search Filed, try to use token "+tokens[1])
    url = "https://github.com/search?l=Java&type=Code&utf8=%E2%9C%93&q="+tokens[1]
    html = search.gethtml(s,url)


ans = search.getlist(s,url)
print(len(ans))

codeId = 0
badcodeId =0
print("Downloading the code snippets")
for i in ans:
    code = search.getcode(s,i)
    if len(code)==0:
        continue
    if search.qualify(code):
        codeId += 1
        outputcode = open("code\\"+str(codeId)+".java","w",encoding='utf-8');
        outputcode.write(code)
        outputcode.close()
    else:
        badcodeId += 1
        outputcode = open("badcode\\"+str(badcodeId)+".java","w",encoding='utf-8');
        outputcode.write(code)
        outputcode.close()
    if ((codeId + badcodeId) % 5 == 0):
        totcode=(codeId+badcodeId)//5
        sss="."*totcode
        print(sss)

print("\nSearch completed, found "+str(codeId)+" code snippets and "+str(badcodeId)+" bad code snippets.")
output=open("codenumber.txt","w")
output.write(str(codeId)+"\n"+str(badcodeId)+"\n");