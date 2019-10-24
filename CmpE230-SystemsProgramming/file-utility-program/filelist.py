import os
import sys
from collections import deque
import argparse
import subprocess
import time
import datetime
import ast
import math
import collections
import re


parser = argparse.ArgumentParser()
parser.add_argument("doc_list", nargs="*", help="to print the files in the given document list") #document list takes zero or more arguments
parser.add_argument("-before", nargs="?", default="None", help="if given to filter the files that was modified before the given date")
parser.add_argument("-after", nargs="?", default="None", help="to filter the files that is modified after the given time")
parser.add_argument("-match", nargs="?", default="None", help="to list the files that matches the given pattern")
parser.add_argument("-bigger", nargs="?", default="None", help="to filter the files that is bigger than the given size")
parser.add_argument("-smaller", nargs="?", default="None", help="to filter the files that is smaller than the given size")
parser.add_argument("-duplcont", action="store_true", default="None", help="to list the files with the same content")
parser.add_argument("-duplname", action="store_true", default="None", help="to list the files with the same name")
parser.add_argument("-zip", nargs="?", default="None", help="zips the filtered files")
parser.add_argument("-delete", action="store_true", default="None", help="if given to delete the filtered doc_list") #store_true for not being have to receive an argument
parser.add_argument("-stats", action="store_true", default="None", help="to print statics of traversal procedure")
parser.add_argument("-nofilelist", action="store_true", default="None", help="if given nothing will be printed")


args = parser.parse_args()
#print args.doc_list

out = []

#DOC_LIST
if args.doc_list:

	list = args.doc_list
	for argv_new in list:
		qlist = deque([argv_new]) 
		while qlist:
	        	currentdir = qlist.popleft()
	        	dircontents = os.listdir(currentdir)
	        	for name in dircontents:
        		        currentitem = currentdir + "/" + name
        		        if os.path.isdir(currentitem):
        	        	        qlist.append(currentitem)
	   	       		else:
        	               # print(currentitem)
					out.append(currentitem)

else:
	
	argv_new = "."
	qlist = deque([argv_new]) 
	while qlist:
        	currentdir = qlist.popleft()
        	dircontents = os.listdir(currentdir)
        	for name in dircontents:
	    	        currentitem = currentdir + "/" + name
    		        if os.path.isdir(currentitem):
    		                qlist.append(currentitem)
    		        else:
    	        	        #print(currentitem)
				out.append(currentitem)

#print args


files_visited = len(out)
sizes_visited = 0
for x in out:
	filesize = os.path.getsize(x)
	sizes_visited += filesize

def parse_str(s):
	try:
		return ast.literal_eval(str(s))
	except:
		print "syntax error"
		sys.exit()


#BEFORE
if args.before != "None":

	if ("T" in args.before):
		mod_before = time.mktime(datetime.datetime.strptime(args.before, "%Y%m%dT%H%M%S").timetuple())
		#print mod_before
	else:
		parse_str(args.before) #it also checks whether there exists any string in before if there is any it means there is whether no inpot of before or there is an invalid input
		mod_before = time.mktime(datetime.datetime.strptime(args.before, "%Y%m%d").timetuple())


	parsed_before = parse_str(mod_before)

	def it_is_before(x):
		mod_x = os.path.getmtime(x)
		parsed_x = parse_str(mod_x)
		
		if parsed_x < parsed_before:
			return True
		
		return False

	out[:] = [x for x in out if it_is_before(x)]


#AFTER

def it_is_after(x):
	mod_x = os.path.getmtime(x)
	parsed_x = parse_str(mod_x)
	
	if parsed_x > parsed_after:
		return True
	
	return False

if args.after != "None":

	if ("T" in args.after):
		mod_after = time.mktime(datetime.datetime.strptime(args.after, "%Y%m%dT%H%M%S").timetuple())
	else:
		parse_str(args.after)
		mod_after = time.mktime(datetime.datetime.strptime(args.after, "%Y%m%d").timetuple())


	parsed_after = parse_str(mod_after)

	out[:] = [x for x in out if it_is_after(x)]


#MATCH
if args.match != "None":

	new_out = []
	new_out.extend(out)

	for x in new_out:
		pattern = re.compile(args.match)
		found = pattern.match(os.path.basename(x))
		if not found:
			out.remove(x)

#BIGGER
def it_is_bigger(x):
	filesize = os.path.getsize(x)
	if filesize > parsed_size:
		return True
	return False

