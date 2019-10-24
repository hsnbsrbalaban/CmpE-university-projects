#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>
#include <mutex>
#include <unistd.h>

using namespace std;

int total_seat_num;
int *seat_array = NULL;
int *requested_seat_array = NULL;
int *is_reserved_by_server = NULL;
bool *is_requested_by_client = NULL;

pthread_mutex_t mymtx = PTHREAD_MUTEX_INITIALIZER;

void *client_runner(void *param);
void *server_runner(void *param);

int main(int argc, char* argv[]) {

	freopen("output.txt", "w", stdout);

	srand((unsigned)time(NULL));

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

	printf("Number of total seats: %d\n", total_seat_num);

	pthread_t client_threads[total_seat_num];
	for(int i = 1; i <= total_seat_num; i++)
		pthread_create(&client_threads[i], 0, client_runner, (void *)i);
	for(int i = 1; i <= total_seat_num; i++)
		pthread_join(client_threads[i], NULL);

	printf("All seats are reserved.\n");

	delete [] seat_array;
	delete [] requested_seat_array;
	delete [] is_reserved_by_server;
	delete [] is_requested_by_client;

	return 0;
}

void *client_runner(void *param) {
	long tid = (long)param;
	pthread_t server_thread;
	pthread_create(&server_thread, 0, server_runner, (void *)tid);

	int sleep_time = rand() & 150 + 50;
	usleep(sleep_time * 1000); // SLEEP_FOR KULLANARAK DENE

	int requested_seat = rand() % total_seat_num + 1;
	requested_seat_array[tid] = requested_seat;
	is_requested_by_client[tid] = true;

	while(is_reserved_by_server[tid] == 0) {
		/*BUSY WAIT*/
	}

	do{
		if(is_reserved_by_server[tid] == 1) {
			break;
		}
		else {
			requested_seat = rand() % total_seat_num + 1;
			requested_seat_array[tid] = requested_seat;
			is_requested_by_client[tid] = true;
		}
	}while(true);

	pthread_join(server_thread, NULL);
	pthread_exit(0);
}

void *server_runner(void *param) {
	long tid = (long)param;
	while(true){
		while(is_requested_by_client[tid] == false){
		/*BUSY WAIT*/
		}
		int requested_seat = requested_seat_array[tid];
		pthread_mutex_lock(&mymtx);
		if(seat_array[requested_seat] == 0){
			seat_array[requested_seat] = 1;
			is_reserved_by_server[tid] = 1;
			printf("Client%d reserves Seat%d\n", tid, requested_seat);
			pthread_mutex_unlock(&mymtx);
			pthread_exit(0);
		}
		else{
			is_reserved_by_server[tid] = 2;
			is_requested_by_client[tid] = false;
			pthread_mutex_unlock(&mymtx);
		}
	}
}
