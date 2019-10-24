#include <iostream>
#include <fstream>
#include <vector>
#include <queue>
#include <set>
#include <stdio.h>

using namespace std;

struct Edge {
    int from = -1, to = -1, weight = -1;
    int thieves[14] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    Edge(int from, int to, int weight) {
        this->from = from;
        this->to = to;
        this->weight = weight;
    }

    ~Edge() = default;
};

struct Vertex {
    int id = -1;
    int coinsCanBeFound[14] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    vector<Edge> edges;

    Vertex(int id) {
        this->id = id;
    }

    ~Vertex() = default;
};

struct State {
    Vertex currentVector = Vertex(0);
    int time = 0;
    int coins[14] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    string path = "";
    set<pair<int, int>> visitedEdges;

    State(Vertex n) {
        currentVector = n;
    };

    ~State() = default;
};

class ComparableForTime {
public:
    bool operator()(State &st1, State &st2) {
        return st1.time > st2.time;
    }
};

int main(int argc, char *argv[]) {

    ifstream input(argv[1]);
    freopen(argv[2], "w", stdout);

    int townNum, roadNum, thiefNum, jewelerNum;
    input >> townNum >> roadNum >> thiefNum >> jewelerNum;
    //Vertexleri tutan vector
    vector<Vertex> vertexVec;
    vertexVec.push_back(Vertex(0)); //Vertexlerin sırası idleri ile aynı olsun diye, ilk başta boş bir vertex ekledim.
    for (int i = 1; i <= townNum; i++) {
        vertexVec.push_back(Vertex(i));
    }
    //Jeweler bilgilerine göre, vertexlere coinleri ekler
    for (int i = 0; i < jewelerNum; i++) {
        int townId, coinNum;
        input >> townId >> coinNum;
        for (int j = 0; j < coinNum; j++) {
            int coinType;
            input >> coinType;
            vertexVec[townId].coinsCanBeFound[coinType] = 1;
        }
    }
    //Road bilgilerine göre vertexlere road ekler, roadlara thiefleri ekler
    for (int i = 0; i < roadNum; i++) {
        int from, to, weight, thiefNum;
        input >> from >> to >> weight >> thiefNum;
        vertexVec[from].edges.push_back(Edge(from, to, weight));
        vertexVec[to].edges.push_back(Edge(to, from, weight));
        for (int j = 0; j < thiefNum; j++) {
            int thiefType;
            input >> thiefType;
            vertexVec[from].edges[vertexVec[from].edges.size() - 1].thieves[thiefType] = 1;
            vertexVec[to].edges[vertexVec[to].edges.size() - 1].thieves[thiefType] = 1;
        }
    }
    //İlk vertexi kontrol eder
    State firstState(vertexVec[1]);
    for (int i = 0; i < 13; i++) {
        if (firstState.currentVector.coinsCanBeFound[i + 1])
            firstState.coins[i + 1] = 1;
    }
    firstState.path += to_string(firstState.currentVector.id);
    firstState.path += " ";
    //Stateleri time'larına göre sıralayan priority_queue
    priority_queue<State, vector<State>, ComparableForTime> stateQueue;
    string finalPath = "-1";
    stateQueue.push(firstState);

    while (!stateQueue.empty()) {
        State curr = stateQueue.top();
        stateQueue.pop();
        if (curr.currentVector.id == townNum) {
            finalPath = curr.path;
            break;
        }
        for (int i = 0; i < curr.currentVector.edges.size(); i++) {
            bool canGo = true;
            for (int j = 0; j < 13; j++) {
                if (curr.currentVector.edges[i].thieves[j + 1] != 0 && curr.coins[j + 1] != 1)
                    canGo = false;
            }
            if (canGo) {
                if (curr.visitedEdges.count(
                        make_pair(curr.currentVector.edges[i].from, curr.currentVector.edges[i].to))) {
                    //do nothing!!!
                } else {
                    curr.visitedEdges.insert(
                            make_pair(curr.currentVector.edges[i].from, curr.currentVector.edges[i].to));

                    State temp(vertexVec[curr.currentVector.edges[i].to]);
                    for (int j = 1; j < 14; j++) {
                        temp.coins[j] = curr.coins[j];
                    }
                    temp.visitedEdges = curr.visitedEdges;
                    temp.path = curr.path;
                    temp.path += to_string(temp.currentVector.id);
                    temp.path += " ";
                    temp.time = curr.time;
                    temp.time += curr.currentVector.edges[i].weight;

                    bool newCoin = false;
                    for (int j = 1; j < 14; j++) {
                        if (temp.currentVector.coinsCanBeFound[j] != 0 && temp.coins[j] != 1) {
                            temp.coins[j] = 1;
                            newCoin = true;
                        }
                    }
                    if (newCoin) {
                        temp.visitedEdges.clear();
                    }
                    stateQueue.push(temp);
                }
            }
        }
    }
    printf("%s", finalPath.c_str());

    return 0;
}