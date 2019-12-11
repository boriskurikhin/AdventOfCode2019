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
//This function finds the angle in degrees to an asteroid facing upwards
double theta(pair<int, int> station, pair<int, int> asteroid) {
    int opposite = abs(asteroid.first - station.first);
    int adjacent = abs(asteroid.second - station.second);
    double theta = atan((double) opposite / (double) adjacent) * 180.0 / 3.1415; //raw inside angle
    //aligned vertically
    if (asteroid.second == station.second) {
        if (asteroid.first < station.first) theta = 0.0; //directly above
        else theta = 180.0;
    } else if (asteroid.first == station.first) {
        if (asteroid.second < station.second) theta = 270.0; //directly on the left
        else theta = 90.0;
    } else {
    //not aligned directly...
    //top quadrants
        if (asteroid.first < station.first) {
            if (asteroid.second < station.second) theta += 270.0; //top left
            else theta = 90.0 - theta;
        } else {
            if (asteroid.second < station.second) theta = 270.0 - theta;
            else theta += 90.0;
        }
    }
    return theta;
}

//This is kinda bruteforce but whatever. It puts the rise/run into lowest terms so we can compare slopes
pair<int, int> lowest_terms (int n1, int n2) {
    int a = n1, b = n2, i = 2, bound = abs(a) < abs(b) ? abs(b) : abs(a);
    while ( i <= bound ) {
        if (a % i == 0 && b % i == 0) {
            a /= i; b /= i; i = 2;
            continue;
        } else i++;
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
    pair <int, int> lazer; //this is going to be where the station is built
    
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

    /* Calculate angles */
    for (pair<pair<int, int>, double> &p : locations) {
        if (lazer.first == p.first.first && lazer.second == p.first.second) continue;
        p.second = theta(lazer, p.first);
    }

    //sort by theta, very important -> closest targets first
    sort(locations.begin( ), locations.end( ), [ ]( const pair<pair<int, int>, double> & lhs, const pair<pair<int, int>, double>& rhs ) {
        return lhs.second < rhs.second;
    });

    int kills = 0;
    string previous_slope = "";
    bool visited[locations.size()]; //because who wants to use vector::erase();????

    //make sure we don't have random memory chilling in our visited array
    fill(visited, visited + locations.size(), false);

    while (kills < locations.size()) {
        for (int i = 0; i < locations.size(); i++) {
            //If we destroyed it already, or are attempting to destroy our station -> don't allow it!
            if (visited[i] || (locations[i].first.first == lazer.first && locations[i].first.second == lazer.second)) 
                continue;
            
            int yDist = locations[i].first.first - lazer.first, xDist = locations[i].first.second - lazer.second;
            pair<int, int> sl = lowest_terms(yDist, xDist);
            
            if (sl.first == 0) sl.second = (sl.second < 0 ? -1 : 1);
            if (sl.second == 0) sl.first = (sl.first < 0 ? - 1 : 1);
            
            //slope to asteroid in lowest terms
            string slope = to_string(sl.first) + "/" + to_string(sl.second);
            
            //if the previous target has the same slope, we can't hit it this round
            if (!slope.compare(previous_slope)) continue;

            kills++; //boom
            
            //end-game, no avengers
            if (kills == 200) {
                cout << "Result: " << locations[i].first.second * 100 + locations[i].first.first << endl;
                return 0;
            }

            //mark asteroid as destroyed, record its' slope
            visited[i] = true;
            previous_slope = slope;
        }
        previous_slope = ""; //reset slope as we're doing a new rotation
    }
    return 0;
}
