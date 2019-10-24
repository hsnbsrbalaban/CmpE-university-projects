#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>
#include <mutex>
#include <unistd.h>

using namespace std;

int total_seat_num;					// Total seat number
int *seat_array = NULL;				// Seat array to control the status of the seats (Empty:0, Full:1)
// Communication arrays
int *requested_seat_array = NULL;	// Specify which seat is requested by the client
int *is_reserved_by_server = NULL;	// Specify if the server served to client or not
bool *is_requested_by_client = NULL;// Specify if the client request a seat

pthread_mutex_t mymtx[101];			// Mutex array

void *client_runner(void *param);	// Client threads runner function	
void *server_runner(void *param);	// Server threads runner function

int main(int argc, char* argv[]) {

	freopen("output.txt", "w", stdout);
	// Seed the random number generator
	srand((unsigned)time(NULL));
	// Initialize the global data
	total_seat_num = stoi(argv[1]);

	seat_array = new int[total_seat_num+1];
	requested_seat_array = new int[total_seat_num+1];
	is_reserved_by_server = new int[total_seat_num+1];
	is_requested_by_client = new bool[total_seat_num+1];

	for(int i = 0; i <= total_seat_num; i++){
		seat_array[i] = 0;
		requested_seat_array[i] = -1;
		is_reserved_by_server[i] = 0;
		is_requested_by_client[i] = false;
	}
	// Initialize the mutexes
	for(int i = 0; i < 101; i++)
		mymtx[i] = PTHREAD_MUTEX_INITIALIZER;

	printf("Number of total seats: %d\n", total_seat_num);
	// Creat client threads
	pthread_t client_threads[total_seat_num];
	for(int i = 1; i <= total_seat_num; i++)
		pthread_create(&client_threads[i], 0, client_runner, (void *)i);
	// Join on each client thread
	for(int i = 1; i <= total_seat_num; i++)
		pthread_join(client_threads[i], NULL);

	printf("All seats are reserved.\n");
	// Delete dynamically allocated data
	delete [] seat_array;
	delete [] requested_seat_array;
	delete [] is_reserved_by_server;
	delete [] is_requested_by_client;

	return 0;
}

void *client_runner(void *param) {
	long tid = (long)param;	// Thread id
	// Create peer server thread
	pthread_t server_thread;
	pthread_create(&server_thread, 0, server_runner, (void *)tid);
	// Sleep for 50-200 ms (randomly chosen for each thread)
	int sleep_time = rand() & 150 + 50;
	usleep(sleep_time * 1000);
	// Select a random seat to request from server
	int requested_seat = rand() % total_seat_num + 1;
	// Lock the corresponding seat's mutex for safe access to data
	pthread_mutex_lock(&mymtx[requested_seat]);
	requested_seat_array[tid] = requested_seat;
	is_requested_by_client[tid] = true;
	// Wait until a response comes from server
	while(is_reserved_by_server[tid] == 0) {
		/*BUSY WAIT*/
	}
	// Unlock the mutex when the response comes
	pthread_mutex_unlock(&mymtx[requested_seat]);
	do{
		// If the seat is reserved then break
		if(is_reserved_by_server[tid] == 1) {
			break;
		}
		else {	// If the seat is not reserved, state another random seat and lock the corresponding mutex
			is_reserved_by_server[tid] = 0;
			requested_seat = rand() % total_seat_num + 1;
			pthread_mutex_lock(&mymtx[requested_seat]);
			requested_seat_array[tid] = requested_seat;
			is_requested_by_client[tid] = true;
			// Wait until a response comes from server
			while(is_reserved_by_server[tid] == 0) {
				/*BUSY WAIT*/
			}
			// Unlock the mutex when server respond
			pthread_mutex_unlock(&mymtx[requested_seat]);
		}
	}while(true);
	// Join on server thread and exit the thread
	pthread_join(server_thread, NULL);
	pthread_exit(0);
}

void *server_runner(void *param) {
	long tid = (long)param;	// Thread id
	while(true){
		// Wait until client requests a seat
		while(is_requested_by_client[tid] == false){
			/*BUSY WAIT*/
		}
		int requested_seat = requested_seat_array[tid];
		// If the requested seat is empty, then mark it as not empty and serve it to client. Then exit the thread
		if(seat_array[requested_seat] == 0){
			seat_array[requested_seat] = 1;
			is_reserved_by_server[tid] = 1;
			printf("Client%d reserves Seat%d\n", tid, requested_seat);
			pthread_exit(0);
		}
		else{ // If the requested seat is not empty, wait for another request
			is_reserved_by_server[tid] = 2;
			is_requested_by_client[tid] = false;
		}
	}
}
