/**
* allTeams predicate finds all the teams that given in the team predicates and returns a list that contains all the teams.
* it uses findall function to search all the teams,
* then it uses permutation fuction to generate several versions of the team list,
* finally it uses the length function to return the number of teams in the list.
*/
allTeams(L,N):- 
	findall(X,team(X,_),LTemp), /*searches the team predicates and insert all the teams in to the LTemp variable*/
	permutation(LTemp,L), 		/*permutate the list LTemp in to L (generate different versions of the list LTemp)*/
	length(L,N).				/*finds the length of list L*/
/**
* wins(T,W,L,N) implies that L involves the teams defeated by team T when we are in week W and N is the number of elements in L.
* it uses findall function two times, in the first search it finds the given team's home matches, in the second search it finds the given team's away matches,
* then it appends two temporary list to the final list (L), and uses length function to return the number of teams in the list.
*/
wins(T,W,L,N):-	
	findall(X, (match(A,T,B,X,D), A=<W, B>D), LTemp1),	/*finds all the matches which are played in or before week W and the given team wins*/
	findall(Z, (match(C,Z,E,T,F), C=<W, F>E), LTemp2),	/*it searches two times for home-away matches and keeps the beated teams in temporary lists*/
	append(LTemp1,LTemp2,L),							/*appends the temporary lists to L*/
	length(L,N).										/*finds the length of L and retrieve it in N*/
/**
* losses(T,W,L,N) implies that L involves the teams that defeated team T when we are in week W and N is the number of elements in L.
* it uses findall function two times, in the first search it finds the given team's home matches, in the second search it finds the given team's away matches,
* then it appends two temporary list to the final list (L), and uses length function to return the number of teams in the list.
*/
losses(T,W,L,N):- 
	findall(X, (match(A,T,B,X,D), A=<W, B<D), LTemp1),	/*finds all the matches which are played in or before week W and the given team wins*/
    findall(Z, (match(C,Z,E,T,F), C=<W, F<E), LTemp2),	/*it searches two times for home-away matches and keeps the winner teams in temporary lists*/
    append(LTemp1,LTemp2,L),							/*appends the temporary lists to L*/
    length(L,N).										/*finds the length of L and retrieve it in N*/
/**
* draws(T,W,L,N) is very similar but now L involves the teams that team T could not defeat also did not lose to.
* it uses findall function two times, in the first search it finds the given team's home matches, in the second search it finds the given team's away matches,
* then it appends two temporary list to the final list (L), and uses length function to return the number of teams in the list.
*/
draws(T,W,L,N):- 
	findall(X, (match(A,T,B,X,D), A=<W, B=D), LTemp1),	/*finds all the matches which are played in or before week W and the score is a draw*/
	findall(Z, (match(C,Z,E,T,F), C=<W, F=E), LTemp2),	/*it searches two times for home-away matches and keeps the opponent teams in temporary lists*/
    append(LTemp1,LTemp2,L),							/*appends the temporary lists to L*/
    length(L,N).										/*finds the length of L and retrieve it in N*/
/**
* scored(T,W,S) implies that S is the total number of goals scored by team T up to (and including) week W.
* it uses findall function two times, in the first search it finds the given team's home matches, in the second search it finds the given team's away matches,
* then it appends two temporary list to the final list (L), and uses sumList predicate to sum up the elements of the list.
*/
scored(T,W,S):-	
	findall(X, (match(A,T,X,_,_), A=<W), LTemp1),	/*finds all the scored goals by the given team in matches which are played in or before the week W*/
	findall(Z, (match(C,_,_,T,Z), C=<W), LTemp2),	/*it searches two times for home-away matches and keeps the goals that scored in temporary lists*/
	append(LTemp1,LTemp2,L),						/*appends the temporary lists to L*/
	sumList(L,S).									/*sums up the elements of L and retrieve it in S*/
/**
* conceded(T,W,S) implies that S is the total number of goals conceded by team T up to (and including) week W.
* it uses findall function two times, in the first search it finds the given team's home matches, in the second search it finds the given team's away matches,
* then it appends two temporary list to the final list (L), and uses sumList predicate to sum up the elements of the list.
*/
conceded(T,W,C):- 
	findall(X, (match(A,T,_,_,X), A=<W), LTemp1),	/*finds all the conceded goals by the given team in matches which are played in or before the week W*/
	findall(Z, (match(C,_,Z,T,_), C=<W), LTemp2),	/*it searches two times for home-away matches and keeps the goals that conceded in temporary lists*/
	append(LTemp1,LTemp2,L),          				/*appends the temporary lists to L*/
    sumList(L,C).									/*sums up the elements of L and retrieve it in C*/
