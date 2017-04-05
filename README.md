# Multi-Game-Engine
A Game Engine that supports many different games (chess, go, gomoku, tic tac toe, ...).

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

  Choose the game to play.
* `validmoves`

  List the valid moves with the current board.
* `move` *move*

  Execute the specified move.
* `d`

  Print a diagram of the current board.

Valid game names are
* `chess`
* `tictactoe`
* `go`
* `gomoku`

The game variants depend on the chosen game:
* `chess` : *no variants supported yet*
* `tictactoe` : *no variants supported yet*
* `go` : *size of the board, for example* `13x13`
* `gomoku` : *no variants supported yet*

The notation of a *move* depends on the actual game being played.

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
* `sa` the top right corner of a 19x19 board
* `as` the bottom left corner of a 19x19 board
* `ss` the bottom right corner of a 19x19 board

### Gomoku Move 

Gomoku moves are specified the same way as Go moves.

### Tic Tac Toe Move 

Tic Tac Toe moves specify the coordinates of the field to play.

Examples:
* `b2` the center field
* `a1` the top left field
* `a3` the top right field
* `c3` the bottom right field

### Mill Move

Mill moves specify the start and end point of a move.
In the case of killing a stone `"x"` and the coordinates of the killed stone are appended.

The coordinates of the mill board use letters to specify the column and numbers to specify the row.

The bottom left corner is `a1`.

```
a7-------------d7-------------g7
|              |              |
|    b6--------d6--------f6   |
|    |         |         |    |
|    |    c5---d5---e5   |    |
|    |    |         |    |    |
a4---b4---c4        e4---f4---g4
|    |    |         |    |    |
|    |    c3---d3---e3   |    |
|    |         |         |    |
|    b2--------d2--------f2   |
|              |              |
a1 ------------d1-------------g1
```

Please note that not all coordinates in the grid are valid positions on the Mill board.

Examples:
* `a7` to set a stone in the top left corner
* `a7d7` to move the stone from the top left corner to the right
* `a7d7xg1` to move the stone from the top left corner to the right, killing the enemy stone in the bottom right
* `a1g7` to jump a stone from the bottom left to the top right corner



