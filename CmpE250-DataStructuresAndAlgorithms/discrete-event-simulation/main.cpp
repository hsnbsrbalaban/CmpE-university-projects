#include <iostream>
#include <queue>
#include <fstream>
#include <deque>
#include <vector>
#include <stdio.h>

using namespace std;

struct Order {
    double time, arrivalTime, orderTime, brewTime, price;
    int id, status, cashierId;

    Order() {}

    Order(double arrivalTime, double orderTime, double brewTime, double price, int id) {
        this->arrivalTime = arrivalTime;
        this->orderTime = orderTime;
        this->brewTime = brewTime;
        this->price = price;
        this->id = id;
        time = arrivalTime;
        status = 0;
        cashierId = 0;
    }
};

class ComparableForTime {
public:
    bool operator()(Order x, Order y) {
        return x.time > y.time;
    }
};

class ComparableForId {
public:
    bool operator()(Order x, Order y) {
        return x.id > y.id;
    }
};

class ComparableForPrice {
public:
    bool operator()(Order x, Order y) {
        return x.price < y.price;
    }
};

struct Cashier {
    int id;
    double workingTime;
    int orderId;
    bool status;

    Cashier() {
        workingTime = 0;
        orderId = 0;
        status = true;
        id = 0;
    }

    Cashier(int i) {
        workingTime = 0;
        orderId = 0;
        status = true;
        id = i;
    }
};

struct Barista {
    double workingTime;
    int orderId;
    bool status;
    int maxLength;
    priority_queue<Order, vector<Order>, ComparableForPrice> baristaQueue;

    Barista() {
        workingTime = 0;
        orderId = 0;
        status = true;
        maxLength = 0;
    }
};

struct Starbucks {
    double customerNum;
    double totalTime;
    int maxCashierQueue = 0;
    int maxBaristaQueue = 0;

    priority_queue<Order, vector<Order>, ComparableForTime> eventQueue;
    priority_queue<Order, vector<Order>, ComparableForPrice> baristaQueue;
    priority_queue<Order, vector<Order>, ComparableForId> finalQueue;
    vector<Cashier> cashierList;
    vector<Barista> baristaList;
    deque<Order> cashierQueue;

    Starbucks(int cashierNum, int customerNum) {
        this->customerNum = customerNum;

        for (int i = 1; i <= cashierNum; i++)
            cashierList.push_back(Cashier(i));

        for (int i = 0; i < cashierNum / 3; i++)
            baristaList.push_back(Barista());
    }

