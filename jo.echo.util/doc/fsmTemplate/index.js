/**
 * Copyright 2016 Jo Jaquinta / TsaTsaTzu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This sample demonstrates a simple skill built with the Amazon Alexa Skills Kit.
 * It implements a finite state machine. https://en.wikipedia.org/wiki/Finite-state_machine
 * For simple skills, a one shot invocation is enough to collect the information necessary 
 * to solve a simple task. Move involved skills require more detailed interactions with the 
 * customer. For these, a FSM is a perfect answer.
 * There are three central concepts to a FSM: state, input symbols, and the transition table.
 * The state is a value representing the current position in the flowchart. In the case of an
 * Alexa skill, this is stored in the session attributes.
 * An input symbol, is input from the user. One of a fixed set of values. In the case of an
 * Alexa skill, it is an intent.
 * The transition table simply maps, for a given state, and a given input symbol, the logic
 * to execute at this point. This can save values, process values, or halt with an error.
 * It almost always include a new state to transition to for further processing.
 *
 * In this example we use a FSM to emulate the maze from the classic Colossal Cave game.
 * The state represents what room in the maze the user is in. The input symbols are the
 * different cardinal directions the user can go in, and are mapped by intents in our
 * interaction model. The state table lists, for each room, and for each possible direction
 * what new room the user goes to, or if they cannot go that way.
 * Additionally one room is marked the "goal". When the user transitions here, the goal
 * flag is set. And if they, later, transition back to the starting position, the process
 * is terminated with a success condition. The player has solved the maze!
 *
 * There are few business problems that involve threading a maze. However there are plenty
 * of others where complex input needs to be collected. Entering a UPS tracking code a
 * few digits at a time, filling in an audio form, or picking an element from a categorized
 * list. A FSM can be used for all of these.
 * 
 * Alexa has no inbuilt mechanism for "turning on" or "turning off" intents. And many people
 * struggle with dealing with the fact the user can invoke any intent at any point. With a
 * FSM you can be aware of the user's current state, and only process those intents that are
 * valid within that state.
 */


var STATE_TRANSITION_TABLE = {
    "init": {
        "$description": "You are in a foyer to a dungeon. The entrance is south of here. ",
        "SOUTH": "maze of little twisty passages"
    },
    "maze of little twisty passages": {
        "$description": "You are in a maze of little twisty passages. ",
        "NORTH": "init",
        "WEST": "twisting maze of little passages",
        "SOUTH": "maze of little twisting passages",
        "SOUTHEAST": "twisty maze of little passages"
    },
    "maze of little twisting passages": {
        "$description": "You are in a maze of little twisting passages. ",
        "SOUTH": "twisty maze of little passages",
        "EAST": "maze of twisting little passages",
        "SOUTHEAST": "little twisty maze of passages"
    },
    "maze of twisty little passages": {
        "$description": "You are in a maze of twisty little passages. ",
        "NORTH": "maze of little twisty passages",
        "WEST": "maze of little twisting passages"
    },
    "maze of twisting little passages": {
        "$description": "You are in a maze of twisting little passages. ",
        "NORTH": "maze of little twisting passages",
        "WEST": "maze of twisty little passages"
    },
    "twisting maze of little passages": {
        "$description": "You are in a twisting maze of little passages. ",
        "SOUTH": "little maze of twisting passages",
        "EAST": "twisty maze of little passages"
    },
    "little twisty maze of passages": {
        "$description": "You are in a little twisty maze of passages. ",
        "NORTH": "twisty maze of little passages",
        "EAST": "maze of little twisty passages"
    },
    "twisty maze of little passages": {
        "$description": "You are in a twisty maze of little passages. ",
        "NORTHEAST": "maze of little twisty passages",
        "NORTHWEST": "little twisty maze of passages",
        "SOUTHWEST": "twisting maze of little passages"
    },
    "little maze of twisting passages": {
        "$description": "You are in a little maze of twisting passages. ",
        "NORTHEAST": "twisting maze of little passages",
        "EAST": "twisting little maze of passages"
    },
    "twisting little maze of passages": {
        "$description": "You are in a twisting little maze of passages. You have found the treasure! ",
        "$goal": true,
        "WEST": "little maze of twisting passages"
    }
};

// Route the incoming request based on type (LaunchRequest, IntentRequest,
// etc.) The JSON body of the request is provided in the event parameter.
exports.handler = function (event, context) {
    try {
        console.log("event.session.application.applicationId=" + event.session.application.applicationId);

        /**
         * Uncomment this if statement and populate with your skill's application ID to
         * prevent someone else from configuring a skill that sends requests to this function.
         */
        /*
        if (event.session.application.applicationId !== "amzn1.echo-sdk-ams.app.[unique-value-here]") {
             context.fail("Invalid Application ID");
        }
        */

        if (event.session.new) {
            onSessionStarted({requestId: event.request.requestId}, event.session);
        }

        if (event.request.type === "LaunchRequest") {
            onLaunch(event.request,
                event.session,
                function callback(sessionAttributes, speechletResponse) {
                    context.succeed(buildResponse(sessionAttributes, speechletResponse));
                });
        } else if (event.request.type === "IntentRequest") {
            onIntent(event.request,
                event.session,
                function callback(sessionAttributes, speechletResponse) {
                    context.succeed(buildResponse(sessionAttributes, speechletResponse));
                });
        } else if (event.request.type === "SessionEndedRequest") {
            onSessionEnded(event.request, event.session);
            context.succeed();
        }
    } catch (e) {
        context.fail("Exception: " + e);
    }
};

