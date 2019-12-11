#include <iostream>
#include <string>
#include <fstream>
#include <vector>
#include <queue>
#include <set>
#include <math.h>
#include <algorithm>

using namespace std;

ifstream inputFile;
int h;
string graph[30];
vector< pair<pair<int, int>, double > > locations;

//soh cah toa
//tan (theta) = o / a
//I hate trig
double theta(pair<int, int> station, pair<int, int> asteroid) {

    //cout << "STATION (" << station.first << ", " << station.second << ") -> ASTEROID (" << asteroid.first << ", " << asteroid.second << ") = ";

    int opposite = abs(asteroid.first - station.first);
    int adjacent = abs(asteroid.second - station.second);

    double theta = atan((double) opposite / (double) adjacent) * 180.0 / 3.1415; //raw inside angle

    //aligned vertically
    if (asteroid.second == station.second) {
        if (asteroid.first < station.first) {
            theta = 0.0; //directly above
        } else {
            theta = 180.0;
        }
    } else if (asteroid.first == station.first) {
        //aligned horizontally
        if (asteroid.second < station.second) {
            theta = 270.0; //directly on the left
        } else {
            theta = 90.0;
        }
    } else {
    //not aligned directly...
    //top quadrants
        if (asteroid.first < station.first) {
            if (asteroid.second < station.second) {
                //top left
                theta += 270.0;
            } else {
                theta = 90.0 - theta;
            }
        } else {
            if (asteroid.second < station.second) {
                theta = 270.0 - theta;
            } else {
                theta += 90.0;
            }
        }
    }
    //cout << theta << endl;
    return theta;
}

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
    
    inputFile.close();

    /* Push back all the locations */
    for (int i = 0; i < h; i++)
        for (int j = 0; j < graph[i].length(); j++)
            if (graph[i][j] == '#')
                locations.push_back(make_pair(make_pair(i, j), 0)); 


    int best = 0;
    pair <int, int> lazer;
    
    //let's get slopes
    for (int i = 0; i < h; i++) {
        for (int j = 0; j < graph[i].length(); j++) {
            if (graph[i][j] == '#') {
                set<string> slopes;
                for (pair<pair <int, int > , double> p: locations) {
                    //skip itself
                    if (p.first.first == i && p.first.second == j)
                        continue;

                    int yDist = p.first.first - i, xDist = p.first.second - j;
                    pair<int, int> sl = lowest_terms(yDist, xDist);
                    //horizontal & vertical checks
                    if (sl.first == 0) sl.second = (sl.second < 0 ? -1 : 1);
                    if (sl.second == 0) sl.first = (sl.first < 0 ? - 1 : 1);
                    slopes.insert(to_string(sl.first) + "/" + to_string(sl.second));
                }
                
                if (slopes.size() > best) {
                    best = slopes.size();
                    lazer = make_pair(i, j);
                }
            }
        }
    }

    //at this stage we know where the station is
    //keep shooting until we hit 200
    //we're looking for the smallest angle facing upwards
    //but we also need to make sure that we're able to hit that asteroid
    //i think??

    std::cout << best << " at (" << lazer.second << ", " << lazer.first << ")\n";

    /* Calculate angles */
    for (pair<pair<int, int>, double> &p : locations) {
        if (lazer.first == p.first.first && lazer.second == p.first.second) continue;
        p.second = theta(lazer, p.first);
    }

    //sort by theta
    sort(locations.begin( ), locations.end( ), [ ]( const pair<pair<int, int>, double> & lhs, const pair<pair<int, int>, double>& rhs ) {
        return lhs.second < rhs.second;
    });

    int kills = 0;
    string previous_slope = "";
    bool visited[locations.size()];

    for (int i = 0; i < locations.size(); i++)
        visited[i] = false;

    while (true) {
        for (int i = 0; i < locations.size(); i++) {
            if (visited[i] || (locations[i].first.first == lazer.first && locations[i].first.second == lazer.second)) 
                continue;
            
            int yDist = locations[i].first.first - lazer.first, xDist = locations[i].first.second - lazer.second;
            pair<int, int> sl = lowest_terms(yDist, xDist);
            
            if (sl.first == 0) sl.second = (sl.second < 0 ? -1 : 1);
            if (sl.second == 0) sl.first = (sl.first < 0 ? - 1 : 1);
            
            string slope = to_string(sl.first) + "/" + to_string(sl.second);
            
            if (!slope.compare(previous_slope)) continue;

            kills++;
            
            if (kills == 200) {
                cout << "Result: " << locations[i].first.second * 100 + locations[i].first.first << endl;
                return 0;
            }

            visited[i] = true;
            previous_slope = slope;
        }
        previous_slope = "";
    }

    return 0;
}
