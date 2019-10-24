/*
 * Student Name: 	Hasan Basri BALABAN
 * Student Number: 	2016400297
 * Compile Status: 	Compiling
 * Program Status: 	Working
 * Notes: I implemented the first approach.
*/
#include <iostream>
#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <fstream>
#include <math.h>
#include <time.h>
#include <random>

using namespace std;

int main(int argc, char* argv[]) {
    // Initialize the MPI environment
    MPI_Init(NULL, NULL);
    // Find out rank, size
    int world_rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &world_rank);
    int world_size;
    MPI_Comm_size(MPI_COMM_WORLD, &world_size);
   	
    int X[200][200];
    int N = world_size-1;
    double beta = stod(argv[3]), pi = stod(argv[4]), gamma = 0.5*log((1-pi)/pi);
    int T = 500000/N;

    if(world_rank == 0){ //if it is the master process
        //Takes input from input file
        ifstream in(argv[1]);
        freopen(argv[2], "w", stdout);

        for(int i=0; i<200; i++)
            for(int j=0; j<200; j++)
                in >> X[i][j];

        //Sends the necessary parts to each slave
        for(int i=1; i<=N; i++)
            MPI_Send(X[200/N*(i-1)], 200*200/N, MPI_INT, i, 0, MPI_COMM_WORLD);

        //Receives the parts of final array from each slave
        int Z[200][200];
        for(int i=1; i<=N; i++)
            MPI_Recv(Z[200/N*(i-1)], 200*200/N, MPI_INT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        
        //Outputs to output file
        for(int i=0; i<200; i++){
            for(int j=0; j<200; j++){
                cout <<Z[i][j] << " ";
            }
            cout << endl;
        }
    }
    else if(world_rank == 1){ //if it is the first slave
        //Receives a (200/N)*200 size data from the master process
        int* temparr = NULL;
        temparr = (int *)malloc(sizeof(int) * 200 * 200 / N);
        MPI_Recv(temparr, 200*200/N, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		//Initiliaze the local data array
        int subarr[200/N][200];
        for(int i=0; i<200/N; i++)
            for(int j=0; j<200; j++)
                subarr[i][j] = *(temparr + i * 200 + j);
		free(temparr); // Deletes the dynamically allocated memory
		
        srand(time(NULL)); //Initiliaze the random number generator
        for(int t=0; t<T; t++){ //Iteration loop
            MPI_Send(subarr[(200/N)-1], 200, MPI_INT, 2, 0, MPI_COMM_WORLD); //Send the last row of local array to second slave process
			//Receive the top row of second slave process' local array
            int* temp = NULL;
            temp = (int *)malloc(sizeof(int) * 200);
            MPI_Recv(temp, 200, MPI_INT, 2, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
			
            int lower[200];
            for(int i=0; i<200; i++)
                lower[i] = *(temp + i);
	    	free(temp); //Free the dynamically alocated memory that used for taking second slave's top row
            int i = rand() % 200/N, j = rand() % 200, sumos = 0; //Generate two random number for taking a random coordinate on the local data array
			//Calculate the sum of pixels that are around the randomly generated coordinate
            for(int k=max(i-1,0); k<=min(i+1,(200/N)-1); k++)
                for(int l=max(j-1,0); l<=min(j+1,199); l++)
                    sumos += subarr[k][l];

           sumos -= subarr[i][j];
            if(i == (200/N)-1) //If the randomly generated coordinate is at the border, calculate the sum by looking at the second slave's top row
                sumos = sumos + lower[j-1] + lower[j] + lower[j+1];
			//Calculate the acceptance probability
            double delta_E = -2*gamma*X[i][j]*subarr[i][j] - 2*beta*subarr[i][j]*sumos;
            if((log((double) rand()/RAND_MAX + 1.)) < delta_E)
                subarr[i][j] = -subarr[i][j];
        }
		//After the iterations end, send the final array to master process
        MPI_Send(subarr[0], 200*200/N, MPI_INT, 0, 0, MPI_COMM_WORLD);
    }
    else if(world_rank == N){ //if it is the last slave
    	//Receives a (200/N)*200 size data from the master process
        int* temparr = NULL;
        temparr = (int *)malloc(sizeof(int) * 200 * 200 / N);
        MPI_Recv(temparr, 200*200/N, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		//Initiliaze the local data array
        int subarr[200/N][200];
        for(int i=0; i<200/N; i++)
            for(int j=0; j<200; j++)
                subarr[i][j] = *(temparr + i * 200 + j);        
		free(temparr); // Deletes the dynamically allocated memory
		
        srand(time(NULL)); //Initiliaze the random number generator
        for(int t=0; t<T; t++){ //Iteration loop
            int* temp = NULL;
            temp = (int *)malloc(sizeof(int) * 200);
            MPI_Recv(temp, 200, MPI_INT, N-1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE); //Receive the last row of upper slave
            MPI_Send(subarr[0], 200, MPI_INT, N-1, 0, MPI_COMM_WORLD); //Send the first row of local data array to upper slave

            int upper[200];
            for(int i=0; i<200; i++)
                upper[i] = *(temp + i);
	    	free(temp);//Free the dynamically alocated memory that used for taking upper slave's last row
            int i = rand() % 200/N, j = rand() % 200, sumos = 0; //Generate two random number for taking a random coordinate on the local data array
			//Calculate the sum of pixels that are around the randomly generated coordinate
            for(int k=max(i-1,0); k<=min(i+1,(200/N)-1); k++)
                for(int l=max(j-1,0); l<=min(j+1,199); l++)
                    sumos += subarr[k][l];
                    
            sumos -= subarr[i][j];
            if(i==0) //If the randomly generated coordinate is at the border, calculate the sum by looking at the upper slave's last row
                sumos = sumos + upper[j-1] + upper[j] + upper[j+1];
			//Calculate the acceptance probability
            double delta_E = -2*gamma*X[i][j]*subarr[i][j] - 2*beta*subarr[i][j]*sumos;
            if((log((double) rand()/RAND_MAX + 1.)) < delta_E)
                subarr[i][j] = -subarr[i][j];
        }
		//After the iterations end, send the final array to master process
        MPI_Send(subarr[0], 200*200/N, MPI_INT, 0, 0, MPI_COMM_WORLD);
    }
    else{ //if it is a intermediate slave
    	//Receives a (200/N)*200 size data from the master process
        int* temparr = NULL;
        temparr = (int *)malloc(sizeof(int) * 200 * 200 / N);
        MPI_Recv(temparr, 200*200/N, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		//Initiliaze the local data array
        int subarr[200/N][200];
        for(int i=0; i<200/N; i++)
            for(int j=0; j<200; j++)
                subarr[i][j] = *(temparr + i * 200 + j);
		free(temparr); // Deletes the dynamically allocated memory
		
        srand(time(NULL)); //Initiliaze the random number generator
        for(int t=0; t<T; t++){ //Iteration loop
            int* temp = NULL;
            int* temp2 = NULL;
            temp = (int *)malloc(sizeof(int) * 200);
            temp2 = (int *)malloc(sizeof(int) * 200);
            MPI_Recv(temp, 200, MPI_INT, world_rank-1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE); //Receive the last row of upper slave
            MPI_Send(subarr[0], 200, MPI_INT, world_rank-1, 0, MPI_COMM_WORLD); //Send the first row of local data array to upper slave
            MPI_Send(subarr[(200/N)-1], 200, MPI_INT, world_rank+1, 0, MPI_COMM_WORLD); //Send the last row of local data array to lower slave
            MPI_Recv(temp2, 200, MPI_INT, world_rank+1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE); //Receive the top row of lower slave

            int upper[200], lower[200];
            for(int i=0; i<200; i++){
                upper[i] = *(temp + i);
                lower[i] = *(temp2 + i);
            }
	    	free(temp); //Free the dynamically alocated memory
	    	free(temp2); //Free the dynamically alocated memory
            int i = rand() % 200/N, j = rand() % 200, sumos = 0; //Generate two random number for taking a random coordinate on the local data array
			//Calculate the sum of pixels that are around the randomly generated coordinate
            for(int k=max(i-1,0); k<=min(i+1,(200/N)-1); k++)
                for(int l=max(j-1,0); l<=min(j+1,199); l++)
                    sumos += subarr[k][l];
            
            sumos -= subarr[i][j];
            if(i==0) //If the randomly generated coordinate is at the border, calculate the sum by looking at the upper slave's last row
                sumos = sumos + upper[j-1] + upper[j] + upper[j+1];
            else if(i==(200/N)-1) //If the randomly generated coordinate is at the border, calculate the sum by looking at the lower slave's top row
                sumos = sumos + lower[j-1] + lower[j] + lower[j+1];
			//Calculate the acceptance probability
            double delta_E = -2*gamma*X[i][j]*subarr[i][j] - 2*beta*subarr[i][j]*sumos;
            if((log((double) rand()/RAND_MAX + 1.)) < delta_E)
                subarr[i][j] = -subarr[i][j];
        }
        //After the iterations end, send the final array to master process
        MPI_Send(subarr[0], 200*200/N, MPI_INT, 0, 0, MPI_COMM_WORLD);
    }
    //Finalize the MPI Environment
    MPI_Finalize();
    return 0;
}
