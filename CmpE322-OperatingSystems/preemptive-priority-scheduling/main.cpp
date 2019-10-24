#include <iostream>
#include <fstream>
#include <vector>
#include <deque>
#include <bits/stdc++.h>

using namespace std;


/*
 *Process struct holds a process' fields and instruction informations.
 */
struct Process {

	string processName, codeFile;
	int priority, arrivalTime;
	int readyQArrival;	// this variable is used to hold the current time properly while scheduling processes
	int totalInstNumber;
	int totalExectTime;
	deque<int> instQueue; // this queue holds the instructions that was read from code file

	Process(string processName_, int priority_, string codeFile_, int arrivalTime_){
		this->processName = processName_;
		this->priority = priority_;
        this->codeFile = codeFile_;
        this->arrivalTime = arrivalTime_;
        this->readyQArrival = arrivalTime_;
        this->totalExectTime = 0;
        //Code File reading part
        string codeFilePath = codeFile + ".txt";
        string line;
        ifstream codeFileBuffer(codeFilePath);
        if(codeFileBuffer.is_open()){
            while(getline(codeFileBuffer, line)){
                vector<string> tokens;
                stringstream s1(line);

                string temp;
                while(getline(s1, temp, '\t')){
                    tokens.push_back(temp);
                }

                instQueue.push_back(stoi(tokens[1]));
                totalExectTime += stoi(tokens[1]);
            }
         }
        else
            cout << "Error while reading "+codeFile+".txt file!" << endl;
        //

        totalInstNumber = instQueue.size();
	}
	~Process() = default;
};
/*
 * Comparable classes return boolean values for process priority queues in the Scheduling struct.
 */
class ComparableForArrivalTime {
public:
    bool operator()(Process * x, Process * y) {
        return x->arrivalTime >= y->arrivalTime;
    }
};

class ComparableForPriority {
public:
    bool operator()(Process * x, Process * y) {
        if(x->priority == y->priority)
            return x->arrivalTime >= y->arrivalTime;
        return x->priority > y->priority;
    }
};
/*
 * Scheduling struct holds the process in priority queues and is responsible 
 * for all scheduling algorithm. 
 */