    void modelOne() {
        while (!eventQueue.empty()) {
            Order temp = eventQueue.top();
            eventQueue.pop();

            switch (temp.status) {
                case 0 : {
                    bool added = false;
                    for (int i = 0; i < cashierList.size(); i++) {
                        if (cashierList[i].status) {
                            cashierList[i].status = false;
                            cashierList[i].workingTime += temp.orderTime;
                            cashierList[i].orderId = temp.id;
                            totalTime = temp.arrivalTime;
                            temp.status = 2;
                            temp.time += temp.orderTime;
                            eventQueue.push(temp);
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        cashierQueue.push_back(temp);
                    }
                    break;
                }
                case 2 : {
                    bool added = false;
                    for (int i = 0; i < cashierList.size(); i++) {
                        if (cashierList[i].orderId == temp.id) {
                            cashierList[i].status = true;
                            totalTime = temp.time;
                            break;
                        }
                    }
                    for (int i = 0; i < baristaList.size(); i++) {
                        if (baristaList[i].status) {
                            baristaList[i].status = false;
                            baristaList[i].workingTime += temp.brewTime;
                            baristaList[i].orderId = temp.id;
                            temp.status = 4;
                            temp.time += temp.brewTime;
                            eventQueue.push(temp);
                            added = true;
                            break;
                        }
                    }
                    if (!cashierQueue.empty()) {
                        if (cashierQueue.size() > maxCashierQueue) maxCashierQueue = cashierQueue.size();
                        Order temp2 = cashierQueue.front();
                        cashierQueue.pop_front();
                        for (int i = 0; i < cashierList.size(); i++) {
                            if (cashierList[i].status) {
                                cashierList[i].status = false;
                                cashierList[i].workingTime += temp2.orderTime;
                                cashierList[i].orderId = temp2.id;
                                temp2.status = 2;
                                temp2.time = totalTime + temp2.orderTime;
                                eventQueue.push(temp2);
                                break;
                            }
                        }
                    }
                    if (!added) {
                        baristaQueue.push(temp);
                    }
                    break;
                }
                case 4 : {
                    for (int i = 0; i < baristaList.size(); i++) {
                        if (baristaList[i].orderId == temp.id) {
                            baristaList[i].status = true;
                            totalTime = temp.time;
                            break;
                        }
                    }
                    temp.time = temp.time - temp.arrivalTime;
                    finalQueue.push(temp);
                    if (!baristaQueue.empty()) {
                        if (baristaQueue.size() > maxBaristaQueue) maxBaristaQueue = baristaQueue.size();
                        Order temp2 = baristaQueue.top();
                        baristaQueue.pop();
                        for (int i = 0; i < baristaList.size(); i++) {
                            if (baristaList[i].status) {
                                baristaList[i].status = false;
                                baristaList[i].workingTime += temp2.brewTime;
                                baristaList[i].orderId = temp2.id;
                                temp2.status = 4;
                                temp2.time = totalTime + temp2.brewTime;
                                eventQueue.push(temp2);
                                break;
                            }
                        }
                    }
                }
            }

        }
    }

    void modelTwo() {
        while (!eventQueue.empty()) {
            Order temp = eventQueue.top();
            eventQueue.pop();
            switch (temp.status) {
                case 0 : {
                    bool added = false;
                    for (int i = 0; i < cashierList.size(); i++) {
                        if (cashierList[i].status) {
                            cashierList[i].status = false;
                            cashierList[i].workingTime += temp.orderTime;
                            cashierList[i].orderId = temp.id;
                            totalTime = temp.arrivalTime;
                            temp.status = 2;
                            temp.time += temp.orderTime;
                            temp.cashierId = cashierList[i].id;
                            eventQueue.push(temp);
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        cashierQueue.push_back(temp);
                    }
                    break;
                }
                case 2 : {
                    bool added = false;
                    cashierList[temp.cashierId-1].status = true;
                    totalTime = temp.time;
                    int baristaId;
                    if (temp.cashierId % 3 == 0) {
                        baristaId = temp.cashierId / 3;
                    } else {
                        baristaId = (temp.cashierId / 3) + 1;
                    }

                    if (baristaList[(baristaId - 1)].status) {
                        baristaList[baristaId - 1].status = false;
                        baristaList[baristaId - 1].workingTime += temp.brewTime;
                        baristaList[baristaId - 1].orderId = temp.id;
                        temp.status = 4;
                        temp.time += temp.brewTime;
                        eventQueue.push(temp);
                        added = true;
                    }
                    if (!cashierQueue.empty()) {
                        if (cashierQueue.size() > maxCashierQueue) maxCashierQueue = cashierQueue.size();
                        Order temp2 = cashierQueue.front();
                        cashierQueue.pop_front();
                        for (int i = 0; i < cashierList.size(); i++) {
                            if (cashierList[i].status) {
                                cashierList[i].status = false;
                                cashierList[i].workingTime += temp2.orderTime;
                                cashierList[i].orderId = temp2.id;
                                temp2.status = 2;
                                temp2.time = totalTime + temp2.orderTime;
                                temp2.cashierId = cashierList[i].id;
                                eventQueue.push(temp2);
                                break;
                            }
                        }
                    }
                    if (!added) {
                        baristaList[baristaId-1].baristaQueue.push(temp);
                    }
                    break;
                }
                case 4 : {
                    int baristaId;
                    if (temp.cashierId % 3 == 0) {
                        baristaId = temp.cashierId / 3;
                    } else {
                        baristaId = (temp.cashierId / 3) + 1;
                    }
                    baristaList[baristaId - 1].status = true;
                    totalTime = temp.time;
                    temp.time -= temp.arrivalTime;
                    finalQueue.push(temp);
                    if (!baristaList[baristaId - 1].baristaQueue.empty()) {
                        if(baristaList[baristaId - 1].baristaQueue.size() > baristaList[baristaId - 1].maxLength)
                            baristaList[baristaId - 1].maxLength = baristaList[baristaId - 1].baristaQueue.size();
                        Order temp2 = baristaList[baristaId - 1].baristaQueue.top();
                        baristaList[baristaId - 1].baristaQueue.pop();

                        if (baristaList[baristaId - 1].status) {
                            baristaList[baristaId - 1].status = false;
                            baristaList[baristaId - 1].workingTime += temp2.brewTime;
                            baristaList[baristaId - 1].orderId = temp2.id;
                            temp2.status = 4;
                            temp2.time = totalTime + temp2.brewTime;
                            eventQueue.push(temp2);
                            break;
                        }
                    }
                }
            }

        }

    }

    void addOrder(Order c) {
        eventQueue.push(c);
    }

};

int main(int argc, char* argv[]) {

    ifstream input(argv[1]);
    freopen(argv[2], "w", stdout);

    int cashierNum, customerNum;

    input >> cashierNum >> customerNum;

    Starbucks starbucks1(cashierNum, customerNum);
    Starbucks starbucks2(cashierNum, customerNum);

    for (int i = 0; i < customerNum; i++) {
        double arrivalTime, orderTime, brewTime, price;
        input >> arrivalTime >> orderTime >> brewTime >> price;
        starbucks1.addOrder(Order(arrivalTime, orderTime, brewTime, price, i));
        starbucks2.addOrder(Order(arrivalTime, orderTime, brewTime, price, i));
    }

    starbucks1.modelOne();
    starbucks2.modelTwo();

    printf("%.2lf\n%d\n%d\n",starbucks1.totalTime, starbucks1.maxCashierQueue, starbucks1.maxBaristaQueue);
    for(int i=0; i<starbucks1.cashierList.size(); i++){
        printf("%.2lf\n", starbucks1.cashierList[i].workingTime / starbucks1.totalTime);
    }
    for(int i=0; i<starbucks1.baristaList.size(); i++){
        printf("%.2lf\n", starbucks1.baristaList[i].workingTime / starbucks1.totalTime);
    }
    while(!starbucks1.finalQueue.empty()){
        printf("%.2lf\n",starbucks1.finalQueue.top().time);
        starbucks1.finalQueue.pop();
    }
    printf("\n");

    printf("%.2lf\n%d\n",starbucks2.totalTime, starbucks2.maxCashierQueue);
    for(int i=0; i<cashierNum/3; i++){
        printf("%d\n",starbucks2.baristaList[i].maxLength);
    }
    for(int i=0; i<starbucks2.cashierList.size(); i++){
        printf("%.2lf\n", starbucks2.cashierList[i].workingTime / starbucks2.totalTime);
    }
    for(int i=0; i<starbucks2.baristaList.size(); i++){
        printf("%.2lf\n", starbucks2.baristaList[i].workingTime / starbucks2.totalTime);
    }
    while(!starbucks2.finalQueue.empty()){
        printf("%.2lf\n",starbucks2.finalQueue.top().time);
        starbucks2.finalQueue.pop();
    }
    fclose(stdout);
    return 0;
}