/**
* average(T,W,A) implies that A is the average (goals scored - goals conceded) of a team T gathered up to (and including) week W.
* it uses findall function two times, in the first search it finds the given team's home matches, in the second search it finds the given team's awat matches,
* then it appends two temporary list to the final list (L), and uses sumList predicate to sum up the elements of the list.
*/
average(T,W,A):-
	findall(X, (match(A,T,Sc,_,Co), A=<W,  X is Sc-Co), LTemp1),	/*finds average of the given team match by match*/
	findall(Z, (match(C,_,Con,T,Sco), C=<W, Z is Sco-Con), LTemp2),	/*it searches two times for home-away matches and keeps the average of each match in temporary lists*/
	append(LTemp1,LTemp2,L),										/*appends the temporary lists to L*/
	sumList(L,A).													/*sums up the elements of L and retrieve it in A*/
/**
* order(L,W) implies that W (week) is given as constant and league order in that week will be retrieved in L.
* it uses allTeamsNoPermu predicate to find a list of all teams in the team predicates,
* then it uses mergesort predicate to sort the finded teams acording to their averages in the given week,
* finally it uses reverse function to reverse the list, because the mergesort predicate returns the list in the ascending order.
*/
order(L,W):-
	allTeamsNoPermu(LTemp),		/*finds the list of all teams in team predicates*/
	mergesort(LTemp,LTemp2,W),	/*sort the list of all teams by checking their averages in the given week*/
	reverse(LTemp2,L).			/*reverses the list to make the list in descending order*/
/**
* topThree([T1,T2, T3],W) implies that T1 T2 and T3 are the top teams when we are in the given week W.
* it uses order predicate to find a list of teams which are ordered according to their averages in the given week,
* then it takes the first three element from the list and declares the given variables T1, T2 and T3.
*/
topThree([T1,T2,T3],W):- 		
	order(L,W),					/*finds the list of teams sorted according to their averages*/
	L = [H1|Tail1], T1 = H1,	/*adds the first three elements of list to the given variables*/
	Tail1 = [H2|Tail2], T2 = H2,
	Tail2 = [H3|_], T3 = H3.	
/**
* allTeamsNoPermu is a helper predicate for order predicate,
* it finds all the teams in the team predicates and returns the list (L).
* it just uses a findall function to search the team predicates.
*/
allTeamsNoPermu(L):-
	findall(X,team(X,_),L).	/*finds the list of all teams in team predicates*/
/**
* sumList predicate is another helper predicate.
* basically, it sums up the elements of a given list.
*/
sumList([], 0).
sumList([H|T], Sum):- 
	sumList(T, Rest), /*reach every element of the list by taking it's head element and sums up the all elements of the given list recursively*/
   	Sum is H + Rest.
/**
* mergesort predicate is a helper predicate, too.
* it sort a given list in ascending order.
* it splits the given list recursively, until all the lists have only one element inside,
* then it merges the splitted lists back, and it merges them according to their averages in the given week.
*/
mergesort([],[],_).    
mergesort([A],[A],_).
mergesort([A,B|R],S,W):-  
   	split([A,B|R],L1,L2),	/*splits the list in to two part*/
   	mergesort(L1,S1,W),		/*calls itself recursively to sort the splitted pieces*/		
   	mergesort(L2,S2,W),
   	merge(S1,S2,S,W).		/*after the last call of splitting, merges the splitted pieces according to their averages on the given week*/
/**
* split predicate is a helper predicate for mergesort predicate,
* it splits a list in to two piece.
*/
split([],[],[]).
split([A],[A],[]).
split([A,B|R],[A|Ra],[B|Rb]):-
	split(R,Ra,Rb).
/**
*merge predicate is also a helper predicate for mergesort predicate,
*it merges the splitted pieces of the list.
*/
merge(A,[],A,_).
merge([],B,B,_).
merge([A|Ra],[B|Rb],[A|M],W):-
	average(A,W,A1),
	average(B,W,A2),
	A1 =< A2,
	merge(Ra,[B|Rb],M,W).
merge([A|Ra],[B|Rb],[B|M],W):-
	average(A,W,A1),
	average(B,W,A2),
	A1 > A2,
	merge([A|Ra],Rb,M,W).