struct Scheduling {
	priority_queue<Process *, vector<Process *>, ComparableForArrivalTime> jobQueue; // PQ that holds all the processes according to their arrival time
    priority_queue<Process *, vector<Process *>, ComparableForPriority> readyQueue;	 // PQ that holds the processes that will be processed
    priority_queue<Process *, vector<Process *>, ComparableForArrivalTime> terminatedQueue;	// PQ that holds the terminated processes
    vector<string> processNameString;
    int currentTime;
    bool isFirst;
    // Constructor
    Scheduling() {
    	currentTime = 0;
    	isFirst = true;
    }
    // This method makes the necessary preemptive scheduling.
    void processReadyPreemptive() {
    	Process * currentProc = readyQueue.top();
    	readyQueue.pop();

    	if(!currentProc->instQueue.empty()){
    		if(currentProc->readyQArrival + currentProc->instQueue.front() < jobQueue.top()->arrivalTime){
    			currentProc->readyQArrival += currentProc->instQueue.front();
                currentProc->instQueue.pop_front();
    			currentTime = currentProc->readyQArrival;
    			if(currentProc->instQueue.empty()){
    			    terminatedQueue.push(currentProc);
                    readyQueue.top()->readyQArrival = currentTime;
                    printOutput();
    			}
    			else{
                    readyQueue.push(currentProc);
    			}
    		}
    		else if(currentProc->readyQArrival + currentProc->instQueue.front() == jobQueue.top()->arrivalTime){
    			currentProc->readyQArrival += currentProc->instQueue.front();
    			currentProc->instQueue.pop_front();
    			currentTime = currentProc->readyQArrival;
    			if(currentProc->instQueue.empty()){
    			    terminatedQueue.push(currentProc);
    			}
    			else{
                    readyQueue.push(currentProc);
    			}
                Process * temp = jobQueue.top();
                jobQueue.pop();
                readyQueue.push(temp);
                while(!jobQueue.empty() && (jobQueue.top()->arrivalTime == temp->arrivalTime)){
                    readyQueue.push(jobQueue.top());
                    jobQueue.pop();
                }
                printOutput();
    		}
    		else{
                currentProc->readyQArrival += currentProc->instQueue.front();
                currentProc->instQueue.pop_front();
                currentTime = currentProc->readyQArrival;

                while(!jobQueue.empty() && (jobQueue.top()->arrivalTime <= currentProc->readyQArrival )){
                    jobQueue.top()->readyQArrival = currentProc->readyQArrival;
                    readyQueue.push(jobQueue.top());
                    jobQueue.pop();
                }

    			if(currentProc->instQueue.empty()){
    			    terminatedQueue.push(currentProc);
    			}
    			else{
                    readyQueue.push(currentProc);
                }
    			printOutput();
    		}
    	}
    }
    // This method makes the necessary nonpreemptive scheduling.
    void processReadyNonpreemptive() {
    	Process * temp = readyQueue.top();
    	readyQueue.pop();

    	if(!temp->instQueue.empty()){
    		temp->readyQArrival += temp->instQueue.front();
    		temp->instQueue.pop_front();
    		currentTime = temp->readyQArrival;
    		readyQueue.push(temp);
    	}
    	else{
    		terminatedQueue.push(temp);
    		readyQueue.top()->readyQArrival = currentTime;
    		printOutput();
    	}
    }
    // This method prints the readyQueue when there is a change in the ready queue.
    void printOutput(){
    	vector<Process *> temp;
    	printf("%d:HEAD-", currentTime);
    	if(readyQueue.empty())
    	    printf("-TAIL\n");
    	else{
            while(!readyQueue.empty()){
                temp.push_back(readyQueue.top());
                int currentInst = readyQueue.top()->totalInstNumber - readyQueue.top()->instQueue.size() + 1;
                printf("%s[%d]-", readyQueue.top()->processName.c_str(), currentInst);
                readyQueue.pop();
            }
            printf("TAIL\n");
    	}
    	for(auto p : temp)
    	    readyQueue.push(p);
    }
    // This method prints the process' turnaround and waiting time.
    void printStats(){
        vector<Process *> tempVec;
        while(!terminatedQueue.empty()){
            tempVec.push_back(terminatedQueue.top());
            terminatedQueue.pop();
        }
        for(int i=0; i<processNameString.size(); i++){
            for(int j=0; j<tempVec.size(); j++){
                if(processNameString[i] == tempVec[j]->processName){
                    printf("Turnaround time for %s = %d ms\n", tempVec[j]->processName.c_str(), tempVec[j]->readyQArrival - tempVec[j]->arrivalTime);
                    printf("Waiting time for %s = %d\n", tempVec[j]->processName.c_str(), tempVec[j]->readyQArrival - tempVec[j]->arrivalTime - tempVec[j]->totalExectTime);
                }
            }
        }
    }
    // This method makes all the scheduling part by using processReadyPremptive and
    // processReadyNonpreemtive methods.
    void execute(){
        printOutput();
        while(true){
            if(isFirst){
                currentTime = jobQueue.top()->arrivalTime;
                Process * temp = jobQueue.top();
                jobQueue.pop();
                readyQueue.push(temp);
                while(!jobQueue.empty() && (jobQueue.top()->arrivalTime == temp->arrivalTime)){
                    readyQueue.push(jobQueue.top());
                    jobQueue.pop();
                }
                printOutput();
                isFirst = false;
            }
            else{
                if(!readyQueue.empty() && !jobQueue.empty()){
                    processReadyPreemptive();
                }
                else if(!readyQueue.empty() && jobQueue.empty()){
                    processReadyNonpreemptive();
                }
                else if(readyQueue.empty() && !jobQueue.empty()){
                    currentTime = jobQueue.top()->arrivalTime;
                    Process * temp = jobQueue.top();
                    jobQueue.pop();
                    readyQueue.push(temp);
                    while(!jobQueue.empty() && (jobQueue.top()->arrivalTime == temp->arrivalTime)){
                        readyQueue.push(jobQueue.top());
                        jobQueue.pop();
                    }
                    printOutput();
                }
                else{
                    break;
                }
            }
        }
        printf("\n");
        printStats();
    }
};

int main() {
	// output file
	freopen("output.txt", "w", stdout);

    auto * schedule = new Scheduling();
    // this part reads the definition file and creates the necessary processes.
    string line;
    ifstream definitionFileBuffer("definition.txt");
    if(definitionFileBuffer.is_open()){
        while(getline(definitionFileBuffer, line)){
            vector<string> tokens;
            stringstream s1(line);

            string temp;
            while(getline(s1, temp, ' ')){  //Tokenizing w.r.t. space ' '
                tokens.push_back(temp);
            }
            //Create a process and push it to schedule object's jobQueue
            string procN = tokens[0];
            string codeF = tokens[2];
            int priority = stoi(tokens[1]);
            int arrivalT = stoi(tokens[3]);

            Process * newP = new Process(procN, priority, codeF, arrivalT);
            schedule->jobQueue.push(newP);
            schedule->processNameString.push_back(procN);
        }
    }
    else
        cout << "Error while reading definition file!" << endl;
    // Start scheduling.
    schedule->execute();
    return 0;
}