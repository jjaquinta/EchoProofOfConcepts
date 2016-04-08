"""
This sample demonstrates a simple skill built with the Amazon Alexa Skills Kit.
The Intent Schema, Custom Slots, and Sample Utterances for this skill, as well
as testing instructions are located at http://amzn.to/1LzFrj6

For additional samples, visit the Alexa Skills Kit Getting Started guide at
http://amzn.to/1LGWsLG
"""

from __future__ import print_function

STATE_TRANSITION_TABLE = {
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
        "$goal": True,
        "WEST": "little maze of twisting passages"
    }
}


def lambda_handler(event, context):
    """ Route the incoming request based on type (LaunchRequest, IntentRequest,
    etc.) The JSON body of the request is provided in the event parameter.
    """
    print("event.session.application.applicationId=" +
          event['session']['application']['applicationId'])

    """
    Uncomment this if statement and populate with your skill's application ID to
    prevent someone else from configuring a skill that sends requests to this
    function.
    """
    # if (event['session']['application']['applicationId'] !=
    #         "amzn1.echo-sdk-ams.app.[unique-value-here]"):
    #     raise ValueError("Invalid Application ID")

    if event['session']['new']:
        on_session_started({'requestId': event['request']['requestId']},
                           event['session'])

    if event['request']['type'] == "LaunchRequest":
        return on_launch(event['request'], event['session'])
    elif event['request']['type'] == "IntentRequest":
        return on_intent(event['request'], event['session'])
    elif event['request']['type'] == "SessionEndedRequest":
        return on_session_ended(event['request'], event['session'])


def on_session_started(session_started_request, session):
    """ Called when the session starts """

    print("on_session_started requestId=" + session_started_request['requestId']
          + ", sessionId=" + session['sessionId'])


def on_launch(launch_request, session):
    """ Called when the user launches the skill without specifying what they
    want
    """

    print("on_launch requestId=" + launch_request['requestId'] +
          ", sessionId=" + session['sessionId'])
    # Dispatch to your skill's launch
    return setInitialState()


def on_intent(intent_request, session):
    """ Called when the user specifies an intent for this skill """

    print("on_intent requestId=" + intent_request['requestId'] +
          ", sessionId=" + session['sessionId'])

    intent = intent_request['intent']

    # Dispatch to your skill's intent handlers
    return processInputSymbol(intent, session['attributes'])


def on_session_ended(session_ended_request, session):
    """ Called when the user ends the session.

    Is not called when the skill returns should_end_session=true
    """
    print("on_session_ended requestId=" + session_ended_request['requestId'] +
          ", sessionId=" + session['sessionId'])
    # add cleanup logic here

# --------------- Functions that control the skill's behavior ------------------


def setInitialState():
    sessionAttributes = {"state": "init", "goal": False }
    return composeResponse(sessionAttributes)


def processInputSymbol(intent, session):
    inputSymbol = intent['name']
    if inputSymbol is "AMAZON.HelpIntent":
        return composeResponse(session)

    state = session['state']
    goal = session['goal']
    tableEntry = STATE_TRANSITION_TABLE[state]
    print(tableEntry)
    newState = tableEntry[inputSymbol]
    
    if newState is None:
        return composeErrorResponse(session)
    
    sessionAttributes = {}
    sessionAttributes['state'] = newState
    if '$goal' in tableEntry and tableEntry['$goal'] is True:
        sessionAttributes['goal'] = True
    else:
        sessionAttributes['goal'] = session['goal']
    return composeResponse(sessionAttributes)



def composeResponse(sessionAttributes):
    cardTitle = getStateTitle(sessionAttributes)
    speechOutput = getStateOutput(sessionAttributes)
    repromptText = getStateReprompt(sessionAttributes)
    shouldEndSession = getStateEnd(sessionAttributes)

    return build_response(sessionAttributes, build_speechlet_response(
        cardTitle, speechOutput, repromptText, shouldEndSession))
    
def composeErrorResponse(sessionAttributes):
    cardTitle = getStateTitle(sessionAttributes)
    speechOutput = "You cannot go in that direction. "+getStateOutput(sessionAttributes)
    repromptText = getStateReprompt(sessionAttributes)
    shouldEndSession = getStateEnd(sessionAttributes)

    return build_response(sessionAttributes, build_speechlet_response(
        cardTitle, speechOutput, repromptText, shouldEndSession))


def getStateTitle(session):
    state = session['state']
    return state


def getStateOutput(session):
    state = session['state']
    goal = session['goal']
    tableEntry = STATE_TRANSITION_TABLE[state]
    output = tableEntry["$description"]
    if (state is "init") and goal:
    	output = output + " You have escaped the dungeon with the treasure!"
    else:
    	output = output + " What direction do you want to go in?"
    return output


def getStateReprompt(session):
    reprompt = "You can try to go north, south, east, west, north east, north west, south east, or south west. "
    return reprompt

def getStateEnd(session):
    state = session['state']
    goal = session['goal']
    end = (state is "init") and goal
    return end

# --------------- Helpers that build all of the responses ----------------------


def build_speechlet_response(title, output, reprompt_text, should_end_session):
    return {
        'outputSpeech': {
            'type': 'PlainText',
            'text': output
        },
        'card': {
            'type': 'Simple',
            'title': 'SessionSpeechlet - ' + title,
            'content': 'SessionSpeechlet - ' + output
        },
        'reprompt': {
            'outputSpeech': {
                'type': 'PlainText',
                'text': reprompt_text
            }
        },
        'shouldEndSession': should_end_session
    }


def build_response(session_attributes, speechlet_response):
    return {
        'version': '1.0',
        'sessionAttributes': session_attributes,
        'response': speechlet_response
    }