if args.bigger != "None":
	
	size_in_args = ""
	for x in args.bigger:
		if(x != 'G' and x != 'M' and x != 'K'):
			size_in_args = size_in_args + x
			#print size_in_args

	x = args.bigger[-1]
	
	parsed_size = parse_str(size_in_args) #if there are no K, M or G parsed_size will exactly be equal to parsed_size
	if(x == 'G'):
		parsed_size = parsed_size * pow(10,9)
	elif (x == 'M'):
		parsed_size = parsed_size * pow(10,6)
	elif (x == 'K'): #if x == K
		parsed_size = parsed_size * pow(10,3)
	else:
		parsed_size = parsed_size	
	#print parsed_size

	out[:] = [x for x in out if it_is_bigger(x)]		
	


#SMALLER
def it_is_smaller(x):
	filesize = os.path.getsize(x)
	if filesize < parsed_size:
		return True
	return False

if args.smaller != "None":

	size_in_args = ""
	for x in args.smaller:
		if(x != 'G' and x != 'M' and x != 'K'):
			size_in_args = size_in_args + x
			#print size_in_args

	x = args.bigger[-1]
	
	parsed_size = parse_str(size_in_args) #if there are no K, M or G parsed_size will exactly be equal to parsed_size
	if(x == 'G'):
		parsed_size = parsed_size * pow(10,9)
	elif (x == 'M'):
		parsed_size = parsed_size * pow(10,6)
	elif (x == 'K'): #if x == K
		parsed_size = parsed_size * pow(10,3)
	else:
		parsed_size = parsed_size	
	#print parsed_size

	out[:] = [x for x in out if it_is_smaller(x)]

#ZIP
if args.zip != "None": 
	if out: #if the list out is not empty --> if it was infact empty zip command would give error
		command = "zip " + args.zip + " "
		for filepath in out:
			command = command + filepath + " "
		os.popen(command).read() #to do the command while preventing the command's printing the output of the command


#DUPLCONT
duplcont_dict = {}
if args.duplcont == True:

	for filepath in out:
		command = "shasum " +filepath
		command_out = os.popen(command).read()

		hash_out = ""

		for x in command_out:
			if(x != ' '):
				hash_out += x
			else:
				break

		if hash_out in duplcont_dict:
			duplcont_dict[hash_out].append(filepath)
		else:
			duplcont_dict.setdefault(hash_out,[filepath])

		duplcont_dict[hash_out].sort()


#DUPLNAME
duplname_dict = {}
if args.duplname == True:

	for filepath in out:
		
		if os.path.basename(filepath) in duplname_dict:
			duplname_dict[os.path.basename(filepath)].append(filepath)
		else:
			duplname_dict.setdefault(os.path.basename(filepath),[filepath])


	duplname_dict = collections.OrderedDict(sorted(duplname_dict.items()))	


#STATS
files_listed = len(out)
sizes_listed = 0
for x in out:
	filesize = os.path.getsize(x)
	sizes_listed += filesize

unique_files = 0
unique_sizes = 0
if args.duplcont == True:
	unique_files = len(duplcont_dict)
	for x in duplcont_dict:
		unique_sizes += os.path.getsize(duplcont_dict[x][0])
elif args.duplname == True:
	unique_files = len(duplname_dict)


#DELETE
if args.delete == True: #if the value of a defaultly set as argparse.SUPRESS option is True, it means that that option is used 

	if out: #if the list out is not empty --> if it was infact empty rm command would give error
		command = "rm "
		for filepath in out:
			command = command + filepath + " "
		os.popen(command).read()


#FOR PRINTING OUT WHILE NO NOFILELIST
if args.nofilelist == 'None':
	if args.duplcont == True:
		for x in duplcont_dict:
			print "-----"
			if len(duplcont_dict[x]) > 1:
				for y in duplcont_dict[x]:
					print y
			else:
				print duplcont_dict[x][0]

		print "-----"

	elif args.duplname == True:
		for x in duplname_dict:
			print "-----"
			if len(duplname_dict[x]) > 1:
				for y in duplname_dict[x]:
					print y
			else:
				print duplname_dict[x][0]

			
		print "-----"

	else:
		for x in out:
			print x


if args.stats == True:

	print "number of files visited: " + str(files_visited)
	print "total size of files visited: " + str(sizes_visited)
	print "number of files listed: " + str(files_listed)
	print "total size of files listed: " + str(sizes_listed)

	if args.duplcont == True:
		print "number of unique files listed: " + str(unique_files)
		print "total size of unique files listed: " + str(unique_sizes)
	elif args.duplname == True:
		print "number of unique files listed: " + str(unique_files)
