## Kevin Bacon Game
The code performs social network analysis via variations of the Kevin Bacon game. Here, the social network can be thought of as a map where the vertices are actors and the edge relationship is "appeared together in a movie". To play the Kevin Bacon game, a breadth first search algorition is applied. The game interface allows the user to change the center of the acting universe to a valid actor, find the shortest path to an actor from the current center of the universe, find the number of actors who have a path (connected by some number of steps) to the current center, find the average path length over all actors who are connected by some path to the current center

The commands for the game are below:

    c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation
    d <low> <high>: list actors sorted by degree, with degree between low and high
    i: list actors with infinite separation from the current center
    p <name>: find path from <name> to current center of the universe
    s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high
    u <name>: make <name> the center of the universe
    q: quit game

The text files for both the tests and the actual data can be found in the `Data` folder
