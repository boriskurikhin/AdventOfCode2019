#include <iostream>
#include <string>
#include <fstream>
#include <vector>
#include <queue>
#include <set>

using namespace std;

ifstream inputFile;
int h;
string graph[1000];
vector<pair<int, int> > locations;

pair<int, int> lowest_terms (int n1, int n2) {
    int a = n1; 
    int b = n2;
    int i = 2;
    int bound = abs(a) < abs(b) ? abs(b) : abs(a);
    while ( i <= bound ) {
        if (a % i == 0 && b % i == 0) {
            a /= i;
            b /= i;
            i = 2;
            continue;
        } else {
            i++;
        }
    }
    return make_pair(a, b);
}

int main() {

    //read from the input file
    inputFile.open("day10input.txt");
    inputFile >> h;
    
    for (int i = 0; i < h; i++)
        inputFile >> graph[i];

    /* Push back all the locations */
    for (int i = 0; i < h; i++)
        for (int j = 0; j < graph[i].length(); j++)
            if (graph[i][j] == '#')
                locations.push_back(make_pair(i, j)); 


    int best = 0;
    
    //let's get slopes
    for (int i = 0; i < h; i++) {
        for (int j = 0; j < graph[i].length(); j++) {
            if (graph[i][j] == '#') {
                cout << "Asteroid at " << i << " and " << j << endl;
                set<string> slopes;
                for (pair <int, int > p : locations) {
                    //skip itself
                    if (p.first == i && p.second == j)
                        continue;

                    int yDist = p.first - i, xDist = p.second - j;
                    pair<int, int> sl = lowest_terms(yDist, xDist);
                    
                    //horizontal & vertical checks
                    if (sl.first == 0) sl.second = (sl.second < 0 ? -1 : 1);
                    if (sl.second == 0) sl.first = (sl.first < 0 ? - 1 : 1);

                    //cout << "slope to " << p.first << ", " << p.second << " is " << sl.first << "/" << sl.second << endl; 

                    slopes.insert(to_string(sl.first) + "/" + to_string(sl.second));
                }
                
                if (slopes.size() > best) {
                    best = slopes.size();
                    cout << "best location is " << j << ", " << i << endl;
                }

                //cout << "(" << i << ", " << j << ") = " << slopes.size() << endl;
            }
        }
    }

    cout << best << endl;

    inputFile.close();
    return 0;
}
