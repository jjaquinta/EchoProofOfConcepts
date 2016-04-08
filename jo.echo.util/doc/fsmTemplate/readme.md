# Finite State Machine

This sample demonstrates a simple skill built with the Amazon Alexa Skills Kit.
It implements a [finite state machine](https://en.wikipedia.org/wiki/Finite-state_machine). 
For simple skills, a one shot invocation is enough to collect the information necessary 
to solve a simple task. Move involved skills require more detailed interactions with the 
customer. For these, a FSM is a perfect answer.

There are three central concepts to a FSM: state, input symbols, and the transition table.

The _state_ is a value representing the current position in the flowchart. In the case of an
Alexa skill, this is stored in the session attributes.

An _input symbol_, is input from the user. One of a fixed set of values. In the case of an
Alexa skill, it is an intent.

The _transition table_ simply maps, for a given state, and a given input symbol, the logic
to execute at this point. This can save values, process values, or halt with an error.
It almost always include a new state to transition to for further processing.

In this example we use a FSM to emulate the maze from the classic Colossal Cave game.
The state represents what room in the maze the user is in. The input symbols are the
different cardinal directions the user can go in, and are mapped by intents in our
interaction model. The state table lists, for each room, and for each possible direction
what new room the user goes to, or if they cannot go that way.

Additionally one room is marked the "goal". When the user transitions here, the goal
flag is set. And if they, later, transition back to the starting position, the process
is terminated with a success condition. The player has solved the maze!

There are few business problems that involve threading a maze. However there are plenty
of others where complex input needs to be collected. Entering a UPS tracking code a
few digits at a time, filling in an audio form, or picking an element from a categorized
list. A FSM can be used for all of these.

Alexa has no inbuilt mechanism for "turning on" or "turning off" intents. And many people
struggle with dealing with the fact the user can invoke any intent at any point. With a
FSM you can be aware of the user's current state, and only process those intents that are
valid within that state.
