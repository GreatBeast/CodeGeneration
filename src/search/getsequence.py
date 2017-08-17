import search
import init
import sys
import os

init.init()

if len(sys.argv)<2:
    raise Exception("Query is empty")
Query=" "
for i in range(1,len(sys.argv)):
    Query=Query+sys.argv[i]+" "
tokens=init.getsearch(Query)

output=open("tokens.txt","w")
L=min(len(tokens),100)
for i in range(L):
    output.write(tokens[i]+'\n')

output.close();
print("Find "+str(L)+" API sequences")
