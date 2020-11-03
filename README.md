# Minesweeper API

## How to use

This is a high-level description of how the API works. For details about the protocol, see the 
[swagger documentation](http://165.227.29.94).

The API exposes two main base paths: `auth` and `games`. `auth` is used to register and 
authenticate players, and `games` is used to play a game.
Once a player is created, the player can:

- Create a new board
- Retrieve a summary of all of her boards
- Retrieve details about a particular board
- Make a move in a particular board
- Change the status active/preserved of a particular board

Each one of these actions is mapped to an API endpoint.

When the player creates a new board, she gets a unique identifier (UID) for future reference. The 
board is initially in `preserved` status, but that can be manually changed, or automatically by 
making the first move. When a board is in active status, the clock for that board is ticking. A 
board can be manually preserved, effectively stopping the timer for that board. To change the status 
of a board or make any move, the board UID is required, plus the information about the requested 
change. Available moves are: reveal, red-flag, question-mark and clear-flag. All types of moves 
require the coordinates of the cell being changed.

The rules governing the game are well-known and not explained here. If the player tries to make any 
invalid move, like red-flagging an already revealed cell, or trying to play after a mine exploded, 
the API will just return the same board in the same status. There is an `isGameOver` property in
the board summary to know whether there are still allowed moves to make. The API will automatically 
set the status of a board to `preserved` when the game is over.

## Implementation

Packages are organized vertically by domain entities. There is an `auth` package for registering and 
login players, and there is a `game` package with the game logic. This makes it easy to keep 
both parts of the system isolated. Each package could be, if needed, extracted into its own 
microservice.

Domain models are completely immutable, which plays well with functional programming, and in this 
case event sourcing. Persistence is designed as an event sourced architecture, implemented with 
akka-persistence.
It is configured to write to a file in the local filesystem using leveldb by default, but can be 
configured to other stores like Cassandra once the app needs to be scaled.

Authentication is implemented using [Silhouette](https://www.silhouette.rocks/) with the 
JWTAuthenticator. Players register or login and get a token that must be sent back to the app on 
every subsequent request in the X-Auth-Token header. Mobile apps can easily use this authentication 
method.

## Testing

For the domain models, unit testing is done using scalacheck property-based testing.
For the rest, tests are implemented as integration tests that spawn the whole application and test 
each endpoint end-to-end, using Scalatest plus Play.

## Documentation

This app relies on Swagger to document all the endpoints. A play-swagger plugin was added that 
automatically adds documentation from comments in the routes file.
This keeps documentation closer to the code so it is easier to keep up to date.
The swagger documentation allows generating client libraries automatically. A Java client library 
was generated and published in [minesweeper-client-java](https://github.com/enriquerodbe/minesweeper-client-java).

## Future work

- The current implementation only allows for one running instance of the app at a time. To make it 
scalable, the persistence plugin must be changed, for example to Cassandra, and the persistent 
actors must be distributed using for example akka cluster sharding.
- Testing can be improved. Although coverage is above 90%, there are some areas for example time 
tracking that weren't thoroughly tested.
- A CI/CD system must be configured to make this easier to work with in a team and simplify deploys.
- https must be configured because currently credentials are being sent as plain text and could 
potentially be intercepted.  