/**
 * Called when the session starts.
 */
function onSessionStarted(sessionStartedRequest, session) {
    console.log("onSessionStarted requestId=" + sessionStartedRequest.requestId +
        ", sessionId=" + session.sessionId);
}

/**
 * Called when the user launches the skill without specifying what they want.
 */
function onLaunch(launchRequest, session, callback) {
    console.log("onLaunch requestId=" + launchRequest.requestId +
        ", sessionId=" + session.sessionId);

    // Dispatch to your skill's launch.
    setInitialState(callback);
}

/**
 * Called when the user specifies an intent for this skill.
 */
function onIntent(intentRequest, session, callback) {
    console.log("onIntent requestId=" + intentRequest.requestId +
        ", sessionId=" + session.sessionId);

    var intent = intentRequest.intent,
        intentName = intentRequest.intent.name;

    // Dispatch to your skill's intent handlers
    processInputSymbol(intent, session.attributes, callback);
}

/**
 * Called when the user ends the session.
 * Is not called when the skill returns shouldEndSession=true.
 */
function onSessionEnded(sessionEndedRequest, session) {
    console.log("onSessionEnded requestId=" + sessionEndedRequest.requestId +
        ", sessionId=" + session.sessionId);
    // Add cleanup logic here
}

// --------------- Functions that control the skill's behavior -----------------------

function setInitialState(callback) {
    // Initialize the session attributes
    var sessionAttributes = {"state": "init", "goal": false };
    composeResponse(sessionAttributes, callback);
}

function processInputSymbol(intent, session, callback) {
    console.log("session=" + JSON.stringify(session));
    var inputSymbol = intent.name;
    console.log("inputSymbol=" + inputSymbol);

    if (inputSymbol === "AMAZON.HelpIntent")
    {
    	composeResponse(session, callback);
    	return;
    }
    var state = session.state;
    var goal = session.goal;
    console.log("state=" + state+", goal="+goal);
    var tableEntry = STATE_TRANSITION_TABLE[state];
    console.log("tableEntry="+JSON.stringify(tableEntry));
    var newState = tableEntry[inputSymbol];
    console.log("newState="+newState);
    if (newState === undefined)
      composeErrorResponse(session, callback);
    else
    {
       var sessionAttributes = {};
       sessionAttributes.state = newState;
       if (tableEntry["$goal"] === true)
       	 sessionAttributes.goal = true;
   	   else
         sessionAttributes.goal = session.goal;
       composeResponse(sessionAttributes, callback);
    }
}

function composeResponse(sessionAttributes, callback) {
    var cardTitle = getStateTitle(sessionAttributes);
    var speechOutput = getStateOutput(sessionAttributes);
    var repromptText = getStateReprompt(sessionAttributes);
    var shouldEndSession = getStateEnd(sessionAttributes);

    callback(sessionAttributes,
        buildSpeechletResponse(cardTitle, speechOutput, repromptText, shouldEndSession));
}

function composeErrorResponse(sessionAttributes, callback) {
    var cardTitle = getStateTitle(sessionAttributes);
    var speechOutput = "You cannot go in that direction. "+getStateOutput(sessionAttributes);
    var repromptText = getStateReprompt(sessionAttributes);
    var shouldEndSession = getStateEnd(sessionAttributes);

    callback(sessionAttributes,
        buildSpeechletResponse(cardTitle, speechOutput, repromptText, shouldEndSession));
}

function getStateTitle(session) {
    var state = session.state;
    return state;
}

function getStateOutput(session) {
    var state = session.state;
    var goal = session.goal;
    var tableEntry = STATE_TRANSITION_TABLE[state];
    var output = tableEntry["$description"];
    if ((state === "init") && goal)
    	output += " You have escaped the dungeon with the treasure!"
    else
    	output += " What direction do you want to go in?"
    return output;
}

function getStateReprompt(session) {
    var reprompt = "You can try to go north, south, east, west, north east, north west, south east, or south west. "
    return reprompt;
}

function getStateEnd(session) {
    var state = session.state;
    var goal = session.goal;
    var end = (state === "init") && goal;
    return end;
}

// --------------- Helpers that build all of the responses -----------------------

function buildSpeechletResponse(title, output, repromptText, shouldEndSession) {
    return {
        outputSpeech: {
            type: "PlainText",
            text: output
        },
        card: {
            type: "Simple",
            title: title,
            content: output
        },
        reprompt: {
            outputSpeech: {
                type: "PlainText",
                text: repromptText
            }
        },
        shouldEndSession: shouldEndSession
    };
}

function buildResponse(sessionAttributes, speechletResponse) {
    return {
        version: "1.0",
        sessionAttributes: sessionAttributes,
        response: speechletResponse
    };
}