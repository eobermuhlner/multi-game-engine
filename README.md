# multi-game-engine
Game Engine that supports many different games (chess, go, gomoku, tic tac toe, ...).

## Engine Algorithm

The Multi Game Engine uses a Monte-Carlo Tree algorithm to decide the best move for a given situation.


## Supported Games

* Chess
* Tic Tac Toe
* Go (in development)
* Gomoku (in development)
* ...

## Protocol (Communication with GUI Front End)

The protocol of the Multi Game Engine is an extension of the UCI Protocol used in the communication between Chess Front Ends and Chess engines.

Additional commands are:
* `game` *gamename* [*gamevariant*]
* `validmoves`
* `move` *move*
* `d`

The notation of a move depends on the actual game being played.

Valid game names are
* `chess`
* `tictactoe`
* `go`
* `gomoku`

The game variants depend on the chosen game:
* `chess` : *no variants supported yet*
* `tictactoe` : *no variants supported yet*
* `go` : *size of the board, for example * `13x13`
* `gomoku` : *no variants supported yet*

### Chess Move 

Chess moves are standard UCI move notations, defining start and end point of the move and optionally specifying the piece that a pawn should be converted into.

Examples:
* `e2e4` to move a pawn forward by two
* `c1h6` to move a bishop across the board
* `e7e8q` to move a pawn forward to the last line and converting it into a queen
* `e1h1` castling to the kings side

### Go  Move 

Go moves specify the coordinates of the field to play.

Letters `a` to `s` are used to specify both rows and columns.

Examples:
* `aa` the top left corner
* `ss` the bottom right corner of a 19x19 board

### Gomoku Move 

Gomoku moves are specified the same way as go moves.

### Tic Tac Toe Move 

Tic Tac Toe moves specify the coordinates of the field to play.

Examples:
* `b2` the center field
* `a1` the top left field
* `a3` the top right field
* `c3` the bottom right field

