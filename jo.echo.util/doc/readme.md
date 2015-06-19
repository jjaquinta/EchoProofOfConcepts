# Echo Utilities

## Generate BAF

The more utterances in your BAF file, the better the Echo does at recognizing text.
In addition to phrasing your intent in several ways, you also want to provide many different suggestions for the slots in the intent.
This can start to be cumbersome. Such synonyms are usually tied to a slot, and if a slot is used in multiple intents
it can be a headache to maintain.
Additionally when you start supporting multiple slots in an intent, keeping up with the matrix of changes each time you add a new
vocabulary word is hard.

GenerateBaf introduces an XML syntax that lets you define word lists, and also places in an intent where those words are to be inserted.
You can insert the same word list into multiple intents. And you can insert multiple word lists into a single intent. 
GenerateBaf does the work of iterating through the different possible combinations.

You can invoke it from the accompanying jar file.
```
java -cp jo_echo_util.jar jo.echo.util.cmd.GenerateBaf -i GroceryList.baf.xml -o GroceryList.xml
```

### Arguments

*-i* Lets you specify an input file. This is mandatory and needs to be a XML file in the supported format.

*-o* Lets you specify the output file. If no output file is specified, then stdout is used.

### XML Format
The easiest way to define the format is by example:

```
<baf>
	<wordList id="item">
		<word>Apple</word>
		<word>Asparagus</word>
		<word>Avocado</word>
		<word>Zucchini</word>
	</wordList>
	<wordList id="quantity">
		<word>one</word>
		<word>two</word>
		<word>three</word>
	</wordList>
	<intent>ADD	I need more {<insert id="item" />|item}</intent>
	<intent>ADD	I need {<insert id="quantity" />|quantity} more {<insert id="item" />|item}</intent>
	<intent>QUERYALL show list</intent>
</baf>
```

### Example
The above input file generates the following output file:

```
ADD	I need more {apple|item}
ADD	I need more {asparagus|item}
ADD	I need more {avocado|item}
ADD	I need more {zucchini|item}
ADD	I need {one|quantity} more {apple|item}
ADD	I need {one|quantity} more {asparagus|item}
ADD	I need {one|quantity} more {avocado|item}
ADD	I need {one|quantity} more {zucchini|item}
ADD	I need {two|quantity} more {apple|item}
ADD	I need {two|quantity} more {asparagus|item}
ADD	I need {two|quantity} more {avocado|item}
ADD	I need {two|quantity} more {zucchini|item}
ADD	I need {three|quantity} more {apple|item}
ADD	I need {three|quantity} more {asparagus|item}
ADD	I need {three|quantity} more {avocado|item}
ADD	I need {three|quantity} more {zucchini|item}
QUERYALL show list
```
 